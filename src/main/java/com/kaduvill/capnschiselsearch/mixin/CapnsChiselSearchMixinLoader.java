package com.kaduvill.capnschiselsearch.mixin;

import com.kaduvill.capnschiselsearch.CapnsChiselSearch;
import com.kaduvill.capnschiselsearch.CapnsChiselSearchConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public class CapnsChiselSearchMixinLoader implements ILateMixinLoader {

    private static final String CHISEL_GUI_SEARCH_MIXINS = "capnschiselsearch.search.mixins.json";
    private static final String ICHISEL_NBT_MIXINS = "capnschiselsearch.ichiselnbt.mixins.json";
    private static final String JEI_FOCUS_MIXINS = "capnschiselsearch.jeifocus.mixins.json";

    private static boolean configSynced;

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
                CHISEL_GUI_SEARCH_MIXINS,
                ICHISEL_NBT_MIXINS,
                JEI_FOCUS_MIXINS
        );
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (!FMLLaunchHandler.side().isClient() || !Loader.isModLoaded("chisel")) {
            return false;
        }

        switch (mixinConfig) {
            case CHISEL_GUI_SEARCH_MIXINS:
                return true;

            case ICHISEL_NBT_MIXINS:
                return syncConfig() && CapnsChiselSearchConfig.enableIChiselNbtVariantFix;

            case JEI_FOCUS_MIXINS:
                return Loader.isModLoaded("jei")
                        && syncConfig()
                        && CapnsChiselSearchConfig.enableJeiFocusSafetyFix;

            default:
                CapnsChiselSearch.LOGGER.warn("Unknown mixin config {}, not queueing it", mixinConfig);
                return false;
        }
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig) {
        CapnsChiselSearch.LOGGER.info("Queued mixin config: {}", mixinConfig);
    }

    private static boolean syncConfig() {
        if (configSynced) {return true;}

        if (!ConfigManager.hasConfigForMod(CapnsChiselSearch.MODID)) {
            CapnsChiselSearch.LOGGER.error("Config metadata unavailable; optional mixins will not be loaded");
            return false;
        }
        ConfigManager.sync(CapnsChiselSearch.MODID, Config.Type.INSTANCE);
        configSynced = true;
        return true;
    }
}