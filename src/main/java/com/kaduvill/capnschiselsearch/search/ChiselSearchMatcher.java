package com.kaduvill.capnschiselsearch.search;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import team.chisel.api.block.ICarvable;
import team.chisel.api.block.VariationData;

public final class ChiselSearchMatcher {
    private ChiselSearchMatcher() {
    }

    public static boolean matchesForCompaction(ItemStack stack, String query) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        String needle = normalize(query);

        if (needle.isEmpty()) {
            return true;
        }

        char operator = needle.charAt(0);

        if (operator == '@' || operator == '&') {
            String operatorNeedle = needle.substring(1).trim();

            if (operatorNeedle.isEmpty()) {
                return true;
            }

            switch (operator) {
                case '@':
                    return getModSearchText(stack).contains(operatorNeedle);

                case '&':
                    return getRegistrySearchText(stack).contains(operatorNeedle);

                default:
                    return false;
            }
        }

        // Server-safe default compaction search.
        // Do not use client tooltip logic here.
        return getDefaultSearchText(stack).contains(needle);
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return TextFormatting.getTextWithoutFormattingCodes(value)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static String getDefaultSearchText(ItemStack stack) {
        StringBuilder builder = new StringBuilder(128);

        appendSearchPart(builder, stack.getDisplayName());
        appendChiselVariationSearchText(builder, stack);

        return builder.toString().toLowerCase(Locale.ROOT);
    }

    private static void appendChiselVariationSearchText(StringBuilder builder, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Block block = Block.getBlockFromItem(stack.getItem());

        if (!(block instanceof ICarvable)) {
            return;
        }

        ICarvable carvable = (ICarvable) block;

        try {
            VariationData variationData = carvable.getVariationData(stack.getMetadata());

            if (variationData == null) {
                return;
            }

            appendSearchPart(builder, variationData.name);
            appendSearchPart(builder, variationData.path);
            appendSearchPart(builder, variationData.group);
        } catch (Throwable ignored) {
            // Some external Chisel integrations may have unusual metadata/variation handling.
            // Compaction search should never crash the container.
        }
    }

    private static String getModSearchText(ItemStack stack) {
        StringBuilder builder = new StringBuilder(64);

        ResourceLocation registryName = stack.getItem().getRegistryName();

        if (registryName == null) {
            return "";
        }

        String modid = registryName.getResourceDomain();

        appendSearchPart(builder, modid);

        ModContainer modContainer = Loader.instance().getIndexedModList().get(modid);

        if (modContainer != null) {
            appendSearchPart(builder, modContainer.getName());
        }

        return builder.toString().toLowerCase(Locale.ROOT);
    }

    private static String getRegistrySearchText(ItemStack stack) {
        ResourceLocation registryName = stack.getItem().getRegistryName();

        if (registryName == null) {
            return "";
        }

        return registryName.toString().toLowerCase(Locale.ROOT);
    }

    private static void appendSearchPart(StringBuilder builder, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String cleanValue = TextFormatting.getTextWithoutFormattingCodes(value);

        if (cleanValue == null || cleanValue.isEmpty()) {
            return;
        }

        builder.append(' ').append(cleanValue);
    }
}