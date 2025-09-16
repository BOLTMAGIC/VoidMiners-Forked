package com.leo.voidminers.block.entity;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.energy.ModEnergyStorage;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import com.leo.voidminers.util.ListUtil;
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
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
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
            ControllerBaseBE.this.inventoryChanged = true;
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

    private boolean canProduceCache = true;
    private boolean inventoryChanged = true;

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
        List<Component> toRet = new ArrayList<>();
        
        // Get base config values
        int baseEnergyTick = ConfigLoader.getInstance().getMinerConfig(name).energyTick();
        int baseDuration = ConfigLoader.getInstance().getMinerConfig(name).duration();
        int energyStorage = energyHandler.getMaxEnergyStored();
        int currentEnergy = energyHandler.getEnergyStored();
        
        // Calculate modifiers
        float energyMod = 1.0f;
        float speedMod = 1.0f;
        float itemMod = 1.0f;
        
        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            energyMod *= entry.getValue().energy();
            speedMod *= entry.getValue().speed();
            itemMod *= entry.getValue().item();
        }

        if(working) {
            // Header with tier name and status
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor()))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));
            
            toRet.add(Component.literal("⚡ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("MINING ACTIVE").withStyle(net.minecraft.ChatFormatting.GREEN)));
            
            // Energy info with bar
            String energyBar = getEnergyBar(currentEnergy, energyStorage);
            toRet.add(Component.literal("⚡ ENERGY: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d", currentEnergy)).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(String.format("%,d RF", energyStorage)).withStyle(net.minecraft.ChatFormatting.WHITE)));
            
            toRet.add(Component.literal(energyBar));
            
            // Consumption with modifiers
            toRet.add(Component.literal("⚡ CONSUMPTION: ").withStyle(net.minecraft.ChatFormatting.RED)
                .append(Component.literal(String.format("%,d RF/tick", getRfTick())).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(getModifierText(" (", energyMod, baseEnergyTick, "×)", net.minecraft.ChatFormatting.AQUA)));
            
            // Duration with modifiers
            toRet.add(Component.literal("⏱ DURATION: ").withStyle(net.minecraft.ChatFormatting.BLUE)
                .append(Component.literal(String.format("%d ticks", getMaxProgress())).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(getModifierText(" (", speedMod, baseDuration, "×)", net.minecraft.ChatFormatting.AQUA)));
            
            // Item modifier if different from 1.0
            if (itemMod != 1.0f) {
                toRet.add(Component.literal("📦 ITEM BOOST: ").withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(String.format("%.1f×", itemMod)).withStyle(net.minecraft.ChatFormatting.WHITE)));
            }
            
            // Progress bar
            float progressPercent = (float) progress / getMaxProgress();
            String progressBar = getProgressBar(progressPercent);
            toRet.add(Component.literal("⏳ PROGRESS: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%.1f%%", progressPercent * 100)).withStyle(net.minecraft.ChatFormatting.WHITE)));
            toRet.add(Component.literal(progressBar));
            
            return toRet;
        }

        if (active) {
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor()))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));
            
            toRet.add(Component.literal("⚠ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("NOT WORKING").withStyle(net.minecraft.ChatFormatting.RED)));
            
            toRet.add(Component.literal("❌ ISSUE: ").withStyle(net.minecraft.ChatFormatting.RED)
                .append(Component.literal("Inventory full or insufficient energy").withStyle(net.minecraft.ChatFormatting.GRAY)));
            
            toRet.add(Component.literal("⚡ ENERGY: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d", currentEnergy)).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(String.format("%,d RF", energyStorage)).withStyle(net.minecraft.ChatFormatting.WHITE)));
            
            toRet.add(Component.literal("⚡ REQUIRED: ").withStyle(net.minecraft.ChatFormatting.RED)
                .append(Component.literal(String.format("%,d RF/tick", getRfTick())).withStyle(net.minecraft.ChatFormatting.WHITE)));
            
            return toRet;
        }

        if (foundStructure) {
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor()))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));
            
            toRet.add(Component.literal("⚠ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("INACTIVE").withStyle(net.minecraft.ChatFormatting.YELLOW)));
            
            toRet.add(Component.literal("❌ ISSUE: ").withStyle(net.minecraft.ChatFormatting.RED)
                .append(Component.literal("Cannot see bedrock/void").withStyle(net.minecraft.ChatFormatting.GRAY)));
            
            toRet.add(Component.literal("💡 TIP: ").withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal("Make sure center block has clear path to bedrock!").withStyle(net.minecraft.ChatFormatting.WHITE)));
            
            return toRet;
        }

        toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
            .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor()))
            .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));
        
        toRet.add(Component.literal("❌ STATUS: ").withStyle(net.minecraft.ChatFormatting.RED)
            .append(Component.literal("STRUCTURE INCOMPLETE").withStyle(net.minecraft.ChatFormatting.DARK_RED)));
        
        toRet.add(Component.literal("💡 TIP: ").withStyle(net.minecraft.ChatFormatting.AQUA)
            .append(Component.literal("Shift + Right-click for structure guide").withStyle(net.minecraft.ChatFormatting.WHITE)));
        
        toRet.add(Component.literal("📋 MISSING BLOCKS:").withStyle(net.minecraft.ChatFormatting.YELLOW));

        MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
            toRet.add(Component.literal("  • ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(string).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(Component.literal(": ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(integer)).withStyle(net.minecraft.ChatFormatting.RED)));
        });

        return toRet;
    }
    
    private net.minecraft.ChatFormatting getTierColor() {
        return switch (name.toLowerCase()) {
            case "rubetine" -> net.minecraft.ChatFormatting.RED;
            case "aurantium" -> net.minecraft.ChatFormatting.GOLD;
            case "citrinetine" -> net.minecraft.ChatFormatting.YELLOW;
            case "verdium" -> net.minecraft.ChatFormatting.GREEN;
            case "azurine" -> net.minecraft.ChatFormatting.BLUE;
            case "caerium" -> net.minecraft.ChatFormatting.DARK_BLUE;
            case "amethystine" -> net.minecraft.ChatFormatting.DARK_PURPLE;
            case "rosarium" -> net.minecraft.ChatFormatting.LIGHT_PURPLE;
            case "ultimate" -> net.minecraft.ChatFormatting.DARK_RED;
            default -> net.minecraft.ChatFormatting.WHITE;
        };
    }
    
    private Component getModifierText(String prefix, float modifier, int baseValue, String suffix, net.minecraft.ChatFormatting color) {
        if (modifier == 1.0f) return Component.empty();
        
        return Component.literal(prefix).withStyle(color)
            .append(Component.literal(String.format("%.1f", modifier)).withStyle(color))
            .append(Component.literal(suffix).withStyle(color));
    }
    
    private String getEnergyBar(int current, int max) {
        float percent = (float) current / max;
        int bars = (int) (percent * 20);
        StringBuilder bar = new StringBuilder("§a");
        
        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                bar.append("█");
            } else if (i == bars && percent * 20 - bars > 0.5) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar.toString() + "§r";
    }
    
    private String getProgressBar(float percent) {
        int bars = (int) (percent * 20);
        StringBuilder bar = new StringBuilder("§e");
        
        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                bar.append("█");
            } else if (i == bars && percent * 20 - bars > 0.5) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar.toString() + "§r";
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
        data.putBoolean("canProduceCache", canProduceCache);
        data.putBoolean("inventoryChanged", inventoryChanged);
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

        if (data.contains("canProduceCache")) {
            canProduceCache = data.getBoolean("canProduceCache");
        }

        if (data.contains("inventoryChanged")) {
            inventoryChanged = data.getBoolean("inventoryChanged");
        }
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
        if(getStructure() == null) {
            setup(structure, name);
        }

        checkStructure(pLevel, pPos);

        active = foundStructure && hasViewOnBedrockOrVoid(pPos);
        level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);

        if(!active) return;

        working = !isItemHandlerFull() && getRfTick() <= energyHandler.getEnergyStored() && canProduceItems();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

        if (!working) {
            return;
        }

        progress++;
        energyHandler.removeEnergy(getRfTick());

        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        sync();

        if (progress < getMaxProgress()) {
            return;
        }

        List<WeightedStack> allOutputs = new ArrayList<>();

        for (MinerRecipe recipe : allRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        ItemStack output = getBoostedStack(getWeightedItem(allOutputs, level.random));


        ItemStack remaining;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!isItemValid(output, itemHandler.getStackInSlot(i))) continue;
            remaining = itemHandler.insertItem(i, output.copy(), false);
            if (remaining.isEmpty()) break;
            output = remaining;
        }

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
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().item();
        }

        int count = (int) (base.getCount() * mod);
        return base.copyWithCount(count);
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

    private boolean isItemHandlerFull() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).getCount() < itemHandler.getStackInSlot(i).getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private List<MinerRecipe> allRecipes() {
        if (level.isClientSide) {
            return new ArrayList<>();
        }

        if (structure == null) {
            return new ArrayList<>();
        }

        return level.getRecipeManager().getAllRecipesFor(MinerRecipe.Type.INSTANCE)
            .stream()
            .filter(recipe -> {
                if (recipe.allowHigherTiers()) {
                    return recipe.minTier() <= MiscUtil.tierMap.get(structure.getPath());
                } else {
                    return recipe.minTier() == MiscUtil.tierMap.get(structure.getPath());
                }
            })
            .filter(recipe -> recipe.dimension().equals(this.level.dimension()))
            .toList();

    }

    private boolean isItemValid(ItemStack stack, ItemStack handler) {
        return handler.isEmpty() || handler.is(stack.getItem()) && stack.getCount() + handler.getCount() <= handler.getMaxStackSize();
    }

    private boolean canInsertItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (isItemValid(stack, itemHandler.getStackInSlot(i))) {
                ItemStack remaining = itemHandler.insertItem(i, stack.copy(), true); // simulate = true
                if (remaining.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canProduceItems() {
        // Update cache if inventory changed or at start of new cycle
        if (inventoryChanged || progress == 0) {
            canProduceCache = calculateCanProduceItems();
            inventoryChanged = false;
        }

        return canProduceCache;
    }

    private boolean calculateCanProduceItems() {
        List<WeightedStack> allOutputs = new ArrayList<>();

        for (MinerRecipe recipe : allRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        if (allOutputs.isEmpty()) {
            return false;
        }

        // Check if any potential output can be inserted
        for (WeightedStack weightedStack : allOutputs) {
            ItemStack output = getBoostedStack(weightedStack.stack.copy());
            if (canInsertItem(output)) {
                return true;
            }
        }

        return false;
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, container);
    }

    public ItemStack getWeightedItem(List<WeightedStack> items, RandomSource random) {
        float totalWeight = ListUtil.getTotalWeight(items);

        float randomValue = random.nextFloat() * totalWeight;

        for (WeightedStack item : items) {
            randomValue -= item.weight;
            if (randomValue <= 0) {
                return item.stack;
            }
        }

        return ItemStack.EMPTY;
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
