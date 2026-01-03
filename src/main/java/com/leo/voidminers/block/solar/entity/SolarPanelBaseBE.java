package com.leo.voidminers.block.solar.entity;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.modifier.ModifierBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.energy.ModEnergyStorage;
import com.leo.voidminers.init.ModBlockEntities;
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
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

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

    private final SolarPanelRuntimeState runtimeState = new SolarPanelRuntimeState();

    private LazyOptional<ModEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    // Cache for detected always-day dimensions (e.g. void-like dims).
    // We populate this lazily after strong confirmations to avoid false positives.
    private static final Set<String> DETECTED_ALWAYS_DAY = Collections.synchronizedSet(new HashSet<>());
    // Temporary counters per dimension: incremented when we observe full skylight at night; must reach THRESHOLD to cache.
    private static final Map<String, Integer> DETECTION_COUNTERS = Collections.synchronizedMap(new HashMap<>());
    // Use a high threshold to avoid transient/edge-case detection (e.g. 200 ticks ~10 seconds)
    private static final int ALWAYS_DAY_CONFIRM_THRESHOLD = 200;

    // Per-instance guard to prevent multiple executions inside the same world tick (e.g. from booster mods)
    private long lastProcessedGameTime = Long.MIN_VALUE;

    /**
     * Strict detection for always-day dimensions:
     * - Explicitly accept dimension ids containing "void"/"voidminers".
     * - Otherwise only detect if all of the following hold:
     *   * Not a vanilla dimension (overworld/nether/end)
     *   * Server-side (avoid client noise)
     *   * Currently night
     *   * The panel has a direct view to sky and skylight == 15 above the panel
     *   * Observed for MANY consecutive ticks (threshold above)
     */
    private boolean isAlwaysDayDimensionAt(Level lvl, BlockPos pos) {
        if (lvl == null) return false;
        String dimKey = lvl.dimension().location().toString();

        // Cached positive - but re-validate: if a cached entry points to a vanilla dimension (overworld/nether/end)
        // we must remove it to avoid persisting false positives from earlier heuristics.
        if (DETECTED_ALWAYS_DAY.contains(dimKey)) {
            String lkCached = dimKey.toLowerCase();
            if (lkCached.contains("overworld") || lkCached.equals("minecraft:overworld") || lkCached.contains("the_nether") || lkCached.contains("the_end") || lkCached.contains("nether") || lkCached.contains("end")) {
                DETECTED_ALWAYS_DAY.remove(dimKey);
            } else {
                return true;
            }
        }

        String lk = dimKey.toLowerCase();

        // Quick explicit name matches
        if (lk.contains("void") || lk.contains("voidminers")) {
            DETECTED_ALWAYS_DAY.add(dimKey);
            return true;
        }

        // Never treat obvious vanilla dims as always-day
        if (lk.contains("overworld") || lk.equals("minecraft:overworld") || lk.contains("the_nether") || lk.contains("the_end") || lk.contains("nether") || lk.contains("end")) {
            return false;
        }

        // Only run the runtime heuristic on server side
        if (lvl.isClientSide) return false;

        // require night + full skylight + direct view to sky
        if (!lvl.isDay()) {
            BlockPos checkPos = pos.above();
            int sky = lvl.getBrightness(LightLayer.SKY, checkPos);
            if (sky >= 15 && hasViewOnSky(pos)) {
                int c = DETECTION_COUNTERS.getOrDefault(dimKey, 0) + 1;
                DETECTION_COUNTERS.put(dimKey, c);
                if (c >= ALWAYS_DAY_CONFIRM_THRESHOLD) {
                    DETECTED_ALWAYS_DAY.add(dimKey);
                    DETECTION_COUNTERS.remove(dimKey);
                    return true;
                }
                return false;
            }
        }

        // Reset counter on any failure
        DETECTION_COUNTERS.remove(dimKey);
        return false;
    }

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
            energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, energyHandler != null ? energyHandler.getLongEnergyStored() : 0);
            lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
            return;
        }

        ConfigLoader.SolarPanelConfig config = ConfigLoader.getInstance().getSolarPanelConfig(name);
        if (config == null) {
            energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, energyHandler != null ? energyHandler.getLongEnergyStored() : 0);
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

    @SuppressWarnings("unused")
    public List<Component> getInteractionTooltip() {
        List<Component> toRet = new ArrayList<>();

        // Get tier name from structure path (solar_rubetine -> rubetine)
        String tierName = name != null ? name : (structure != null ? structure.getPath().replace("solar_", "") : "unknown");
        long currentEnergy = energyHandler != null ? energyHandler.getLongEnergyStored() : runtimeState.lastEnergyStored();
        long maxEnergy = energyHandler != null ? energyHandler.getLongMaxEnergyStored() : runtimeState.lastEnergyCapacity();
        boolean blockedByDimension = runtimeState.isBlockedByDimension();
        boolean working = runtimeState.isWorking();
        boolean active = runtimeState.isActive();
        float efficiency = runtimeState.lastEfficiency() > 0 ? runtimeState.lastEfficiency() : getSolarEfficiency();
        long generation = runtimeState.lastGeneration() > 0 ? runtimeState.lastGeneration() : getRfTick();

        if (blockedByDimension) {
            toRet.add(Component.literal("═══ ").withStyle(net.minecraft.ChatFormatting.GRAY)
                .append(Component.literal(tierName.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(tierName)))
                .append(Component.literal(" ═══").withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("⚠ STATUS: ").withStyle(net.minecraft.ChatFormatting.GOLD)
                .append(Component.literal("DISABLED IN THIS DIMENSION").withStyle(net.minecraft.ChatFormatting.RED)));

            String dimensionId = level != null ? level.dimension().location().toString() : "unknown";
            toRet.add(Component.literal("🌌 DIMENSION: ").withStyle(net.minecraft.ChatFormatting.BLUE)
                .append(Component.literal(dimensionId).withStyle(net.minecraft.ChatFormatting.GRAY)));

            toRet.add(Component.literal("🛠 REASON: ").withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal("Solar generation is disabled in this dimension.").withStyle(net.minecraft.ChatFormatting.WHITE)));

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
                .append(Component.literal(String.format("%,d RF/tick", generation)).withStyle(net.minecraft.ChatFormatting.WHITE)));

            // Solar efficiency
            toRet.add(Component.literal("☀ EFFICIENCY: ").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%.1f%%", efficiency)).withStyle(net.minecraft.ChatFormatting.WHITE)));


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
                float currentEfficiency = efficiency;
                if (currentEfficiency <= 0) {
                    currentEfficiency = getSolarEfficiency();
                }
                if (currentEfficiency <= 0) {
                    // Determine more accurate reason based on skylight / view / weather
                    if (level == null) {
                        reason = "No sunlight available";
                    } else if (!hasViewOnSky(getBlockPos())) {
                        reason = "Blocked by blocks above (no clear view to sky)";
                    } else {
                        int skyLight = level.getBrightness(LightLayer.SKY, getBlockPos().above());
                        if (skyLight <= 0) {
                            reason = "No skylight (darkness / underground)";
                        } else if (level.isRaining()) {
                            reason = level.isThundering() ? "Thunderstorm (reduced efficiency)" : "Raining (reduced efficiency)";
                        } else {
                            reason = "No sunlight available";
                        }
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

            float potentialEfficiency = efficiency > 0 ? efficiency : getSolarEfficiency();
            toRet.add(Component.literal("⚡ POTENTIAL: ").withStyle(net.minecraft.ChatFormatting.BLUE)
                .append(Component.literal(String.format("%,d RF/tick", computeGenerationFromEfficiency(potentialEfficiency))).withStyle(net.minecraft.ChatFormatting.WHITE)));

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

            // Use the same visibility logic as runtime: if hasViewOnSky returns true, it's not blocked
            if (level != null && hasViewOnSky(worldPosition)) {
                blockingIssue = "None (direct view to sky)";
                tip = "";
            } else if (level != null) {
                for (int i = 1; i <= 10; i++) {
                    BlockPos checkPos = worldPosition.above(i);
                    BlockState state = level.getBlockState(checkPos);
                    // treat as blocking only if the block is not air and does NOT propagate skylight
                    if (!state.isAir() && !state.propagatesSkylightDown(level, checkPos)) {
                        blockingIssue = String.format("Blocked by %s at %d blocks above",
                            state.getBlock().getName().getString(), i);
                        tip = String.format("Remove the %s above the panel",
                            state.getBlock().getName().getString());
                        break;
                    }
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
            MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) ->
                toRet.add(Component.literal("  • ").withStyle(net.minecraft.ChatFormatting.GRAY)
                    .append(Component.literal(string).withStyle(net.minecraft.ChatFormatting.WHITE))
                    .append(Component.literal(": ").withStyle(net.minecraft.ChatFormatting.GRAY))
                    .append(Component.literal(String.valueOf(integer)).withStyle(net.minecraft.ChatFormatting.RED)))
            );
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
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT());
        data.put("items", itemHandler.serializeNBT());
        data.putInt("progress", this.progress);
        if (name != null) data.putString("name", this.name);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);

        CompoundTag runtimeTag = new CompoundTag();
        runtimeState.saveTo(runtimeTag);
        data.put("runtimeState", runtimeTag);
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
            itemHandler.deserializeNBT(data.getCompound("items"));
        }

        if (data.contains("progress")) {
            progress = data.getInt("progress");
        }

        if (data.contains("name")) {
            name = data.getString("name");
        }

        if (data.contains("structure")) {
            structure = ResourceLocation.parse(data.getString("structure"));
        }

        if (data.contains("showStructure")) {
            showStructure = data.getBoolean("showStructure");
        }

        if (data.contains("runtimeState")) {
            runtimeState.loadFrom(data.getCompound("runtimeState"));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        
        // Ensure we have a valid energy handler even if setup wasn't called yet
        if (energyHandler == null) {
            energyHandler = new ModEnergyStorage(ENERGY_CAPACITY, 0L, ENERGY_CAPACITY, 0L);
        }
        
        setupEnergyStorage();
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
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
        // Only run the logic on server to avoid client-side drift/visual-only updates.
        if (pLevel != null && pLevel.isClientSide) return;
         if (getStructure() == null || this.name == null) {
             setup(structure, name);
         }

        // Per-tile guard: prevent multiple executions in the same world tick (e.g. from booster mods)
        if (pLevel != null) {
            long gameTime = pLevel.getGameTime();
            if (this.lastProcessedGameTime == gameTime) return;
            this.lastProcessedGameTime = gameTime;
        }

        checkStructure(pLevel, pPos);

        boolean dimensionAllowed = this.name == null || ConfigLoader.getInstance().isSolarDimensionAllowed(pLevel.dimension(), this.name);
        boolean blockedChanged = runtimeState.updateBlockedByDimension(!dimensionAllowed);
        if (blockedChanged && level != null) {
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
        }

        if (runtimeState.isBlockedByDimension()) {
            runtimeState.setActive(false);
            runtimeState.setWorking(false);
            runtimeState.setHasSkyView(false);
            runtimeState.setLastGeneration(0);
            runtimeState.setLastEfficiency(0);
            runtimeState.updateEnergySnapshot(energyHandler.getLongEnergyStored(), energyHandler.getLongMaxEnergyStored());
            pushEnergyToNeighbors();
            if (level != null) {
                level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
            }
            return;
        }

        boolean skyView = hasViewOnSky(pPos);
        runtimeState.setHasSkyView(skyView);
        boolean active = foundStructure && skyView;
        runtimeState.setActive(active);

        if (level != null) {
            level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), 3);
        }

        if (!active) {
            runtimeState.setWorking(false);
            runtimeState.setLastGeneration(0);
            runtimeState.setLastEfficiency(0);
            runtimeState.updateEnergySnapshot(energyHandler.getLongEnergyStored(), energyHandler.getLongMaxEnergyStored());
            return;
        }

        // Early night check: if this is a normal dimension and it's night, force zero efficiency
        float solarEff;
        if (level != null) {
            // Explicit safety: do not allow Overworld to be treated as always-day under any circumstances.
            String dim = level.dimension().location().toString().toLowerCase();
            if (dim.equals("minecraft:overworld") && !level.isDay()) {
                solarEff = 0.0f;
            } else {
             boolean isAlwaysDayEarly = isAlwaysDayDimensionAt(level, pPos);
             boolean isDayEarly = level.isDay();
             if (!isAlwaysDayEarly && !isDayEarly) {
                 solarEff = 0.0f;
             } else {
                 solarEff = getSolarEfficiency();
             }
            }
         } else {
             solarEff = getSolarEfficiency();
         }

        // Safety: enforce zero generation at night unless dimension is always-day (keeps previous guard)
        if (level != null) {
            boolean isAlwaysDayDimension = isAlwaysDayDimensionAt(level, pPos);

            BlockPos abovePos = pPos.above();
            boolean isDayNow = level.isDay();
            int skyLightNow = level.getBrightness(LightLayer.SKY, abovePos);

            if (!isAlwaysDayDimension && !isDayNow && skyLightNow < 15) {
                solarEff = 0.0f;
            }
        }

        runtimeState.setLastEfficiency(solarEff);
        boolean energyFull = isEnergyHandlerFull();
        boolean working = !energyFull && solarEff > 0;
        runtimeState.setWorking(working);

        // Always push energy to neighbors, even when buffer is full
        pushEnergyToNeighbors();
        runtimeState.updateEnergySnapshot(energyHandler.getLongEnergyStored(), energyHandler.getLongMaxEnergyStored());

        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        if (!working) {
            runtimeState.setLastGeneration(0);
            return;
        }

        progress++;
        long energyGenerated = computeGenerationFromEfficiency(solarEff);
        runtimeState.setLastGeneration(energyGenerated);

        // Final runtime guard: if it's night in a normal dimension, cancel generation
        if (level != null && energyGenerated > 0) {
            boolean isAlwaysDay = isAlwaysDayDimensionAt(level, pPos);
            boolean isDayNow = level.isDay();
            int skyNow = level.getBrightness(LightLayer.SKY, pPos.above());
            if (!isAlwaysDay && !isDayNow && skyNow < 15) {
                energyGenerated = 0;
                runtimeState.setLastGeneration(0);
                runtimeState.setLastEfficiency(0);
            }
        }

        if (energyGenerated > 0) {
            energyHandler.addEnergy(energyGenerated);
        }

        runtimeState.updateEnergySnapshot(energyHandler.getLongEnergyStored(), energyHandler.getLongMaxEnergyStored());

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

            long availableNow = energyHandler.getLongEnergyStored();
            if (availableNow <= 0) break;

            int toSend = (int) Math.min(availableNow, Integer.MAX_VALUE);
            cap.ifPresent(receiver -> {
                int accepted = receiver.receiveEnergy(toSend, false);
                if (accepted > 0) {
                    energyHandler.removeEnergy(accepted);
                }
            });
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
        float efficiency = runtimeState.lastEfficiency();
        if (efficiency <= 0) {
            efficiency = getSolarEfficiency();
        }
        return computeGenerationFromEfficiency(efficiency);
    }

    public int getMaxProgress() {
        if (name == null) {
            return 100; // Default duration if name is null
        }

        // No modifier effect on cycle time - keep it constant
        return ConfigLoader.getInstance().getSolarPanelConfig(name).duration();
    }

    private long computeGenerationFromEfficiency(float efficiencyPercentage) {
        if (name == null) {
            return 0;
        }

        // Final safety: if it's night in a normal dimension, do not generate at all
        if (level != null) {
            String dim = level.dimension().location().toString().toLowerCase();
            // Overworld explicit block
            if (dim.equals("minecraft:overworld") && !level.isDay()) return 0;

            boolean isAlwaysDay = isAlwaysDayDimensionAt(level, worldPosition);
            boolean isDayNow = level.isDay();
            int skyNow = level.getBrightness(LightLayer.SKY, worldPosition.above());
            if (!isAlwaysDay && !isDayNow && skyNow < 15) {
                return 0;
            }
        }

        float generationModifier = 1.0f;
        float efficiencyModifier = 1.0f;

        for (Map.Entry<BlockInWorld, ConfigLoader.SolarModifierConfig> entry : modifierMap.entrySet()) {
            generationModifier *= entry.getValue().generation();
            efficiencyModifier *= (2.0f - entry.getValue().efficiency());
        }

        ConfigLoader.SolarPanelConfig config = ConfigLoader.getInstance().getSolarPanelConfig(name);
        if (config == null) {
            return 0;
        }

        float totalModifier = generationModifier * efficiencyModifier;
        long baseGeneration = config.energyGeneration();
        float efficiencyFraction = Math.max(0.0f, efficiencyPercentage / 100.0f);

        return Math.max(0, (long) (baseGeneration * totalModifier * efficiencyFraction));
    }

    public float getSolarEfficiency() {
        if (level == null) return 0;

        // Dimension checks
        String dimName = level.dimension().location().toString().toLowerCase();
        // Explicit: Overworld must respect day/night even if heuristics/previous cache say otherwise
        if (dimName.equals("minecraft:overworld") && !level.isDay()) return 0;
        boolean isAlwaysDayDimension = isAlwaysDayDimensionAt(level, getBlockPos());
        boolean isVoidDimension = isAlwaysDayDimension; // isAlwaysDayDimensionAt already checks name-based void detection

        // Void dimension keeps its special handling
        if (isVoidDimension) {
            if (!hasViewOnSky(getBlockPos())) return 0.0f;
            float efficiency = 100.0f;
            float weatherPenalty = 1.0f;
            if (level.isRaining()) {
                weatherPenalty = 0.3f;
                if (level.isThundering()) weatherPenalty = 0.15f;
                for (Map.Entry<BlockInWorld, ConfigLoader.SolarModifierConfig> entry : modifierMap.entrySet()) {
                    float resistance = entry.getValue().weatherResistance();
                    if (resistance >= 3.0f) { weatherPenalty = 1.0f; break; }
                    else if (resistance > 1.0f) {
                        float protectionBoost = (resistance - 1.0f);
                        weatherPenalty = weatherPenalty * (1.0f + protectionBoost);
                        weatherPenalty = Math.min(1.0f, weatherPenalty);
                    }
                }
            }
            efficiency *= weatherPenalty;
            return Math.max(0, Math.min(100, efficiency));
        }

        // Normal dimensions: require sky visibility
        if (!hasViewOnSky(getBlockPos())) return 0.0f;

        BlockPos above = getBlockPos().above();
        long timeOfDay = level.getDayTime() % 24000L;

        // If skylight is zero, nothing to do
        if (level.getBrightness(LightLayer.SKY, above) <= 0) return 0.0f;

        // In non-always-day dimensions, strictly no generation at night
        if (!isAlwaysDayDimension && !level.isDay()) {
            return 0.0f;
        }

        // Compute sunFactor: triangular peak at 6000 between 0..12000
        float sunFactor;
        if (isAlwaysDayDimension) {
            // Treat always-day as full daylight curve (use skylight fraction instead of time)
            sunFactor = 1.0f;
        } else {
            // timeOfDay is < 12000 here
            float distance = Math.abs((float) timeOfDay - 6000f);
            sunFactor = Math.max(0f, 1f - (distance / 6000f));
        }

        float efficiency = sunFactor * ((float) level.getBrightness(LightLayer.SKY, above) / 15.0f) * 100.0f;

        // Apply weather penalties and modifier protections
        float weatherPenalty = 1.0f;
        if (level.isRaining()) {
            weatherPenalty = 0.3f;
            if (level.isThundering()) weatherPenalty = 0.15f;

            for (Map.Entry<BlockInWorld, ConfigLoader.SolarModifierConfig> entry : modifierMap.entrySet()) {
                float resistance = entry.getValue().weatherResistance();
                if (resistance >= 3.0f) {
                    weatherPenalty = 1.0f;
                    break;
                } else if (resistance > 1.0f) {
                    float protectionBoost = (resistance - 1.0f);
                    weatherPenalty = weatherPenalty * (1.0f + protectionBoost);
                    weatherPenalty = Math.min(1.0f, weatherPenalty);
                }
            }
        }

        efficiency *= weatherPenalty;

        return Math.max(0, Math.min(100, efficiency));
    }


    private boolean hasViewOnSky(BlockPos pos) {
        if (level == null) return false;
        // Special handling for void-like dimensions: if the dimension id contains 'void' treat specially.
        String dimensionName = level.dimension().location().toString().toLowerCase();
        boolean isVoidDimension = dimensionName.contains("void") || dimensionName.contains("voidminers");


        // In void-like dimension, just check for solid obstructions directly above
        if (isVoidDimension) {
            for (int i = 1; i <= 10; i++) {
                BlockPos checkPos = pos.above(i);
                BlockState state = level.getBlockState(checkPos);
                if (!state.isAir()) {
                    return false;
                }
            }
            return true;
        }

        // Normal dimension logic: use actual skylight reaching the panel. If skylight > 0 we consider it visible.
        BlockPos above = pos.above();
        int skyLightHere = level.getBrightness(LightLayer.SKY, above);

        // If any skylight reaches the panel position, it's visible to sky (handles glass/custom transparent blocks)
        if (skyLightHere > 0) return true;

        // No skylight reached. Fall back to scanning for an actual solid obstruction to provide better tooltip info.
        for (int i = 1; i <= 10; i++) {
            BlockPos checkPos = pos.above(i);
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && !state.propagatesSkylightDown(level, checkPos)) {
                return false;
            }
        }

        return false;
    }

    private boolean isEnergyHandlerFull() {
        return energyHandler.getLongEnergyStored() >= energyHandler.getLongMaxEnergyStored();
    }

    public void drops() {
        if (level == null) return;

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

        if (!foundPatternPath.equals(expectedStructurePath)) {
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
