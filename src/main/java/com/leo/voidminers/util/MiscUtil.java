package com.leo.voidminers.util;

import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.SolarSet;
import net.minecraft.locale.Language;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    public static Map<String, Integer> tierMap = new HashMap<>();
    public static Map<String, Integer> colorMap = new HashMap<>();

    public static Map<String, List<List<List<BlockState>>>> structureMap = new HashMap<>();
    // Map storing controller anchor coordinates for each structure key: {x, y, z}
    public static Map<String, int[]> controllerAnchorMap = new HashMap<>();

    static {
        for (int i = 0; i < CrystalSet.sets().size(); i++) {
            CrystalSet set = CrystalSet.sets().get(i);

            tierMap.put(
                set.name,
                i + 1
            );
        }
        
        // Add solar panel tiers (same tier system as miners)
        for (int i = 0; i < SolarSet.sets().size(); i++) {
            SolarSet set = SolarSet.sets().get(i);

            tierMap.put(
                "solar_" + set.name,
                i + 1
            );
        }

        colorMap.put(
            "rubetine",
            0xFFFF0000
        );

        colorMap.put(
            "aurantium",
            0xFFFFAA00
        );

        colorMap.put(
            "citrinetine",
            0xFFFFFF00
        );

        colorMap.put(
            "verdium",
            0xFF00FF00
        );

        colorMap.put(
            "azurine",
            0xFF00FFFF
        );

        colorMap.put(
            "caerium",
            0xFF0000FF
        );

        colorMap.put(
            "amethystine",
            0xFFAA00FF
        );

        colorMap.put(
            "rosarium",
            0xFFFF00FF
        );

        colorMap.put(
            "ultimate",
            0xFFD700FF
        );
    }


    public static Map<String, Integer> getNeededBlocks(List<List<List<BlockState>>> structure) {
        Map<String, Integer> blocks = new HashMap<>();

        for (List<List<BlockState>> l1 : structure) {
            for (List<BlockState> l2 : l1) {
                for (BlockState state : l2) {
                    if (!state.getBlock().equals(Blocks.AIR)) {
                        String name = Language.getInstance().getOrDefault(state.getBlock().getDescriptionId());

                        if (!blocks.containsKey(name)) {
                            blocks.put(name, 1);
                        } else {
                            //noinspection DataFlowIssue
                            blocks.compute(name, (k, i) -> i + 1);
                        }
                    }
                }
            }
        }

        return blocks;
    }
}
