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
    public boolean USE_REAL_SOLAR_CALCULATIONS = true;

    @Expose
    public double SOLAR_PANEL_LATITUDE = 45.0;

    @Expose
    public Map<String, MinerConfig> MINER_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new MinerConfig(10000000, 1000, 300,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.85f, 0.95f, 1.1f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.05f, 0.9f, 1.15f)),
                MapUtil.createEntry("item", new ModifierConfig(1.1f, 1.05f, 1.5f))
            )
        )),
        MapUtil.createEntry("aurantium", new MinerConfig(25000000, 900, 350,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.83f, 0.93f, 1.12f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.07f, 0.88f, 1.18f)),
                MapUtil.createEntry("item", new ModifierConfig(1.12f, 1.07f, 1.55f))
            )
        )),
        MapUtil.createEntry("citrinetine", new MinerConfig(50000000,800, 400,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.81f, 0.91f, 1.14f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.09f, 0.86f, 1.21f)),
                MapUtil.createEntry("item", new ModifierConfig(1.14f, 1.09f, 1.6f))
            )
        )),
        MapUtil.createEntry("verdium", new MinerConfig(100000000,700, 450,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.79f, 0.89f, 1.16f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.11f, 0.84f, 1.24f)),
                MapUtil.createEntry("item", new ModifierConfig(1.16f, 1.11f, 1.65f))
            )
        )),
        MapUtil.createEntry("azurine", new MinerConfig(250000000,600, 500,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.77f, 0.87f, 1.18f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.13f, 0.82f, 1.27f)),
                MapUtil.createEntry("item", new ModifierConfig(1.18f, 1.13f, 1.7f))
            )
        )),
        MapUtil.createEntry("caerium", new MinerConfig(500000000,500, 550,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.75f, 0.85f, 1.2f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.15f, 0.8f, 1.3f)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1.15f, 1.75f))
            )
        )),
        MapUtil.createEntry("amethystine", new MinerConfig(750000000,400, 600,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.73f, 0.83f, 1.22f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.17f, 0.78f, 1.33f)),
                MapUtil.createEntry("item", new ModifierConfig(1.22f, 1.17f, 1.8f))
            )
        )),
        MapUtil.createEntry("rosarium", new MinerConfig(1000000000,300, 650,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.71f, 0.81f, 1.24f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.19f, 0.76f, 1.36f)),
                MapUtil.createEntry("item", new ModifierConfig(1.24f, 1.19f, 1.85f))
            )
        )),
        MapUtil.createEntry("ultimate", new MinerConfig(2147483647,200, 700,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.65f, 0.75f, 1.3f)),
                MapUtil.createEntry("speed", new ModifierConfig(1.25f, 0.7f, 1.45f)),
                MapUtil.createEntry("item", new ModifierConfig(1.3f, 1.25f, 2.0f))
            )
        ))
    );

    @Expose
    public Map<String, SolarPanelConfig> SOLAR_PANEL_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new SolarPanelConfig(5000000, 100, 20,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.15f, 0.9f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.85f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.5f))
            )
        )),
        MapUtil.createEntry("aurantium", new SolarPanelConfig(10000000, 80, 40,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.18f, 0.88f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.83f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.55f))
            )
        )),
        MapUtil.createEntry("citrinetine", new SolarPanelConfig(20000000, 60, 80,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.21f, 0.86f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.81f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.6f))
            )
        )),
        MapUtil.createEntry("verdium", new SolarPanelConfig(40000000, 50, 160,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.24f, 0.84f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.79f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.65f))
            )
        )),
        MapUtil.createEntry("azurine", new SolarPanelConfig(80000000, 40, 320,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.27f, 0.82f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.77f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.7f))
            )
        )),
        MapUtil.createEntry("caerium", new SolarPanelConfig(160000000, 30, 640,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.3f, 0.8f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.75f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.75f))
            )
        )),
        MapUtil.createEntry("amethystine", new SolarPanelConfig(320000000, 25, 1280,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.33f, 0.78f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.73f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.8f))
            )
        )),
        MapUtil.createEntry("rosarium", new SolarPanelConfig(640000000, 20, 2560,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.36f, 0.76f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.71f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.85f))
            )
        )),
        MapUtil.createEntry("ultimate", new SolarPanelConfig(2147483647, 15, 5120,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.45f, 0.7f, 1.0f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.0f, 0.65f, 1.0f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.0f))
            )
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
        return MINER_CONFIGS.getOrDefault(name, new MinerConfig(0,0, 0, Map.of()));
    }

    public SolarPanelConfig getSolarPanelConfig(String name) {
        return SOLAR_PANEL_CONFIGS.getOrDefault(name, new SolarPanelConfig(0,0, 0, Map.of()));
    }

    public ModifierConfig getModifierConfig(String name, String type) {
        return getMinerConfig(name).modifiers.getOrDefault(type, new ModifierConfig(1, 1, 1));
    }

    public ModifierConfig getModifierConfig(Block block) {
        String blockName = ForgeRegistries.BLOCKS.getKey(block).getPath();
        String minerTier = blockName.split("_")[0];
        String modifierType = blockName.split("_")[1];

        return getModifierConfig(minerTier, modifierType);
    }

    public SolarModifierConfig getSolarModifierConfig(String name, String type) {
        return getSolarPanelConfig(name).modifiers.getOrDefault(type, new SolarModifierConfig(1, 1, 1));
    }

    public SolarModifierConfig getSolarModifierConfig(Block block, String solarTier) {
        String blockName = ForgeRegistries.BLOCKS.getKey(block).getPath();

        // For solar modifiers, the format is: solar_<tier>_<type>_modifier
        // Example: solar_ultimate_efficiency_modifier
        String[] parts = blockName.split("_");

        // Get the modifier type (efficiency, weather, output)
        String modifierType = parts.length >= 3 ? parts[2] : "unknown";

        // Map modifier types to config keys
        String solarModifierType = switch (modifierType) {
            case "efficiency" -> "efficiency";
            case "weather" -> "weather_resistance";
            case "output" -> "generation";
            default -> modifierType;
        };

        return getSolarModifierConfig(solarTier, solarModifierType);
    }

    public record MinerConfig(@Expose int energyStorage, @Expose int duration, @Expose int energyTick, @Expose Map<String, ModifierConfig> modifiers) {

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

            return new MinerConfig(energyStorage, duration, energy, modifiers);
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
        }
    }

    public record SolarPanelConfig(@Expose int energyStorage, @Expose int duration, @Expose int energyGeneration, @Expose Map<String, SolarModifierConfig> modifiers) {

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

            return new SolarPanelConfig(energyStorage, duration, energyGeneration, modifiers);
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
        }
    }

    public record SolarModifierConfig(@Expose float generation, @Expose float efficiency, @Expose float weatherResistance) {
        
        public static SolarModifierConfig fromBuf(FriendlyByteBuf buf) {
            float generation = buf.readFloat();
            float efficiency = buf.readFloat();
            float weatherResistance = buf.readFloat();

            return new SolarModifierConfig(generation, efficiency, weatherResistance);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeFloat(generation);
            buf.writeFloat(efficiency);
            buf.writeFloat(weatherResistance);
        }
    }

    public record ModifierConfig(@Expose float energy, @Expose float speed, @Expose float item) {
        public static ModifierConfig fromBuf(FriendlyByteBuf buf) {
            float energy = buf.readFloat();
            float speed = buf.readFloat();
            float item = buf.readFloat();

            return new ModifierConfig(energy, speed, item);
        }

        public void toBuf(FriendlyByteBuf buf) {
            buf.writeFloat(energy);
            buf.writeFloat(speed);
            buf.writeFloat(item);
        }
    }
}
