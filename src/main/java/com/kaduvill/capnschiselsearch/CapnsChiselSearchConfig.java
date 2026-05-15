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

    @Config.Name("Enable Search Compaction")
    @Config.Comment({
            "When enabled, searches can compact matching Chisel variants into the visible slots.",
            "This changes the real Chisel selection inventory and requires this mod on the server in multiplayer.",
            "Keep disabled for client-only use."
    })
    public static boolean enableSearchCompaction = true;

    @Config.Name("Max Search Results")
    @Config.Comment({
            "This does not increase the number of visible GUI slots.",
            "Higher values allow larger virtual result lists but cost more CPU while searching.",
            "Default: 4096"
    })
    @Config.RangeInt(min = 63, max = 32767)
    public static int maxSearchResults = 4096;
}