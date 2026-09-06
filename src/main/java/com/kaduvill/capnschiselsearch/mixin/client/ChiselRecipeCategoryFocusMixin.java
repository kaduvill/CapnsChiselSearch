package com.kaduvill.capnschiselsearch.mixin.client;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.chisel.common.integration.jei.ChiselRecipeCategory;

@Mixin(value = ChiselRecipeCategory.class, remap = false)
public abstract class ChiselRecipeCategoryFocusMixin {

    @Redirect(
            method = "setRecipe(Lmezz/jei/api/gui/IRecipeLayout;Lteam/chisel/common/integration/jei/ChiselRecipeWrapper;Lmezz/jei/api/ingredients/IIngredients;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/gui/IRecipeLayout;getFocus()Lmezz/jei/api/recipe/IFocus;"
            )
    )
    private IFocus<?> capnschiselsearch$validateFocus(IRecipeLayout recipeLayout) {
        IFocus<?> focus = recipeLayout.getFocus();
        return focus == null || focus.getValue() instanceof ItemStack ? focus : null;
    }
}