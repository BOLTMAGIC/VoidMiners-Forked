package com.leo.voidminers.multiblock.miner;

import com.leo.voidminers.VoidMiners;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;

import java.util.EnumMap;
import java.util.Map;

public final class MinerMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMiners.MODID, "miners");

    private static final Map<MinerPatternDefinition, SimpleMultiBlockAislePatternBuilder> PATTERNS;

    static {
        PATTERNS = new EnumMap<>(MinerPatternDefinition.class);
        for (MinerPatternDefinition definition : MinerPatternDefinition.values()) {
            PATTERNS.put(definition, definition.build());
        }
    }

    private MinerMultiblocks() {
    }

    public static void init() {
        PATTERNS.forEach((definition, builder) -> MANAGER.register(definition.key(), builder.build()));
    }

    public static SimpleMultiBlockAislePatternBuilder getPattern(MinerPatternDefinition definition) {
        return PATTERNS.get(definition);
    }
}
