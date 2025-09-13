package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.SolarPanelBaseBlock;
import com.leo.voidminers.block.ModifierBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class SolarSet {
    public static SolarSet RUBETINE;
    public static SolarSet AURANTIUM;
    public static SolarSet CITRINETINE;
    public static SolarSet VERDIUM;
    public static SolarSet AZURINE;
    public static SolarSet CAERIUM;
    public static SolarSet AMETHYSTINE;
    public static SolarSet ROSARIUM;
    public static SolarSet ULTIMATE;


    public final String name;
    public final RegistryObject<Item> SOLAR_CRYSTAL;
    public final RegistryObject<Block> SOLAR_CRYSTAL_BLOCK;
    public final RegistryObject<Block> SOLAR_PANEL_CONTROLLER;
    public final RegistryObject<Block> SOLAR_FRAME;
    public final RegistryObject<Block> EFFICIENCY_MOD;
    public final RegistryObject<Block> WEATHER_MOD;
    public final RegistryObject<Block> OUTPUT_MOD;

    SolarSet(String name, RegistryObject<Item> solarCrystal, RegistryObject<Block> solarCrystalBlock, RegistryObject<Block> solarPanelController, RegistryObject<Block> solarFrame, RegistryObject<Block> efficiencyMod, RegistryObject<Block> weatherMod, RegistryObject<Block> outputMod) {
        this.name = name;
        SOLAR_CRYSTAL = solarCrystal;
        SOLAR_CRYSTAL_BLOCK = solarCrystalBlock;
        SOLAR_PANEL_CONTROLLER = solarPanelController;
        SOLAR_FRAME = solarFrame;
        EFFICIENCY_MOD = efficiencyMod;
        WEATHER_MOD = weatherMod;
        OUTPUT_MOD = outputMod;
    }

    public static RegistryObject<Item> fastCreateSolarItem(String name, Rarity rarity) {
        return ModItems.ITEMS.register("solar_" + name, () -> new Item(new Item.Properties().rarity(rarity)));
    }

    public static RegistryObject<Block> fastCreateSolarBlock(String name, float hardness, float resistance, Rarity rarity) {
        return ModBlocks.registerBlock("solar_" + name + "_block",
            () -> new Block(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops()
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateSolarModifier(String name, float hardness, float resistance, Rarity rarity, SolarModifierType type) {
        return ModBlocks.registerBlock("solar_" + name + "_" + type.type + "_modifier",
            () -> new ModifierBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                name  // Use just the name without "solar_" prefix for config lookup
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateSolarController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure) {
        return ModBlocks.registerBlock("solar_" + name + "_panel",
            () -> new SolarPanelBaseBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                structure,
                name  // Use just the name without "solar_" prefix for config lookup
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateSolarFrame(String name, float hardness, float resistance, Rarity rarity) {
        return ModBlocks.registerBlock("solar_" + name + "_frame",
            () -> new Block(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops()
            ),
            rarity
        );
    }

    public static void initSolarSets() {
        RUBETINE = createSolarSet("rubetine", ModRarities.RUBETINE);
        AURANTIUM = createSolarSet("aurantium", ModRarities.AURANTIUM);
        CITRINETINE = createSolarSet("citrinetine", ModRarities.CITRINETINE);
        VERDIUM = createSolarSet("verdium", ModRarities.VERDIUM);
        AZURINE = createSolarSet("azurine", ModRarities.AZURINE);
        CAERIUM = createSolarSet("caerium", ModRarities.CAERIUM);
        AMETHYSTINE = createSolarSet("amethystine", ModRarities.AMETHYSTINE);
        ROSARIUM = createSolarSet("rosarium", ModRarities.ROSARIUM);
        ULTIMATE = createSolarSet("ultimate", ModRarities.ULTIMATE);
    }

    public static SolarSet createSolarSet(String name, Rarity rarity) {
        return new SolarSet(
            name,
            fastCreateSolarItem(name, rarity),
            fastCreateSolarBlock(name, 10, 5, rarity),
            fastCreateSolarController(name, 10, 50, rarity, ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "solar_" + name)),
            fastCreateSolarFrame(name, 10, 50, rarity),
            fastCreateSolarModifier(name, 10, 50, rarity, SolarModifierType.EFFICIENCY),
            fastCreateSolarModifier(name, 10, 50, rarity, SolarModifierType.WEATHER),
            fastCreateSolarModifier(name, 10, 50, rarity, SolarModifierType.OUTPUT)
        );
    }

    public static List<SolarSet> sets() {
        List<SolarSet> sets = new ArrayList<>();

        sets.add(RUBETINE);
        sets.add(AURANTIUM);
        sets.add(CITRINETINE);
        sets.add(VERDIUM);
        sets.add(AZURINE);
        sets.add(CAERIUM);
        sets.add(AMETHYSTINE);
        sets.add(ROSARIUM);
        sets.add(ULTIMATE);

        return sets;
    }

    public enum SolarModifierType {
        EFFICIENCY("efficiency"),
        WEATHER("weather"),
        OUTPUT("output"),
        NULL("null");

        public final String type;

        SolarModifierType(String type) {
            this.type = type;
        }

        public static SolarModifierType getFromName(String name) {
            return switch (name.toLowerCase()) {
                case "efficiency" -> EFFICIENCY;
                case "weather" -> WEATHER;
                case "output" -> OUTPUT;
                default -> null;
            };
        }
    }
}