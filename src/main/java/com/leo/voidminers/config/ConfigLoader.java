package com.leo.voidminers.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.leo.voidminers.util.MapUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import java.util.Arrays;

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

    @Expose
    public float ROSARIUM_STELLAR_CORE_DROP_CHANCE = 0.05f;

    @Expose
    public float ULTIMATE_STELLAR_CORE_DROP_CHANCE = 0.1f;

    @Expose
    public Map<String, MinerConfig> MINER_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new MinerConfig(10000000, 1000, 300,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.85f, 0.95f, 1.1f, Arrays.asList("§6Energy Efficiency: §f-15% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.05f, 0.9f, 1.15f, Arrays.asList("§aSpeed Boost: §f+5% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.1f, 1.05f, 1.5f, Arrays.asList("§bItem Multiplier: §f+10% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6RUBETINE MINER", "§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§cEnergy per tick: §f300 FE/t")
        )),
        MapUtil.createEntry("aurantium", new MinerConfig(25000000, 900, 350,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.83f, 0.93f, 1.12f, Arrays.asList("§6Energy Efficiency: §f-17% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.07f, 0.88f, 1.18f, Arrays.asList("§aSpeed Boost: §f+7% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.12f, 1.07f, 1.55f, Arrays.asList("§bItem Multiplier: §f+12% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6AURANTIUM MINER", "§eStored energy: §f0 FE/§625.00 MFE", "§9Capacity: §f25.00 MFE", "§cEnergy per tick: §f350 FE/t")
        )),
        MapUtil.createEntry("citrinetine", new MinerConfig(50000000,800, 400,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.81f, 0.91f, 1.14f, Arrays.asList("§6Energy Efficiency: §f-19% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.09f, 0.86f, 1.21f, Arrays.asList("§aSpeed Boost: §f+9% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.14f, 1.09f, 1.6f, Arrays.asList("§bItem Multiplier: §f+14% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6CITRINETINE MINER", "§eStored energy: §f0 FE/§650.00 MFE", "§9Capacity: §f50.00 MFE", "§cEnergy per tick: §f400 FE/t")
        )),
        MapUtil.createEntry("verdium", new MinerConfig(100000000,700, 450,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.79f, 0.89f, 1.16f, Arrays.asList("§6Energy Efficiency: §f-21% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.11f, 0.84f, 1.24f, Arrays.asList("§aSpeed Boost: §f+11% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.16f, 1.11f, 1.65f, Arrays.asList("§bItem Multiplier: §f+16% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6VERDIUM MINER", "§eStored energy: §f0 FE/§6100.00 MFE", "§9Capacity: §f100.00 MFE", "§cEnergy per tick: §f450 FE/t")
        )),
        MapUtil.createEntry("azurine", new MinerConfig(250000000,600, 500,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.77f, 0.87f, 1.18f, Arrays.asList("§6Energy Efficiency: §f-23% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.13f, 0.82f, 1.27f, Arrays.asList("§aSpeed Boost: §f+13% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.18f, 1.13f, 1.7f, Arrays.asList("§bItem Multiplier: §f+18% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6AZURINE MINER", "§eStored energy: §f0 FE/§6250.00 MFE", "§9Capacity: §f250.00 MFE", "§cEnergy per tick: §f500 FE/t")
        )),
        MapUtil.createEntry("caerium", new MinerConfig(500000000,500, 550,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.75f, 0.85f, 1.2f, Arrays.asList("§6Energy Efficiency: §f-25% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.15f, 0.8f, 1.3f, Arrays.asList("§aSpeed Boost: §f+15% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1.15f, 1.75f, Arrays.asList("§bItem Multiplier: §f+20% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6CAERIUM MINER", "§eStored energy: §f0 FE/§6500.00 MFE", "§9Capacity: §f500.00 MFE", "§cEnergy per tick: §f550 FE/t")
        )),
        MapUtil.createEntry("amethystine", new MinerConfig(750000000,400, 600,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.73f, 0.83f, 1.22f, Arrays.asList("§6Energy Efficiency: §f-27% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.17f, 0.78f, 1.33f, Arrays.asList("§aSpeed Boost: §f+17% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.22f, 1.17f, 1.8f, Arrays.asList("§bItem Multiplier: §f+22% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6AMETHYSTINE MINER", "§eStored energy: §f0 FE/§6750.00 MFE", "§9Capacity: §f750.00 MFE", "§cEnergy per tick: §f600 FE/t")
        )),
        MapUtil.createEntry("rosarium", new MinerConfig(1000000000,300, 650,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.71f, 0.81f, 1.24f, Arrays.asList("§6Energy Efficiency: §f-29% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.19f, 0.76f, 1.36f, Arrays.asList("§aSpeed Boost: §f+19% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.24f, 1.19f, 1.85f, Arrays.asList("§bItem Multiplier: §f+24% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6ROSARIUM MINER", "§eStored energy: §f0 FE/§61.00 GFE", "§9Capacity: §f1.00 GFE", "§cEnergy per tick: §f650 FE/t")
        )),
        MapUtil.createEntry("ultimate", new MinerConfig(2147483647,200, 700,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.65f, 0.75f, 1.3f, Arrays.asList("§6Energy Efficiency: §f-35% consumption", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("speed", new ModifierConfig(1.25f, 0.7f, 1.45f, Arrays.asList("§aSpeed Boost: §f+25% faster", "§eCustomizable tooltip line 2"))),
                MapUtil.createEntry("item", new ModifierConfig(1.3f, 1.25f, 2.0f, Arrays.asList("§bItem Multiplier: §f+30% items", "§eCustomizable tooltip line 2")))
            ),
            Arrays.asList("§6ULTIMATE MINER", "§eStored energy: §f0 FE/§62.15 GFE", "§9Capacity: §f2.15 GFE", "§cEnergy per tick: §f700 FE/t")
        ))
    );

    @Expose
    public Map<String, SolarPanelConfig> SOLAR_PANEL_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new SolarPanelConfig(5000000, 100, 20,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.15f, 0.9f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§65.00 MFE", "§9Capacity: §f5.00 MFE", "§aGeneration Boost: §f+15%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.85f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§65.00 MFE", "§9Capacity: §f5.00 MFE", "§bSpeed Boost: §f+15%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.5f, Arrays.asList("§eStored energy: §f0 FE/§65.00 MFE", "§9Capacity: §f5.00 MFE", "§9Weather Protection: §f+50%")))
            ),
            Arrays.asList("§6RUBETINE SOLAR PANEL", "§eStored energy: §f0 FE/§65.00 MFE", "§9Capacity: §f5.00 MFE", "§aGeneration: §f20 FE/t")
        )),
        MapUtil.createEntry("aurantium", new SolarPanelConfig(10000000, 80, 40,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.18f, 0.88f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§aGeneration Boost: §f+18%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.83f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§bEfficiency Boost: §f+17%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.55f, Arrays.asList("§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§9Weather Protection: §f+55%")))
            ),
            Arrays.asList("§6AURANTIUM SOLAR PANEL", "§eStored energy: §f0 FE/§610.00 MFE", "§9Capacity: §f10.00 MFE", "§aGeneration: §f40 FE/t")
        )),
        MapUtil.createEntry("citrinetine", new SolarPanelConfig(20000000, 60, 80,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.21f, 0.86f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§620.00 MFE", "§9Capacity: §f20.00 MFE", "§aGeneration Boost: §f+21%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.81f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§620.00 MFE", "§9Capacity: §f20.00 MFE", "§bEfficiency Boost: §f+19%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.6f, Arrays.asList("§eStored energy: §f0 FE/§620.00 MFE", "§9Capacity: §f20.00 MFE", "§9Weather Protection: §f+60%")))
            ),
            Arrays.asList("§6CITRINETINE SOLAR PANEL", "§eStored energy: §f0 FE/§620.00 MFE", "§9Capacity: §f20.00 MFE", "§aGeneration: §f80 FE/t")
        )),
        MapUtil.createEntry("verdium", new SolarPanelConfig(40000000, 50, 160,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.24f, 0.84f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§640.00 MFE", "§9Capacity: §f40.00 MFE", "§aGeneration Boost: §f+24%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.79f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§640.00 MFE", "§9Capacity: §f40.00 MFE", "§bEfficiency Boost: §f+21%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.65f, Arrays.asList("§eStored energy: §f0 FE/§640.00 MFE", "§9Capacity: §f40.00 MFE", "§9Weather Protection: §f+65%")))
            ),
            Arrays.asList("§6VERDIUM SOLAR PANEL", "§eStored energy: §f0 FE/§640.00 MFE", "§9Capacity: §f40.00 MFE", "§aGeneration: §f160 FE/t")
        )),
        MapUtil.createEntry("azurine", new SolarPanelConfig(80000000, 40, 320,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.27f, 0.82f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§680.00 MFE", "§9Capacity: §f80.00 MFE", "§aGeneration Boost: §f+27%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.77f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§680.00 MFE", "§9Capacity: §f80.00 MFE", "§bEfficiency Boost: §f+23%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.7f, Arrays.asList("§eStored energy: §f0 FE/§680.00 MFE", "§9Capacity: §f80.00 MFE", "§9Weather Protection: §f+70%")))
            ),
            Arrays.asList("§6AZURINE SOLAR PANEL", "§eStored energy: §f0 FE/§680.00 MFE", "§9Capacity: §f80.00 MFE", "§aGeneration: §f320 FE/t")
        )),
        MapUtil.createEntry("caerium", new SolarPanelConfig(160000000, 30, 640,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.3f, 0.8f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§6160.00 MFE", "§9Capacity: §f160.00 MFE", "§aGeneration Boost: §f+30%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.75f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§6160.00 MFE", "§9Capacity: §f160.00 MFE", "§bEfficiency Boost: §f+25%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.75f, Arrays.asList("§eStored energy: §f0 FE/§6160.00 MFE", "§9Capacity: §f160.00 MFE", "§9Weather Protection: §f+75%")))
            ),
            Arrays.asList("§6CAERIUM SOLAR PANEL", "§eStored energy: §f0 FE/§6160.00 MFE", "§9Capacity: §f160.00 MFE", "§aGeneration: §f640 FE/t")
        )),
        MapUtil.createEntry("amethystine", new SolarPanelConfig(320000000, 25, 1280,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.33f, 0.78f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§6320.00 MFE", "§9Capacity: §f320.00 MFE", "§aGeneration Boost: §f+33%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.73f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§6320.00 MFE", "§9Capacity: §f320.00 MFE", "§bEfficiency Boost: §f+27%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.8f, Arrays.asList("§eStored energy: §f0 FE/§6320.00 MFE", "§9Capacity: §f320.00 MFE", "§9Weather Protection: §f+80%")))
            ),
            Arrays.asList("§6AMETHYSTINE SOLAR PANEL", "§eStored energy: §f0 FE/§6320.00 MFE", "§9Capacity: §f320.00 MFE", "§aGeneration: §f1280 FE/t")
        )),
        MapUtil.createEntry("rosarium", new SolarPanelConfig(640000000, 20, 2560,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.36f, 0.76f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§6640.00 MFE", "§9Capacity: §f640.00 MFE", "§aGeneration Boost: §f+36%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.71f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§6640.00 MFE", "§9Capacity: §f640.00 MFE", "§bEfficiency Boost: §f+29%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.85f, Arrays.asList("§eStored energy: §f0 FE/§6640.00 MFE", "§9Capacity: §f640.00 MFE", "§9Weather Protection: §f+85%")))
            ),
            Arrays.asList("§6ROSARIUM SOLAR PANEL", "§eStored energy: §f0 FE/§6640.00 MFE", "§9Capacity: §f640.00 MFE", "§aGeneration: §f2560 FE/t")
        )),
        MapUtil.createEntry("ultimate", new SolarPanelConfig(2147483647, 15, 5120,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.45f, 0.7f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§65.12 GFE", "§9Capacity: §f5.12 GFE", "§aGeneration Boost: §f+45%"))),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.65f, 1.0f, Arrays.asList("§eStored energy: §f0 FE/§65.12 GFE", "§9Capacity: §f5.12 GFE", "§bEfficiency Boost: §f+35%"))),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.0f, Arrays.asList("§eStored energy: §f0 FE/§65.12 GFE", "§9Capacity: §f5.12 GFE", "§9Weather Protection: §f+100%")))
            ),
            Arrays.asList("§6ULTIMATE SOLAR PANEL", "§eStored energy: §f0 FE/§62.15 GFE", "§9Capacity: §f2.15 GFE", "§aGeneration: §f5120 FE/t")
        ))
    );

    public void load() {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
        File file = configPath.toFile();

        try {
            if (!file.exists()) {
                System.out.println("Configuration file does not exist. Creating a new one.");
                saveDefaultConfig(file, gson);
            } else {
                try (JsonReader jsonReader = new JsonReader(new FileReader(file))) {
                    INSTANCE = gson.fromJson(jsonReader, ConfigLoader.class);
                    if (INSTANCE == null) {
                        throw new JsonSyntaxException("Parsed configuration is null.");
                    }
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            System.err.println("Invalid configuration file. Regenerating default config.");
            saveDefaultConfig(file, gson);
        }
    }

    private void saveDefaultConfig(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file)) {
            if(INSTANCE == null) INSTANCE = new ConfigLoader();

            gson.toJson(INSTANCE, ConfigLoader.class, writer);
            System.out.println("Default configuration file created successfully.");
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

    public record SolarPanelConfig(@Expose int energyStorage, @Expose int duration, @Expose int energyGeneration, @Expose Map<String, SolarModifierConfig> modifiers, @Expose List<String> tooltip) {

        public static SolarPanelConfig fromBuf(FriendlyByteBuf buf) {
            int energyStorage = buf.readInt();
            int duration = buf.readInt();
            int energyGeneration = buf.readInt();

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
            buf.writeInt(energyStorage);
            buf.writeInt(duration);
            buf.writeInt(energyGeneration);

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
}
