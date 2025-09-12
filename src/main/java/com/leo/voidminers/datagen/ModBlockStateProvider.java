package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.SolarSet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VoidMiners.MODID, exFileHelper);
        this.existingFileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockItem(ModBlocks.FRAME_BASE);

        quadLayerBlockWithItem(ModBlocks.FRAME_BASE, "voidminers:block/null/frame", "voidminers:block/_core/frame", "voidminers:block/_core/cover", "voidminers:block/null/core");

        dualLayerBlockWithItem(ModBlocks.STRUCTURE_PANEL, "voidminers:block/_core/panel", "voidminers:block/_core/cover");

        simpleBlockWithItem(ModBlocks.GLASS_PANEL);

        quadLayerBlockWithItem(ModBlocks.NULL_MOD, "voidminers:block/null/modifier", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/null/core");

        for (CrystalSet set : CrystalSet.sets()) {
            simpleAllCubeWithItem(
                set.CRYSTAL_BLOCK,
                set.name
            );

            tripleLayerBlockWithItem(set.FRAME, "voidminers:block/" + set.name + "/frame", "voidminers:block/_core/frame", "voidminers:block/_core/cover");

            simpleBlockWithItem(set.MINER_CONTROLLER);

            quadLayerBlockWithItem(set.ENERGY_MOD, "voidminers:block/_core/energy", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.SPEED_MOD, "voidminers:block/_core/speed", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.ITEM_MOD, "voidminers:block/_core/item", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");
        }
        
        // Solar panels
        for (SolarSet set : SolarSet.sets()) {
            simpleAllCubeWithItem(
                set.SOLAR_CRYSTAL_BLOCK,
                set.name
            );

            tripleLayerBlockWithItem(set.SOLAR_FRAME, "voidminers:block/" + set.name + "/frame", "voidminers:block/_core/frame", "voidminers:block/_core/cover");

            // Create the miner block model for solar panels with correct path
            BlockModelBuilder minerModel = models().cubeAll("block/" + set.name + "/miner", 
                modLoc("block/" + set.name + "/miner"));
            simpleBlockWithItem(set.SOLAR_PANEL_CONTROLLER.get(), minerModel);

            quadLayerBlockWithItem(set.EFFICIENCY_MOD, "voidminers:block/_core/energy", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.WEATHER_MOD, "voidminers:block/_core/speed", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.OUTPUT_MOD, "voidminers:block/_core/item", "voidminers:block/_core/modifier", "voidminers:block/_core/cover", "voidminers:block/" + set.name + "/core");
        }
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(blockRegistryObject.getId().getPath(), ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + blockRegistryObject.getId().getPath())));
    }

    private void simpleBlockWithItem(RegistryObject<? extends Block> block) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + block.getId().getPath())));
    }

    private void simpleBlockWithItem(RegistryObject<? extends Block> block, String name) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + name)));
    }

    private void simpleBlockItem(RegistryObject<? extends Block> block) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + block.getId().getPath())));
    }

    private void dualLayerBlockWithItem(RegistryObject<? extends Block> block, String layer0, String layer1) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/_template/dual_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1);

        simpleBlockWithItem(block.get(), model);
    }

    private void tripleLayerBlockWithItem(RegistryObject<? extends Block> block, String layer0, String layer1, String layer2) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/_template/triple_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1)
            .texture("layer2", layer2);

        simpleBlockWithItem(block.get(), model);
    }

    private void quadLayerBlockWithItem(RegistryObject<? extends Block> block, String layer0, String layer1, String layer2, String layer3) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/_template/quad_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1)
            .texture("layer2", layer2)
            .texture("layer3", layer3);

        simpleBlockWithItem(block.get(), model);
    }

    private void simpleAllCubeWithItem(RegistryObject<Block> block, String name) {
        simpleBlockWithItem(block.get(), models().cubeAll(name(block.get()), ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "block/" + name + "/block")));
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private static ResourceLocation stripSetName(ResourceLocation name) {
        int index = name.getPath().indexOf('_');

        if (index == -1) {
            return name;
        }

        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), name.getPath().substring(index + 1));
    }
}
