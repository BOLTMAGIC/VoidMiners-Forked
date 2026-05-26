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
import net.minecraft.ChatFormatting;
import com.leo.voidminers.config.ConfigLoader;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull RecipeType<MinerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui." + VoidMiners.MODID + ".miner", tier);
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MinerRecipe minerRecipe, @NotNull IFocusGroup iFocusGroup) {
        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            4,
            -1
        ).addItemStack(minerRecipe.output().stack);
    }

    @Override
    public void draw(MinerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // compute percent chance based on total weights for the recipe's dimension
        double total = JeiPlugin.getTotalWeightForDimension(recipe.dimension().location(), this.tier);
        double percent;
        if (total == 0.0) {
            percent = 0.0;
        } else {
            double weight = recipe.output().weight;
            // If this item accounts for (effectively) the entire total (e.g. only item in the dimension),
            // avoid rounding small floating errors producing a tiny displayed percentage like "<0.001%".
            // Use a relative comparison to detect equality within a small epsilon.
            double relDiff = Math.abs(total - weight) / Math.max(Math.abs(total), Math.abs(weight));
            if (weight > 0.0 && relDiff <= 1e-9) {
                percent = 100.0;
            } else {
                percent = (weight / total) * 100.0;
            }
        }
        String percentStr = formatPercent(percent);

        // If color-blind mode is enabled, remove all colors and use the default text color.
        boolean cb = ConfigLoader.getInstance().COLOR_BLIND_PERCENT;
        Component percentComponent;
        if (cb) {
            // Color-blind mode: no coloring at all
            percentComponent = Component.literal(percentStr + "%");
        } else {
            // choose color based on percent ranges
            ChatFormatting color;
            // default palette
            if (percent <= 0.0) {
                color = ChatFormatting.DARK_RED;
            } else if (percent <= 10.0) {
                color = ChatFormatting.GOLD; // orange-like
            } else if (percent <= 25.0) {
                color = ChatFormatting.YELLOW;
            } else if (percent <= 50.0) {
                color = ChatFormatting.GREEN;
            } else {
                color = ChatFormatting.DARK_GREEN;
            }

            // include percent sign inside the styled component so the '%' is colored as well
            percentComponent = Component.literal(percentStr + "%").withStyle(color);
        }

        // For JEI we display only the percent Component (centered). The dimension icon will be placed at the far right.
        Component displayComponent = percentComponent;
        String dimensionName = recipe.dimension().location().toLanguageKey();

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(VoidMiners.MODID, "textures/gui/icon/" + getDimensionIcon(recipe.dimension()) + ".png");

        Font font = Minecraft.getInstance().font;

        // Calculate text width and center percent within the category background (width = 125)
        int textWidth = font.width(displayComponent);
        int backgroundWidth = 125;
        int percentX = Math.max(0, (backgroundWidth - textWidth) / 2);

        // Place the dimension icon at the far right with small padding
        int iconX = backgroundWidth - 16 - 4; // icon width 16, padding 4

        // Draw percent (may be colored) centered
        guiGraphics.drawString(font, displayComponent, percentX, 4, 0xFFFFFFFF);
        guiGraphics.blit(
            texture,
            iconX,
            -1,
            0,
            0,
            16,
            16,
            16,
            16
        );

        if (!isHovering(mouseX, mouseY, iconX, 0, iconX + 16, 16)) {
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

    public static String formatPercent(double number) {
        if (number == 0.0) {
            return "0";
        }
        // Use BigDecimal with MathContext(3) to round to 3 significant digits as requested by the user.
        java.math.BigDecimal bd = new java.math.BigDecimal(number);
        java.math.BigDecimal bdRounded = bd.round(new java.math.MathContext(3));

        // If rounding would produce zero (tiny positive values), keep the special marker
        if (bdRounded.compareTo(java.math.BigDecimal.ZERO) == 0 && bd.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return "<0.001";
        }

        // Return the rounded value as string. Use toString() (may use exponential notation for very small numbers).
        return bdRounded.toString();
    }

    public static String getDimensionIcon(ResourceKey<Level> dimension) {
        return dimension.location().toString().replace(':', '.');
    }
}
