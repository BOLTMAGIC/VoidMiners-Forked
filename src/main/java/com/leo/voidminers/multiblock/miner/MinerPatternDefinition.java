package com.leo.voidminers.multiblock.miner;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.datagen.ModBlockTagGenerator;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.ModBlocks;
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

enum MinerPatternDefinition {
    RUBETINE("rubetine", CrystalSet.RUBETINE, ModBlockTagGenerator.FRAME_1),
    AURANTIUM("aurantium", CrystalSet.AURANTIUM, ModBlockTagGenerator.FRAME_2),
    CITRINETINE("citrinetine", CrystalSet.CITRINETINE, ModBlockTagGenerator.FRAME_3),
    VERDIUM("verdium", CrystalSet.VERDIUM, ModBlockTagGenerator.FRAME_4),
    AZURINE("azurine", CrystalSet.AZURINE, ModBlockTagGenerator.FRAME_5),
    CAERIUM("caerium", CrystalSet.CAERIUM, ModBlockTagGenerator.FRAME_6),
    AMETHYSTINE("amethystine", CrystalSet.AMETHYSTINE, ModBlockTagGenerator.FRAME_7),
    ROSARIUM("rosarium", CrystalSet.ROSARIUM, ModBlockTagGenerator.FRAME_8),
    ULTIMATE("ultimate", CrystalSet.ULTIMATE, ModBlockTagGenerator.FRAME_9);

    private final String structurePath;
    private final ResourceLocation structureId;
    private final CrystalSet crystalSet;
    private final TagKey<Block> frameTag;

    MinerPatternDefinition(String structurePath, CrystalSet crystalSet, TagKey<Block> frameTag) {
        this.structurePath = structurePath;
        this.structureId = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, structurePath);
        this.crystalSet = crystalSet;
        this.frameTag = frameTag;
    }

    String key() {
        return structurePath;
    }

    ResourceLocation structureId() {
        return structureId;
    }

    SimpleMultiBlockAislePatternBuilder build() {
        List<List<String>> layers = MinerPatternLayers.load(structurePath);

        Map<Character, Predicate<BlockInWorld>> lookup = new HashMap<>();
        lookup.put('*', blockMatches(crystalSet.MINER_CONTROLLER));
        lookup.put('P', tagMatches(ModBlockTagGenerator.PANELS));
        lookup.put('F', tagMatches(frameTag));
        lookup.put('M', tagMatches(ModBlockTagGenerator.MODIFIERS));

        Map<Character, Supplier<BlockState>> blockProviders = new HashMap<>();
        blockProviders.put('P', ModBlocks.GLASS_PANEL.get()::defaultBlockState);
        blockProviders.put('F', crystalSet.FRAME.get()::defaultBlockState);
        blockProviders.put('M', ModBlocks.NULL_MOD.get()::defaultBlockState);

        return MinerPatternHelper.createAccessiblePattern(
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
