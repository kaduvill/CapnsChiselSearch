package com.kaduvill.capnschiselsearch.mixin.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.kaduvill.capnschiselsearch.CapnsChiselSearch;
import com.kaduvill.capnschiselsearch.CapnsChiselSearchConfig;
import com.kaduvill.capnschiselsearch.api.IChiselSearchCompaction;
import com.kaduvill.capnschiselsearch.search.ChiselSearchMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.common.inventory.ContainerChisel;
import team.chisel.common.inventory.InventoryChiselSelection;

@Mixin(value = InventoryChiselSelection.class, remap = false)
public abstract class InventoryChiselSelectionMixin implements IChiselSearchCompaction {
    private String capnschiselsearch$searchQuery = "";
    private int capnschiselsearch$searchOffset = 0;

    @Shadow(remap = false)
    public int size;

    @Shadow(remap = false)
    public int activeVariations;

    @Shadow(remap = false)
    @Nullable
    ContainerChisel container;

    @Shadow(remap = false)
    public abstract void clearItems();

    @Shadow(remap = false)
    public abstract ItemStack getStackInSpecialSlot();

    @Shadow(remap = false)
    public abstract void setInventorySlotContents(int slot, ItemStack stack);

    @Inject(method = "updateItems", at = @At("TAIL"), remap = false)
    private void capnschiselsearch$applyVirtualVariantWindow(CallbackInfo ci) {
        if (!CapnsChiselSearchConfig.enableSearchCompaction) {
            return;
        }

        if (this.container == null) {
            return;
        }

        ItemStack chiseledItem = getStackInSpecialSlot();

        if (chiseledItem == null || chiseledItem.isEmpty()) {
            return;
        }

        List<ItemStack> allVariations = this.container.getCarving().getItemsForChiseling(chiseledItem);

        if (allVariations == null || allVariations.isEmpty()) {
            return;
        }

        String query = capnschiselsearch$getSearchQuery();
        boolean hasQuery = !query.isEmpty();

        /*
         * If there is no query and the full group fits in the normal Chisel grid,
         * keep vanilla behavior.
         */
        if (!hasQuery && allVariations.size() <= this.size) {
            this.capnschiselsearch$searchOffset = 0;
            return;
        }

        List<ItemStack> virtualVariations = new ArrayList<>();

        for (ItemStack variation : allVariations) {
            if (virtualVariations.size() >= CapnsChiselSearchConfig.maxSearchResults) {
                break;
            }

            if (!hasQuery || ChiselSearchMatcher.matchesForCompaction(variation, query)) {
                virtualVariations.add(variation);
            }
        }

        clearItems();
        this.activeVariations = 0;

        if (virtualVariations.isEmpty()) {
            this.capnschiselsearch$searchOffset = 0;
            return;
        }

        int maxOffset = Math.max(0, virtualVariations.size() - this.size);

        if (this.capnschiselsearch$searchOffset < 0) {
            this.capnschiselsearch$searchOffset = 0;
        }

        if (this.capnschiselsearch$searchOffset > maxOffset) {
            this.capnschiselsearch$searchOffset = maxOffset;
        }

        for (int i = this.capnschiselsearch$searchOffset; i < virtualVariations.size(); i++) {
            if (this.activeVariations >= this.size) {
                break;
            }

            setInventorySlotContents(this.activeVariations, virtualVariations.get(i));
            this.activeVariations++;
        }

        CapnsChiselSearch.LOGGER.debug(
                "Virtual Chisel variants: query '{}', showing {} of {}, offset {}, total {}, cap {}",
                query,
                this.activeVariations,
                virtualVariations.size(),
                this.capnschiselsearch$searchOffset,
                allVariations.size(),
                CapnsChiselSearchConfig.maxSearchResults
        );
    }

    @Override
    public void capnschiselsearch$setSearchQuery(String query) {
        String clean = query == null ? "" : TextFormatting.getTextWithoutFormattingCodes(query).trim();

        if (clean.length() > 64) {
            clean = clean.substring(0, 64);
        }

        if (!clean.equals(this.capnschiselsearch$searchQuery)) {
            this.capnschiselsearch$searchOffset = 0;
        }

        this.capnschiselsearch$searchQuery = clean;
    }

    @Override
    public String capnschiselsearch$getSearchQuery() {
        return this.capnschiselsearch$searchQuery == null ? "" : this.capnschiselsearch$searchQuery.trim();
    }

    @Override
    public void capnschiselsearch$scrollSearchOffset(int delta) {
        if (delta == 0) {
            return;
        }

        this.capnschiselsearch$searchOffset += delta;

        if (this.capnschiselsearch$searchOffset < 0) {
            this.capnschiselsearch$searchOffset = 0;
        }
    }

    @Override
    public int capnschiselsearch$getSearchOffset() {
        return this.capnschiselsearch$searchOffset;
    }

    @Override
    public boolean capnschiselsearch$isCompacting() {
        return CapnsChiselSearchConfig.enableSearchCompaction && !capnschiselsearch$getSearchQuery().isEmpty();
    }
}