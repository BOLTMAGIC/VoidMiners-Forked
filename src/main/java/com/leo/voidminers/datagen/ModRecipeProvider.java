package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(
                RecipeCategory.MISC,
                ModBlocks.STRUCTURE_PANEL.get(),
                1
            )
            .pattern("IGI")
            .pattern("GRG")
            .pattern("IGI")
            .define('R', Items.REDSTONE)
            .define('G', Items.GOLD_NUGGET)
            .define('I', Items.IRON_INGOT)
            .unlockedBy("hasItem", has(Items.IRON_INGOT))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(
                RecipeCategory.MISC,
                ModBlocks.FRAME_BASE.get(),
                1
            )
            .pattern("GIG")
            .pattern("ISI")
            .pattern("GIG")
            .define('S', ModBlocks.STRUCTURE_PANEL.get())
            .define('G', Items.GOLD_NUGGET)
            .define('I', Items.IRON_INGOT)
            .unlockedBy("hasItem", has(Items.IRON_INGOT))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(
                RecipeCategory.MISC,
                ModBlocks.NULL_MOD.get(),
                1
            )
            .pattern("OIO")
            .pattern("IGI")
            .pattern("OIO")
            .define('O', Blocks.OBSIDIAN)
            .define('G', ModBlocks.STRUCTURE_PANEL.get())
            .define('I', Items.IRON_INGOT)
            .unlockedBy("hasItem", has(Items.IRON_INGOT))
            .save(pWriter);

        ShapelessRecipeBuilder.shapeless(
                RecipeCategory.MISC,
                ModBlocks.GLASS_PANEL.get(),
                1
            )
            .requires(ModBlocks.STRUCTURE_PANEL.get())
            .requires(Tags.Items.GLASS)
            .unlockedBy("hasItem", has(ModBlocks.STRUCTURE_PANEL.get()))
            .save(pWriter);

        ShapedRecipeBuilder.shaped(
                RecipeCategory.MISC,
                CrystalSet.RUBETINE.CRYSTAL.get(),
                4
            )
            .pattern("RBR")
            .pattern("BDB")
            .pattern("RBR")
            .define('R', Items.REDSTONE)
            .define('B', Items.BLAZE_POWDER)
            .define('D', Items.DIAMOND)
            .unlockedBy("hasItem", has(Items.DIAMOND))
            .save(pWriter);

        List<CrystalSet> allSets = CrystalSet.sets();
        for (int i = 0; i < allSets.size(); i++) {
            CrystalSet set = allSets.get(i);

            // Skip sets without crystals (like ultimate set)
            if (set.CRYSTAL == null) {
                continue;
            }

            ShapedRecipeBuilder.shaped(
                    RecipeCategory.MISC,
                    set.MINER_CONTROLLER.get(),
                    1
                )
                .pattern("GGG")
                .pattern("GCG")
                .pattern("BOB")
                .define('G', Tags.Items.GLASS)
                .define('B', set.CRYSTAL_BLOCK.get())
                .define('O', Blocks.OBSIDIAN)
                .define('C', i > 0 ? allSets.get(i - 1).MINER_CONTROLLER.get() : Items.DIAMOND)
                .unlockedBy("hasItem", has(set.CRYSTAL_BLOCK.get()))
                .save(pWriter);

            ShapedRecipeBuilder.shaped(
                    RecipeCategory.MISC,
                    set.SPEED_MOD.get(),
                    1
                )
                .pattern("CcC")
                .pattern("cMc")
                .pattern("CcC")
                .define('C', set.CRYSTAL.get())
                .define('c', Items.SUGAR)
                .define('M', i > 0 ? allSets.get(i - 1).SPEED_MOD.get() : ModBlocks.NULL_MOD.get())
                .unlockedBy("hasItem", has(ModBlocks.NULL_MOD.get()))
                .save(pWriter);

            ShapedRecipeBuilder.shaped(
                    RecipeCategory.MISC,
                    set.FRAME.get(),
                    1
                )
                .pattern("COC")
                .pattern("OFO")
                .pattern("COC")
                .define('C', set.CRYSTAL.get())
                .define('O', Blocks.OBSIDIAN)
                .define('F', i > 0 ? allSets.get(i - 1).FRAME.get() : ModBlocks.FRAME_BASE.get())
                .unlockedBy("hasItem", has(ModBlocks.FRAME_BASE.get()))
                .save(pWriter);

            ShapedRecipeBuilder.shaped(
                    RecipeCategory.MISC,
                    set.ENERGY_MOD.get(),
                    1
                )
                .pattern("CcC")
                .pattern("cMc")
                .pattern("CcC")
                .define('C', set.CRYSTAL.get())
                .define('c', Items.REDSTONE)
                .define('M', i > 0 ? allSets.get(i - 1).ENERGY_MOD.get() : ModBlocks.NULL_MOD.get())
                .unlockedBy("hasItem", has(ModBlocks.NULL_MOD.get()))
                .save(pWriter);

            ShapedRecipeBuilder.shaped(
                    RecipeCategory.MISC,
                    set.ITEM_MOD.get(),
                    1
                )
                .pattern("CcC")
                .pattern("cMc")
                .pattern("CcC")
                .define('C', set.CRYSTAL.get())
                .define('c', Items.DIAMOND)
                .define('M', i > 0 ? allSets.get(i - 1).ITEM_MOD.get() : ModBlocks.NULL_MOD.get())
                .unlockedBy("hasItem", has(ModBlocks.NULL_MOD.get()))
                .save(pWriter);

            ShapelessRecipeBuilder.shapeless(
                    RecipeCategory.MISC,
                    set.CRYSTAL_BLOCK.get(),
                    1
                )
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .requires(set.CRYSTAL.get())
                .unlockedBy("hasItem", has(set.CRYSTAL.get()))
                .save(pWriter);

            ShapelessRecipeBuilder.shapeless(
                    RecipeCategory.MISC,
                    set.CRYSTAL.get(),
                    9
                )
                .requires(set.CRYSTAL_BLOCK.get())
                .unlockedBy("hasItem", has(set.CRYSTAL_BLOCK.get()))
                .save(pWriter, ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, set.name + "_crystal_from_block"));
        }

        List<WeightedStack> OVERWORLD = List.of(
            new WeightedStack(
                Items.EMERALD_ORE,
                1f
            ),
            new WeightedStack(
                Items.DIAMOND_ORE,
                2f
            ),
            new WeightedStack(
                Items.GOLD_ORE,
                4f
            ),
            new WeightedStack(
                Items.REDSTONE_ORE,
                6f
            ),
            new WeightedStack(
                Items.LAPIS_ORE,
                6f
            ),
            new WeightedStack(
                Items.IRON_ORE,
                8f
            ),
            new WeightedStack(
                Items.COPPER_ORE,
                12f
            ),
            new WeightedStack(
                Items.COAL_ORE,
                16f
            ),
            new WeightedStack(
                CrystalSet.RUBETINE.CRYSTAL.get(),
                2f
            ),
            new WeightedStack(
                CrystalSet.AURANTIUM.CRYSTAL.get(),
                2f
            )
        );

        for (WeightedStack stack : OVERWORLD) {
            MinerRecipe.Builder.builder(
                stack,
                1,
                Level.OVERWORLD
            ).save(pWriter);
        }

        List<WeightedStack> NETHER = List.of(
            new WeightedStack(
                Items.NETHER_QUARTZ_ORE,
                10f
            ),
            new WeightedStack(
                Items.NETHER_GOLD_ORE,
                5f
            ),
            new WeightedStack(
                Items.ANCIENT_DEBRIS,
                0.1f
            ),
            new WeightedStack(
                CrystalSet.RUBETINE.CRYSTAL.get(),
                2f
            ),
            new WeightedStack(
                CrystalSet.AURANTIUM.CRYSTAL.get(),
                2f
            )
        );

        for (WeightedStack stack : NETHER) {
            MinerRecipe.Builder.builder(
                stack,
                1,
                Level.NETHER
            ).save(pWriter);
        }

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.CITRINETINE.CRYSTAL.get(),
                2f
            ),
            2,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.CITRINETINE.CRYSTAL.get(),
                4f
            ),
            2,
            Level.NETHER
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.VERDIUM.CRYSTAL.get(),
                2f
            ),
            3,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.VERDIUM.CRYSTAL.get(),
                4f
            ),
            3,
            Level.NETHER
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.AZURINE.CRYSTAL.get(),
                2f
            ),
            4,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.AZURINE.CRYSTAL.get(),
                4f
            ),
            4,
            Level.NETHER
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.CAERIUM.CRYSTAL.get(),
                2f
            ),
            5,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.CAERIUM.CRYSTAL.get(),
                4f
            ),
            5,
            Level.NETHER
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.AMETHYSTINE.CRYSTAL.get(),
                2f
            ),
            6,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.AMETHYSTINE.CRYSTAL.get(),
                4f
            ),
            6,
            Level.NETHER
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.ROSARIUM.CRYSTAL.get(),
                2f
            ),
            7,
            Level.OVERWORLD
        ).save(pWriter);

        MinerRecipe.Builder.builder(
            new WeightedStack(
                CrystalSet.ROSARIUM.CRYSTAL.get(),
                4f
            ),
            7,
            Level.NETHER
        ).save(pWriter);
    }

}
