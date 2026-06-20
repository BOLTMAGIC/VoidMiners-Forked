package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.controller.entity.ControllerBaseBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CacheInvalidationHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor la = event.getLevel();
        if (la instanceof Level level) {
            BlockPos pos = event.getPos();
            ControllerBaseBE.invalidateCacheFor(level, pos);
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        LevelAccessor la = event.getLevel();
        if (la instanceof Level level) {
            BlockPos pos = event.getPos();
            ControllerBaseBE.invalidateCacheFor(level, pos);
        }
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        LevelAccessor la = event.getLevel();
        if (la instanceof Level level) {
            BlockPos pos = event.getPos();
            ControllerBaseBE.invalidateCacheFor(level, pos);
        }
    }

}


