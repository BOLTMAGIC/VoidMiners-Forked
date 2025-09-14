package com.leo.voidminers.multiblock;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.datagen.ModBlockTagGenerator;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.SolarSet;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.mangorage.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SolarPanelMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMiners.MODID, "solar_panels");

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_RUBETINE = createAccessiblePattern(
            VoidMiners.MODID + ":solar_rubetine",
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
                    '*', a -> a.getState().is(SolarSet.RUBETINE.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_1)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.RUBETINE.SOLAR_FRAME.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_AURANTIUM = createAccessiblePattern(
            VoidMiners.MODID + ":solar_aurantium",
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
                    '*', a -> a.getState().is(SolarSet.AURANTIUM.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_2),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.AURANTIUM.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_CITRINETINE = createAccessiblePattern(
            VoidMiners.MODID + ":solar_citrinetine",
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
                    '*', a -> a.getState().is(SolarSet.CITRINETINE.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_3),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.CITRINETINE.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_VERDIUM = createAccessiblePattern(
            VoidMiners.MODID + ":solar_verdium",
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
                    '*', a -> a.getState().is(SolarSet.VERDIUM.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_4),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.VERDIUM.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_AZURINE = createAccessiblePattern(
            VoidMiners.MODID + ":solar_azurine",
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
                    '*', a -> a.getState().is(SolarSet.AZURINE.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_5),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.AZURINE.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_CAERIUM = createAccessiblePattern(
            VoidMiners.MODID + ":solar_caerium",
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
                    '*', a -> a.getState().is(SolarSet.CAERIUM.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_6),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.CAERIUM.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_AMETHYSTINE = createAccessiblePattern(
            VoidMiners.MODID + ":solar_amethystine",
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
                    '*', a -> a.getState().is(SolarSet.AMETHYSTINE.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_7),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.AMETHYSTINE.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_ROSARIUM = createAccessiblePattern(
            VoidMiners.MODID + ":solar_rosarium",
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
                    '*', a -> a.getState().is(SolarSet.ROSARIUM.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_8),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.ROSARIUM.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder SOLAR_ULTIMATE = createAccessiblePattern(
            VoidMiners.MODID + ":solar_ultimate",
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
                    '*', a -> a.getState().is(SolarSet.ULTIMATE.SOLAR_PANEL_CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlockTagGenerator.PANELS),
                    'F', a -> a.getState().is(ModBlockTagGenerator.FRAME_9),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.ULTIMATE.SOLAR_FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );


    //TODO remove this hack when a better way to access the blocks is found
    public static SimpleMultiBlockAislePatternBuilder createAccessiblePattern(
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
        MANAGER.register("solar_rubetine", SOLAR_RUBETINE.build());
        MANAGER.register("solar_aurantium", SOLAR_AURANTIUM.build());
        MANAGER.register("solar_citrinetine", SOLAR_CITRINETINE.build());
        MANAGER.register("solar_verdium", SOLAR_VERDIUM.build());
        MANAGER.register("solar_azurine", SOLAR_AZURINE.build());
        MANAGER.register("solar_caerium", SOLAR_CAERIUM.build());
        MANAGER.register("solar_amethystine", SOLAR_AMETHYSTINE.build());
        MANAGER.register("solar_rosarium", SOLAR_ROSARIUM.build());
        MANAGER.register("solar_ultimate", SOLAR_ULTIMATE.build());
    }

}