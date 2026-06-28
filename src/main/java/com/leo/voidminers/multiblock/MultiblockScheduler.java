package com.leo.voidminers.multiblock;

import com.leo.voidminers.block.controller.entity.ControllerBaseBE;
import com.leo.voidminers.block.solar.entity.SolarPanelBaseBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global scheduler that limits number of full multiblock checks per server tick.
 * BEs can enqueue themselves for a full check; the scheduler will process up to
 * MAX_PER_TICK entries each server tick in a round-robin fashion.
 */
@Mod.EventBusSubscriber
public class MultiblockScheduler {

    private static final ConcurrentLinkedQueue<ScheduledEntry> QUEUE = new ConcurrentLinkedQueue<>();
    private static final Set<String> ENQUEUED = ConcurrentHashMap.newKeySet();

    // Tunable: max number of BEs fully checked per server tick
    private static final int MAX_PER_TICK = 20;

    private record ScheduledEntry(ResourceKey<Level> dimKey, BlockPos pos) {
        String key() { return dimKey.location() + "|" + pos.asLong(); }
    }

    public static void schedule(Level level, BlockPos pos) {
        if (level == null) return;
        if (level.isClientSide) return;
        ResourceKey<Level> dimKey = level.dimension();
        ScheduledEntry e = new ScheduledEntry(dimKey, pos);
        String key = e.key();
        if (ENQUEUED.add(key)) {
            QUEUE.add(e);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event) {
        if (event.phase != ServerTickEvent.Phase.END) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (int i = 0; i < MAX_PER_TICK; i++) {
            ScheduledEntry e = QUEUE.poll();
            if (e == null) break;
            ENQUEUED.remove(e.key());

            try {
                ServerLevel lvl = server.getLevel(e.dimKey);
                if (lvl == null) continue;

                BlockPos pos = e.pos;
                var be = lvl.getBlockEntity(pos);
                if (be instanceof ControllerBaseBE cbe) {
                    cbe.checkStructure(lvl, pos);
                } else if (be instanceof SolarPanelBaseBE sbe) {
                    sbe.checkStructure(lvl, pos);
                }
            } catch (Throwable ignored) {
                // Defensive - ignore malformed entries or missing worlds
            }
        }
    }
}


