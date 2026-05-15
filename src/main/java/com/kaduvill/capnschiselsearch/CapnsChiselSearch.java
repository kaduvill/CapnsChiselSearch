package com.kaduvill.capnschiselsearch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kaduvill.capnschiselsearch.network.PacketChiselSearchQuery;


@Mod(
        modid = CapnsChiselSearch.MODID,
        name = CapnsChiselSearch.NAME,
        version = CapnsChiselSearch.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        acceptableRemoteVersions = "*",
        //clientSideOnly = true,
        dependencies = CapnsChiselSearch.DEPENDENCIES
)
public class CapnsChiselSearch {
    public static final String MODID = "capnschiselsearch";
    public static final String NAME = "Capn's Chisel Search";
    public static final String VERSION = "GRADLETOKEN_VERSION";
    public static final String DEPENDENCIES = "required-after:mixinbooter;required-after:chisel";
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("{} initialized", NAME);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(
                PacketChiselSearchQuery.Handler.class,
                PacketChiselSearchQuery.class,
                0,
                Side.SERVER
        );
    }
}