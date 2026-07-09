package com.leo.voidminers.block.controller.entity;

import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.energy.ModEnergyStorage;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ControllerTooltipBuilder {

    private ControllerTooltipBuilder() {
    }

    static List<Component> build(ControllerBaseBE controller) {
        List<Component> tooltip = new ArrayList<>();
        ControllerRuntimeState runtimeState = controller.getRuntimeStateInternal();
        ModEnergyStorage energyHandler = controller.getEnergyHandlerInternal();
        Map<BlockInWorld, ConfigLoader.ModifierConfig> modifierMap = controller.getModifierMapInternal();
        Level level = controller.getLevel();
        String name = controller.getMinerNameInternal();
        ResourceLocation structure = controller.getStructure();

        if (runtimeState.isBlockedByDimension()) {
            String headerName = name != null ? name.toUpperCase() + " MINER" : "VOID MINER";
            tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(headerName).withStyle(getTierColor(name)))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.literal("⚠ STATUS: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("DISABLED IN THIS DIMENSION").withStyle(ChatFormatting.RED)));

            String dimensionId = level != null ? level.dimension().location().toString() : "unknown";
            tooltip.add(Component.literal("🌌 DIMENSION: ").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(dimensionId).withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.literal("🛠 CONFIG PATH: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal("config/void-miners.json5 → MINER_DIMENSION_SETTINGS").withStyle(ChatFormatting.WHITE)));
        }

        if (name == null) {
            tooltip.add(Component.literal("❓ STATUS: ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal("Miner tier not initialized").withStyle(ChatFormatting.GRAY)));
        }

        int energyStorage = energyHandler.getMaxEnergyStored();
        int currentEnergy = energyHandler.getEnergyStored();

        float energyMod = 1.0f;
        float speedMod = 1.0f;
        float itemMod = 1.0f;

        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            energyMod *= entry.getValue().energy();
            speedMod *= entry.getValue().speed();
            itemMod *= entry.getValue().item();
        }

        int extraSlots = 0;
        int maxExtraSlots = 0;
        String storageUpgradeDisplayName = "";

        ConfigLoader cfg = ConfigLoader.getInstance();
        // get the applied upgrade tier stored on the block (appliedUpgradeTier)
        int appliedTier = controller.getAppliedUpgradeTier();
        if (appliedTier == 3) {
            extraSlots = cfg.UPGRADE_T3_SLOTS;
            storageUpgradeDisplayName = "Storage Upgrade T3";
        } else if (appliedTier == 2) {
            extraSlots = cfg.UPGRADE_T2_SLOTS;
            storageUpgradeDisplayName = "Storage Upgrade T2";
        } else if (appliedTier == 1) {
            extraSlots = cfg.UPGRADE_T1_SLOTS;
            storageUpgradeDisplayName = "Storage Upgrade T1";
        }
        maxExtraSlots = cfg.UPGRADE_T3_SLOTS;

        if (controller.isWorkingInternal()) {
            tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor(name)))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.literal("⚡ STATUS: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("MINING ACTIVE").withStyle(ChatFormatting.GREEN)));

            String energyBar = getEnergyBar(currentEnergy, energyStorage);
            tooltip.add(Component.literal("⚡ ENERGY: ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d", currentEnergy)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%,d RF", energyStorage)).withStyle(ChatFormatting.WHITE)));
            tooltip.add(Component.literal(energyBar));

            tooltip.add(Component.literal("⚡ CONSUMPTION: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.format("%,d RF/tick", controller.getRfTick())).withStyle(ChatFormatting.WHITE))
                .append(getModifierText(energyMod)));

            tooltip.add(Component.literal("⏱ DURATION: ").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(String.format("%d ticks", controller.getMaxProgress())).withStyle(ChatFormatting.WHITE))
                .append(getModifierText(speedMod)));

            if (itemMod != 1.0f) {
                tooltip.add(Component.literal("📦 ITEM BOOST: ").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(String.format("%.1f×", itemMod)).withStyle(ChatFormatting.WHITE)));
            }

            float progressPercent = controller.getMaxProgress() == 0 ? 0F : (float) controller.getCurrentProgress() / controller.getMaxProgress();
            String progressBar = getProgressBar(progressPercent);
            tooltip.add(Component.literal("⏳ PROGRESS: ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%.1f%%", progressPercent * 100)).withStyle(ChatFormatting.WHITE)));
            tooltip.add(Component.literal(progressBar));

            tooltip.add(getUpgradeInfoText(storageUpgradeDisplayName, extraSlots));
            return tooltip;
        }

        if (controller.isActiveInternal()) {
            tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor(name)))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.literal("⚠ STATUS: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("NOT WORKING").withStyle(ChatFormatting.RED)));

            tooltip.add(Component.literal("⚡ ENERGY: ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d", runtimeState.getLastEnergyStored())).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%,d RF", runtimeState.getLastEnergyCapacity())).withStyle(ChatFormatting.WHITE)));

            tooltip.add(Component.literal("⚡ DEMAND: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.format("%,d RF/tick", runtimeState.getLastEnergyDemand())).withStyle(ChatFormatting.WHITE))
                .append(getModifierText(energyMod)));

            tooltip.add(Component.literal("📊 MODIFIERS: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(String.format("%d installed", modifierMap.size())).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" → energy ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.2f×", energyMod)).withStyle(ChatFormatting.RED))
                .append(Component.literal(" | speed ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.2f×", speedMod)).withStyle(ChatFormatting.BLUE))
                .append(Component.literal(" | items ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.2f×", itemMod)).withStyle(ChatFormatting.LIGHT_PURPLE)));

            if (runtimeState.isLastEnergyDemandTooHigh()) {
                tooltip.add(Component.literal("❌ ENERGY LIMIT: ").withStyle(ChatFormatting.RED)
                    .append(Component.literal("Required RF/tick exceeds the miner's internal buffer. Remove some item/speed modifiers or add energy modifiers.").withStyle(ChatFormatting.GRAY)));
            }

            if (runtimeState.isLastEnergyInsufficient() && !runtimeState.isLastEnergyDemandTooHigh()) {
                long deficit = Math.max(0L, (long) runtimeState.getLastEnergyDemand() - runtimeState.getLastEnergyStored());
                tooltip.add(Component.literal("⚡ DEFICIT: ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.format("Missing %,d RF to start the next cycle", deficit)).withStyle(ChatFormatting.GRAY)));
            }

            if (runtimeState.isLastInventoryFull()) {
                tooltip.add(Component.literal("📦 OUTPUT: ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal("All slots are at capacity. Extract items or upgrade storage.").withStyle(ChatFormatting.GRAY)));
            }

            if (runtimeState.isLastOutputBlocked()) {
                if (runtimeState.getLastOutputBlockReason() == OutputBlockReason.NO_RECIPES) {
                    String dimensionName = level != null ? level.dimension().location().toString() : "unknown";
                    tooltip.add(Component.literal("📜 RECIPES: ").withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal("No valid miner recipes for this tier in ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(dimensionName).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(String.format(" (detected %,d)", runtimeState.getLastRecipeCount())).withStyle(ChatFormatting.GRAY)));
                } else if (!runtimeState.getLastBlockedStack().isEmpty()) {
                    tooltip.add(Component.literal("📦 BLOCKED ITEM: ").withStyle(ChatFormatting.YELLOW)
                        .append(runtimeState.getLastBlockedStack().getHoverName().copy().withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(String.format(" × %,d", runtimeState.getLastBlockedStack().getCount())).withStyle(ChatFormatting.WHITE)));

                    getBlockedReasonText(extraSlots, maxExtraSlots, itemMod, controller, tooltip);

                } else {
                    tooltip.add(Component.literal("📦 OUTPUT: ").withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal("No slot can accept the next output. Clear space or provide matching stacks.").withStyle(ChatFormatting.GRAY)));
                }
            }

            if (!runtimeState.isLastEnergyDemandTooHigh()
                && !runtimeState.isLastEnergyInsufficient()
                && !runtimeState.isLastInventoryFull()
                && !runtimeState.isLastOutputBlocked()) {
                tooltip.add(Component.literal("ℹ DIAGNOSTIC: ").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal("Awaiting next energy sync. If the issue persists, check external power supply.").withStyle(ChatFormatting.GRAY)));
            }

            tooltip.add(getUpgradeInfoText(storageUpgradeDisplayName, extraSlots));
            return tooltip;
        }

        if (controller.hasFoundStructureInternal()) {
            tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor(name)))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.literal("⚠ STATUS: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("INACTIVE").withStyle(ChatFormatting.YELLOW)));

            tooltip.add(Component.literal("❌ ISSUE: ").withStyle(ChatFormatting.RED)
                .append(Component.literal("Cannot see bedrock/void").withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.literal("💡 TIP: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal("Make sure center block has clear path to bedrock!").withStyle(ChatFormatting.WHITE)));

            tooltip.add(getUpgradeInfoText(storageUpgradeDisplayName, extraSlots));
            return tooltip;
        }

        tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(name.toUpperCase() + " MINER").withStyle(getTierColor(name)))
            .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("❌ STATUS: ").withStyle(ChatFormatting.RED)
            .append(Component.literal("STRUCTURE INCOMPLETE").withStyle(ChatFormatting.DARK_RED)));

        tooltip.add(Component.literal("💡 TIP: ").withStyle(ChatFormatting.AQUA)
            .append(Component.literal("Shift + Right-click for structure guide").withStyle(ChatFormatting.WHITE)));

        tooltip.add(Component.literal("📋 MISSING BLOCKS:").withStyle(ChatFormatting.YELLOW));

        if (structure != null && MiscUtil.structureMap.containsKey(structure.toString())) {
            MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> tooltip.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(string).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(integer)).withStyle(ChatFormatting.RED))));
        }

        tooltip.add(getUpgradeInfoText(storageUpgradeDisplayName, extraSlots));

        return tooltip;
    }

    private static ChatFormatting getTierColor(String name) {
        if (name == null) {
            return ChatFormatting.WHITE;
        }
        return switch (name.toLowerCase()) {
            case "rubetine" -> ChatFormatting.RED;
            case "aurantium" -> ChatFormatting.GOLD;
            case "citrinetine" -> ChatFormatting.YELLOW;
            case "verdium" -> ChatFormatting.GREEN;
            case "azurine" -> ChatFormatting.BLUE;
            case "caerium" -> ChatFormatting.DARK_BLUE;
            case "amethystine" -> ChatFormatting.DARK_PURPLE;
            case "rosarium" -> ChatFormatting.LIGHT_PURPLE;
            case "ultimate" -> ChatFormatting.DARK_RED;
            default -> ChatFormatting.WHITE;
        };
    }

    private static Component getModifierText(float modifier) {
        if (modifier == 1.0f) {
            return Component.empty();
        }

        return Component.literal(" (").withStyle(ChatFormatting.AQUA)
            .append(Component.literal(String.format("%.1f", modifier)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal("×)").withStyle(ChatFormatting.AQUA));
    }

    private static String getEnergyBar(int current, int max) {
        if (max <= 0) {
            return "§8▌§r";
        }

        float percent = (float) current / max;
        int bars = (int) (percent * 20);
        StringBuilder bar = new StringBuilder("§a");

        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                bar.append("█");
            } else if (i == bars && percent * 20 - bars > 0.5) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar + "§r";
    }

    private static String getProgressBar(float percent) {
        int bars = (int) (percent * 20);
        StringBuilder bar = new StringBuilder("§e");

        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                bar.append("█");
            } else if (i == bars && percent * 20 - bars > 0.5) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar + "§r";
    }

    private static Component getUpgradeInfoText(String displayName, int extraSlots) {
        return Component.literal("⚙ UPGRADE: ").withStyle(ChatFormatting.AQUA)
            .append(Component.literal(displayName).withStyle(ChatFormatting.WHITE))
            .append(Component.literal(" (+" + extraSlots + " slots)").withStyle(ChatFormatting.GRAY));
    }

    private static void getBlockedReasonText(int extraSlots, int maxExtraSlots, float itemMod, ControllerBaseBE controller, List<Component> tooltip) {
        float itemModToSlots = itemMod / 64;
        int totalSlots = extraSlots + controller.getBaseOutputSlots();
        float currentMaxItemMod = totalSlots * 64;
        int maxTotalSlots = maxExtraSlots + controller.getBaseOutputSlots();
        float maxItemMod = maxTotalSlots * 64;

        tooltip.add(Component.literal("📦 Reason: ").withStyle(ChatFormatting.YELLOW));

        if (itemModToSlots > maxTotalSlots) {
            tooltip.add(Component.literal("Too much Item Modifiers, max is : ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("%.2f×", maxItemMod)).withStyle(ChatFormatting.LIGHT_PURPLE)));
        } else if (itemModToSlots > totalSlots) {
            tooltip.add(Component.literal("Too much Item Modifiers, current max is : ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("%.2f×", currentMaxItemMod)).withStyle(ChatFormatting.LIGHT_PURPLE)));

            tooltip.add(Component.literal("Either use less Item Modifiers or use a better Storage Upgrade").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Miner's inventory is too full to accept another batch of items, empty it.").withStyle(ChatFormatting.GRAY));
        }
    }
}
