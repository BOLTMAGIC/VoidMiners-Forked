package com.leo.voidminers.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradeItem extends Item {
    private final String tooltipKey;

    public UpgradeItem(String tooltipKey, Properties properties) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltip, pIsAdvanced);

        // Read the current config values at tooltip-time so changes in the config are reflected immediately
        com.leo.voidminers.config.ConfigLoader cfg = com.leo.voidminers.config.ConfigLoader.getInstance();

        int slots = switch (tooltipKey) {
            case "tooltip.voidminers.upgrade_max_storage_t1" -> cfg.UPGRADE_T1_SLOTS;
            case "tooltip.voidminers.upgrade_max_storage_t2" -> cfg.UPGRADE_T2_SLOTS;
            case "tooltip.voidminers.upgrade_max_storage_t3" -> cfg.UPGRADE_T3_SLOTS;
            default -> -1;
        };

        if (slots >= 0) {
            // Use the translatable string with an argument so the language file can contain a placeholder
            pTooltip.add(Component.translatable(tooltipKey, slots));
        } else {
            // Fallback to original behavior
            pTooltip.add(Component.translatable(tooltipKey));
        }
    }
}