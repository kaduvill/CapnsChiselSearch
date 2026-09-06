package com.kaduvill.capnschiselsearch;

import net.minecraftforge.common.config.Config;

@Config(modid = CapnsChiselSearch.MODID, name = "capns_chisel_search")
public class CapnsChiselSearchConfig {

    @Config.RequiresMcRestart
    @Config.Name("Enable iChisel NBT Variant Fix")
    @Config.Comment({
            "Fixes the iChisel button refusing to chisel between variants that use the same item and metadata but different NBT.",
            "This is useful for Chisel groups made from items like Modular Machinery blueprints.",
            "Client-side only."
    })
    public static boolean enableIChiselNbtVariantFix = true;

    @Config.RequiresMcRestart
    @Config.Name("Enable JEI Focus Safety Fix")
    @Config.Comment({
            "Prevents Chisel's JEI recipe category from crashing when given a non-ItemStack recipe focus.",
            "This includes malformed bookmark focuses from affected HEI versions (4.29.0 - 4.34.3 confirmed).",
            "Client-side only."
    })
    public static boolean enableJeiFocusSafetyFix = true;
}