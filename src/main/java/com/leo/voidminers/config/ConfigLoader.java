package com.leo.voidminers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.leo.voidminers.util.MapUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import java.util.Arrays;
import java.util.Locale;

public class ConfigLoader {
    public static final String CONFIG_FILE = "void-miners.json5";
    private static ConfigLoader INSTANCE = new ConfigLoader();

    private ConfigLoader() {}

    public static ConfigLoader getInstance() {
        return INSTANCE != null ? INSTANCE : new ConfigLoader();
    }

    @Expose
    public boolean ALLOW_NO_ENERGY_MINERS = false;

    @Expose
    public boolean ALLOW_NO_ENERGY_SOLAR_PANELS = false;

    // New option: allow tick-acceleration for multiblocks (e.g. Block Booster/Torcherino).
    // Default: false -> Multiblocks will NOT be executed multiple times per game-tick by external tick-acceleration.
    @Expose
    public boolean ALLOW_TICK_ACCELERATION_MULTIBLOCKS = false;

    /** If true, JEI percent display uses a color-blind friendly palette. */
    @Expose
    public boolean COLOR_BLIND_PERCENT = false;

    @Expose
    public DimensionControl MINER_DIMENSION_SETTINGS = new DimensionControl();

    @Expose
    public DimensionControl SOLAR_DIMENSION_SETTINGS = new DimensionControl();

