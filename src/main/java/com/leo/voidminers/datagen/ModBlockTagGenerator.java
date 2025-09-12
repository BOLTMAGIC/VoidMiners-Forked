package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.SolarSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VoidMiners.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.NEEDS_STONE_TOOL)
            .add(
                ModBlocks.FRAME_BASE.get(),
                ModBlocks.STRUCTURE_PANEL.get(),
                ModBlocks.GLASS_PANEL.get(),
                ModBlocks.NULL_MOD.get()
            );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(
                ModBlocks.FRAME_BASE.get(),
                ModBlocks.STRUCTURE_PANEL.get(),
                ModBlocks.GLASS_PANEL.get(),
                ModBlocks.NULL_MOD.get()
            );

        List<CrystalSet> allSets = CrystalSet.sets();
        for (int i = 0; i < allSets.size(); i++) {
            CrystalSet set = allSets.get(i);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                    set.MINER_CONTROLLER.get(),
                    set.CRYSTAL_BLOCK.get(),
                    set.FRAME.get(),
                    set.ENERGY_MOD.get(),
                    set.SPEED_MOD.get(),
                    set.ITEM_MOD.get()
                );

            this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(
                    set.MINER_CONTROLLER.get(),
                    set.CRYSTAL_BLOCK.get(),
                    set.FRAME.get(),
                    set.ENERGY_MOD.get(),
                    set.SPEED_MOD.get(),
                    set.ITEM_MOD.get()
                );

            for (int j = 1; j < CrystalSet.sets().size() + 1; j++) {
                if (i + 1 >= j) {
                    this.tag(create("frames/" + j))
                        .add(set.FRAME.get());
                }
            }

            this.tag(MODIFIERS)
                .add(
                    set.ENERGY_MOD.get(),
                    set.SPEED_MOD.get(),
                    set.ITEM_MOD.get()
                );
        }

        this.tag(PANELS)
            .add(
                ModBlocks.STRUCTURE_PANEL.get(),
                ModBlocks.GLASS_PANEL.get()
            );

        this.tag(MODIFIERS)
            .add(
                ModBlocks.NULL_MOD.get()
            );

        // Add Solar Set blocks to tags
        List<SolarSet> allSolarSets = SolarSet.sets();
        for (int i = 0; i < allSolarSets.size(); i++) {
            SolarSet solarSet = allSolarSets.get(i);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                    solarSet.SOLAR_PANEL_CONTROLLER.get(),
                    solarSet.SOLAR_CRYSTAL_BLOCK.get(),
                    solarSet.SOLAR_FRAME.get(),
                    solarSet.EFFICIENCY_MOD.get(),
                    solarSet.WEATHER_MOD.get(),
                    solarSet.OUTPUT_MOD.get()
                );

            this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(
                    solarSet.SOLAR_PANEL_CONTROLLER.get(),
                    solarSet.SOLAR_CRYSTAL_BLOCK.get(),
                    solarSet.SOLAR_FRAME.get(),
                    solarSet.EFFICIENCY_MOD.get(),
                    solarSet.WEATHER_MOD.get(),
                    solarSet.OUTPUT_MOD.get()
                );

            // Add solar frames to frame tags
            for (int j = 1; j < allSolarSets.size() + 1; j++) {
                if (i + 1 >= j) {
                    this.tag(create("frames/" + j))
                        .add(solarSet.SOLAR_FRAME.get());
                }
            }

            // Add solar modifiers to MODIFIERS tag
            this.tag(MODIFIERS)
                .add(
                    solarSet.EFFICIENCY_MOD.get(),
                    solarSet.WEATHER_MOD.get(),
                    solarSet.OUTPUT_MOD.get()
                );
        }
    }

    public static final TagKey<Block> FRAME_1 = create("frames/1");
    public static final TagKey<Block> FRAME_2 = create("frames/2");
    public static final TagKey<Block> FRAME_3 = create("frames/3");
    public static final TagKey<Block> FRAME_4 = create("frames/4");
    public static final TagKey<Block> FRAME_5 = create("frames/5");
    public static final TagKey<Block> FRAME_6 = create("frames/6");
    public static final TagKey<Block> FRAME_7 = create("frames/7");
    public static final TagKey<Block> FRAME_8 = create("frames/8");
    public static final TagKey<Block> FRAME_9 = create("frames/9");

    public static final TagKey<Block> PANELS = create("panels");

    public static final TagKey<Block> MODIFIERS = create("modifiers");

    private static TagKey<Block> create(String pName) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, pName));
    }
}
