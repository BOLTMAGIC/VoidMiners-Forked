package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.block.SolarPanelBaseBlock;
import com.leo.voidminers.block.ControllerBaseBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.util.MapUtil;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeBusClientEvent {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent toolTipEvent) {
        List<Component> toolTip = toolTipEvent.getToolTip();
        ItemStack itemStack = toolTipEvent.getItemStack();

        if(itemStack.is(ModItems.STRUCTURE_HELPER.get())) {
            toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + "creative_only").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }

        if (!(itemStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        // Handle Solar Panel tooltips
        if (blockItem.getBlock() instanceof SolarPanelBaseBlock solarPanel) {
            ConfigLoader.SolarPanelConfig solarConfig = ConfigLoader.getInstance().getSolarPanelConfig(solarPanel.name);

            // Use custom tooltip from config if available
            if (solarConfig.tooltip() != null && !solarConfig.tooltip().isEmpty()) {
                for (String line : solarConfig.tooltip()) {
                    toolTip.add(Component.literal(line));
                }
            } else {
                // Fallback to default tooltip system if no custom tooltip is defined
                toolTip.add(Component.literal("BUFFER: ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.format("%,d RF", solarConfig.energyStorage())).withStyle(ChatFormatting.WHITE)));

                toolTip.add(Component.literal("GENERATION: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(String.format("%,d RF/tick", solarConfig.energyGeneration())).withStyle(ChatFormatting.WHITE)));

                toolTip.add(Component.literal("CYCLE: ").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(String.format("%d ticks", solarConfig.duration())).withStyle(ChatFormatting.WHITE)));
            }

            return;
        }

        // Handle Miner (Controller) tooltips
        if (blockItem.getBlock() instanceof ControllerBaseBlock controllerBlock) {
            ConfigLoader.MinerConfig minerConfig = ConfigLoader.getInstance().getMinerConfig(controllerBlock.getTierName());

            // Use custom tooltip from config if available
            if (minerConfig.tooltip() != null && !minerConfig.tooltip().isEmpty()) {
                for (String line : minerConfig.tooltip()) {
                    toolTip.add(Component.literal(line));
                }
            } else {
                // Fallback to default tooltip system if no custom tooltip is defined
                toolTip.add(Component.literal("BUFFER: ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(String.format("%,d RF", minerConfig.energyStorage())).withStyle(ChatFormatting.WHITE)));

                toolTip.add(Component.literal("ENERGY CONSUMPTION: ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(String.format("%,d RF/tick", minerConfig.energyTick())).withStyle(ChatFormatting.WHITE)));

                toolTip.add(Component.literal("CYCLE: ").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(String.format("%d ticks", minerConfig.duration())).withStyle(ChatFormatting.WHITE)));
            }

            return;
        }

        // Handle Solar Frame tooltips
        String frameBlockName = ForgeRegistries.BLOCKS.getKey(blockItem.getBlock()).getPath();
        if (frameBlockName.startsWith("solar_") && frameBlockName.endsWith("_frame")) {
            // Extract tier from solar_<tier>_frame format
            String[] parts = frameBlockName.split("_");
            if (parts.length >= 3) {
                String tier = parts[1]; // Extract tier from "solar_<tier>_frame"
                ConfigLoader.SolarPanelConfig solarConfig = ConfigLoader.getInstance().getSolarPanelConfig(tier);

                // Create a simple tooltip for solar frames
                toolTip.add(Component.literal("§6SOLAR " + tier.toUpperCase() + " FRAME").withStyle(getTierColor(tier)));
                toolTip.add(Component.literal("§7Structural component for Solar Panel multiblock").withStyle(ChatFormatting.GRAY));
            }

            return;
        }

        // Handle Modifier tooltips
        if (blockItem.getBlock() instanceof ModifierBlock mb) {
            String blockName = ForgeRegistries.BLOCKS.getKey(mb).getPath();
            String[] parts = blockName.split("_");

            // Determine if it's a solar or miner modifier
            boolean isSolar = parts[0].equals("solar");

            if (isSolar && parts.length >= 4) {
                // Solar modifier format: solar_<tier>_<type>_modifier
                String tier = parts[1];
                String modifierType = parts[2];

                ConfigLoader.SolarModifierConfig solarConfig = ConfigLoader.getInstance().getSolarModifierConfig(mb, tier);

                // Use custom tooltip from config if available
                if (solarConfig.tooltip() != null && !solarConfig.tooltip().isEmpty()) {
                    for (String line : solarConfig.tooltip()) {
                        toolTip.add(Component.literal(line));
                    }
                } else {
                    // Fallback to default tooltip system if no custom tooltip is defined
                    ConfigLoader.SolarPanelConfig panelConfig = ConfigLoader.getInstance().getSolarPanelConfig(tier);
                    long capacity = panelConfig.energyStorage();
                    String capacityUnit = capacity >= 1000000 ? String.format("%.2f MFE", capacity / 1000000.0) : String.format("%,d FE", capacity);

                    toolTip.add(Component.literal("Stored energy: ").withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal("0 FE/").withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(capacityUnit).withStyle(ChatFormatting.WHITE)));

                    toolTip.add(Component.literal("Capacity: ").withStyle(ChatFormatting.BLUE)
                        .append(Component.literal(capacityUnit).withStyle(ChatFormatting.WHITE)));

                    switch (modifierType) {
                        case "output" -> {
                            toolTip.add(Component.literal("GENERATION BOOST: ").withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(String.format("%.0f%%", (solarConfig.generation() - 1) * 100)).withStyle(ChatFormatting.WHITE)));
                        }
                        case "efficiency" -> {
                            toolTip.add(Component.literal("SPEED BOOST: ").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(String.format("%.0f%%", (1 - solarConfig.efficiency()) * 100)).withStyle(ChatFormatting.WHITE)));
                        }
                        case "weather" -> {
                            toolTip.add(Component.literal("WEATHER PROTECTION: ").withStyle(ChatFormatting.BLUE)
                                .append(Component.literal(String.format("%.0f%%", (solarConfig.weatherResistance() - 1) * 100)).withStyle(ChatFormatting.WHITE)));
                        }
                    }
                }
            } else if (!isSolar && parts.length >= 3) {
                // Miner modifier format: <tier>_<type>_modifier
                String tier = parts[0];
                String modifierType = parts[1];

                ConfigLoader.ModifierConfig modConfig = ConfigLoader.getInstance().getModifierConfig(tier, modifierType);

                // Use custom tooltip from config if available
                if (modConfig.tooltip() != null && !modConfig.tooltip().isEmpty()) {
                    for (String line : modConfig.tooltip()) {
                        toolTip.add(Component.literal(line));
                    }
                } else {
                    // Fallback to default tooltip system if no custom tooltip is defined
                    toolTip.add(Component.literal("--- ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(tier.toUpperCase() + " " + modifierType.toUpperCase() + " MODIFIER").withStyle(getTierColor(tier)))
                        .append(Component.literal(" ---").withStyle(ChatFormatting.GRAY)));

                    switch (modifierType) {
                        case "energy" -> {
                            float energyReduction = (1 - modConfig.energy()) * 100;
                            toolTip.add(Component.literal("ENERGY EFFICIENCY: ").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(String.format("-%.0f%% consumption", energyReduction)).withStyle(ChatFormatting.WHITE)));
                        }
                        case "speed" -> {
                            float speedBoost = (1 - modConfig.speed()) * 100;
                            toolTip.add(Component.literal("SPEED BOOST: ").withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(String.format("+%.0f%% faster", speedBoost)).withStyle(ChatFormatting.WHITE)));
                        }
                        case "item" -> {
                            float itemBoost = (modConfig.item() - 1) * 100;
                            toolTip.add(Component.literal("ITEM MULTIPLIER: ").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(String.format("+%.0f%% items", itemBoost)).withStyle(ChatFormatting.WHITE)));
                        }
                    }
                }
            }
        }
    }
    
    private static ChatFormatting getTierColor(String tierName) {
        return switch (tierName.toLowerCase()) {
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

}
