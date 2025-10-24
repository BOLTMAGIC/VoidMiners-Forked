package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.base.BaseTransparentBlock;
import com.leo.voidminers.block.modifier.ModifierBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VoidMiners.MODID);

    public static final RegistryObject<Block> FRAME_BASE = registerBlock("frame_base",
        () -> new Block(
            BlockBehaviour.Properties.of()
                .strength(10, 5)
                .requiresCorrectToolForDrops()
        )
    );

    public static final RegistryObject<Block> STRUCTURE_PANEL = registerBlock("structure_panel",
        () -> new Block(
            BlockBehaviour.Properties.of()
                .strength(10, 5)
                .requiresCorrectToolForDrops()
        )
    );

    public static final RegistryObject<Block> GLASS_PANEL = registerBlock("glass_panel",
        () -> new BaseTransparentBlock(
            BlockBehaviour.Properties.of()
                .strength(10, 5)
                .requiresCorrectToolForDrops().sound(SoundType.GLASS)
        )
    );

    public static final RegistryObject<Block> NULL_MOD = registerBlock("null_modifier",
        () -> new ModifierBlock(
            BlockBehaviour.Properties.of()
                .strength(10, 50)
                .requiresCorrectToolForDrops(),
            "null"
        )
    );

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Rarity rarity) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, rarity);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, Rarity rarity) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity)));
    }
}
