package com.leo.voidminers.block.solar.entity.renderer;

import com.leo.voidminers.block.solar.entity.SolarPanelBaseBE;
import com.leo.voidminers.util.MiscUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class SolarPanelRenderer implements BlockEntityRenderer<SolarPanelBaseBE> {

    public SolarPanelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SolarPanelBaseBE pBlockEntity, float pPartialTick, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (!pBlockEntity.showStructure) return;

        if (pBlockEntity.getStructure() == null) return;

        String structure = pBlockEntity.getStructure().toString();

        if (!MiscUtil.structureMap.containsKey(structure)) return;

        List<List<List<BlockState>>> blocks = MiscUtil.structureMap.get(structure);
        if (blocks == null || blocks.isEmpty() || blocks.get(0).isEmpty()) return;

        int xOffset;
        int zOffset;
        int yOffset = getTierPreviewYOffset(pBlockEntity);

        // If we have a stored controller anchor for this structure, use it to center the preview
        if (MiscUtil.controllerAnchorMap.containsKey(structure)) {
            int[] anchor = MiscUtil.controllerAnchorMap.get(structure);
            xOffset = anchor[0];
            // anchor[1] is the converted layer index; we still use yOffset derived from tier
            zOffset = anchor[2];
        } else {
            xOffset = blocks.size() / 2;
            zOffset = blocks.get(0).get(0).size() / 2;
        }

        pose.pushPose();
        // Center on controller anchor position and apply tier-based Y offset
        pose.translate(-xOffset, yOffset, -zOffset);
        pose.pushPose();
        pose.mulPose(Axis.ZN.rotationDegrees(90));

        //TODO Find a better way to do this, it's performance intensive doing 3 loops each render tick

        for (int x = 0; x < blocks.size(); x++) {
            List<List<BlockState>> b2 = blocks.get(x);

            for (int y = 0; y < b2.size(); y++) {
                List<BlockState> b3 = b2.get(y);

                for (int z = 0; z < b3.size(); z++) {
                    BlockState block = b3.get(z);

                    pose.pushPose();
                    pose.translate(x, y, z);

                    renderBlock(
                        block,
                        pose,
                        pBuffer
                    );

                    pose.popPose();
                }
            }
        }

        pose.popPose();
        pose.popPose();
    }

    public void renderBlock(BlockState state, PoseStack pose, MultiBufferSource buffer) {
        Minecraft minecraft = Minecraft.getInstance();

        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();

        blockRenderer.renderSingleBlock(
            state,
            pose,
            buffer,
            LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            blockRenderer.getBlockModel(state).getModelData(minecraft.level, new BlockPos(0, 0, 0), state, ModelData.EMPTY),
            RenderType.translucent()
        );
    }

    @Override
    public boolean shouldRenderOffScreen(SolarPanelBaseBE pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(SolarPanelBaseBE pBlockEntity, Vec3 pCameraPos) {
        return pBlockEntity.getBlockPos().getCenter().distanceTo(pCameraPos) <= 100;
    }

    private int getTierPreviewYOffset(SolarPanelBaseBE blockEntity) {
        // Explicit tier mapping for solar preview Y placement.
        int tier = MiscUtil.tierMap.getOrDefault(blockEntity.getStructure().getPath(), 1);
        return switch (tier) {
            case 1 -> 4;
            case 2 -> 5;
            case 3, 4, 5 -> 6;
            case 6, 7 -> 7;
            case 8 -> 9;
            default -> 9;
        };
    }
}



