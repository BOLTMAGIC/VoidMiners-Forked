package com.leo.voidminers.multiblock;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.datagen.ModBlockTagGenerator;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MinerMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMiners.MODID, "miners");

    public static final SimpleMultiBlockAislePatternBuilder RUBETINE = createAccessiblePattern(
        VoidMiners.MODID + ":rubetine",
        List.of(
            List.of(
                "     ",
                "     ",
                "  *  ",
                "     ",
                "     "
            ),
            List.of(
                "     ",
                "  F  ",
                " F F ",
                "  F  ",
                "     "
            ),
            List.of(
                "  F  ",
                "     ",
                "F   F",
                "     ",
                "  F  "
            ),
            List.of(
                " FFF ",
                "FPPPF",
                "FPPPF",
                "FPPPF",
                " FFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.RUBETINE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_1)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.RUBETINE.FRAME.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder AURANTIUM = createAccessiblePattern(
        VoidMiners.MODID + ":aurantium",
        List.of(
            List.of(
                "       ",
                "       ",
                "       ",
                "   *   ",
                "       ",
                "       ",
                "       "
            ),
            List.of(
                "       ",
                "   F   ",
                "   F   ",
                " FF FF ",
                "   F   ",
                "   F   ",
                "       "
            ),
            List.of(
                "   F   ",
                "       ",
                "       ",
                "F     F",
                "       ",
                "       ",
                "   F   "
            ),
            List.of(
                "   F   ",
                "       ",
                "       ",
                "F     F",
                "       ",
                "       ",
                "   F   "
            ),
            List.of(
                " FFFFF ",
                "FPPMPPF",
                "FPPPPPF",
                "FPPPPPF",
                "FPPPPPF",
                "FPPMPPF",
                " FFFFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.AURANTIUM.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_2),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.AURANTIUM.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder CITRINETINE = createAccessiblePattern(
        VoidMiners.MODID + ":citrinetine",
        List.of(
            List.of(
                "       ",
                "       ",
                "       ",
                "   *   ",
                "       ",
                "       ",
                "       "
            ),
            List.of(
                "       ",
                "       ",
                "   F   ",
                "  F F  ",
                "   F   ",
                "       ",
                "       "
            ),
            List.of(
                "       ",
                "   F   ",
                "       ",
                " F   F ",
                "       ",
                "   F   ",
                "       "
            ),
            List.of(
                "   F   ",
                "       ",
                "       ",
                "F     F",
                "       ",
                "       ",
                "   F   "
            ),
            List.of(
                "   F   ",
                "       ",
                "       ",
                "F     F",
                "       ",
                "       ",
                "   F   "
            ),
            List.of(
                " FFFFF ",
                "FMPPPMF",
                "FPPPPPF",
                "FPPPPPF",
                "FPPPPPF",
                "FMPPPMF",
                " FFFFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.CITRINETINE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_3),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.CITRINETINE.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder VERDIUM = createAccessiblePattern(
        VoidMiners.MODID + ":verdium",
        List.of(
            List.of(
                "         ",
                "         ",
                "         ",
                "         ",
                "    *    ",
                "         ",
                "         ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "         ",
                "    F    ",
                "    F    ",
                "  FF FF  ",
                "    F    ",
                "    F    ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "    F    ",
                "         ",
                "         ",
                " F     F ",
                "         ",
                "         ",
                "    F    ",
                "         "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "  FFFFF  ",
                " FMPMPMF ",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                " FMPMPMF ",
                "  FFFFF  "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.VERDIUM.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_4),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.VERDIUM.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder AZURINE = createAccessiblePattern(
        VoidMiners.MODID + ":azurine",
        List.of(
            List.of(
                "         ",
                "         ",
                "         ",
                "         ",
                "    *    ",
                "         ",
                "         ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "    F    ",
                "    F    ",
                "    F    ",
                " FFF FFF ",
                "    F    ",
                "    F    ",
                "    F    ",
                "         "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "  FFFFF  ",
                " FMPPPMF ",
                "FMPPPPPMF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FMPPPPPMF",
                " FMPPPMF ",
                "  FFFFF  "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.AZURINE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_5),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.AZURINE.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder CAERIUM = createAccessiblePattern(
        VoidMiners.MODID + ":caerium",
        List.of(
            List.of(
                "         ",
                "         ",
                "         ",
                "         ",
                "    *    ",
                "         ",
                "         ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "         ",
                "         ",
                "    F    ",
                "   F F   ",
                "    F    ",
                "         ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "         ",
                "    F    ",
                "         ",
                "  F   F  ",
                "         ",
                "    F    ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "    F    ",
                "         ",
                "         ",
                " F     F ",
                "         ",
                "         ",
                "    F    ",
                "         "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "  FFFFF  ",
                " FMMMMMF ",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                "FPPPPPPPF",
                " FMMMMMF ",
                "  FFFFF  "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.CAERIUM.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_6),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.CAERIUM.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder AMETHYSTINE = createAccessiblePattern(
        VoidMiners.MODID + ":amethystine",
        List.of(
            List.of(
                "         ",
                "         ",
                "         ",
                "         ",
                "    *    ",
                "         ",
                "         ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "         ",
                "    F    ",
                "    F    ",
                "  FF FF  ",
                "    F    ",
                "    F    ",
                "         ",
                "         "
            ),
            List.of(
                "         ",
                "    F    ",
                "         ",
                "         ",
                " F     F ",
                "         ",
                "         ",
                "    F    ",
                "         "
            ),
            List.of(
                "         ",
                "    F    ",
                "         ",
                "         ",
                " F     F ",
                "         ",
                "         ",
                "    F    ",
                "         "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "    F    ",
                "         ",
                "         ",
                "         ",
                "F       F",
                "         ",
                "         ",
                "         ",
                "    F    "
            ),
            List.of(
                "   FFF   ",
                "  FMMMF  ",
                " FPPPPPF ",
                "FMPPPPPMF",
                "FMPPPPPMF",
                "FMPPPPPMF",
                " FPPPPPF ",
                "  FMMMF  ",
                "   FFF   "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.AMETHYSTINE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_7),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.AMETHYSTINE.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder ROSARIUM = createAccessiblePattern(
        VoidMiners.MODID + ":rosarium",
        List.of(
            List.of(
                "       ",
                "       ",
                "       ",
                "   *   ",
                "       ",
                "       ",
                "       "
            ),
            List.of(
                "       ",
                "       ",
                "   F   ",
                "  F F  ",
                "   F   ",
                "       ",
                "       "
            ),
            List.of(
                "       ",
                "   F   ",
                "       ",
                " F   F ",
                "       ",
                "   F   ",
                "       "
            ),
            List.of(
                "   F   ",
                "       ",
                "       ",
                "F     F",
                "       ",
                "       ",
                "   F   "
            ),
            List.of(
                "  FFF  ",
                " FPMPF ",
                "FPMPMPF",
                "FMPPPMF",
                "FPMPMPF",
                " FPMPF ",
                "  FFF  "
            ),
            List.of(
                " F   F ",
                "F     F",
                "       ",
                "       ",
                "       ",
                "F     F",
                " F   F "
            ),
            List.of(
                "F     F",
                "       ",
                "       ",
                "       ",
                "       ",
                "       ",
                "F     F"
            ),
            List.of(
                " FFFFF ",
                "FMPMPMF",
                "FPPPPPF",
                "FMPPPMF",
                "FPPPPPF",
                "FMPMPMF",
                " FFFFF "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.ROSARIUM.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_8),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.ROSARIUM.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );

    public static final SimpleMultiBlockAislePatternBuilder ULTIMATE = createAccessiblePattern(
        VoidMiners.MODID + ":ultimate",
        List.of(
            List.of(
                "           ",
                "           ",
                "           ",
                "           ",
                "           ",
                "     *     ",
                "           ",
                "           ",
                "           ",
                "           ",
                "           "
            ),
            List.of(
                "           ",
                "           ",
                "           ",
                "     F     ",
                "     F     ",
                "   FF FF   ",
                "     F     ",
                "     F     ",
                "           ",
                "           ",
                "           "
            ),
            List.of(
                "           ",
                "           ",
                "     F     ",
                "           ",
                "           ",
                "  F     F  ",
                "           ",
                "           ",
                "     F     ",
                "           ",
                "           "
            ),
            List.of(
                "           ",
                "     F     ",
                "           ",
                "           ",
                "           ",
                " F       F ",
                "           ",
                "           ",
                "           ",
                "     F     ",
                "           "
            ),
            List.of(
                "     F     ",
                "           ",
                "           ",
                "           ",
                "           ",
                "F         F",
                "           ",
                "           ",
                "           ",
                "           ",
                "     F     "
            ),
            List.of(
                "     F     ",
                "           ",
                "           ",
                "           ",
                "           ",
                "F         F",
                "           ",
                "           ",
                "           ",
                "           ",
                "     F     "
            ),
            List.of(
                "     F     ",
                "           ",
                "           ",
                "           ",
                "           ",
                "F         F",
                "           ",
                "           ",
                "           ",
                "           ",
                "     F     "
            ),
            List.of(
                "   FFFFF   ",
                "  FMMMMMF  ",
                " FMPPPPPMF ",
                "FMPPPPPPPMF",
                "FMPPPPPPPMF",
                "FMPPPPPPPMP",
                "FMPPPPPPPMF",
                "FMPPPPPPPMF",
                " FMPPPPPMF ",
                "  FMMMMMF  ",
                "   FFFFF   "
            ),
            List.of(
                " F       F ",
                "F         F",
                "           ",
                "           ",
                "           ",
                "           ",
                "           ",
                "           ",
                "           ",
                "F         F",
                " F       F "
            )
        ),
        Map.of(
            '*', a -> a.getState().is(CrystalSet.ULTIMATE.MINER_CONTROLLER.get()),
            'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
            'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_9),
            'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
        ),
        Map.of(
            'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
            'F', CrystalSet.ULTIMATE.FRAME.get()::defaultBlockState,
            'M', ModBlocks.NULL_MOD.get()::defaultBlockState
        )
    );


    //TODO remove this hack when a better way to access the blocks is found
    public static SimpleMultiBlockAislePatternBuilder createAccessiblePattern(String structure, List<List<String>> stringPattern, Map<Character, Predicate<BlockInWorld>> lookup, Map<Character, Supplier<BlockState>> blockProvider) {
        SimpleMultiBlockAislePatternBuilder pattern = SimpleMultiBlockAislePatternBuilder.start();
        List<List<List<BlockState>>> blocks = new ArrayList<>();

        for (List<String> strings : stringPattern) {
            pattern.aisle(strings.toArray(new String[]{}));

            List<List<BlockState>> blockForAisle = new ArrayList<>();

            for (String s : strings) {
                blockForAisle.add(
                    getStatesForString(s, blockProvider)
                );
            }

            blocks.add(blockForAisle);
        }

        MiscUtil.structureMap.put(
            structure,
            blocks
        );

        lookup.forEach(pattern::where);

        blockProvider.forEach(pattern::block);

        return pattern;
    }

    private static List<BlockState> getStatesForString(String s, Map<Character, Supplier<BlockState>> map) {
        List<BlockState> toReturn = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                toReturn.add(
                    Blocks.AIR.defaultBlockState()
                );
            } else {
                if (map.containsKey(c)) {
                    toReturn.add(
                        map.get(c).get()
                    );
                }
            }


        }

        return toReturn;
    }


    public static void init() {
        MANAGER.register("rubetine", RUBETINE.build());
        MANAGER.register("aurantium", AURANTIUM.build());
        MANAGER.register("citrinetine", CITRINETINE.build());
        MANAGER.register("verdium", VERDIUM.build());
        MANAGER.register("azurine", AZURINE.build());
        MANAGER.register("caerium", CAERIUM.build());
        MANAGER.register("amethystine", AMETHYSTINE.build());
        MANAGER.register("rosarium", ROSARIUM.build());
        MANAGER.register("ultimate", ULTIMATE.build());
    }

}
