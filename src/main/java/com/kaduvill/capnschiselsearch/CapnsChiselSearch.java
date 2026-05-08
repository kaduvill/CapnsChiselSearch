package com.kaduvill.capnschiselsearch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = CapnsChiselSearch.MODID,
        name = CapnsChiselSearch.NAME,
        version = CapnsChiselSearch.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        acceptableRemoteVersions = "*",
        clientSideOnly = true,
        dependencies = CapnsChiselSearch.DEPENDENCIES
)
public class CapnsChiselSearch {
    public static final String MODID = "capnschiselsearch";
    public static final String NAME = "Capn's Chisel Search";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String DEPENDENCIES = "required-after:mixinbooter;required-after:chisel";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("{} initialized", NAME);
    }
}