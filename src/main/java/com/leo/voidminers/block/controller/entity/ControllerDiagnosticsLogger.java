package com.leo.voidminers.block.controller.entity;

import com.leo.voidminers.VoidMiners;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

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
        if (!VoidMiners.LOGGER.isDebugEnabled()) {
            return;
        }

        if (previousDimensionBlockState == null || previousDimensionBlockState != blockedByDimension) {
            logDebug(name, structure, position, "Dimension access {}", blockedByDimension ? "blocked" : "permitted");
        }

        if (previousStructureState == null || previousStructureState != foundStructure) {
            logDebug(name, structure, position, "Structure detection {}", foundStructure ? "succeeded" : "lost");
        }

        if (previousVoidViewState == null || previousVoidViewState != hasVoidView) {
            logDebug(name, structure, position, "Void exposure {}", hasVoidView ? "established" : "lost");
        }

        if (previousActiveState == null || previousActiveState != active) {
            logDebug(name, structure, position, "Active state {}", active ? "enabled" : "disabled");
        }

        HaltReason haltReason = determineHaltReason(blockedByDimension, foundStructure, hasVoidView, state);

        if (previousWorkingState == null || previousWorkingState != working) {
            if (working) {
                logDebug(name, structure, position, "Cycle running (demand={} RF/t, stored={} / {} RF)", energyDemand, energyStored, energyCapacity);
            } else {
                logDebug(name, structure, position,
                        "Cycle halted: {}, demand={} RF/t, stored={} / {} RF, inventoryFull={}, outputBlocked={}, bufferExceeded={}, energyLow={}",
                        haltReason.description(),
                        energyDemand,
                        energyStored,
                        energyCapacity,
                        state.isLastInventoryFull(),
                        state.isLastOutputBlocked(),
                        state.isLastEnergyDemandTooHigh(),
                        state.isLastEnergyInsufficient());
            }
        } else if (!working && previousHaltReason != haltReason) {
            logDebug(name, structure, position,
                    "Halt reason changed to {} (inventoryFull={}, outputBlocked={}, bufferExceeded={}, energyLow={})",
                    haltReason.description(),
                    state.isLastInventoryFull(),
                    state.isLastOutputBlocked(),
                    state.isLastEnergyDemandTooHigh(),
                    state.isLastEnergyInsufficient());
        }

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

    private void logDebug(String name,
                          ResourceLocation structure,
                          BlockPos position,
                          String message,
                          Object... args) {
        if (!VoidMiners.LOGGER.isDebugEnabled()) {
            return;
        }

        Object[] contextualArgs = new Object[args.length + 2];
        contextualArgs[0] = name != null ? name : (structure != null ? structure.toString() : "unconfigured");
        contextualArgs[1] = position;
        System.arraycopy(args, 0, contextualArgs, 2, args.length);

        VoidMiners.LOGGER.debug("[{} @ {}] " + message, contextualArgs);
    }
}
