package com.leo.voidminers.multiblock.miner;

import com.leo.voidminers.util.MiscUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class MinerPatternHelper {

    private MinerPatternHelper() {
    }

    static SimpleMultiBlockAislePatternBuilder createAccessiblePattern(
            String structureKey,
            List<List<String>> layers,
            Map<Character, Predicate<BlockInWorld>> lookup,
            Map<Character, Supplier<BlockState>> blockProvider
    ) {
        SimpleMultiBlockAislePatternBuilder pattern = SimpleMultiBlockAislePatternBuilder.start();
        List<List<List<BlockState>>> blocks = new ArrayList<>();

        for (List<String> layerRows : layers) {
            pattern.aisle(layerRows.toArray(new String[0]));

            List<List<BlockState>> blockForAisle = new ArrayList<>();
            for (String row : layerRows) {
                blockForAisle.add(getStatesForString(row, blockProvider));
            }
            blocks.add(blockForAisle);
        }

        MiscUtil.structureMap.put(structureKey, blocks);

        lookup.forEach(pattern::where);
        blockProvider.forEach(pattern::block);

        return pattern;
    }

    private static List<BlockState> getStatesForString(String row, Map<Character, Supplier<BlockState>> map) {
        List<BlockState> states = new ArrayList<>(row.length());
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == ' ') {
                states.add(Blocks.AIR.defaultBlockState());
            } else if (map.containsKey(c)) {
                states.add(map.get(c).get());
            } else {
                // Preserve row width for markers like '*', so preview alignment stays correct.
                states.add(Blocks.AIR.defaultBlockState());
            }
        }
        return states;
    }
}
