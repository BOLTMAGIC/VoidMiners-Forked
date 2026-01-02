package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.item.StructureHelper;
import com.leo.voidminers.item.UltimateStellarCore;
import com.leo.voidminers.item.UpgradeItem;
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

    // Max Storage Upgrades (show tooltip via UpgradeItem)
    public static final RegistryObject<Item> UPGRADE_MAX_STORAGE_T1 = ITEMS.register("upgrade_max_storage_t1",
        () -> new UpgradeItem("tooltip.voidminers.upgrade_max_storage_t1", new Item.Properties())
    );

    public static final RegistryObject<Item> UPGRADE_MAX_STORAGE_T2 = ITEMS.register("upgrade_max_storage_t2",
        () -> new UpgradeItem("tooltip.voidminers.upgrade_max_storage_t2", new Item.Properties())
    );

    public static final RegistryObject<Item> UPGRADE_MAX_STORAGE_T3 = ITEMS.register("upgrade_max_storage_t3",
        () -> new UpgradeItem("tooltip.voidminers.upgrade_max_storage_t3", new Item.Properties())
    );
}
