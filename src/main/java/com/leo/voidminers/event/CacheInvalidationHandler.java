package com.leo.voidminers.event;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.controller.entity.ControllerBaseBE;
import com.leo.voidminers.block.solar.entity.SolarPanelBaseBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = VoidMiners.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CacheInvalidationHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor la = event.getLevel();
        if (la instanceof Level level) {
            BlockPos pos = event.getPos();
            ControllerBaseBE.invalidateCacheFor(level, pos);
            SolarPanelBaseBE.invalidateSolarCacheFor(level, pos);
            // Also notify any solar panels beneath this position so they can stop generating immediately
            notifySolarPanelsUnder(level, pos);
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        LevelAccessor la = event.getLevel();
        if (la instanceof Level level) {
            BlockPos pos = event.getPos();
            ControllerBaseBE.invalidateCacheFor(level, pos);
            SolarPanelBaseBE.invalidateSolarCacheFor(level, pos);
            notifySolarPanelsUnder(level, pos);
        }
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        LevelAccessor la = event.getLevel();
        if (la instanceof Level level) {
            BlockPos pos = event.getPos();
            ControllerBaseBE.invalidateCacheFor(level, pos);
            SolarPanelBaseBE.invalidateSolarCacheFor(level, pos);
            notifySolarPanelsUnder(level, pos);
        }
    }

    private static void notifySolarPanelsUnder(Level level, BlockPos pos) {
        // scan downward up to 320 blocks to find solar panel block entities and notify them
        int minY = Math.max(level.getMinBuildHeight(), pos.getY() - 320);
        for (int y = pos.getY() - 1; y >= minY; y--) {
            BlockPos check = new BlockPos(pos.getX(), y, pos.getZ());
            var be = level.getBlockEntity(check);
            if (be instanceof SolarPanelBaseBE sbe) {
                // notify solar panel BE about change above
                sbe.handleBlockAboveChanged();
            }
            if (be instanceof ControllerBaseBE cbe) {
                // invalidate controller structure cache so it re-evaluates next tick
                cbe.handleStructureChanged();
            }
        }
    }

}
