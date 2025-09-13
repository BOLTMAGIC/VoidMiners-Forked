package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.item.StructureHelper;
import com.leo.voidminers.item.UltimateStellarCore;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VoidMiners.MODID);

    public static final RegistryObject<Item> STRUCTURE_HELPER = ITEMS.register("structure_helper",
        () -> new StructureHelper(
            new Item.Properties()
        )
    );

    public static final RegistryObject<Item> ULTIMATE_STELLAR_CORE = ITEMS.register("ultimate_stellar_core",
        () -> new UltimateStellarCore()
    );
}
