package com.leo.voidminers.multiblock.solar;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.datagen.ModBlockTagGenerator;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.SolarSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

enum SolarPanelPatternDefinition {
    RUBETINE("solar_rubetine", SolarSet.RUBETINE, ModBlockTagGenerator.FRAME_1),
    AURANTIUM("solar_aurantium", SolarSet.AURANTIUM, ModBlockTagGenerator.FRAME_2),
    CITRINETINE("solar_citrinetine", SolarSet.CITRINETINE, ModBlockTagGenerator.FRAME_3),
    VERDIUM("solar_verdium", SolarSet.VERDIUM, ModBlockTagGenerator.FRAME_4),
    AZURINE("solar_azurine", SolarSet.AZURINE, ModBlockTagGenerator.FRAME_5),
    CAERIUM("solar_caerium", SolarSet.CAERIUM, ModBlockTagGenerator.FRAME_6),
    AMETHYSTINE("solar_amethystine", SolarSet.AMETHYSTINE, ModBlockTagGenerator.FRAME_7),
    ROSARIUM("solar_rosarium", SolarSet.ROSARIUM, ModBlockTagGenerator.FRAME_8),
    ULTIMATE("solar_ultimate", SolarSet.ULTIMATE, ModBlockTagGenerator.FRAME_9);

    private final String structurePath;
    private final ResourceLocation structureId;
    private final SolarSet solarSet;
    private final TagKey<Block> frameTag;

    SolarPanelPatternDefinition(String structurePath, SolarSet solarSet, TagKey<Block> frameTag) {
        this.structurePath = structurePath;
        this.structureId = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, structurePath);
        this.solarSet = solarSet;
        this.frameTag = frameTag;
    }

    String key() {
        return structurePath;
    }

    ResourceLocation structureId() {
        return structureId;
    }

    SimpleMultiBlockAislePatternBuilder build() {
        List<List<String>> layers = SolarPatternLayers.load(structurePath);

        Map<Character, Predicate<BlockInWorld>> lookup = new HashMap<>();
        lookup.put('*', blockMatches(solarSet.SOLAR_PANEL_CONTROLLER));
        lookup.put('P', tagMatches(ModBlockTagGenerator.PANELS));
        lookup.put('F', tagMatches(frameTag));
        lookup.put('M', tagMatches(ModBlockTagGenerator.MODIFIERS));

        Map<Character, Supplier<BlockState>> blockProviders = new HashMap<>();
        blockProviders.put('P', ModBlocks.GLASS_PANEL.get()::defaultBlockState);
        blockProviders.put('F', solarSet.SOLAR_FRAME.get()::defaultBlockState);
        blockProviders.put('M', ModBlocks.NULL_MOD.get()::defaultBlockState);

        return SolarPanelPatternHelper.createAccessiblePattern(
            structureId.toString(),
            layers,
            lookup,
            blockProviders
        );
    }

    private Predicate<BlockInWorld> blockMatches(Supplier<Block> blockSupplier) {
        return blockInWorld -> blockInWorld.getState().is(blockSupplier.get());
    }

    private Predicate<BlockInWorld> tagMatches(TagKey<Block> tagKey) {
        return blockInWorld -> blockInWorld.getState().is(tagKey);
    }
}
