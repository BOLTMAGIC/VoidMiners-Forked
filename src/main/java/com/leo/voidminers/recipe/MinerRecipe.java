package com.leo.voidminers.recipe;

import com.google.gson.JsonObject;
import com.leo.voidminers.VoidMiners;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MinerRecipe implements Recipe<Container> {
    private final WeightedStack output;
    private final int minTier;
    private final boolean allowHigherTiers;
    private final ResourceLocation id;
    private final ResourceKey<Level> dimension;

    public MinerRecipe(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceLocation id, ResourceKey<Level> dimension) {
        this.output = output;
        this.minTier = minTier;
        this.allowHigherTiers = allowHigherTiers;
        this.id = id;
        this.dimension = dimension;
    }

    public boolean allowHigherTiers() {
        return allowHigherTiers;
    }

    public WeightedStack output() {
        return output.copy();
    }

    public ResourceKey<Level> dimension() {
        return dimension;
    }

    public int minTier() {
        return minTier;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return !pLevel.isClientSide();
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.stack.isEmpty() ? ItemStack.EMPTY : output.stack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MinerRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "miner";
    }

    public static class Serializer implements RecipeSerializer<MinerRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MinerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonObject jsonOutput = GsonHelper.getAsJsonObject(pSerializedRecipe, "output");

            ItemStack stack;

            if(jsonOutput.get("item").isJsonObject()) {
                stack = CraftingHelper.getItemStack(jsonOutput.getAsJsonObject("item"), true, true);
            } else {
                stack = ForgeRegistries.ITEMS.getValue(
                    ResourceLocation.parse(jsonOutput.get("item").getAsString())
                ).getDefaultInstance();
            }

            WeightedStack output = new WeightedStack(
                stack,
                GsonHelper.getAsDouble(jsonOutput, "weight", 1.0)
            );

            int minTier = GsonHelper.getAsInt(pSerializedRecipe, "minTier");

            boolean allowHigherTiers = true;

            if(pSerializedRecipe.has("allowHigherTiers")) {
                allowHigherTiers = GsonHelper.getAsBoolean(pSerializedRecipe, "allowHigherTiers");
            }

            String jsonDim = GsonHelper.getAsString(pSerializedRecipe, "dimension", "minecraft:overworld");

            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(jsonDim));

            return new MinerRecipe(output, minTier, allowHigherTiers, pRecipeId, dimension);
        }

        @Override
        public @Nullable MinerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            ItemStack stack = pBuffer.readItem();
            double weight = pBuffer.readDouble();

            WeightedStack output = new WeightedStack(stack, weight);

            int minTier = pBuffer.readInt();
            boolean allowHigherTiers = pBuffer.readBoolean();

            ResourceKey<Level> dimension = pBuffer.readResourceKey(Registries.DIMENSION);

            return new MinerRecipe(output, minTier, allowHigherTiers, pRecipeId, dimension);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MinerRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.output.stack);
            pBuffer.writeDouble(pRecipe.output.weight);

            pBuffer.writeInt(pRecipe.minTier);
            pBuffer.writeBoolean(pRecipe.allowHigherTiers);

            pBuffer.writeResourceKey(pRecipe.dimension);
        }
    }

    public static class Builder implements RecipeBuilder, FinishedRecipe {
        private final WeightedStack output;
        private final int minTier;
        private final boolean allowHigherTiers;
        private final ResourceLocation id;
        private final ResourceKey<Level> dimension;

        private Builder(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceLocation id, ResourceKey<Level> dimension) {
            this.output = output;
            this.minTier = minTier;
            this.id = id;
            this.allowHigherTiers = allowHigherTiers;
            this.dimension = dimension;
        }

        public static Builder builder(WeightedStack output, int minTier, ResourceKey<Level> dimension) {
            return Builder.builder(output, minTier, true, dimension);
        }

        public static Builder builder(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceKey<Level> dimension) {
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, dimension.location().getPath() + "/tier" + minTier + "_miner/" + ForgeRegistries.ITEMS.getKey(output.stack.getItem()).getPath());
            return new Builder(output, minTier, allowHigherTiers, recipeId, dimension);
        }

        @Override
        public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String pGroupName) {
            return this;
        }

        @Override
        public @NotNull Item getResult() {
            return this.output.stack.getItem();
        }

        @Override
        public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation recipeId) {
            finishedRecipeConsumer.accept(this);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonObject outputItem = new JsonObject();

            outputItem.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output.stack.getItem()).toString());

            if (this.output.stack.getCount() != 1) {
                outputItem.addProperty("count", this.output.stack.getCount());
            }
            if (this.output.stack.getTag() != null) {
                outputItem.addProperty("nbt", this.output.stack.getTag().toString());
            }

            outputItem.addProperty("weight", this.output.weight);

            json.add("output", outputItem);

            json.addProperty("minTier", this.minTier);
            json.addProperty("allowHigherTiers", this.allowHigherTiers);
            json.addProperty("dimension", dimension.location().toString());
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
