package com.leo.voidminers.block.modifier;

import net.minecraft.world.level.block.Block;

public class ModifierBlock extends Block {
    public String name;

    public ModifierBlock(Properties pProperties, String name) {
        super(pProperties);
        this.name = name;
    }
}
