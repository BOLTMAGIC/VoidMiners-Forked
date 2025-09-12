package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.ModifierBlock;
import com.leo.voidminers.block.SolarPanelBaseBlock;
import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.util.MapUtil;
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
            
            toolTip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(solarPanel.name.toUpperCase() + " SOLAR PANEL").withStyle(getTierColor(solarPanel.name)))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY)));
            
            toolTip.add(Component.literal("⚡ BUFFER: ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%,d RF", solarConfig.energyStorage())).withStyle(ChatFormatting.WHITE)));
            
            toolTip.add(Component.literal("☀ GENERATION: ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.format("%,d RF/tick", solarConfig.energyGeneration())).withStyle(ChatFormatting.WHITE)));
                
            toolTip.add(Component.literal("⏱ CYCLE: ").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(String.format("%d ticks", solarConfig.duration())).withStyle(ChatFormatting.WHITE)));
            
            return;
        }

        // Handle Modifier tooltips
        if (blockItem.getBlock() instanceof ModifierBlock mb) {
            ConfigLoader.ModifierConfig modConfig = ConfigLoader.getInstance().getModifierConfig(mb);

            toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + ".energy", modConfig.energy()).withStyle(ChatFormatting.DARK_RED));
            toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + ".speed", modConfig.speed()).withStyle(ChatFormatting.DARK_GREEN));
            toolTip.add(Component.translatable("tooltip." + VoidMiners.MODID + ".item", modConfig.item()).withStyle(ChatFormatting.DARK_BLUE));
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
