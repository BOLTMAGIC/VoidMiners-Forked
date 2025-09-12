package com.leo.voidminers;

import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.init.*;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.SolarSet;
import com.leo.voidminers.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VoidMiners.MODID)
public class VoidMiners {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "voidminers";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public VoidMiners() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        ConfigLoader.getInstance().load();
        CrystalSet.initSets();
        SolarSet.initSolarSets();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(VoidMiners::clientSetup);
        modEventBus.addListener(VoidMiners::commonSetup);

    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(VoidMiners::setupRenders);
    }

    // TODO: move render type to model json
    private static void setupRenders() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GLASS_PANEL.get(), RenderType.translucent());
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.register();
    }
}
