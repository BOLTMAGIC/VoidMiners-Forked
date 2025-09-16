package com.leo.voidminers.compat.jei;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.recipe.MinerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class MinerCategory implements IRecipeCategory<MinerRecipe> {
    public final ResourceLocation UID;
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "textures/gui/jei_background.png");

    public RecipeType<MinerRecipe> RECIPE_TYPE;

    private final IDrawable background;
    private final IDrawable icon;
    public final Block blockIcon;
    public final int tier;

    public MinerCategory(IGuiHelper guiHelper, Block blockIcon, int tier) {
        UID = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "miner/tier" + tier + "_miner");
        RECIPE_TYPE = new RecipeType<>(UID, MinerRecipe.class);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 125, 15);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, blockIcon.asItem().getDefaultInstance());
        this.blockIcon = blockIcon;
        this.tier = tier;
    }

    @Override
    public RecipeType<MinerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + VoidMiners.MODID + ".miner", tier);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MinerRecipe minerRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            4,
            -1
        ).addItemStack(minerRecipe.output().stack);
    }

    @Override
    public void draw(MinerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Component weight = Component.translatable("tooltip." + VoidMiners.MODID + ".structure.weight", customFormat(recipe.output().weight));
        String dimensionName = recipe.dimension().location().toLanguageKey();

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "textures/gui/icon/" + getDimensionIcon(recipe.dimension()) + ".png");

        Font font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, weight, 24, 4, 0xFFFFFFFF);
        guiGraphics.blit(
            texture,
            99,
            -1,
            0,
            0,
            16,
            16,
            16,
            16
        );

        if (!isHovering(mouseX, mouseY, 99, 0, 115, 16)) {
            return;
        }
        guiGraphics.renderTooltip(font, Component.translatable(dimensionName), (int) mouseX, (int) mouseY - 10);
    }

    public static boolean isHovering(double mouseX, double mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1
            && mouseX <= x2
            && mouseY >= y1
            && mouseY <= y2;
    }

    public static String customFormat(double number) {
        String numberStr = Double.toString(number);
        String[] parts = numberStr.split("\\.");

        String decimalPart = parts.length > 1 ? parts[1] : "";

        int lastNumber = 0;
        int checkNumber = 0;
        boolean checkNext = true;
        int zeroCount = 0;

        for (int i = 0; i < decimalPart.length(); i++) {
            if (decimalPart.charAt(i) != '0') {
                int currentCheck = Integer.parseInt(String.valueOf(decimalPart.charAt(i)));
                if (checkNext) {
                    lastNumber = currentCheck;
                    checkNext = false;
                } else {
                    checkNumber = currentCheck;
                    break;
                }
            } else {
                zeroCount++;
            }
        }

        if (checkNumber >= 6) {
            lastNumber++;
        }

        return parts[0] + "." + "0".repeat(Math.max(0, zeroCount)) + lastNumber;
    }

    public static String getDimensionIcon(ResourceKey<Level> dimension) {
        return dimension.location().toString().replace(':', '.');
    }
}
