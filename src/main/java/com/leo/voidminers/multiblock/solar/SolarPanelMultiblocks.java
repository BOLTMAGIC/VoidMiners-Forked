package com.leo.voidminers.multiblock.solar;

import com.leo.voidminers.VoidMiners;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;

import java.util.EnumMap;
import java.util.Map;

public final class SolarPanelMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMiners.MODID, "solar_panels");

    private static final Map<SolarPanelPatternDefinition, SimpleMultiBlockAislePatternBuilder> PATTERNS;

    static {
        PATTERNS = new EnumMap<>(SolarPanelPatternDefinition.class);
        for (SolarPanelPatternDefinition definition : SolarPanelPatternDefinition.values()) {
            PATTERNS.put(definition, definition.build());
        }
    }

    private SolarPanelMultiblocks() {
    }

    public static void init() {
        PATTERNS.forEach((definition, builder) -> MANAGER.register(definition.key(), builder.build()));
    }

    public static SimpleMultiBlockAislePatternBuilder getPattern(SolarPanelPatternDefinition definition) {
        return PATTERNS.get(definition);
    }
}
