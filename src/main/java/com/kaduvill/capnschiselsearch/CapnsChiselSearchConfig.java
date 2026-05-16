package com.kaduvill.capnschiselsearch;

import net.minecraftforge.common.config.Config;

@Config(modid = CapnsChiselSearch.MODID, name = "capns_chisel_search")
public class CapnsChiselSearchConfig {
    @Config.Name("Enable iChisel NBT Variant Fix")
    @Config.Comment({
            "Fixes the iChisel button refusing to chisel between variants that use the same item and metadata but different NBT.",
            "This is useful for Chisel groups made from items like Modular Machinery blueprints.",
            "Client-side only. When disabled, vanilla Chisel behavior is preserved."
    })
    public static boolean enableIChiselNbtVariantFix = true;
}