    @Expose
    public Map<String, MinerConfig> MINER_CONFIGS = MapUtil.of(
            MapUtil.createEntry("rubetine", new MinerConfig(10000000, 1000, 300,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.85f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-15% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.10f, 0.9f, 1.00f, Arrays.asList("§aSpeed Boost: §f+10% faster", "§eEnergy: §f+10% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.50f, 1.00f, 1.50f, Arrays.asList("§bItem Multiplier: §f+50% items", "§eEnergy: §f+50% consumption")))
                    ),
                    Arrays.asList("§6RUBETINE MINER", "§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§cEnergy per tick: §f300 FE/t")
            )),
            MapUtil.createEntry("aurantium", new MinerConfig(25000000, 900, 350,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.83f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-17% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.12f, 0.88f, 1.00f, Arrays.asList("§aSpeed Boost: §f+12% faster", "§eEnergy: §f+12% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.55f, 1.00f, 1.60f, Arrays.asList("§bItem Multiplier: §f+60% items", "§eEnergy: §f+55% consumption")))
                    ),
                    Arrays.asList("§6AURANTIUM MINER", "§eStored energy: §f0 FE/§625.00 MFE", "§9Capacity: §f25.00 MFE", "§cEnergy per tick: §f350 FE/t")
            )),
            MapUtil.createEntry("citrinetine", new MinerConfig(50000000,800, 400,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.81f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-19% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.14f, 0.86f, 1.00f, Arrays.asList("§aSpeed Boost: §f+14% faster", "§eEnergy: §f+14% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.60f, 1.00f, 1.70f, Arrays.asList("§bItem Multiplier: §f+70% items", "§eEnergy: §f+60% consumption")))
                    ),
                    Arrays.asList("§6CITRINETINE MINER", "§eStored energy: §f0 FE/§650.00 MFE", "§9Capacity: §f50.00 MFE", "§cEnergy per tick: §f400 FE/t")
            )),
            MapUtil.createEntry("verdium", new MinerConfig(100000000,700, 450,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.79f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-21% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.16f, 0.84f, 1.00f, Arrays.asList("§aSpeed Boost: §f+16% faster", "§eEnergy: §f+16% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.65f, 1.00f, 1.80f, Arrays.asList("§bItem Multiplier: §f+80% items", "§eEnergy: §f+65% consumption")))
                    ),
                    Arrays.asList("§6VERDIUM MINER", "§eStored energy: §f0 FE/§6100.00 MFE", "§9Capacity: §f100.00 MFE", "§cEnergy per tick: §f450 FE/t")
            )),
            MapUtil.createEntry("azurine", new MinerConfig(250000000,600, 500,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.77f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-23% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.18f, 0.82f, 1.00f, Arrays.asList("§aSpeed Boost: §f+18% faster", "§eEnergy: §f+18% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.70f, 1.00f, 1.90f, Arrays.asList("§bItem Multiplier: §f+90% items", "§eEnergy: §f+70% consumption")))
                    ),
                    Arrays.asList("§6AZURINE MINER", "§eStored energy: §f0 FE/§6250.00 MFE", "§9Capacity: §f250.00 MFE", "§cEnergy per tick: §f500 FE/t")
            )),
            MapUtil.createEntry("caerium", new MinerConfig(500000000,500, 550,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.75f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-25% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.20f, 0.8f, 1.00f, Arrays.asList("§aSpeed Boost: §f+20% faster", "§eEnergy: §f+20% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.75f, 1.00f, 2.00f, Arrays.asList("§bItem Multiplier: §f+100% items", "§eEnergy: §f+75% consumption")))
                    ),
                    Arrays.asList("§6CAERIUM MINER", "§eStored energy: §f0 FE/§6500.00 MFE", "§9Capacity: §f500.00 MFE", "§cEnergy per tick: §f550 FE/t")
            )),
            MapUtil.createEntry("amethystine", new MinerConfig(750000000,400, 600,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.73f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-27% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.22f, 0.78f, 1.00f, Arrays.asList("§aSpeed Boost: §f+22% faster", "§eEnergy: §f+22% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.80f, 1.00f, 2.10f, Arrays.asList("§bItem Multiplier: §f+110% items", "§eEnergy: §f+80% consumption")))
                    ),
                    Arrays.asList("§6AMETHYSTINE MINER", "§eStored energy: §f0 FE/§6750.00 MFE", "§9Capacity: §f750.00 MFE", "§cEnergy per tick: §f600 FE/t")
            )),
            MapUtil.createEntry("rosarium", new MinerConfig(1000000000,300, 650,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.71f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-29% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.24f, 0.76f, 1.00f, Arrays.asList("§aSpeed Boost: §f+24% faster", "§eEnergy: §f+24% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(1.85f, 1.00f, 2.20f, Arrays.asList("§bItem Multiplier: §f+120% items", "§eEnergy: §f+85% consumption")))
                    ),
                    Arrays.asList("§6ROSARIUM MINER", "§eStored energy: §f0 FE/§61.00 GFE", "§9Capacity: §f1.00 GFE", "§cEnergy per tick: §f650 FE/t")
            )),
            MapUtil.createEntry("ultimate", new MinerConfig(2147483647,200, 700,
                    MapUtil.of(
                            MapUtil.createEntry("energy", new ModifierConfig(0.65f, 1.00f, 1.00f, Arrays.asList("§6Energy Efficiency: §f-35% consumption", "§eNo other changes"))),
                            MapUtil.createEntry("speed", new ModifierConfig(1.30f, 0.7f, 1.00f, Arrays.asList("§aSpeed Boost: §f+30% faster", "§eEnergy: §f+30% consumption"))),
                            MapUtil.createEntry("item", new ModifierConfig(2.00f, 1.00f, 2.50f, Arrays.asList("§bItem Multiplier: §f+150% items", "§eEnergy: §f+100% consumption")))
                    ),
                    Arrays.asList("§6ULTIMATE MINER", "§eStored energy: §f0 FE/§62.15 GFE", "§9Capacity: §f2.15 GFE", "§cEnergy per tick: §f700 FE/t")
            ))
    );

