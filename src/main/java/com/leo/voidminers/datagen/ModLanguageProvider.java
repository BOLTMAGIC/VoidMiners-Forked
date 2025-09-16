package com.leo.voidminers.datagen;

import com.leo.voidminers.VoidMiners;
import com.leo.voidminers.init.ModBlocks;
import com.leo.voidminers.init.ModItems;
import com.leo.voidminers.init.CrystalSet;
import com.leo.voidminers.init.SolarSet;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, VoidMiners.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        this.add(VoidMiners.MODID + ".itemGroup.items", "Items");

        this.add(ModItems.STRUCTURE_HELPER.get(), "Structure Helper");
        this.add(ModItems.ULTIMATE_STELLAR_CORE.get(), "Ultimate Stellar Core");

        this.add("gui." + VoidMiners.MODID + ".miner", "Tier %d Miner");
        this.add("gui." + VoidMiners.MODID + ".miners", "Miners");

        this.add("tooltip." + VoidMiners.MODID + ".controller.working", "Miner is working correctly.");

        this.add("tooltip." + VoidMiners.MODID + ".controller.energy", "Energy: %d rf/t");

        this.add("tooltip." + VoidMiners.MODID + ".controller.duration", "Duration: %d ticks");

        this.add("tooltip." + VoidMiners.MODID + ".controller.not_working", "Miner is not working correctly!\nCheck if the miner has at least 1 empty slot, and that the required energy is smaller than the total energy storage");

        this.add("tooltip." + VoidMiners.MODID + ".controller.not_active", "Miner is assembled correctly, but is not active.\nMake sure that the miner can see the void / bedrock!\nThe distance between bedrock and miner does NOT matter, only that the center block can see bedrock!");

        this.add("tooltip." + VoidMiners.MODID + ".controller.missing_structure", "Miner is not assembled correctly, shift r-click the block for a guide. Total needed blocks: ");

        this.add("tooltip." + VoidMiners.MODID + ".solar_panel.missing_structure", "Solar Panel is not assembled correctly, shift r-click the block for a guide. Total needed blocks: ");

        this.add("tooltip." + VoidMiners.MODID + ".structure.weight", "Weight: %s");

        this.add(ModBlocks.FRAME_BASE.get(), "Frame Base");

        this.add(ModBlocks.STRUCTURE_PANEL.get(), "Structure Panel");

        this.add(ModBlocks.GLASS_PANEL.get(), "Glass Panel");

        this.add(ModBlocks.NULL_MOD.get(), "Null Modifier");

        for (CrystalSet set : CrystalSet.sets()) {
            // Skip crystal translation if it's null (like ultimate set)
            if (set.CRYSTAL != null) {
                this.add(set.CRYSTAL.get(), cFL(set.name));
            }

            this.add(set.CRYSTAL_BLOCK.get(), cFL(set.name) + " Block");

            this.add(set.MINER_CONTROLLER.get(), cFL(set.name) + " Miner");

            this.add(set.FRAME.get(), cFL(set.name) + " Frame");

            this.add(set.ENERGY_MOD.get(), cFL(set.name) + " Energy Modifier");

            this.add(set.SPEED_MOD.get(), cFL(set.name) + " Speed Modifier");

            this.add(set.ITEM_MOD.get(), cFL(set.name) + " Item Modifier");
        }

        this.add("tooltip." + VoidMiners.MODID + ".energy", "Energy Modifier: %dx");

        this.add("tooltip." + VoidMiners.MODID + ".speed", "Duration Modifier: %dx");

        this.add("tooltip." + VoidMiners.MODID + ".item", "Item Amount Modifier: %dx");

        this.add("tooltip." + VoidMiners.MODID + "creative_only", "Creative Only");

        this.add("minecraft.overworld", "Overworld");
        this.add("minecraft.the_nether", "The Nether");
        this.add("minecraft.the_end", "The End");
        
        // Solar panels translations
        for (SolarSet set : SolarSet.sets()) {
            // Skip solar crystal translation if it's null (like ultimate set)
            if (set.SOLAR_CRYSTAL != null) {
                this.add(set.SOLAR_CRYSTAL.get(), "Solar " + cFL(set.name));
            }
            this.add(set.SOLAR_CRYSTAL_BLOCK.get(), "Solar " + cFL(set.name) + " Block");
            this.add(set.SOLAR_PANEL_CONTROLLER.get(), "Solar " + cFL(set.name) + " Panel");
            this.add(set.SOLAR_FRAME.get(), "Solar " + cFL(set.name) + " Frame");
            this.add(set.EFFICIENCY_MOD.get(), "Solar " + cFL(set.name) + " Efficiency Modifier");
            this.add(set.WEATHER_MOD.get(), "Solar " + cFL(set.name) + " Weather Modifier");
        }
    }


    /**
     * Capitalizes first letter of a string
     *
     * @param input the string to capitalize e.g. "alpha"
     * @return the string capitalized e.g. "Alpha"
     */
    public static String cFL(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}