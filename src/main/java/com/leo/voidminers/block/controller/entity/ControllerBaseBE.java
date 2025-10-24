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
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerBaseBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = 1000000;

    private ModEnergyStorage energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, 0);

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            ControllerBaseBE.this.runtimeState.markInventoryChanged();
            ControllerBaseBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    };

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, ConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    public boolean active;
    public boolean working;

    private final ControllerRuntimeState runtimeState = new ControllerRuntimeState();
    private final ControllerDiagnosticsLogger diagnosticsLogger = new ControllerDiagnosticsLogger();
    private final ControllerInventoryHelper inventoryHelper = new ControllerInventoryHelper(this);

    private LazyOptional<ModEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public ControllerBaseBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CONTROLLER_BASE_BE.get(), pPos, pBlockState);
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
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT());
        data.put("items", itemHandler.serializeNBT());
        data.putInt("progress", this.progress);
        if (name != null) data.putString("name", this.name);
        data.putBoolean("active", active);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        runtimeState.saveTo(data);
        pTag.put(VoidMiners.MODID, data);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag data = pTag.getCompound(VoidMiners.MODID);
        if (data.isEmpty())
            return;

        if (data.contains("energy")) {
            energyHandler.deserializeNBT(data.get("energy"));
        }

        if (data.contains("items")) {
            itemHandler.deserializeNBT(data.getCompound("items"));
        }

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
    }

    @Override
    public CompoundTag getUpdateTag() {
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
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ResourceLocation structure, String name) {
        if (getStructure() == null) {
            setup(structure, name);
        }

        checkStructure(pLevel, pPos);

        boolean hasVoidView = hasViewOnBedrockOrVoid(pPos);

        boolean dimensionAllowed = name == null || ConfigLoader.getInstance().isMinerDimensionAllowed(pLevel.dimension(), name);
        boolean blockedChanged = runtimeState.updateBlockedByDimension(!dimensionAllowed);
        if (blockedChanged) {
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
        }

        if (runtimeState.isBlockedByDimension()) {
            active = false;
            working = false;
            long stored = energyHandler.getLongEnergyStored();
            long capacity = energyHandler.getLongMaxEnergyStored();
            runtimeState.resetCycleSnapshot(stored, capacity);
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

        active = foundStructure && hasVoidView;
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
        setChanged(getLevel(), getBlockPos(), getBlockState());

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

    public ItemStack getBoostedStack(ItemStack base) {
        return inventoryHelper.getBoostedStack(base);
    }

    public ItemStack getWeightedItem(List<WeightedStack> items, RandomSource random) {
        return inventoryHelper.getWeightedItem(items, random);
    }

    public void drops() {
        inventoryHelper.drops();
    }

    private boolean hasViewOnBedrockOrVoid(BlockPos pos) {
        for (int i = 0; i < 320; i++) {
            BlockPos check = pos.below(i);

            if(level.getBlockState(check).is(Blocks.BEDROCK)) return true;

            if (level.getBlockState(check).propagatesSkylightDown(level, check) || level.isFluidAtPosition(check, (fluidState -> !fluidState.isEmpty()))) continue;
            
            return false;
        }

        return true;
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        RegisteredMultiBlockPattern pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, Rotation.NONE);
        if (pattern == null) {
            foundStructure = false;
            return;
        }

        MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, Rotation.NONE);
        if (result == null || !pattern.ID().equals(structure)) {
            return;
        }

        modifierMap.clear();
        foundStructure = true;
        result.blocks().stream().filter(block -> block.getState().getBlock() instanceof ModifierBlock).forEach(block -> {
            ConfigLoader.ModifierConfig modifier = ConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());

            if (!modifierMap.containsKey(block)) {
                modifierMap.put(block, modifier);
            }
        });
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
    }

    public ResourceLocation getStructure() {
        return structure;
    }
}
