package com.leo.voidminers.block.controller.entity;

enum HaltReason {
    NONE("operational"),
    DIMENSION_BLOCKED("dimension restricted"),
    STRUCTURE_MISSING("multiblock missing"),
    NO_VOID_VIEW("no void exposure"),
    INVENTORY_FULL("inventory full"),
    OUTPUT_BLOCKED("no valid output slot"),
    ENERGY_BUFFER("energy demand exceeds buffer"),
    ENERGY_DEFICIT("insufficient stored energy");

    private final String description;

    HaltReason(String description) {
        this.description = description;
    }

    String description() {
        return description;
    }
}
