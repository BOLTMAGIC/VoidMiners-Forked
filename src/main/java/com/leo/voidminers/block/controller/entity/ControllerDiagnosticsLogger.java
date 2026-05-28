package com.leo.voidminers.block.controller.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
final class ControllerDiagnosticsLogger {

    private Boolean previousStructureState;
    private Boolean previousVoidViewState;
    private Boolean previousActiveState;
    private Boolean previousWorkingState;
    private Boolean previousDimensionBlockState;
    private HaltReason previousHaltReason = HaltReason.NONE;

    void logStateTransitions(String name,
                             ResourceLocation structure,
                             BlockPos position,
                             boolean blockedByDimension,
                             boolean foundStructure,
                             boolean hasVoidView,
                             boolean active,
                             boolean working,
                             ControllerRuntimeState state,
                             int energyDemand,
                             long energyStored,
                             long energyCapacity) {
        HaltReason haltReason = determineHaltReason(blockedByDimension, foundStructure, hasVoidView, state);

        previousDimensionBlockState = blockedByDimension;
        previousStructureState = foundStructure;
        previousVoidViewState = hasVoidView;
        previousActiveState = active;
        previousWorkingState = working;
        previousHaltReason = haltReason;
    }

    void reset() {
        previousStructureState = null;
        previousVoidViewState = null;
        previousActiveState = null;
        previousWorkingState = null;
        previousDimensionBlockState = null;
        previousHaltReason = HaltReason.NONE;
    }

    private HaltReason determineHaltReason(boolean blockedByDimension,
                                           boolean foundStructure,
                                           boolean hasVoidView,
                                           ControllerRuntimeState state) {
        if (blockedByDimension) {
            return HaltReason.DIMENSION_BLOCKED;
        }
        if (!foundStructure) {
            return HaltReason.STRUCTURE_MISSING;
        }
        if (!hasVoidView) {
            return HaltReason.NO_VOID_VIEW;
        }
        if (state.isLastInventoryFull()) {
            return HaltReason.INVENTORY_FULL;
        }
        if (state.isLastOutputBlocked()) {
            return HaltReason.OUTPUT_BLOCKED;
        }
        if (state.isLastEnergyDemandTooHigh()) {
            return HaltReason.ENERGY_BUFFER;
        }
        if (state.isLastEnergyInsufficient()) {
            return HaltReason.ENERGY_DEFICIT;
        }
        return HaltReason.NONE;
    }
}
