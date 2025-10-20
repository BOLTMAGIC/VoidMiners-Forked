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
import net.minecraftforge.energy.IEnergyStorage;
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
            if (SolarPanelBaseBE.this.level != null) {
                SolarPanelBaseBE.this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, ConfigLoader.SolarModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    public boolean active;
    public boolean working;
    private boolean blockedByDimension = false;

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

        if (name == null) {
            energyHandler = new ModEnergyStorage((long)ENERGY_CAPACITY, (long)ENERGY_CAPACITY, (long)ENERGY_CAPACITY, energyHandler != null ? energyHandler.getLongEnergyStored() : 0);
            lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
            return;
        }

        ConfigLoader.SolarPanelConfig config = ConfigLoader.getInstance().getSolarPanelConfig(name);
        if (config == null) {
            energyHandler = new ModEnergyStorage((long)ENERGY_CAPACITY, (long)ENERGY_CAPACITY, (long)ENERGY_CAPACITY, energyHandler != null ? energyHandler.getLongEnergyStored() : 0);
            lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
            return;
        }

        long storage = config.energyStorage();
        if (!ConfigLoader.getInstance().ALLOW_NO_ENERGY_SOLAR_PANELS && storage <= 0) storage = ENERGY_CAPACITY;

        // Allow both input and output for energy transfer - use long values for high-capacity panels
        energyHandler = new ModEnergyStorage(storage, storage, storage, energyHandler != null ? energyHandler.getLongEnergyStored() : 0);
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
    }

    public int getBeamColor() {
        if (structure == null) return 0xFFFFFFFF;
        return MiscUtil.colorMap.getOrDefault(structure.getPath().replace("solar_", ""), 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> toRet = new ArrayList<>();

        // Get tier name from structure path (solar_rubetine -> rubetine)
        String tierName = name != null ? name : (structure != null ? structure.getPath().replace("solar_", "") : "unknown");
        long currentEnergy = energyHandler != null ? energyHandler.getLongEnergyStored() : 0;
        long maxEnergy = energyHandler != null ? energyHandler.getLongMaxEnergyStored() : 0;

        if (blockedByDimension) {
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(tierName.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(tierName)))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("⚠ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("DISABLED IN THIS DIMENSION").withStyle(net.minecraft.ChatFormatting.RED)));

            String dimensionId = level != null ? level.dimension().location().toString() : "unknown";
            toRet.add(Component.literal("🌌 DIMENSION: ").withStyle(net.minecraft.ChatFormatting.BLUE)
                .append(Component.literal(dimensionId).withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("🛠 CONFIG PATH: ").withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal("config/void-miners.json5 → SOLAR_DIMENSION_SETTINGS").withStyle(net.minecraft.ChatFormatting.WHITE)));

            return toRet;
        }

        if(working) {
            // Header with tier name and status
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(tierName.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(tierName)))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));
            
            toRet.add(Component.literal("☀ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("GENERATING POWER").withStyle(net.minecraft.ChatFormatting.GREEN)));
            
            // Energy info
            String energyBar = getEnergyBar(currentEnergy, maxEnergy);
            toRet.add(Component.literal("⚡ ENERGY: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d", currentEnergy)).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(String.format("%,d RF", maxEnergy)).withStyle(net.minecraft.ChatFormatting.WHITE)));
            
            toRet.add(Component.literal(energyBar));
            
            // Generation info
            toRet.add(Component.literal("⚡ GENERATION: ").withStyle(net.minecraft.ChatFormatting.GREEN)
                .append(Component.literal(String.format("%,d RF/tick", getRfTick())).withStyle(net.minecraft.ChatFormatting.WHITE)));
            
            // Solar efficiency
            toRet.add(Component.literal("☀ EFFICIENCY: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%.1f%%", getSolarEfficiency())).withStyle(net.minecraft.ChatFormatting.WHITE)));


            return toRet;
        }

        if (active) {
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(tierName.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(tierName)))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("⚠ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("NOT GENERATING").withStyle(net.minecraft.ChatFormatting.RED)));

            // More detailed reason detection
            String reason;
            if (isEnergyHandlerFull()) {
                reason = "Energy storage full";
            } else {
                float efficiency = getSolarEfficiency();
                if (efficiency <= 0) {
                    long timeOfDay = level.getDayTime() % 24000;
                    if (timeOfDay >= 12000) {
                        reason = "Night time (wait for day)";
                    } else if (level.isRaining()) {
                        reason = level.isThundering() ? "Thunderstorm (15% efficiency)" : "Raining (30% efficiency)";
                    } else {
                        reason = "No sunlight available";
                    }
                } else {
                    reason = "Unknown issue";
                }
            }

            toRet.add(Component.literal("❌ REASON: ").withStyle(net.minecraft.ChatFormatting.RED)
                .append(Component.literal(reason).withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("⚡ ENERGY: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d", currentEnergy)).withStyle(net.minecraft.ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(String.format("%,d RF", maxEnergy)).withStyle(net.minecraft.ChatFormatting.WHITE)));

            toRet.add(Component.literal("⚡ POTENTIAL: ").withStyle(net.minecraft.ChatFormatting.BLUE)
                .append(Component.literal(String.format("%,d RF/tick", getRfTick())).withStyle(net.minecraft.ChatFormatting.WHITE)));

            return toRet;
        }

        if (foundStructure) {
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(tierName.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(tierName)))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("⚠ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("INACTIVE").withStyle(net.minecraft.ChatFormatting.YELLOW)));

            // Check what's blocking the sky access
            String blockingIssue = "No clear view to sky";
            String tip = "Remove blocks above the panel";

            // Check for specific obstructions
            for (int i = 1; i <= 10; i++) {
                BlockPos checkPos = worldPosition.above(i);
                BlockState state = level.getBlockState(checkPos);
                if (!state.isAir()) {
                    blockingIssue = String.format("Blocked by %s at %d blocks above",
                        state.getBlock().getName().getString(), i);
                    tip = String.format("Remove the %s above the panel",
                        state.getBlock().getName().getString());
                    break;
                }
            }

            toRet.add(Component.literal("❌ ISSUE: ").withStyle(net.minecraft.ChatFormatting.RED)
                .append(Component.literal(blockingIssue).withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("💡 TIP: ").withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal(tip).withStyle(net.minecraft.ChatFormatting.WHITE)));

            return toRet;
        }

        // Structure incomplete
        toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
            .append(Component.literal(tierName.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(tierName)))
            .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));
        
        toRet.add(Component.literal("❌ STATUS: ").withStyle(net.minecraft.ChatFormatting.RED)
            .append(Component.literal("STRUCTURE INCOMPLETE").withStyle(net.minecraft.ChatFormatting.DARK_RED)));
        
        toRet.add(Component.literal("💡 TIP: ").withStyle(net.minecraft.ChatFormatting.AQUA)
            .append(Component.literal("Shift + Right-click for structure guide").withStyle(net.minecraft.ChatFormatting.WHITE)));
        
        toRet.add(Component.literal("📋 MISSING BLOCKS:").withStyle(net.minecraft.ChatFormatting.YELLOW));

        if (structure != null && MiscUtil.structureMap.containsKey(structure.toString())) {
            MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
                toRet.add(Component.literal("  • ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(string).withStyle(net.minecraft.ChatFormatting.WHITE))
                    .append(Component.literal(": ").withStyle(net.minecraft.ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(integer)).withStyle(net.minecraft.ChatFormatting.RED)));
            });
        } else {
            toRet.add(Component.literal("  • Structure data not available").withStyle(net.minecraft.ChatFormatting.GRAY));
        }

        return toRet;
    }
    
    private net.minecraft.ChatFormatting getTierColor(String tierName) {
        if (tierName == null) {
            return net.minecraft.ChatFormatting.WHITE;
        }
        return switch (tierName.toLowerCase()) {
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
    
    private String getEnergyBar(long current, long max) {
        if (max == 0) return "│░░░░░░░░░░│ 0%";

        double percentage = (double) current / max;
        int filledBars = (int) (percentage * 10);

        StringBuilder bar = new StringBuilder("│");
        for (int i = 0; i < 10; i++) {
            if (i < filledBars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append(String.format("│ %.1f%%", percentage * 100));

        return bar.toString();
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
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
        data.putBoolean("blockedByDimension", blockedByDimension);
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

        if (data.contains("blockedByDimension")) {
            blockedByDimension = data.getBoolean("blockedByDimension");
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        
        // Ensure we have a valid energy handler even if setup wasn't called yet
        if (energyHandler == null) {
            energyHandler = new ModEnergyStorage((long)ENERGY_CAPACITY, 0L, (long)ENERGY_CAPACITY, 0L);
        }
        
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
        if(getStructure() == null || this.name == null) {
            setup(structure, name);
        }

        checkStructure(pLevel, pPos);

        boolean dimensionAllowed = this.name == null || ConfigLoader.getInstance().isSolarDimensionAllowed(pLevel.dimension(), this.name);
        boolean newBlockedState = !dimensionAllowed;
        if (blockedByDimension != newBlockedState) {
            blockedByDimension = newBlockedState;
            if (level != null) {
                level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
            }
        } else {
            blockedByDimension = newBlockedState;
        }

        if (blockedByDimension) {
            active = false;
            working = false;
            pushEnergyToNeighbors();
            if (level != null) {
                level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
            }
            return;
        }

        boolean skyView = hasViewOnSky(pPos);
        active = foundStructure && skyView;


        if (level != null) {
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
        }

        if(!active) {
            working = false;
            return;
        }

        float solarEff = getSolarEfficiency();
        boolean energyFull = isEnergyHandlerFull();
        working = !energyFull && solarEff > 0;

        // Always push energy to neighbors, even when buffer is full
        pushEnergyToNeighbors();

        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        if (!working) {
            return;
        }

        progress++;
        long energyGenerated = getRfTick();
        if (energyGenerated > 0) {
            energyHandler.addEnergy(energyGenerated);
        }

        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
        sync();

        if (progress >= getMaxProgress()) {
            progress = 0;
        }
        sync();
    }

    private void pushEnergyToNeighbors() {
        if (level == null || level.isClientSide) return;
        if (energyHandler == null) return;

        long available = energyHandler.getLongEnergyStored();
        if (available <= 0) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            LazyOptional<IEnergyStorage> cap = neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());
            if (!cap.isPresent()) continue;

            IEnergyStorage receiver = cap.orElse(null);
            if (receiver == null) continue;

            if (available <= 0) break;

            int toSend = (int) Math.min(available, Integer.MAX_VALUE);
            int accepted = receiver.receiveEnergy(toSend, false);
            if (accepted > 0) {
                energyHandler.removeEnergy(accepted);
                available -= accepted;
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }

    private void sync() {
        if (level != null) {
            setChanged(level, getBlockPos(), getBlockState());

            if(level.isClientSide) return;

            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public long getRfTick() {
        if (name == null) {
            return 0;
        }

        // Calculate modifier bonuses
        // Output Modifier: Boosts generation via generation value
        // Efficiency Modifier: Also boosts generation via efficiency value
        // Weather Modifier: Reduces weather penalties (applied in getSolarEfficiency)
        float mod = 1.0f;
        float efficiencyMod = 1.0f;

        for (Map.Entry<BlockInWorld, ConfigLoader.SolarModifierConfig> entry : modifierMap.entrySet()) {
            float genMod = entry.getValue().generation();
            float effMod = (2.0f - entry.getValue().efficiency());
            mod *= genMod;
            efficiencyMod *= effMod;
            
        }

        // Both modifiers multiply together for total generation boost
        // Example: Output (1.5x) * Efficiency (1.5x) = 2.25x total
        float totalMod = mod * efficiencyMod;

        ConfigLoader.SolarPanelConfig config = ConfigLoader.getInstance().getSolarPanelConfig(name);
        if (config == null) {
            return 0;
        }

        long baseGeneration = config.energyGeneration();
        float efficiency = getSolarEfficiency() / 100.0f; // Convert percentage to decimal
        long finalRF = Math.max(0, (long) (baseGeneration * totalMod * efficiency));


        // Final formula: BaseGeneration * AllModifiers * SolarEfficiency
        // Example Ultimate: 5120 * 2.25 (both modifiers) * 1.0 (100% day) = 11,520 RF/tick
        return finalRF;
    }

    public int getMaxProgress() {
        if (name == null) {
            return 100; // Default duration if name is null
        }

        // No modifier effect on cycle time - keep it constant
        return ConfigLoader.getInstance().getSolarPanelConfig(name).duration();
    }

    public float getSolarEfficiency() {
        if (level == null) return 0;

        // Check if we're in void dimension
        String dimensionName = level.dimension().location().toString();
        boolean isVoidDimension = dimensionName.contains("void") || dimensionName.contains("voidminers");

        // Simple Minecraft time-based calculation
        float efficiency = 100.0f;

        // Day/Night cycle check
        long timeOfDay = level.getDayTime() % 24000;
        boolean isDaytime = timeOfDay >= 0 && timeOfDay < 12000; // 0-12000 is day, 12000-24000 is night

        if (!isDaytime) {
            // No generation at night
            return 0.0f;
        }

        // Calculate sun angle efficiency (highest at noon)
        float dayProgress = timeOfDay / 12000.0f; // 0 to 1 during the day
        float solarAngle = (float) Math.sin(dayProgress * Math.PI); // Peak at noon (0.5)
        efficiency *= Math.max(0.3f, solarAngle); // Minimum 30% during dawn/dusk

        // Sky light level - skip this check in void dimension
        if (!isVoidDimension) {
            int skyLight = level.getBrightness(LightLayer.SKY, getBlockPos().above());
            if (skyLight < 15) {
                efficiency *= (skyLight / 15.0f);
            }
        }
        // In void dimension, assume full sky light access if no blocks above

        // Weather conditions
        float weatherPenalty = 1.0f;
        if (level.isRaining()) {
            weatherPenalty = 0.3f; // 30% efficiency in rain
            if (level.isThundering()) {
                weatherPenalty = 0.15f; // 15% efficiency in thunderstorm
            }

            // Apply weather resistance modifiers
            for (Map.Entry<BlockInWorld, ConfigLoader.SolarModifierConfig> entry : modifierMap.entrySet()) {
                float resistance = entry.getValue().weatherResistance();
                if (resistance >= 3.0f) {
                    // Ultimate tier: complete weather immunity
                    weatherPenalty = 1.0f;
                    break; // No need to check other modifiers
                } else if (resistance > 1.0f) {
                    // Other tiers: multiplicative boost
                    float protectionBoost = (resistance - 1.0f);
                    weatherPenalty = weatherPenalty * (1.0f + protectionBoost);
                    weatherPenalty = Math.min(1.0f, weatherPenalty); // Cap at 100% efficiency
                }
            }
        }
        efficiency *= weatherPenalty;

        return Math.max(0, Math.min(100, efficiency));
    }


    private boolean hasViewOnSky(BlockPos pos) {
        // Special handling for void dimension
        String dimensionName = level.dimension().location().toString();
        boolean isVoidDimension = dimensionName.contains("void") || dimensionName.contains("voidminers");


        // In void dimension, just check for blocks above
        if (isVoidDimension) {
            // Check for any obstructions above the panel
            for (int i = 1; i <= 10; i++) {  // Check 10 blocks up
                BlockPos checkPos = pos.above(i);
                BlockState state = level.getBlockState(checkPos);

                if (!state.isAir()) {
                    return false;
                }
            }
            return true;  // No obstructions in void dimension
        }

        // Normal dimension logic
        BlockPos checkPos = pos.above();

        // Check the position directly above first
        if (!level.canSeeSky(checkPos)) {
            return false;
        }

        // Check for any obstructions up to sky height
        for (int i = 1; i < level.getMaxBuildHeight() - pos.getY(); i++) {
            checkPos = pos.above(i);
            BlockState state = level.getBlockState(checkPos);

            // If we hit a solid, non-transparent block, no sky access
            if (!state.isAir() && !state.propagatesSkylightDown(level, checkPos)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEnergyHandlerFull() {
        return energyHandler.getLongEnergyStored() >= energyHandler.getLongMaxEnergyStored();
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, container);
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        // Try to find any structure at this position
        RegisteredMultiBlockPattern pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, Rotation.NONE);
        if (pattern == null) {
            // If no structure found, try with other rotations
            for (Rotation rotation : Rotation.values()) {
                if (rotation != Rotation.NONE) {
                    pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, rotation);
                    if (pattern != null) break;
                }
            }
        }

        if (pattern == null) {
            foundStructure = false;
            return;
        }

        // Check if the found pattern matches the expected structure
        String expectedStructurePath = (structure != null ? structure.getPath() : null);
        String foundPatternPath = pattern.ID().getPath();

        if (expectedStructurePath == null || !foundPatternPath.equals(expectedStructurePath)) {
            foundStructure = false;
            return;
        }

        // Verify the structure actually matches with rotation support
        MultiblockMatchResult result = null;
        for (Rotation rotation : Rotation.values()) {
            result = pattern.pattern().matchesWithResult(pLevel, pPos, rotation);
            if (result != null) break;
        }

        if (result == null) {
            foundStructure = false;
            return;
        }

        modifierMap.clear();
        foundStructure = true;
        result.blocks().stream().filter(block -> block.getState().getBlock() instanceof ModifierBlock).forEach(block -> {
            // For solar panels, we need a method to get solar modifier config
            ConfigLoader.SolarModifierConfig modifier = ConfigLoader.getInstance().getSolarModifierConfig(block.getState().getBlock(), name);

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
