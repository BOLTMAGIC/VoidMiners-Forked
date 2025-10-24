package com.leo.voidminers.multiblock.solar;

import com.leo.voidminers.util.MiscUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class SolarPanelPatternHelper {

    private SolarPanelPatternHelper() {
    }

    static SimpleMultiBlockAislePatternBuilder createAccessiblePattern(
            String structure,
            List<List<String>> stringPattern,
            Map<Character, Predicate<BlockInWorld>> lookup,
            Map<Character, Supplier<BlockState>> blockProvider
    ) {
        SimpleMultiBlockAislePatternBuilder pattern = SimpleMultiBlockAislePatternBuilder.start();
        List<List<List<BlockState>>> blocks = new ArrayList<>();

        List<List<String>> reversed = new ArrayList<>(stringPattern);
        Collections.reverse(reversed);

        for (List<String> strings : reversed) {
            pattern.aisle(strings.toArray(new String[0]));

            List<List<BlockState>> blockForAisle = new ArrayList<>();
            for (String s : strings) {
                blockForAisle.add(getStatesForString(s, blockProvider));
            }
            blocks.add(blockForAisle);
        }

        MiscUtil.structureMap.put(structure, blocks);

        lookup.forEach(pattern::where);
        blockProvider.forEach(pattern::block);

        return pattern;
    }

    private static List<BlockState> getStatesForString(String s, Map<Character, Supplier<BlockState>> map) {
        List<BlockState> toReturn = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                toReturn.add(Blocks.AIR.defaultBlockState());
            } else if (map.containsKey(c)) {
                toReturn.add(map.get(c).get());
            }
        }

        return toReturn;
    }
}
