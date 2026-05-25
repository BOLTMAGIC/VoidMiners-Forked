package com.leo.voidminers.block.controller;

import com.leo.voidminers.block.base.BaseTransparentBlock;
import com.leo.voidminers.block.controller.entity.ControllerBaseBE;
import com.leo.voidminers.util.ShapeUtil;
import com.leo.voidminers.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ControllerBaseBlock extends BaseTransparentBlock implements EntityBlock {
    final ResourceLocation structure;
    final String name;

    public ControllerBaseBlock(Properties pProperties, ResourceLocation structure, String name) {
        super(pProperties);
        this.structure = structure;
        this.name = name;
    }

    public String getTierName() {
        return name;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            ((ControllerBaseBE) blockEntity).drops();
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ControllerBaseBE(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ControllerBaseBE blockEntity = (ControllerBaseBE) pLevel.getBlockEntity(pPos);

        if (pLevel.isClientSide) {
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        if (pPlayer.isCrouching()) {
            blockEntity.updateShowStructure();
            return InteractionResult.CONSUME;
        }

        // If player holds an upgrade item, attempt to insert it into the upgrade slots
        ItemStack held = pPlayer.getItemInHand(pHand);
        if (!held.isEmpty()) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(held.getItem());
            String id = key != null ? key.toString() : "";

            boolean isUpgrade = id.endsWith("upgrade_max_storage_t1")
                || id.endsWith("upgrade_max_storage_t2")
                || id.endsWith("upgrade_max_storage_t3");

            if (isUpgrade) {
                // Server-side handling: determine tier
                int tier = id.endsWith("upgrade_max_storage_t3") ? 3 : id.endsWith("upgrade_max_storage_t2") ? 2 : 1;

                int current = blockEntity.getAppliedUpgradeTier();
                if (current == tier) {
                    pPlayer.displayClientMessage(Component.literal("Upgrade already applied"), true);
                    return InteractionResult.CONSUME;
                }

                // Prevent placing a smaller upgrade into a miner that already has a higher-tier installed
                if (current > tier) {
                    pPlayer.displayClientMessage(Component.literal("Cannot apply lower-tier upgrade while a higher-tier upgrade is installed"), true);
                    return InteractionResult.CONSUME;
                }

                // Prepare previous upgrade stack to return to player (if any)
                ItemStack previousStack = ItemStack.EMPTY;
                if (current == 1) previousStack = new ItemStack(ModItems.UPGRADE_MAX_STORAGE_T1.get());
                if (current == 2) previousStack = new ItemStack(ModItems.UPGRADE_MAX_STORAGE_T2.get());
                if (current == 3) previousStack = new ItemStack(ModItems.UPGRADE_MAX_STORAGE_T3.get());

                // Apply the new upgrade
                blockEntity.setAppliedUpgradeTier(tier);

                // consume one item from hand (unless creative)
                if (!pPlayer.getAbilities().instabuild) {
                    held.shrink(1);
                    pPlayer.setItemInHand(pHand, held);
                }

                // Try to give previous upgrade back to player's inventory; otherwise spawn in world
                if (!previousStack.isEmpty()) {
                    boolean added = pPlayer.getInventory().add(previousStack);
                    if (!added) {
                        ItemEntity drop = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), previousStack);
                        pLevel.addFreshEntity(drop);
                    }
                }

                // Notify clients
                if (blockEntity.getLevel() != null) {
                    blockEntity.getLevel().sendBlockUpdated(pPos, pState, pState, 3);
                }

                pPlayer.displayClientMessage(Component.literal("Upgrade applied: T" + tier), true);
                return InteractionResult.CONSUME;
            }
        }

        for (Component component : blockEntity.getInteractionTooltip()) {
            pPlayer.displayClientMessage(component, false);
        }

        return InteractionResult.CONSUME;

    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        ControllerBaseBE controller = ((ControllerBaseBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((ControllerBaseBE) this.newBlockEntity(pPos, pState));
        }

        controller.setup(structure, name);

        // If placed by a player and this is the tier-1 (rubetine) miner, inform them there are no modifier slots
        if (!pLevel.isClientSide && pPlacer instanceof Player player) {
            if ("rubetine".equalsIgnoreCase(this.name)) {
                // Show up to 3 times per player using persistent player tags
                String tag1 = "voidminers:seen_tier1_miner_1";
                String tag2 = "voidminers:seen_tier1_miner_2";
                String tag3 = "voidminers:seen_tier1_miner_3";
                java.util.Set<String> tags = player.getTags();

                if (!tags.contains(tag3)) {
                    // Compose a message with colored parts: "Rubetine" red and "second tier" gold (orange-like)
                    Component msg = Component.literal("")
                        .append(Component.literal("Rubetine").withStyle(net.minecraft.ChatFormatting.RED))
                        .append(Component.literal(" Void Miner has no slot for modifiers. These can be added starting at the "))
                        .append(Component.literal("second tier").withStyle(net.minecraft.ChatFormatting.GOLD))
                        .append(Component.literal("."));
                    // Show as actionbar for visibility
                    player.displayClientMessage(msg, true);

                    if (!tags.contains(tag1)) {
                        player.addTag(tag1);
                    } else if (!tags.contains(tag2)) {
                        player.addTag(tag2);
                    } else {
                        player.addTag(tag3);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return ((level, blockPos, blockState, be) -> ((ControllerBaseBE) be).tick(pLevel, blockPos, blockState, structure, name));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.or(
            ShapeUtil.shapeFromDimension(0, 0, 0, 16, 2, 16),
            ShapeUtil.shapeFromDimension(2, 2, 2, 12, 12, 12),
            ShapeUtil.shapeFromDimension(7, 0f, 1, 2, 15f, 14),
            ShapeUtil.shapeFromDimension(1, 7, 9, 6, 2, 6),
            ShapeUtil.shapeFromDimension(1, 7, 1, 6, 2, 6),
            ShapeUtil.shapeFromDimension(9, 7, 9, 6, 2, 6),
            ShapeUtil.shapeFromDimension(9, 7, 1, 6, 2, 6),
            ShapeUtil.shapeFromDimension(1, 0f, 7, 6, 15f, 2),
            ShapeUtil.shapeFromDimension(9, 0f, 7, 6, 15f, 2)
        );
    }
}
