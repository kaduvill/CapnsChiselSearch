package com.kaduvill.capnschiselsearch.mixin;

import com.kaduvill.capnschiselsearch.CapnsChiselSearch;
import com.kaduvill.capnschiselsearch.CapnsChiselSearchConfig;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class CapnsChiselSearchMixinLoader implements ILateMixinLoader {
    private static final String CHISEL_GUI_MIXINS = "capnschiselsearch.mixins.json";

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList(CHISEL_GUI_MIXINS);
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        boolean shouldQueue = Loader.isModLoaded("chisel")
                && CapnsChiselSearchConfig.enableChiselGuiSearch;

        if (shouldQueue) {
            CapnsChiselSearch.LOGGER.info("Queueing mixin config: {}", mixinConfig);
        }

        return shouldQueue;
    }
}