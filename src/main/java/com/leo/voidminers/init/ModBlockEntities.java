package com.leo.voidminers.init;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.block.entity.ControllerBaseBE;
import com.leo.voidminers.block.entity.SolarPanelBaseBE;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VoidMiners.MODID);

    public static RegistryObject<BlockEntityType<ControllerBaseBE>> CONTROLLER_BASE_BE = BLOCK_ENTITIES.register("controller_base",
        () -> BlockEntityType.Builder.of(
            ControllerBaseBE::new,
            Blocks.STONE
        ).build(null)
    );

    public static RegistryObject<BlockEntityType<SolarPanelBaseBE>> SOLAR_PANEL_BASE_BE = BLOCK_ENTITIES.register("solar_panel_base",
        () -> BlockEntityType.Builder.of(
            SolarPanelBaseBE::new,
            Blocks.STONE
        ).build(null)
    );
}
