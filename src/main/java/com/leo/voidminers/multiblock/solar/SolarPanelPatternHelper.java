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

        // Find controller '*' position in the original pattern (before reversal)
        // stringPattern is layers (y increases), each layer is list of rows (z), each row is x positions
        int controllerX = -1, controllerY = -1, controllerZ = -1;
        for (int y = 0; y < stringPattern.size(); y++) {
            List<String> layer = stringPattern.get(y);
            for (int z = 0; z < layer.size(); z++) {
                String row = layer.get(z);
                int idx = row.indexOf('*');
                if (idx >= 0) {
                    controllerX = idx;
                    controllerY = y;
                    controllerZ = z;
                    break;
                }
            }
            if (controllerX >= 0) break;
        }

        // The renderer uses reversed layering (we reverse when creating blocks), so convert Y accordingly
        List<List<String>> reversed = new ArrayList<>(stringPattern);
        Collections.reverse(reversed);
        if (controllerX >= 0) {
            int convertedY = reversed.size() - 1 - controllerY;
            // store as [x, y, z]
            MiscUtil.controllerAnchorMap.put(structure, new int[]{controllerX, convertedY, controllerZ});
        }

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
        List<BlockState> toReturn = new ArrayList<>(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                toReturn.add(Blocks.AIR.defaultBlockState());
            } else if (map.containsKey(c)) {
                toReturn.add(map.get(c).get());
            } else {
                // Preserve row width for markers like '*', so preview alignment stays correct.
                toReturn.add(Blocks.AIR.defaultBlockState());
            }
        }

        return toReturn;
    }
}
