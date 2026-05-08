package com.kaduvill.capnschiselsearch.mixin.client;

import java.util.Locale;
import java.io.IOException;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import team.chisel.client.gui.GuiChisel;
import team.chisel.common.inventory.ContainerChisel;

@Mixin(value = GuiChisel.class, remap = false)
public abstract class GuiChiselMixin extends GuiContainer {
    private static final int SEARCH_FIELD_ID = 9000;
    private static final int SEARCH_FIELD_HEIGHT = 12;

    private static String capnschiselsearch$lastSearchText = "";

    @Shadow(remap = false)
    public ContainerChisel container;

    private GuiTextField capnschiselsearch$searchField;

    protected GuiChiselMixin(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    private void capnschiselsearch$initGui(CallbackInfo ci) {
        Keyboard.enableRepeatEvents(true);

        int x = capnschiselsearch$getSearchX();
        int y = capnschiselsearch$getSearchY();
        int width = capnschiselsearch$getSearchWidth();

        this.capnschiselsearch$searchField = new GuiTextField(
                SEARCH_FIELD_ID,
                this.fontRenderer,
                x,
                y,
                width,
                SEARCH_FIELD_HEIGHT
        );

        this.capnschiselsearch$searchField.setMaxStringLength(64);
        this.capnschiselsearch$searchField.setEnableBackgroundDrawing(true);
        this.capnschiselsearch$searchField.setText(capnschiselsearch$lastSearchText);
        this.capnschiselsearch$searchField.setFocused(false);
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"), remap = true)
    private void capnschiselsearch$onGuiClosed(CallbackInfo ci) {
        Keyboard.enableRepeatEvents(false);

        if (this.capnschiselsearch$searchField != null) {
            capnschiselsearch$lastSearchText = this.capnschiselsearch$searchField.getText();
        }
    }

    @Inject(method = "drawScreen", at = @At("TAIL"), remap = true)
    private void capnschiselsearch$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (this.capnschiselsearch$searchField == null) {
            return;
        }

        this.capnschiselsearch$searchField.drawTextBox();

        if (this.capnschiselsearch$searchField.getText().isEmpty() && !this.capnschiselsearch$searchField.isFocused()) {
            this.fontRenderer.drawString(
                    "Search...",
                    this.capnschiselsearch$searchField.x + 4,
                    this.capnschiselsearch$searchField.y + 3,
                    0x707070
            );
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (this.capnschiselsearch$searchField != null) {
            this.capnschiselsearch$searchField.updateCursorCounter();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.capnschiselsearch$searchField != null) {
            if (keyCode == Keyboard.KEY_F && capnschiselsearch$isCtrlDown()) {
                this.capnschiselsearch$searchField.setFocused(true);
                return;
            }

            if (this.capnschiselsearch$searchField.isFocused()) {
                if (keyCode == Keyboard.KEY_ESCAPE) {
                    this.capnschiselsearch$searchField.setFocused(false);
                    return;
                }

                String before = this.capnschiselsearch$searchField.getText();

                if (this.capnschiselsearch$searchField.textboxKeyTyped(typedChar, keyCode)) {
                    String after = this.capnschiselsearch$searchField.getText();

                    if (!before.equals(after)) {
                        capnschiselsearch$lastSearchText = after;
                    }

                    return;
                }

                return;
            }
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void capnschiselsearch$mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (this.capnschiselsearch$searchField != null) {
            this.capnschiselsearch$searchField.mouseClicked(mouseX, mouseY, mouseButton);

            if (capnschiselsearch$isMouseOverSearchField(mouseX, mouseY)) {
                ci.cancel();
                return;
            }
        }

        Slot slot = this.getSlotAtPosition(mouseX, mouseY);

        if (capnschiselsearch$isFilteredOut(slot)) {
            ci.cancel();
        }
    }

    @Inject(method = "drawSlot", at = @At("TAIL"), remap = true)
    private void capnschiselsearch$drawSlot(Slot slot, CallbackInfo ci) {
        capnschiselsearch$drawSearchOverlay(slot);
    }

    private boolean capnschiselsearch$isCtrlDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    private boolean capnschiselsearch$isHitechGui() {
        return ((Object) this).getClass().getName().endsWith("GuiHitechChisel");
    }

    private int capnschiselsearch$getSearchX() {
        return this.guiLeft + (capnschiselsearch$isHitechGui() ? 88 : 62);
    }

    private int capnschiselsearch$getSearchY() {
        return Math.max(4, this.guiTop - 14);
    }

    private int capnschiselsearch$getSearchWidth() {
        return capnschiselsearch$isHitechGui() ? 162 : 180;
    }

    private boolean capnschiselsearch$isMouseOverSearchField(int mouseX, int mouseY) {
        if (this.capnschiselsearch$searchField == null) {
            return false;
        }

        return mouseX >= this.capnschiselsearch$searchField.x
                && mouseX < this.capnschiselsearch$searchField.x + this.capnschiselsearch$searchField.width
                && mouseY >= this.capnschiselsearch$searchField.y
                && mouseY < this.capnschiselsearch$searchField.y + this.capnschiselsearch$searchField.height;
    }

    private boolean capnschiselsearch$isSelectionSlot(Slot slot) {
        return slot != null
                && this.container != null
                && this.container.getInventoryChisel() != null
                && slot.slotNumber >= 0
                && slot.slotNumber < this.container.getInventoryChisel().size;
    }

    private boolean capnschiselsearch$isFilteredOut(Slot slot) {
        return capnschiselsearch$isSelectionSlot(slot)
                && !capnschiselsearch$getNeedle().isEmpty()
                && slot.getHasStack()
                && !capnschiselsearch$matchesSearch(slot.getStack());
    }

    private boolean capnschiselsearch$matchesSearch(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        String needle = capnschiselsearch$getNeedle();

        if (needle.isEmpty()) {
            return true;
        }

        ResourceLocation registryName = stack.getItem().getRegistryName();

        String registry = registryName == null ? "" : registryName.toString();
        String modid = registryName == null ? "" : registryName.getResourceDomain();
        String path = registryName == null ? "" : registryName.getResourcePath();

        String haystack = (
                stack.getDisplayName() + " " +
                        stack.getItem().getUnlocalizedName(stack) + " " +
                        registry + " " +
                        modid + " " +
                        path
        ).toLowerCase(Locale.ROOT);

        return haystack.contains(needle);
    }

    private String capnschiselsearch$getNeedle() {
        if (this.capnschiselsearch$searchField == null) {
            return "";
        }

        return this.capnschiselsearch$searchField.getText().trim().toLowerCase(Locale.ROOT);
    }

    private void capnschiselsearch$drawSearchOverlay(Slot slot) {
        if (!capnschiselsearch$isSelectionSlot(slot)) {
            return;
        }

        String needle = capnschiselsearch$getNeedle();

        if (needle.isEmpty() || !slot.getHasStack()) {
            return;
        }

        int x = slot.xPos;
        int y = slot.yPos;

        if (capnschiselsearch$matchesSearch(slot.getStack())) {
            int color = 0xCC55FF55;

            drawRect(x - 1, y - 1, x + 17, y, color);
            drawRect(x - 1, y + 16, x + 17, y + 17, color);
            drawRect(x - 1, y - 1, x, y + 17, color);
            drawRect(x + 16, y - 1, x + 17, y + 17, color);
        } else {
            drawRect(x, y, x + 16, y + 16, 0xAA000000);
        }
    }
}