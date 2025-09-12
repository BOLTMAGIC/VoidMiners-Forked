package com.leo.voidminers.block.entity;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.energy.ModEnergyStorage;
import com.leo.voidminers.init.ModBlockEntities;
import com.leo.voidminers.multiblock.SolarPanelMultiblocks;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
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

public class SolarPanelBaseBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = 1000000;

    private ModEnergyStorage energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, 0, ENERGY_CAPACITY, 0);

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            SolarPanelBaseBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
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

    private LazyOptional<ModEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public SolarPanelBaseBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SOLAR_PANEL_BASE_BE.get(), pPos, pBlockState);
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
        energyHandler = new ModEnergyStorage(storage, 0, storage, energyHandler.getEnergyStored());
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
    }

    public int getBeamColor() {
        return MiscUtil.colorMap.getOrDefault(structure.getPath().replace("solar_", ""), 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> toRet = new ArrayList<>();

        if(working) {
            return List.of(Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.working"),
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.generation", getRfTick()),
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.efficiency", getSolarEfficiency() + "%"));
        }

        if (active) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.not_working"),
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.generation", getRfTick())
            );
        }

        if (foundStructure) {
            return List.of(
                Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.not_active")
            );
        }

        toRet.add(Component.translatable("tooltip." + VoidMiners.MODID + ".solar_panel.missing_structure") );

        MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
            toRet.add(Component.literal(string + ": " + integer));
        });

        return toRet;
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

        active = foundStructure && hasViewOnSky(pPos);
        level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);

        if(!active) return;

        working = !isEnergyHandlerFull() && getSolarEfficiency() > 0;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

        if (!working) {
            return;
        }

        progress++;
        int energyGenerated = getRfTick();
        energyHandler.addEnergy(energyGenerated);

        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        sync();

        if (progress >= getMaxProgress()) {
            progress = 0;
        }
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
            mod *= entry.getValue().energy(); // For solar panels, this becomes generation multiplier
        }

        int baseGeneration = ConfigLoader.getInstance().getMinerConfig(name).energyTick();
        float efficiency = getSolarEfficiency() / 100.0f; // Convert percentage to decimal

        return (int) (baseGeneration * mod * efficiency);
    }

    public int getMaxProgress() {
        float mod = 1;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().speed(); // For solar panels, this affects generation cycle speed
        }

        return (int) (ConfigLoader.getInstance().getMinerConfig(name).duration() * mod);
    }

    public float getSolarEfficiency() {
        if (level == null) return 0;

        // Base efficiency factors
        float efficiency = 100.0f;

        // Sky light level (0-15)
        int skyLight = level.getBrightness(LightLayer.SKY, getBlockPos().above());
        efficiency *= (skyLight / 15.0f);

        // Weather conditions
        if (level.isRaining()) {
            efficiency *= 0.2f; // 20% efficiency in rain
            if (level.isThundering()) {
                efficiency *= 0.5f; // 10% efficiency in thunderstorm
            }
        }

        // Day/Night cycle
        if (level.isDay()) {
            long timeOfDay = level.getDayTime() % 24000;
            if (timeOfDay >= 6000 && timeOfDay <= 18000) { // Daytime (6AM to 6PM)
                float dayProgress = (timeOfDay - 6000) / 12000.0f; // 0 to 1
                float solarAngle = (float) Math.sin(dayProgress * Math.PI); // Peak at noon
                efficiency *= solarAngle;
            } else {
                efficiency *= 0.05f; // 5% efficiency during sunrise/sunset
            }
        } else {
            efficiency *= 0.0f; // No generation at night
        }

        // Weather resistance modifier
        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            // We can use the 'item' modifier as weather resistance for solar panels
            if (level.isRaining()) {
                efficiency /= entry.getValue().item(); // Weather resistance reduces rain penalty
            }
        }

        return Math.max(0, efficiency);
    }

    private boolean hasViewOnSky(BlockPos pos) {
        // Check if there's a clear view to the sky above the solar panel
        for (int i = 1; i < level.getMaxBuildHeight() - pos.getY(); i++) {
            BlockPos checkPos = pos.above(i);
            BlockState state = level.getBlockState(checkPos);
            
            // If we hit a non-transparent block, no sky access
            if (!state.propagatesSkylightDown(level, checkPos) && !level.isFluidAtPosition(checkPos, (fluidState -> !fluidState.isEmpty()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isEnergyHandlerFull() {
        return energyHandler.getEnergyStored() >= energyHandler.getMaxEnergyStored();
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, container);
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        RegisteredMultiBlockPattern pattern = SolarPanelMultiblocks.MANAGER.findAnyStructure(pLevel, pPos, Rotation.NONE);
        if (pattern == null) {
            foundStructure = false;
            return;
        }

        MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, Rotation.NONE);
        if (result == null || !pattern.ID().equals(structure)) {
            foundStructure = false;
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