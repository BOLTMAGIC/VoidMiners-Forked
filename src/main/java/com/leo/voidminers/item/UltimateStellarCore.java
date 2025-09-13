package com.leo.voidminers.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class UltimateStellarCore extends Item {
    public UltimateStellarCore() {
        super(new Item.Properties()
            .rarity(Rarity.EPIC)
            .stacksTo(64)
        );
    }
}