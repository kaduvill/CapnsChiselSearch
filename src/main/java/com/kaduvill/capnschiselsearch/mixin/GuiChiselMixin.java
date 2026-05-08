package com.kaduvill.capnschiselsearch.mixin;

import com.kaduvill.capnschiselsearch.CapnsChiselSearch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.client.gui.GuiChisel;

@Mixin(value = GuiChisel.class, remap = false)
public abstract class GuiChiselMixin {
    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    private void capnschiselsearch$initGui(CallbackInfo ci) {
        CapnsChiselSearch.LOGGER.info("Opened Chisel GUI - Capn's Chisel Search mixin is alive");
    }
}