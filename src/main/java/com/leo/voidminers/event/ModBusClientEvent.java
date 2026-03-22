package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.controller.entity.renderer.ControllerRenderer;
import com.leo.voidminers.block.solar.entity.renderer.SolarPanelRenderer;
import com.leo.voidminers.init.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBusClientEvent {

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CONTROLLER_BASE_BE.get(), ControllerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_PANEL_BASE_BE.get(), SolarPanelRenderer::new);
    }


}
