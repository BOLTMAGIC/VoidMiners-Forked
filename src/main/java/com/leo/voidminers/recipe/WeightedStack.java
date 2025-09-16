package com.leo.voidminers.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WeightedStack {
    public ItemStack stack;
    public double weight;

    public WeightedStack(ItemStack stack, double weight) {
        this.stack = stack;
        this.weight = weight;
    }

    public WeightedStack(Item item, double weight) {
        this.stack = item.getDefaultInstance();
        this.weight = weight;
    }

    public WeightedStack copy() {
        return new WeightedStack(stack, weight);
    }
}
