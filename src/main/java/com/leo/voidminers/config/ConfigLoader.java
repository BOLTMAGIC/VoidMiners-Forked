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
    public Map<String, MinerConfig> MINER_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new MinerConfig(10000000, 1000, 300,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("aurantium", new MinerConfig(25000000, 900, 350,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("citrinetine", new MinerConfig(50000000,800, 400,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("verdium", new MinerConfig(100000000,700, 450,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("azurine", new MinerConfig(250000000,600, 500,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("caerium", new MinerConfig(500000000,500, 550,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("amethystine", new MinerConfig(750000000,400, 600,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("rosarium", new MinerConfig(1000000000,300, 650,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.9f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.1f, 0.95f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.2f, 1, 1.75f))
            )
        )),
        MapUtil.createEntry("ultimate", new MinerConfig(2147483647,200, 700,
            MapUtil.of(
                MapUtil.createEntry("energy", new ModifierConfig(0.8f, 1, 1)),
                MapUtil.createEntry("speed", new ModifierConfig(1.2f, 0.9f, 1)),
                MapUtil.createEntry("item", new ModifierConfig(1.5f, 1, 2.0f))
            )
        ))
    );

    @Expose
    public Map<String, SolarPanelConfig> SOLAR_PANEL_CONFIGS = MapUtil.of(
        MapUtil.createEntry("rubetine", new SolarPanelConfig(5000000, 100, 20,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.1f, 1.0f, 1.2f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.05f, 1.1f, 1.1f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.75f))
            )
        )),
        MapUtil.createEntry("aurantium", new SolarPanelConfig(10000000, 80, 40,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.15f, 1.0f, 1.25f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.05f, 1.15f, 1.15f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.8f))
            )
        )),
        MapUtil.createEntry("citrinetine", new SolarPanelConfig(20000000, 60, 80,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.2f, 1.0f, 1.3f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.1f, 1.2f, 1.2f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.85f))
            )
        )),
        MapUtil.createEntry("verdium", new SolarPanelConfig(40000000, 50, 160,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.25f, 1.0f, 1.35f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.1f, 1.25f, 1.25f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.9f))
            )
        )),
        MapUtil.createEntry("azurine", new SolarPanelConfig(80000000, 40, 320,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.3f, 1.0f, 1.4f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.15f, 1.3f, 1.3f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 1.95f))
            )
        )),
        MapUtil.createEntry("caerium", new SolarPanelConfig(160000000, 30, 640,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.35f, 1.0f, 1.45f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.15f, 1.35f, 1.35f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.0f))
            )
        )),
        MapUtil.createEntry("amethystine", new SolarPanelConfig(320000000, 25, 1280,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.4f, 1.0f, 1.5f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.2f, 1.4f, 1.4f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.1f))
            )
        )),
        MapUtil.createEntry("rosarium", new SolarPanelConfig(640000000, 20, 2560,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.45f, 1.0f, 1.6f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.2f, 1.45f, 1.45f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.2f))
            )
        )),
        MapUtil.createEntry("ultimate", new SolarPanelConfig(2147483647, 15, 5120,
            MapUtil.of(
                MapUtil.createEntry("generation", new SolarModifierConfig(1.5f, 1.0f, 1.8f)),
                MapUtil.createEntry("efficiency", new SolarModifierConfig(1.3f, 1.5f, 1.6f)),
                MapUtil.createEntry("weather_resistance", new SolarModifierConfig(1.0f, 1.0f, 2.5f))
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
