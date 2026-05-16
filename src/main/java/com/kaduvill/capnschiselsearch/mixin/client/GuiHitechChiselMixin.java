package com.kaduvill.capnschiselsearch.mixin.client;

import com.kaduvill.capnschiselsearch.CapnsChiselSearchConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.chisel.client.gui.GuiHitechChisel;

@Mixin(value = GuiHitechChisel.class, remap = false)
public abstract class GuiHitechChiselMixin {
    @Redirect(
            method = "actionPerformed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;areItemsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean capnschiselsearch$compareHitechTargetIncludingNbt(ItemStack targetStack, ItemStack selectedStack) {
        if (!CapnsChiselSearchConfig.enableIChiselNbtVariantFix) {
            return ItemStack.areItemsEqual(targetStack, selectedStack);
        }

        return ItemStack.areItemsEqual(targetStack, selectedStack)
                && ItemStack.areItemStackTagsEqual(targetStack, selectedStack);
    }
}