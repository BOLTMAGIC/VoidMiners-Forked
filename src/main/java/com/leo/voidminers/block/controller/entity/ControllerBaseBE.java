package com.leo.voidminers.block.controller.entity;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.modifier.ModifierBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.energy.ModEnergyStorage;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.leo.voidminers.multiblock.MultiblockScheduler;

public class ControllerBaseBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = 1000000;

    private ModEnergyStorage energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, 0);

    // Default output slots (base). Will be resized by upgrades.
    private static final int BASE_OUTPUT_SLOTS = 9;

    // itemHandler can be recreated when upgrades change; keep non-final
    private ItemStackHandler itemHandler = createItemHandler();

    // Upgrade slots (hidden from production; used to calculate storage increase)
    private final ItemStackHandler upgradeHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            // Recalculate storage when upgrades change
            ControllerBaseBE.this.recalculateStorageFromUpgrades();
            ControllerBaseBE.this.runtimeState.markInventoryChanged();
            if (ControllerBaseBE.this.level != null) {
                ControllerBaseBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    // LazyOptionals for capabilities
    private LazyOptional<ModEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty(); // used for internal item handler cast
    private LazyOptional<net.minecraftforge.items.IItemHandler> lazyCombinedItemHandler = LazyOptional.empty();

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, ConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    // Applied upgrade tier stored as NBT (0 = none, 1 = T1, 2 = T2, 3 = T3)
    private int appliedUpgradeTier = 0;

    public boolean active;
    public boolean working;

    private final ControllerRuntimeState runtimeState = new ControllerRuntimeState();
    private final ControllerDiagnosticsLogger diagnosticsLogger = new ControllerDiagnosticsLogger();
    private final ControllerInventoryHelper inventoryHelper = new ControllerInventoryHelper(this);

    // Per-instance guard to prevent multiple executions inside the same world tick (e.g. from booster mods)
    private long lastProcessedGameTime = Long.MIN_VALUE;
    // Per-instance guard for void/bedrock checks
    private long lastVoidCheckGameTime = Long.MIN_VALUE;
    // Per-instance guard for structure pattern checks (cache heavy multiblock matching)
    private long lastStructureCheckGameTime = Long.MIN_VALUE;
    // Snapshot of last successful structure match: map absolute positions -> block state
    private Map<BlockPos, BlockState> lastStructureSnapshot = null;

    // Cache shared across controllers to avoid repeated column scans by multiple miners
    private static final ConcurrentHashMap<ColumnKey, CachedView> columnCache = new ConcurrentHashMap<>();

    public ControllerBaseBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CONTROLLER_BASE_BE.get(), pPos, pBlockState);
    }

    // Helper types for column cache
        private record ColumnKey(ResourceLocation dim, int x, int z) {

    }

    private record CachedView(boolean hasView, long checkedAt) {
    }

    // Helper to create item handlers with correct callback
    private static ItemStackHandler createItemHandler() {
        return new ItemStackHandler(ControllerBaseBE.BASE_OUTPUT_SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                // Note: we cannot reference outer instance here; caller should set their own handler or use wrapper method
            }
        };
    }

    // Recreate itemHandler with a new slot count, migrating items
    private void replaceItemHandler(int newSlots) {
        ItemStackHandler newHandler = new ItemStackHandler(newSlots) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                ControllerBaseBE.this.runtimeState.markInventoryChanged();
                if (ControllerBaseBE.this.level != null) {
                    ControllerBaseBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        };

        // Copy existing stacks into new handler
        int copySlots = Math.min(itemHandler.getSlots(), newHandler.getSlots());
        for (int i = 0; i < copySlots; i++) {
            newHandler.setStackInSlot(i, itemHandler.getStackInSlot(i));
        }

        this.itemHandler = newHandler;

        // Update lazy references so external capability and internal getter reflect the new handler
        if (this.lazyItemHandler != null) this.lazyItemHandler.invalidate();
        this.lazyItemHandler = LazyOptional.of(() -> this.itemHandler);

        // Invalidate combined handler so it will be recreated on demand
        if (this.lazyCombinedItemHandler != null) this.lazyCombinedItemHandler.invalidate();
        this.lazyCombinedItemHandler = LazyOptional.empty();
    }

    // Calculate new output slots from upgrades and replace handler if needed
    private void recalculateStorageFromUpgrades() {
        // Read applied tier from BE (stored as NBT). This represents the active upgrade
        int tier = this.appliedUpgradeTier;
        ConfigLoader cfg = ConfigLoader.getInstance();
        int extraSlots = 0;
        if (tier == 3) {
            extraSlots = cfg.UPGRADE_T3_SLOTS;
        } else if (tier == 2) {
            extraSlots = cfg.UPGRADE_T2_SLOTS;
        } else if (tier == 1) {
            extraSlots = cfg.UPGRADE_T1_SLOTS;
        }

        int desiredSlots = BASE_OUTPUT_SLOTS + extraSlots;
        if (desiredSlots != itemHandler.getSlots()) {
            replaceItemHandler(desiredSlots);
        }
    }

    public void setup(ResourceLocation structure, String name) {
        this.structure = structure;
        this.name = name;
        setupEnergyStorage();
    }

    public void setupEnergyStorage() {
        // Only invalidate and recreate the energy capability; keep item capability intact
        if (lazyEnergyHandler != null) {
            lazyEnergyHandler.invalidate();
        }
        int storage = ConfigLoader.getInstance().getMinerConfig(name).energyStorage();

        if (!ConfigLoader.getInstance().ALLOW_NO_ENERGY_MINERS && storage <= 0) storage = ENERGY_CAPACITY;
        energyHandler = new ModEnergyStorage(storage, storage, 0, energyHandler.getEnergyStored());
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
    }

    public int getBeamColor() {
        return MiscUtil.colorMap.getOrDefault(structure.getPath(), 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        return ControllerTooltipBuilder.build(this);
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT());
        data.put("items", itemHandler.serializeNBT());
        data.put("upgrades", upgradeHandler.serializeNBT());
        // Persist applied upgrade tier
        data.putInt("appliedUpgradeTier", this.appliedUpgradeTier);
        data.putInt("progress", this.progress);
        if (name != null) data.putString("name", this.name);
        data.putBoolean("active", active);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        runtimeState.saveTo(data);
        pTag.put(VoidMiners.MODID, data);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        CompoundTag data = pTag.getCompound(VoidMiners.MODID);
        if (data.isEmpty())
            return;

        if (data.contains("energy")) {
            energyHandler.deserializeNBT(data.get("energy"));
        }

        if (data.contains("items")) {
            // Load into current handler; if different size desired we will recalc after loading upgrades
            itemHandler.deserializeNBT(data.getCompound("items"));
        }

        if (data.contains("upgrades")) {
            upgradeHandler.deserializeNBT(data.getCompound("upgrades"));
        }

        // Load applied upgrade tier (if present)
        if (data.contains("appliedUpgradeTier")) {
            this.appliedUpgradeTier = data.getInt("appliedUpgradeTier");
        }

        // After loading upgrades, recalculate storage and migrate items if needed
        recalculateStorageFromUpgrades();

        if (data.contains("progress")) {
            progress = data.getInt("progress");
        }

        if (data.contains("name")) {
            name = data.getString("name");
        }

        if (data.contains("active")) {
            active = data.getBoolean("active");
        }

        if (data.contains("structure")) {
            structure = ResourceLocation.parse(data.getString("structure"));
        }

        if (data.contains("showStructure")) {
            showStructure = data.getBoolean("showStructure");
        }
        runtimeState.loadFrom(data);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setupEnergyStorage();
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        // Provide combined item handler lazily; create on demand
        lazyCombinedItemHandler = LazyOptional.of(() -> new CombinedInvWrapper(itemHandler, upgradeHandler));
        // Ensure the active itemHandler instance has correct onContentsChanged behavior
        // (recreate handler with controller-aware callback)
        replaceItemHandler(itemHandler.getSlots());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            // Expose combined handler (outputs + upgrade slots) to external callers
            if (lazyCombinedItemHandler == null || !lazyCombinedItemHandler.isPresent()) {
                lazyCombinedItemHandler = LazyOptional.of(() -> new CombinedInvWrapper(itemHandler, upgradeHandler));
            }
            return lazyCombinedItemHandler.cast();
        }

        return super.getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (lazyCombinedItemHandler == null || !lazyCombinedItemHandler.isPresent()) {
                lazyCombinedItemHandler = LazyOptional.of(() -> new CombinedInvWrapper(itemHandler, upgradeHandler));
            }
            return lazyCombinedItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ResourceLocation structure, String name) {
        // Only run on server side to avoid client-side drift / visual-only updates
        if (pLevel != null && pLevel.isClientSide) return;
        if (getStructure() == null) {
            setup(structure, name);
        }

        // Per-tile guard: prevent multiple executions in the same world tick (e.g. from booster mods)
        // Respect config: if ALLOW_TICK_ACCELERATION_MULTIBLOCKS == true, skip the guard and allow external tick-acceleration.
        if (pLevel != null && !ConfigLoader.getInstance().ALLOW_TICK_ACCELERATION_MULTIBLOCKS) {
            long gameTime = pLevel.getGameTime();
            if (this.lastProcessedGameTime == gameTime) return;
            this.lastProcessedGameTime = gameTime;
        }

        // Avoid running the heavy multiblock pattern matching every tick.
        // Use a cached result and only re-run at most every MINER_CHECK_INTERVAL_TICKS
        // or if we're close to finishing a cycle (progress lookahead).
        maybeCheckStructure(pLevel, pPos);

        boolean hasVoidView = hasViewOnBedrockOrVoid(pPos);

        boolean dimensionAllowed = name == null || ConfigLoader.getInstance().isMinerDimensionAllowed(Objects.requireNonNull(pLevel).dimension(), name);
        boolean blockedChanged = runtimeState.updateBlockedByDimension(!dimensionAllowed);
        if (blockedChanged) {
            assert level != null;
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
        }

        if (runtimeState.isBlockedByDimension()) {
            active = false;
            working = false;
            long stored = energyHandler.getLongEnergyStored();
            long capacity = energyHandler.getLongMaxEnergyStored();
            runtimeState.resetCycleSnapshot(stored, capacity);
            assert level != null;
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
            diagnosticsLogger.logStateTransitions(
                name,
                structure,
                worldPosition,
                runtimeState.isBlockedByDimension(),
                foundStructure,
                hasVoidView,
                active,
                working,
                runtimeState,
                runtimeState.getLastEnergyDemand(),
                stored,
                capacity
            );
            return;
        }

        // If we have never validated the structure yet (freshly placed controller), be optimistic
        // and allow operation until the pre-production check runs. This means the BE may run
        // a full cycle and then detect the structure is invalid just before producing.
        boolean assumeStructure = foundStructure;
        if (!foundStructure && this.lastStructureSnapshot == null && this.lastStructureCheckGameTime == Long.MIN_VALUE) {
            assumeStructure = true;
        }
        active = assumeStructure && hasVoidView;
        assert level != null;
        level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);

        if (!active) {
            working = false;
            long stored = energyHandler.getLongEnergyStored();
            long capacity = energyHandler.getLongMaxEnergyStored();
            runtimeState.resetCycleSnapshot(stored, capacity);
            diagnosticsLogger.logStateTransitions(
                name,
                structure,
                worldPosition,
                runtimeState.isBlockedByDimension(),
                foundStructure,
                hasVoidView,
                active,
                working,
                runtimeState,
                runtimeState.getLastEnergyDemand(),
                stored,
                capacity
            );
            return;
        }

        int energyDemand = getRfTick();
        long energyStored = energyHandler.getLongEnergyStored();
        long energyCapacity = energyHandler.getLongMaxEnergyStored();

        runtimeState.updateEnergySnapshot(energyDemand, energyStored, energyCapacity);
        runtimeState.setLastInventoryFull(inventoryHelper.isItemHandlerFull());

        boolean canOutput = inventoryHelper.canProduceItems();
        runtimeState.setLastOutputBlocked(!canOutput);

        runtimeState.setLastEnergyDemandTooHigh(energyDemand > energyCapacity);
        runtimeState.setLastEnergyInsufficient(!runtimeState.isLastEnergyDemandTooHigh() && energyDemand > energyStored);

        working = !runtimeState.isLastInventoryFull()
            && !runtimeState.isLastOutputBlocked()
            && !runtimeState.isLastEnergyDemandTooHigh()
            && !runtimeState.isLastEnergyInsufficient();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

        diagnosticsLogger.logStateTransitions(
            name,
            structure,
            worldPosition,
            runtimeState.isBlockedByDimension(),
            foundStructure,
            hasVoidView,
            active,
            working,
            runtimeState,
            energyDemand,
            energyStored,
            energyCapacity
        );

        if (!working) {
            if (runtimeState.isLastEnergyDemandTooHigh()) {
                progress = 0;
            }
            return;
        }

        progress++;
        energyHandler.removeEnergy(energyDemand);
        runtimeState.setLastEnergyStored(energyHandler.getLongEnergyStored());

        assert pLevel != null;
        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        sync();

        if (progress < getMaxProgress()) {
            return;
        }

        List<WeightedStack> allOutputs = new ArrayList<>();

        for (MinerRecipe recipe : inventoryHelper.getValidRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        ItemStack output = inventoryHelper.getBoostedStack(
            inventoryHelper.getWeightedItem(allOutputs, level.random)
        );

        inventoryHelper.insertItemStack(output);

        progress = 0;
        sync();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }


    private void sync() {
        assert level != null;
        setChanged(level, getBlockPos(), getBlockState());

        if(level.isClientSide) return;

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public int getRfTick() {
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().energy();
        }

        return (int) (ConfigLoader.getInstance().getMinerConfig(name).energyTick() * mod);
    }

    public int getMaxProgress() {
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().speed();
        }

        return (int) (ConfigLoader.getInstance().getMinerConfig(name).duration() * mod);
    }

    public void drops() {
        inventoryHelper.drops();
    }

    private boolean hasViewOnBedrockOrVoid(BlockPos pos) {
        if (level == null) return false;

        long gameTime = level.getGameTime();

        // Build a column key for cache lookup
        ColumnKey key = new ColumnKey(level.dimension().location(), pos.getX(), pos.getZ());

        CachedView cached = columnCache.get(key);

        // Read config-tunable values
        int checkInterval = ConfigLoader.getInstance().MINER_CHECK_INTERVAL_TICKS;
        int cacheTtl = ConfigLoader.getInstance().MINER_CACHE_TTL_TICKS;
        int progressLookahead = ConfigLoader.getInstance().MINER_PROGRESS_LOOKAHEAD;

        // If we have a fresh cached value, use it
        if (cached != null && gameTime - cached.checkedAt <= cacheTtl) {
            return cached.hasView;
        }

        // If we are not due for a new check yet, and we have a cached (even stale) value, reuse it
        if (gameTime - this.lastVoidCheckGameTime < checkInterval && cached != null) {
            return cached.hasView;
        }

        // Heuristic: if we're very close to finishing (about to generate an item), do the check regardless
        if (this.progress >= 0 && this.progress < getMaxProgress() - progressLookahead && (gameTime - this.lastVoidCheckGameTime) < checkInterval && cached != null) {
            return cached.hasView;
        }

        // Only check down to the world's minimum build height, and at most 320 blocks
        int minY = level.getMinBuildHeight();
        int startY = pos.getY() - 1;
        int lowestY = Math.max(minY, pos.getY() - 320);

        for (int y = startY; y >= lowestY; y--) {
            BlockPos check = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState state = level.getBlockState(check);

            if (state.is(Blocks.BEDROCK)) {
                break;
            }

            if (state.isAir()) continue; // air -> continue downward

            if (!state.getFluidState().isEmpty()) continue; // fluid -> treat as transparent

            if (state.propagatesSkylightDown(level, check)) continue; // transparent by skylight rules

            // Non-air, non-fluid, non-transparent block blocks view
            // store and return immediately
            columnCache.put(key, new CachedView(false, gameTime));
            this.lastVoidCheckGameTime = gameTime;
            return false;
        }

        // No blockers found in range -> view to void
        columnCache.put(key, new CachedView(true, gameTime));
        this.lastVoidCheckGameTime = gameTime;
        return true;
    }

    /**
     * Invalidate the cached view result for the column at the given position in the given level.
     * This should be called when a block changes in that column (place/break/fluid change).
     */
    public static void invalidateCacheFor(Level level, BlockPos pos) {
        if (level == null || pos == null) return;
        ColumnKey key = new ColumnKey(level.dimension().location(), pos.getX(), pos.getZ());
        columnCache.remove(key);
    }

    /**
     * Called when blocks in the multiblock may have changed and the structure cache must be invalidated.
     * This forces a re-evaluation on the next tick.
     */
    public void handleStructureChanged() {
        this.lastStructureCheckGameTime = Long.MIN_VALUE;
        // Clear previous match state so we don't keep stale modifiers
        this.foundStructure = false;
        this.modifierMap.clear();
        this.lastStructureSnapshot = null;
        setChanged();
    }

    /**
     * Run the expensive structure matching only when due.
     */
    private void maybeCheckStructure(Level pLevel, BlockPos pPos) {
        if (pLevel == null) return;

        long gameTime = pLevel.getGameTime();
        int interval = ConfigLoader.getInstance().MINER_CHECK_INTERVAL_TICKS;
        int lookahead = ConfigLoader.getInstance().MINER_PROGRESS_LOOKAHEAD;
        // If we have a recent snapshot, do a cheap validation first
        if (this.lastStructureSnapshot != null) {
            if (isSnapshotStillValid(pLevel)) {
                // Snapshot still valid: keep foundStructure/modifierMap and skip expensive check
                this.lastStructureCheckGameTime = gameTime;
                this.foundStructure = true;
                return;
            } else {
                // Snapshot invalid. If we're not near completion, avoid running expensive full match now;
                // mark structure as not found and clear modifiers. We'll run full check only when close to producing.
                if (!(this.progress >= 0 && this.progress >= getMaxProgress() - lookahead)) {
                    this.foundStructure = false;
                    this.modifierMap.clear();
                    this.lastStructureSnapshot = null;
                    this.lastStructureCheckGameTime = gameTime;
                    return;
                }
                // else fall through and run full check because we're about to produce items
            }
        } else {
            // If we've checked recently, and we're not close to finishing, reuse the cached timing guard
            if (this.lastStructureCheckGameTime != Long.MIN_VALUE) {
                if (gameTime - this.lastStructureCheckGameTime < interval) {
                    if (!(this.progress >= 0 && this.progress >= getMaxProgress() - lookahead)) {
                        return;
                    }
                }
            }
        }

        // Time to perform a fresh (possibly expensive) check - enqueue into global scheduler
        this.lastStructureCheckGameTime = gameTime;
        MultiblockScheduler.schedule(pLevel, pPos);
    }

    /**
     * Cheaply validate that the last recorded structure snapshot still matches the world.
     * Returns true if all recorded positions still have the same block type/state.
     */
    private boolean isSnapshotStillValid(Level lvl) {
        if (lvl == null || this.lastStructureSnapshot == null) return false;
        try {
            for (Map.Entry<BlockPos, BlockState> e : this.lastStructureSnapshot.entrySet()) {
                BlockPos pos = e.getKey();
                BlockState recorded = e.getValue();
                BlockState now = lvl.getBlockState(pos);
                if (now.getBlock() != recorded.getBlock()) return false;
                if (!now.equals(recorded)) return false;
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        // Reset before attempting to find a matching structure so stale state isn't preserved
        foundStructure = false;
        modifierMap.clear();
        this.lastStructureSnapshot = null;

        // Try all rotations to allow the controller to be placed at any orientation
        for (Rotation rot : Rotation.values()) {
            RegisteredMultiBlockPattern pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, rot);
            if (pattern == null) continue;

            MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, rot);
            if (result == null) continue;

            if (!pattern.ID().equals(structure)) continue;

            // We found a matching pattern for the configured structure in this rotation
            foundStructure = true;
            result.blocks().stream()
                .filter(block -> block.getState().getBlock() instanceof ModifierBlock)
                .forEach(block -> {
                    ConfigLoader.ModifierConfig modifier = ConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());
                    if (!modifierMap.containsKey(block)) {
                        modifierMap.put(block, modifier);
                    }
                });

            // Build a snapshot of the structure blocks so we can cheaply validate later
            try {
                Map<BlockPos, BlockState> snap = new HashMap<>();
                for (BlockInWorld b : result.blocks()) {
                    snap.put(b.getPos(), b.getState());
                }
                this.lastStructureSnapshot = snap;
            } catch (Throwable ignored) {
                this.lastStructureSnapshot = null;
            }

            break;
        }
    }

    ControllerRuntimeState getRuntimeStateInternal() {
        return runtimeState;
    }

    Map<BlockInWorld, ConfigLoader.ModifierConfig> getModifierMapInternal() {
        return modifierMap;
    }

    ModEnergyStorage getEnergyHandlerInternal() {
        return energyHandler;
    }

    ItemStackHandler getItemHandlerInternal() {
        return itemHandler;
    }

    public ItemStackHandler getUpgradeHandlerInternal() {
        return upgradeHandler;
    }

    int getCurrentProgress() {
        return progress;
    }

    String getMinerNameInternal() {
        return name;
    }

    boolean hasFoundStructureInternal() {
        return foundStructure;
    }

    boolean isActiveInternal() {
        return active;
    }

    boolean isWorkingInternal() {
        return working;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
        lazyItemHandler.invalidate();
        lazyCombinedItemHandler.invalidate();
    }

    public ResourceLocation getStructure() {
        return structure;
    }

    public int getAppliedUpgradeTier() {
        return appliedUpgradeTier;
    }

    public void setAppliedUpgradeTier(int tier) {
        this.appliedUpgradeTier = tier;
        // Recalculate storage immediately when tier changes
        recalculateStorageFromUpgrades();
        // mark changed so NBT syncs
        setChanged();
    }
}
