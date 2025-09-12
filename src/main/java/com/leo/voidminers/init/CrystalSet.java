package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.block.ModifierBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class CrystalSet {
    public static CrystalSet RUBETINE;
    public static CrystalSet AURANTIUM;
    public static CrystalSet CITRINETINE;
    public static CrystalSet VERDIUM;
    public static CrystalSet AZURINE;
    public static CrystalSet CAERIUM;
    public static CrystalSet AMETHYSTINE;
    public static CrystalSet ROSARIUM;
    public static CrystalSet ULTIMATE;


    public final String name;
    public final RegistryObject<Item> CRYSTAL;
    public final RegistryObject<Block> CRYSTAL_BLOCK;
    public final RegistryObject<Block> MINER_CONTROLLER;
    public final RegistryObject<Block> FRAME;
    public final RegistryObject<Block> SPEED_MOD;
    public final RegistryObject<Block> ENERGY_MOD;
    public final RegistryObject<Block> ITEM_MOD;

    CrystalSet(String name, RegistryObject<Item> crystal, RegistryObject<Block> crystalBlock, RegistryObject<Block> minerController, RegistryObject<Block> frame, RegistryObject<Block> energyMod, RegistryObject<Block> speedMod, RegistryObject<Block> itemMod) {
        this.name = name;
        CRYSTAL = crystal;
        CRYSTAL_BLOCK = crystalBlock;
        MINER_CONTROLLER = minerController;
        FRAME = frame;
        SPEED_MOD = speedMod;
        ENERGY_MOD = energyMod;
        ITEM_MOD = itemMod;
    }

    public static RegistryObject<Item> fastCreateItem(String name, Rarity rarity) {
        return ModItems.ITEMS.register(name, () -> new Item(new Item.Properties().rarity(rarity)));
    }

    public static RegistryObject<Block> fastCreateBlock(String name, float hardness, float resistance, Rarity rarity) {
        return ModBlocks.registerBlock(name,
            () -> new Block(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops()
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateModifier(String name, float hardness, float resistance, Rarity rarity, ModifierType type) {
        return ModBlocks.registerBlock(name + "_" + type.type + "_modifier",
            () -> new ModifierBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                name
            ),
            rarity
        );
    }

    public static RegistryObject<Block> fastCreateController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure) {
        return ModBlocks.registerBlock(name + "_miner",
            () -> new ControllerBaseBlock(
                BlockBehaviour.Properties.of()
                    .strength(hardness, resistance)
                    .requiresCorrectToolForDrops(),
                structure,
                name
            ),
            rarity
        );
    }

    public static void initSets() {
        RUBETINE = createSet("rubetine", ModRarities.RUBETINE);
        AURANTIUM = createSet("aurantium", ModRarities.AURANTIUM);
        CITRINETINE = createSet("citrinetine", ModRarities.CITRINETINE);
        VERDIUM = createSet("verdium", ModRarities.VERDIUM);
        AZURINE = createSet("azurine", ModRarities.AZURINE);
        CAERIUM = createSet("caerium", ModRarities.CAERIUM);
        AMETHYSTINE = createSet("amethystine", ModRarities.AMETHYSTINE);
        ROSARIUM = createSet("rosarium", ModRarities.ROSARIUM);
        ULTIMATE = createSet("ultimate", ModRarities.ULTIMATE);
    }

    public static CrystalSet createSet(String name, Rarity rarity) {
        return new CrystalSet(
            name,
            fastCreateItem(name, rarity),
            fastCreateBlock(name + "_block", 10, 5, rarity),
            fastCreateController(name, 10, 50, rarity, ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, name)),
            fastCreateBlock(name + "_frame", 10, 50, rarity),
            fastCreateModifier(name, 10, 50, rarity, ModifierType.ENERGY),
            fastCreateModifier(name, 10, 50, rarity, ModifierType.SPEED),
            fastCreateModifier(name, 10, 50, rarity, ModifierType.ITEM)
        );
    }

    public static List<CrystalSet> sets() {
        List<CrystalSet> sets = new ArrayList<>();

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

    public enum ModifierType {
        ENERGY("energy"),
        SPEED("speed"),
        ITEM("item"),
        NULL("null");

        public final String type;

        ModifierType(String type) {
            this.type = type;
        }

        public static ModifierType getFromName(String name) {
            return switch (name.toLowerCase()) {
                case "energy" -> ENERGY;
                case "speed" -> SPEED;
                case "item" -> ITEM;
                default -> null;
            };
        }
    }
}
