package com.leo.voidminers.compat.jei;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.recipe.MinerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    List<MinerCategory> tiers = new ArrayList<>();
    // Cached total weights per tier. Each tier maps dimension ResourceLocation -> summed weights for that tier.
    // Key: tier index (0-based, corresponds to the index used when registering categories)
    public static final Map<Integer, Map<ResourceLocation, Double>> TOTAL_WEIGHTS_BY_TIER = new HashMap<>();

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        List<CrystalSet> sets = CrystalSet.sets();
        tiers = new ArrayList<>();
        for (int i = 0; i < sets.size(); i++) {
            CrystalSet set = sets.get(i);
            tiers.add(
                new MinerCategory(
                    registration.getJeiHelpers().getGuiHelper(),
                    set.MINER_CONTROLLER.get(),
                    i + 1
                )
            );
        }

        registration.addRecipeCategories(
            tiers.toArray(new MinerCategory[]{})
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        List<MinerRecipe> minerRecipes = manager.getAllRecipesFor(MinerRecipe.Type.INSTANCE);

        // Recompute totals per tier inside addRecipeToTier (per-tier totals are dependent on which recipes
        // are visible to each tier). Clear previous cached data and let addRecipeToTier populate the map.
        TOTAL_WEIGHTS_BY_TIER.clear();

        for (int i = 0; i < tiers.size(); i++) {
            addRecipeToTier(i, minerRecipes, registration);
        }
    }

    /**
     * Get the total summed weight for the given dimension within the given tier number.
     * @param dimLoc dimension ResourceLocation
     * @param tierNumber the MinerCategory.tier value (1-based)
     * @return summed weight for that dimension in that tier
     */
    public static double getTotalWeightForDimension(ResourceLocation dimLoc, int tierNumber) {
        int idx = Math.max(0, tierNumber - 1);
        Map<ResourceLocation, Double> map = TOTAL_WEIGHTS_BY_TIER.get(idx);
        if (map == null) return 0.0;
        return map.getOrDefault(dimLoc, 0.0);
    }

    public void addRecipeToTier(int tier, List<MinerRecipe> recipes, IRecipeRegistration registration) {

        Comparator<MinerRecipe> alphabetical = Comparator.comparing(MinerRecipe::dimension);
        Comparator<MinerRecipe> weight = Comparator.comparing((recipe) -> recipe.output().weight);
        weight = weight.reversed();

        List<MinerRecipe> foundRecipes = recipes.stream().filter(
            recipe -> {
                if(recipe.allowHigherTiers()){
                    return recipe.minTier() <= tier + 1;
                } else {
                    return recipe.minTier() == tier + 1;
                }
            }
        ).sorted(
            alphabetical.thenComparing(weight))
            .toList();

        // Compute total weights per-dimension for this tier and cache it
        Map<ResourceLocation, Double> totals = new HashMap<>();
        for (MinerRecipe r : foundRecipes) {
            ResourceLocation dl = r.dimension().location();
            double current = totals.getOrDefault(dl, 0.0);
            totals.put(dl, current + r.output().weight);
        }
        TOTAL_WEIGHTS_BY_TIER.put(tier, totals);

        registration.addRecipes(
            tiers.get(tier).getRecipeType(),
            foundRecipes
        );
    }


    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        List<CrystalSet> allSets = CrystalSet.sets();
        for (int i = 0; i < allSets.size(); i++) {
            CrystalSet set = allSets.get(i);
            registration.addRecipeCatalyst(
                set.MINER_CONTROLLER.get().asItem().getDefaultInstance(),
                tiers.get(i).getRecipeType()
            );
        }
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
    }
}
