package com.kaduvill.capnschiselsearch;

import net.minecraftforge.common.config.Config;

@Config(modid = CapnsChiselSearch.MODID, name = "capns_chisel_search")
public class CapnsChiselSearchConfig {
    @Config.Name("Enable Chisel GUI Search")
    @Config.Comment({
            "Adds a search field to the Chisel GUI.",
            "Non-matching variants are dimmed and ignored by mouse clicks.",
            "Client-side only."
    })
    public static boolean enableChiselGuiSearch = true;
}