    @Expose
    public Map<String, SolarPanelConfig> SOLAR_PANEL_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new SolarPanelConfig(5000000, 100, 20,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.85f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+15%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.0f, Arrays.asList("§9Weather Protection: §f+17%")))
            ),
            Arrays.asList("§6RUBETINE SOLAR PANEL", "§eStored energy: §f0 FE/§65.00 MFE", "§9Capacity: §f5.00 MFE", "§aGeneration: §f20 FE/t")
        )),
        MapUtil.createEntry("aurantium", new SolarPanelConfig(10000000, 80, 40,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.83f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+17%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.40f, Arrays.asList("§9Weather Protection: §f+40%")))
            ),
            Arrays.asList("§6AURANTIUM SOLAR PANEL", "§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§aGeneration: §f40 FE/t")
        )),
        MapUtil.createEntry("citrinetine", new SolarPanelConfig(20000000, 60, 80,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.81f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+19%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.57f, Arrays.asList("§9Weather Protection: §f+57%")))
            ),
            Arrays.asList("§6CITRINETINE SOLAR PANEL", "§eStored energy: §f0 FE/§620.00 MFE", "§9Capacity: §f20.00 MFE", "§aGeneration: §f80 FE/t")
        )),
        MapUtil.createEntry("verdium", new SolarPanelConfig(40000000, 50, 160,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.79f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+21%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.80f, Arrays.asList("§9Weather Protection: §f+80%")))
            ),
            Arrays.asList("§6VERDIUM SOLAR PANEL", "§eStored energy: §f0 FE/§640.00 MFE", "§9Capacity: §f40.00 MFE", "§aGeneration: §f160 FE/t")
        )),
        MapUtil.createEntry("azurine", new SolarPanelConfig(80000000, 40, 320,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.77f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+23%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.97f, Arrays.asList("§9Weather Protection: §f+97%")))
            ),
            Arrays.asList("§6AZURINE SOLAR PANEL", "§eStored energy: §f0 FE/§680.00 MFE", "§9Capacity: §f80.00 MFE", "§aGeneration: §f320 FE/t")
        )),
        MapUtil.createEntry("caerium", new SolarPanelConfig(160000000, 30, 640,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.75f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+25%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.20f, Arrays.asList("§9Weather Protection: §f+120%")))
            ),
            Arrays.asList("§6CAERIUM SOLAR PANEL", "§eStored energy: §f0 FE/§6160.00 MFE", "§9Capacity: §f160.00 MFE", "§aGeneration: §f640 FE/t")
        )),
        MapUtil.createEntry("amethystine", new SolarPanelConfig(320000000, 25, 1280,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.73f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+27%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.37f, Arrays.asList("§9Weather Protection: §f+137%")))
            ),
            Arrays.asList("§6AMETHYSTINE SOLAR PANEL", "§eStored energy: §f0 FE/§6320.00 MFE", "§9Capacity: §f320.00 MFE", "§aGeneration: §f1280 FE/t")
        )),
        MapUtil.createEntry("rosarium", new SolarPanelConfig(640000000, 20, 2560,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.71f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+29%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.60f, Arrays.asList("§9Weather Protection: §f+160%")))
            ),
            Arrays.asList("§6ROSARIUM SOLAR PANEL", "§eStored energy: §f0 FE/§6640.00 MFE", "§9Capacity: §f640.00 MFE", "§aGeneration: §f2560 FE/t")
        )),
        MapUtil.createEntry("ultimate", new SolarPanelConfig(2147483647L, 15, 5120,
            MapUtil.of(
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.65f, 1.0f, Arrays.asList("§bEfficiency Boost: §f+35%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 3.33f, Arrays.asList("§9Weather Protection: §f+233%")))
            ),
            Arrays.asList("§6ULTIMATE SOLAR PANEL", "§eStored energy: §f0 FE/§62.15 GFE", "§9Capacity: §f2.15 GFE", "§aGeneration: §f5120 FE/t")
        ))
    );

    // Configurable upgrade slot values (can be changed in the config file)
    @Expose
    public int UPGRADE_T1_SLOTS = 3;

    @Expose
    public int UPGRADE_T2_SLOTS = 9;

    @Expose
    public int UPGRADE_T3_SLOTS = 27;

    public void load() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
        File file = configPath.toFile();

        try {
            if (!file.exists()) {
                saveDefaultConfig(file, gson);
            } else {
                // Use the merge logic to preserve user settings while adding new defaults
                mergeDefaultConfig(file, gson);
            }
        } catch (JsonSyntaxException e) {
            saveDefaultConfig(file, gson);
        }
    }

    private void saveDefaultConfig(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file)) {
            if(INSTANCE == null) INSTANCE = new ConfigLoader();

            String json = gson.toJson(INSTANCE);
            json = json.replace("§", "\\u00a7"); // § zu Unicode escape
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create default configuration file.", e);
        }
    }

    public MinerConfig getMinerConfig(String name) {
        return MINER_CONFIGS.getOrDefault(name, new MinerConfig(0,0, 0, Map.of(), Arrays.asList("§7Default miner tooltip")));
    }

    public SolarPanelConfig getSolarPanelConfig(String name) {
        return SOLAR_PANEL_CONFIGS.getOrDefault(name, new SolarPanelConfig(0,0, 0, Map.of(), Arrays.asList("§7Default solar panel tooltip")));
    }

    public boolean isMinerDimensionAllowed(ResourceKey<Level> dimension, String tierName) {
        if (dimension == null) {
            return true;
        }

        return MINER_DIMENSION_SETTINGS.isAllowed(tierName, dimension.location().toString());
    }

    public boolean isSolarDimensionAllowed(ResourceKey<Level> dimension, String tierName) {
        if (dimension == null) {
            return true;
        }

        return SOLAR_DIMENSION_SETTINGS.isAllowed(tierName, dimension.location().toString());
    }

    public ModifierConfig getModifierConfig(String name, String type) {
        return getMinerConfig(name).modifiers.getOrDefault(type, new ModifierConfig(1, 1, 1, Arrays.asList("§7Default tooltip")));
    }

    public ModifierConfig getModifierConfig(Block block) {
        String blockName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        String minerTier = blockName.split("_")[0];
        String modifierType = blockName.split("_")[1];

        return getModifierConfig(minerTier, modifierType);
    }

    public SolarModifierConfig getSolarModifierConfig(String name, String type) {
        return getSolarPanelConfig(name).modifiers.getOrDefault(type, new SolarModifierConfig(1, 1, 1, Arrays.asList("§7Default solar tooltip")));
    }

    public SolarModifierConfig getSolarModifierConfig(Block block, String solarTier) {
        String blockName = ForgeRegistries.BLOCKS.getKey(block).getPath();

        // For solar modifiers, the format is: solar_<tier>_<type>_modifier
        // Example: solar_ultimate_weather_modifier
        String[] parts = blockName.split("_");

        // Get the modifier tier from block name, not the solar panel tier
        String modifierTier = parts.length >= 2 ? parts[1] : "unknown";

        // Get the modifier type (efficiency, weather, output)
        String modifierType = parts.length >= 3 ? parts[2] : "unknown";

        // Map modifier types to config keys
        String solarModifierType = switch (modifierType) {
            case "efficiency" -> "efficiency";
            case "weather" -> "weather_resistance";
            case "output" -> "generation";
            default -> modifierType;
        };

        return getSolarModifierConfig(modifierTier, solarModifierType);
    }

    public record MinerConfig(@Expose int energyStorage, @Expose int duration, @Expose int energyTick, @Expose Map<String, ModifierConfig> modifiers, @Expose List<String> tooltip) {

        public static MinerConfig fromBuf(FriendlyByteBuf buf) {
            int energyStorage = buf.readInt();
            int duration = buf.readInt();
            int energy = buf.readInt();

            int entries = buf.readInt();

            Map<String, ModifierConfig> modifiers = new HashMap<>();

            for (int i = 0; i < entries; i++) {
                modifiers.put(
                    buf.readUtf(),
                    ModifierConfig.fromBuf(buf)
                );
            }

            int tooltipSize = buf.readInt();
            List<String> tooltip = new ArrayList<>();
            for (int i = 0; i < tooltipSize; i++) {
                tooltip.add(buf.readUtf());
            }

            return new MinerConfig(energyStorage, duration, energy, modifiers, tooltip);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeInt(energyStorage);
            buf.writeInt(duration);
            buf.writeInt(energyTick);

            buf.writeInt(modifiers.size());

            modifiers.forEach((key, value) -> {
                buf.writeUtf(key);
                value.toBuf(buf);
            });

            buf.writeInt(tooltip != null ? tooltip.size() : 0);
            if (tooltip != null) {
                for (String line : tooltip) {
                    buf.writeUtf(line);
                }
            }
        }
    }

    public record SolarPanelConfig(@Expose long energyStorage, @Expose int duration, @Expose long energyGeneration, @Expose Map<String, SolarModifierConfig> modifiers, @Expose List<String> tooltip) {

        public static SolarPanelConfig fromBuf(FriendlyByteBuf buf) {
            long energyStorage = buf.readLong();
            int duration = buf.readInt();
            long energyGeneration = buf.readLong();

            int entries = buf.readInt();

            Map<String, SolarModifierConfig> modifiers = new HashMap<>();

            for (int i = 0; i < entries; i++) {
                modifiers.put(
                    buf.readUtf(),
                    SolarModifierConfig.fromBuf(buf)
                );
            }

            int tooltipSize = buf.readInt();
            List<String> tooltip = new ArrayList<>();
            for (int i = 0; i < tooltipSize; i++) {
                tooltip.add(buf.readUtf());
            }

            return new SolarPanelConfig(energyStorage, duration, energyGeneration, modifiers, tooltip);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeLong(energyStorage);
            buf.writeInt(duration);
            buf.writeLong(energyGeneration);

            buf.writeInt(modifiers.size());

            modifiers.forEach((key, value) -> {
                buf.writeUtf(key);
                value.toBuf(buf);
            });

            buf.writeInt(tooltip != null ? tooltip.size() : 0);
            if (tooltip != null) {
                for (String line : tooltip) {
                    buf.writeUtf(line);
                }
            }
        }
    }

    private static boolean matchesAny(List<String> patterns, String dimensionId) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }

        for (String pattern : patterns) {
            if (matchesDimensionPattern(pattern, dimensionId)) {
                return true;
            }
        }

        return false;
    }

    private static boolean matchesDimensionPattern(String pattern, String dimensionId) {
        if (pattern == null || pattern.isBlank()) {
            return false;
        }

        String trimmed = pattern.trim();
        if (trimmed.equals("*")) {
            return true;
        }

        if (trimmed.endsWith("*")) {
            String prefix = trimmed.substring(0, trimmed.length() - 1);
            return dimensionId.startsWith(prefix);
        }

        return dimensionId.equalsIgnoreCase(trimmed);
    }

    public static class DimensionControl {
        @Expose
        public boolean enabled = false;

        @Expose
        public boolean defaultAllow = true;

        @Expose
        public List<String> globalAllow = new ArrayList<>();

        @Expose
        public List<String> globalBlock = new ArrayList<>();

        @Expose
        public Map<String, DimensionRule> tierRules = new HashMap<>();

        public boolean isAllowed(String tierName, String dimensionId) {
            if (!enabled) {
                return true;
            }

            // Tier-specific rules take priority
            DimensionRule tierRule = resolveTierRule(tierName);
            if (tierRule != null) {
                if (tierRule.matchesBlocked(dimensionId)) {
                    return false;
                }

                if (tierRule.hasAllowed()) {
                    return tierRule.matchesAllowed(dimensionId);
                }

                Boolean tierDefault = tierRule.defaultAllow;
                if (tierDefault != null) {
                    return tierDefault;
                }
            }

            // Then apply global rules
            if (matchesAny(globalBlock, dimensionId)) {
                return false;
            }

            if (globalAllow != null && !globalAllow.isEmpty()) {
                return matchesAny(globalAllow, dimensionId);
            }

            return defaultAllow;
        }

        private DimensionRule resolveTierRule(String tierName) {
            if (tierRules == null || tierRules.isEmpty()) {
                return null;
            }

            if (tierName == null || tierName.isBlank()) {
                return tierRules.getOrDefault("*", null);
            }

            DimensionRule direct = tierRules.get(tierName);
            if (direct != null) {
                return direct;
            }

            String lower = tierName.toLowerCase(Locale.ROOT);
            DimensionRule lowerRule = tierRules.get(lower);
            if (lowerRule != null) {
                return lowerRule;
            }

            for (Map.Entry<String, DimensionRule> entry : tierRules.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(tierName)) {
                    return entry.getValue();
                }
            }

            return tierRules.getOrDefault("*", null);
        }
    }

    public static class DimensionRule {
        @Expose
        public Boolean defaultAllow;

        @Expose
        public List<String> allowed = new ArrayList<>();

        @Expose
        public List<String> blocked = new ArrayList<>();

        private boolean matchesAllowed(String dimensionId) {
            return matchesAny(allowed, dimensionId);
        }

        private boolean matchesBlocked(String dimensionId) {
            return matchesAny(blocked, dimensionId);
        }

        private boolean hasAllowed() {
            return allowed != null && !allowed.isEmpty();
        }
    }

    public record SolarModifierConfig(@Expose float generation, @Expose float efficiency, @Expose float weatherResistance, @Expose List<String> tooltip) {
        
        public static SolarModifierConfig fromBuf(FriendlyByteBuf buf) {
            float generation = buf.readFloat();
            float efficiency = buf.readFloat();
            float weatherResistance = buf.readFloat();

            int tooltipSize = buf.readInt();
            List<String> tooltip = new ArrayList<>();
            for (int i = 0; i < tooltipSize; i++) {
                tooltip.add(buf.readUtf());
            }

            return new SolarModifierConfig(generation, efficiency, weatherResistance, tooltip);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeFloat(generation);
            buf.writeFloat(efficiency);
            buf.writeFloat(weatherResistance);

            buf.writeInt(tooltip != null ? tooltip.size() : 0);
            if (tooltip != null) {
                for (String line : tooltip) {
                    buf.writeUtf(line);
                }
            }
        }
    }

    public record ModifierConfig(@Expose float energy, @Expose float speed, @Expose float item, @Expose List<String> tooltip) {
        public static ModifierConfig fromBuf(FriendlyByteBuf buf) {
            float energy = buf.readFloat();
            float speed = buf.readFloat();
            float item = buf.readFloat();

            int tooltipSize = buf.readInt();
            List<String> tooltip = new ArrayList<>();
            for (int i = 0; i < tooltipSize; i++) {
                tooltip.add(buf.readUtf());
            }

            return new ModifierConfig(energy, speed, item, tooltip);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeFloat(energy);
            buf.writeFloat(speed);
            buf.writeFloat(item);

            buf.writeInt(tooltip != null ? tooltip.size() : 0);
            if (tooltip != null) {
                for (String line : tooltip) {
                    buf.writeUtf(line);
                }
            }
        }
    }

    private void mergeJsonObjects(JsonObject target, JsonObject source) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonObject()) {
                // If the value is a nested object, recursively merge
                JsonObject nestedTarget = target.has(key) && target.get(key).isJsonObject()
                        ? target.getAsJsonObject(key)
                        : null;
                if (nestedTarget == null) {
                    nestedTarget = new JsonObject();
                    target.add(key, nestedTarget);
                }
                mergeJsonObjects(nestedTarget, value.getAsJsonObject());
            } else {
                // For non-object values, only add the value if it's missing in the target
                if (!target.has(key)) {
                    target.add(key, value);
                }
            }
        }
    }

    private void mergeDefaultConfig(File file, Gson gson) {
        try {
            // Read existing config as text and normalize section sign escapes
            String existingContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            existingContent = existingContent.replace("�", "§")
                    .replace("\\u00a7", "§");

            JsonObject existingConfig = gson.fromJson(existingContent, JsonObject.class);

            // Create default config JSON from a fresh ConfigLoader instance
            StringWriter defaultConfigWriter = new StringWriter();
            gson.toJson(new ConfigLoader(), defaultConfigWriter);
            JsonObject defaultConfig = gson.fromJson(defaultConfigWriter.toString(), JsonObject.class);

            // Merge default config into existing config (only add missing keys)
            mergeJsonObjects(existingConfig, defaultConfig);

            // Convert merged config back to JSON string
            String mergedJson = gson.toJson(existingConfig);

            // Ensure INSTANCE reflects the merged config (use unescaped § for parsing)
            String mergedJsonForParse = mergedJson.replace("\\u00a7", "§");
            INSTANCE = gson.fromJson(mergedJsonForParse, ConfigLoader.class);

            // Write merged config back to file, escaping section sign as before
            String toWrite = mergedJson.replace("§", "\\u00a7");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(toWrite);
            }
        } catch (IOException | JsonSyntaxException e) {
            // Fallback: overwrite with defaults
            System.err.println("Failed to merge config, falling back to defaults: " + e.getMessage());
            saveDefaultConfig(file, gson);
        }
    }
}
