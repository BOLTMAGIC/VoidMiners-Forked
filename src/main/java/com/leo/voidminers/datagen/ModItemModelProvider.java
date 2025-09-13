package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.SolarSet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VoidMiners.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.STRUCTURE_HELPER);

        for (CrystalSet set : CrystalSet.sets()) {
            if (set.CRYSTAL != null) {
                simpleItem(
                    set.CRYSTAL
                );
            }
        }

        // Solar items - use existing tier textures
        for (SolarSet set : SolarSet.sets()) {
            if (set.SOLAR_CRYSTAL != null) {
                simpleItemWithTexture(set.SOLAR_CRYSTAL, set.name);
            }
        }
    }

    private void simpleItem(RegistryObject<? extends Item> item) {
        simpleItem(item.getId().getPath());
    }

    private void simpleItem(String name) {
        withExistingParent(name,
            ResourceLocation.parse("item/generated")).texture("layer0",
            ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "item/" + name));
    }

    private void simpleItemWithTexture(RegistryObject<? extends Item> item, String textureName) {
        withExistingParent(item.getId().getPath(),
            ResourceLocation.parse("item/generated")).texture("layer0",
            ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "item/" + textureName));
    }
}
