package com.leo.voidminers.block.controller.entity;

import com.leo.voidminers.config.ConfigLoader;
import com.leo.voidminers.recipe.MinerRecipe;
import com.leo.voidminers.recipe.WeightedStack;
import com.leo.voidminers.util.ListUtil;
import com.leo.voidminers.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ControllerInventoryHelper {

    private final ControllerBaseBE controller;

    ControllerInventoryHelper(ControllerBaseBE controller) {
        this.controller = controller;
    }

    boolean isItemHandlerFull() {
        ItemStackHandler handler = controller.getItemHandlerInternal();
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).getCount() < handler.getStackInSlot(i).getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    List<MinerRecipe> getValidRecipes() {
        Level level = controller.getLevel();
        if (level == null || level.isClientSide()) {
            return new ArrayList<>();
        }

        ResourceLocation structure = controller.getStructure();
        if (structure == null) {
            return new ArrayList<>();
        }

        return level.getRecipeManager().getAllRecipesFor(MinerRecipe.Type.INSTANCE)
            .stream()
            .filter(recipe -> {
                int tier = MiscUtil.tierMap.get(structure.getPath());
                return recipe.allowHigherTiers()
                    ? recipe.minTier() <= tier
                    : recipe.minTier() == tier;
            })
            .filter(recipe -> recipe.dimension().equals(level.dimension()))
            .toList();
    }

    boolean canProduceItems() {
        ControllerRuntimeState runtimeState = controller.getRuntimeStateInternal();
        if (runtimeState.isInventoryChanged() || controller.getCurrentProgress() == 0) {
            runtimeState.setCanProduceCache(calculateCanProduceItems());
            runtimeState.clearInventoryChangedFlag();
        }

        runtimeState.setLastOutputBlocked(!runtimeState.canProduceCache());
        return runtimeState.canProduceCache();
    }

    private boolean calculateCanProduceItems() {
        ControllerRuntimeState runtimeState = controller.getRuntimeStateInternal();
        List<WeightedStack> allOutputs = new ArrayList<>();

        for (MinerRecipe recipe : getValidRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        runtimeState.setLastRecipeCount(allOutputs.size());
        runtimeState.resetBlockedStack();

        if (allOutputs.isEmpty()) {
            runtimeState.setLastOutputBlockReason(OutputBlockReason.NO_RECIPES);
            return false;
        }

        // Determine the most likely output (highest weight) as representative blocked item
        double highestWeight = -1.0;
        ItemStack highestWeightStack = ItemStack.EMPTY;
        for (WeightedStack weightedStack : allOutputs) {
            ItemStack output = getBoostedStack(weightedStack.stack.copy());
            // Track the highest-weight output (most likely)
            if (weightedStack.weight > highestWeight) {
                highestWeight = weightedStack.weight;
                highestWeightStack = output;
            }
            if (canInsertItem(output)) {
                runtimeState.setLastOutputBlockReason(OutputBlockReason.NONE);
                runtimeState.resetBlockedStack();
                return true;
            }
        }
        // If none fit, show the highest-weight (most likely) output as blocked
        if (!highestWeightStack.isEmpty()) {
            runtimeState.setLastBlockedStack(highestWeightStack);
        }

        runtimeState.setLastOutputBlockReason(OutputBlockReason.NO_VALID_SLOT);
        return false;
    }

    boolean canInsertItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        ItemStackHandler handler = controller.getItemHandlerInternal();
        int remainingCount = stack.getCount();
        int maxStackSize = stack.getMaxStackSize();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack slotStack = handler.getStackInSlot(i);

            if (slotStack.isEmpty()) {
                remainingCount -= maxStackSize;
                if (remainingCount <= 0) {
                    return true;
                }
            } else if (slotStack.is(stack.getItem())) {
                int spaceInSlot = maxStackSize - slotStack.getCount();
                if (spaceInSlot > 0) {
                    remainingCount -= spaceInSlot;
                    if (remainingCount <= 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    void insertItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStackHandler handler = controller.getItemHandlerInternal();
        int remainingCount = stack.getCount();
        int maxStackSize = stack.getMaxStackSize();

        for (int i = 0; i < handler.getSlots(); i++) {
            if (remainingCount <= 0) {
                break;
            }

            ItemStack slotStack = handler.getStackInSlot(i);

            if (slotStack.isEmpty()) {
                int toInsert = Math.min(remainingCount, maxStackSize);
                handler.setStackInSlot(i, stack.copyWithCount(toInsert));
                remainingCount -= toInsert;
            } else if (slotStack.is(stack.getItem())) {
                int spaceInSlot = maxStackSize - slotStack.getCount();
                if (spaceInSlot > 0) {
                    int toInsert = Math.min(remainingCount, spaceInSlot);
                    slotStack.grow(toInsert);
                    remainingCount -= toInsert;
                }
            }
        }
    }

    void drops() {
        ItemStackHandler handler = controller.getItemHandlerInternal();
        SimpleContainer container = new SimpleContainer(handler.getSlots());

        for (int i = 0; i < handler.getSlots(); i++) {
            container.addItem(handler.getStackInSlot(i));
        }

        Level level = controller.getLevel();
        if (level != null) {
            Containers.dropContents(level, controller.getBlockPos(), container);
        }
    }

    ItemStack getWeightedItem(List<WeightedStack> items, RandomSource random) {
        double totalWeight = ListUtil.getTotalWeight(items);
        double randomValue = random.nextDouble() * totalWeight;

        for (WeightedStack item : items) {
            randomValue -= item.weight;
            if (randomValue <= 0) {
                return item.stack;
            }
        }

        return ItemStack.EMPTY;
    }

    ItemStack getBoostedStack(ItemStack base) {
        if (base.isEmpty()) {
            return ItemStack.EMPTY;
        }

        float mod = 1.0f;
        for (Map.Entry<BlockInWorld, ConfigLoader.ModifierConfig> entry : controller.getModifierMapInternal().entrySet()) {
            mod *= entry.getValue().item();
        }

        int count = (int) (base.getCount() * mod);
        return base.copyWithCount(count);
    }
}
