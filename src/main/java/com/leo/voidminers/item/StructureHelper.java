package com.leo.voidminers.item;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.controller.entity.ControllerBaseBE;
import com.leo.voidminers.block.solar.entity.SolarPanelBaseBE;
import com.leo.voidminers.multiblock.miner.MinerMultiblocks;
import com.leo.voidminers.multiblock.solar.SolarPanelMultiblocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;

public class StructureHelper extends Item {
    public StructureHelper(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getPlayer() == null) return InteractionResult.PASS;

        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }

        BlockEntity entity = level.getBlockEntity(pos);

        ResourceLocation structure = null;
        RegisteredMultiBlockPattern multiBlock = null;

        if (entity instanceof ControllerBaseBE controller) {
            structure = controller.getStructure();
            multiBlock = MinerMultiblocks.MANAGER.getStructure(structure.getPath());
        } else if (entity instanceof SolarPanelBaseBE solarPanel) {
            structure = solarPanel.getStructure();
            multiBlock = SolarPanelMultiblocks.MANAGER.getStructure(structure.getPath());
        } else {
            return InteractionResult.PASS;
        }

        if (structure == null || multiBlock == null) {
            return InteractionResult.PASS;
        }

        if (!((ServerPlayer) pContext.getPlayer()).gameMode.isCreative()) {
            return InteractionResult.CONSUME;
        }

        multiBlock.pattern().construct(level, pos);
        return InteractionResult.CONSUME;
    }
}
