package mchorse.emoticons.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

public class GuiEmotes extends GuiScreen {
    private static final int LIST_WIDTH = 120;
    private static final int BOTTOM_HEIGHT = 50;
    private static final int BTN_HEIGHT = 20;

    private EmoteKeys keys;
    private AnimatorEmoticonsController controller;

    private List<String> allEmoteKeys = new ArrayList<String>();
    private List<String> filteredKeys = new ArrayList<String>();

    private int scrollOffset = 0;
    private int selectedIndex = -1;
    private int slotIndex = 0;

    private List<GuiButton> slotButtons = new ArrayList<GuiButton>();
    private String searchText = "";
    private boolean searchFocused = false;

    private float previewRotation = 0;
    private boolean isDragging = false;
    private int lastMouseX = 0;

    public GuiEmotes() {
        Minecraft mc = Minecraft.getMinecraft();
        this.keys = ClientProxy.keys;

        EmoteController ec = (EmoteController) EmoteController.get(mc.thePlayer);

        if (ec != null) {
            if (ec.controller == null) {
                ec.setupAnimator(mc.thePlayer);
            }

            if (ec.controller != null) {
                this.controller = new AnimatorEmoticonsController(
                        ec.controller.animationName,
                        ec.controller.userData);
                this.controller.fetchAnimation();

                if (mc.thePlayer instanceof AbstractClientPlayer) {
                    ResourceLocation skinTex = ((AbstractClientPlayer) mc.thePlayer).getLocationSkin();
                    if (this.controller.userConfig != null && this.controller.userConfig.meshes.containsKey("body")) {
                        this.controller.userConfig.meshes.get("body").texture = EmoteController.getSkin(skinTex);
                    }
                }
            }
        }

        for (Object key : Emotes.EMOTES.keySet()) {
            allEmoteKeys.add((String) key);
        }
        Collections.sort(allEmoteKeys, String.CASE_INSENSITIVE_ORDER);
        filteredKeys.addAll(allEmoteKeys);
    }

    @Override
    public void initGui() {
        this.slotButtons.clear();
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(10, LIST_WIDTH + 5, 34, 60, 20, "Play"));
        this.buttonList.add(new GuiButton(11, LIST_WIDTH + 70, 34, 60, 20, "Stop"));
        this.buttonList.add(new GuiButton(12, LIST_WIDTH + 135, 34, 90, 20, "Volume: " + (int)(this.keys.volume * 100) + "%"));
        int btnW = Math.max(30, (this.width - LIST_WIDTH - 10 - 5 * 5) / 6);
        int btnY = this.height - BOTTOM_HEIGHT + (BOTTOM_HEIGHT - BTN_HEIGHT) / 2;

        for (int i = 0; i < 6; i++) {
            String emoteName = this.keys.emotes.get(i);
            String label = (i + 1) + ": " + formatEmoteName(emoteName);
            int btnX = LIST_WIDTH + 5 + i * (btnW + 5);
            GuiButton btn = new GuiButton(i, btnX, btnY, btnW, BTN_HEIGHT, label);
            this.slotButtons.add(btn);
            this.buttonList.add(btn);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int id = button.id;
        if (id == 10 && selectedIndex >= 0 && selectedIndex < filteredKeys.size()) {
            EmoteKeyboardHandler.play((Emote) Emotes.EMOTES.get(filteredKeys.get(selectedIndex)), this.mc.thePlayer);
            this.mc.displayGuiScreen(null);
        }
        if (id == 11) {
            EmoteKeyboardHandler.play((Emote) Emotes.EMOTES.get(""), this.mc.thePlayer);
        }
        if (id == 12) {
            this.keys.volume -= 0.2f;
            if (this.keys.volume < -0.01f) {
                this.keys.volume = 1.0f;
            }
            button.displayString = "Volume: " + (int)(this.keys.volume * 100) + "%";
        }
        if (id >= 0 && id < 6) {
            this.slotIndex = id;
            playPreview(this.keys.emotes.get(slotIndex));
        }
    }

    @Override
    public void updateScreen() {
        if (this.controller != null) {
            this.controller.update(Minecraft.getMinecraft().thePlayer);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        Minecraft mc = Minecraft.getMinecraft();
        int listH = this.height - BOTTOM_HEIGHT - 32;
        int rowH = 12;
        int visibleRows = listH / rowH;
        int startY = 32;

        Gui.drawRect(0, 0, LIST_WIDTH, this.height, 0xCC000000);
        this.drawString(this.fontRendererObj, "Search:", 5, 5, 0xAAAAAA);
        Gui.drawRect(5, 15, LIST_WIDTH - 5, 29, 0xFF555555);
        Gui.drawRect(6, 16, LIST_WIDTH - 6, 28, 0xFF222222);
        String displaySearch = searchText + (searchFocused ? "_" : "");
        this.drawString(this.fontRendererObj, displaySearch, 8, 18, 0xFFFFFF);

        for (int i = 0; i < visibleRows; i++) {
            int idx = i + scrollOffset;
            if (idx >= filteredKeys.size())
                break;
            String key = filteredKeys.get(idx);
            boolean isSelected = (idx == selectedIndex);
            boolean isHovered = mouseX < LIST_WIDTH && mouseY >= startY + i * rowH && mouseY < startY + (i + 1) * rowH;
            int bg = isSelected ? 0xFF4444AA : (isHovered ? 0xFF333355 : 0x00000000);
            if (bg != 0)
                Gui.drawRect(0, startY + i * rowH, LIST_WIDTH, startY + (i + 1) * rowH, bg);
            this.drawString(this.fontRendererObj, formatEmoteName(key), 5, startY + i * rowH + 2,
                    isSelected ? 0xFFFFFF : 0xCCCCCC);
        }

        if (filteredKeys.size() > visibleRows) {
            int scrollH = Math.max(10, (int) ((float) visibleRows / filteredKeys.size() * listH));
            int scrollY = startY
                    + (int) ((float) scrollOffset / Math.max(1, filteredKeys.size() - visibleRows) * (listH - scrollH));
            Gui.drawRect(LIST_WIDTH - 4, scrollY, LIST_WIDTH - 1, scrollY + scrollH, 0xFF888888);
        }

        this.drawGradientRect(LIST_WIDTH, this.height - BOTTOM_HEIGHT - 20, this.width, this.height - BOTTOM_HEIGHT,
                0x00000000, 0x88000000);
        Gui.drawRect(LIST_WIDTH, this.height - BOTTOM_HEIGHT, this.width, this.height, 0x99000000);

        if (this.controller != null && this.controller.animation != null) {
            int previewX = LIST_WIDTH + (this.width - LIST_WIDTH) / 2;
            int previewY = this.height / 2 + 20;

            if (!Mouse.isButtonDown(0)) {
                isDragging = false;
            }
            if (isDragging) {
                previewRotation += (mouseX - lastMouseX);
                lastMouseX = mouseX;
            }

            drawModel(mc, previewX, previewY, 50, partialTicks);
        }

        if (selectedIndex >= 0 && selectedIndex < filteredKeys.size()) {
            String name = formatEmoteName(filteredKeys.get(selectedIndex));
            int cx = LIST_WIDTH + (this.width - LIST_WIDTH) / 2;
            int nameW = this.fontRendererObj.getStringWidth(name);
            GL11.glPushMatrix();
            GL11.glTranslatef(cx - nameW, this.height - BOTTOM_HEIGHT - 32, 0);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            this.drawString(this.fontRendererObj, name, 0, 0, 0xFFFFFF);
            GL11.glPopMatrix();
        }

        this.drawGradientRect(LIST_WIDTH, 0, this.width, 32, 0x88000000, 0x00000000);
        String title = "Emotes ";
        String subtitle = "(" + Emotes.EMOTES.size() + " total)";
        this.drawString(this.fontRendererObj, title, LIST_WIDTH + 5, 10, 0xFFFFFF);
        this.drawString(this.fontRendererObj, subtitle, LIST_WIDTH + 5 + this.fontRendererObj.getStringWidth(title), 10, 0xAAAAAA);

        this.drawString(this.fontRendererObj, "Editing slot: " + (slotIndex + 1), LIST_WIDTH + 5,
                this.height - BOTTOM_HEIGHT + 5, 0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawModel(Minecraft mc, int x, int y, int scale, float partialTicks) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 100.0f);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(previewRotation, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        this.controller.render(mc.thePlayer, 0, 0, 0, 0.0f, partialTicks);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (Exception e) {}

        if (mouseX >= 5 && mouseX < LIST_WIDTH - 5 && mouseY >= 15 && mouseY < 29) {
            searchFocused = true;
            return;
        }
        searchFocused = false;

        if (button == 0 && mouseX >= LIST_WIDTH && mouseY < this.height - BOTTOM_HEIGHT) {
            isDragging = true;
            lastMouseX = mouseX;
        }

        int startY = 32;
        int rowH = 12;
        if (button == 0 && mouseX >= 0 && mouseX < LIST_WIDTH && mouseY >= startY
                && mouseY < this.height - BOTTOM_HEIGHT) {
            int idx = (mouseY - startY) / rowH + scrollOffset;
            if (idx >= 0 && idx < filteredKeys.size()) {
                this.selectedIndex = idx;
                String key = filteredKeys.get(idx);
                if (slotIndex >= 0 && slotIndex < 6) {
                    this.keys.emotes.set(slotIndex, key);
                    updateSlotButton(slotIndex);
                }
                playPreview(key);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int listH = this.height - BOTTOM_HEIGHT - 32;
            int rowH = 12;
            int visibleRows = listH / rowH;
            int delta = wheel > 0 ? -3 : 3;
            scrollOffset = Math.max(0, Math.min(Math.max(0, filteredKeys.size() - visibleRows), scrollOffset + delta));
        }
    }

    @Override
    protected void keyTyped(char chr, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            return;
        }
        if (searchFocused) {
            if (keyCode == 14 && !searchText.isEmpty()) {
                searchText = searchText.substring(0, searchText.length() - 1);
                applyFilter();
            } else if (chr >= 32 && chr < 127) {
                searchText += chr;
                applyFilter();
            }
        } else {
            super.keyTyped(chr, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        EmoteKeys.toFile(this.keys, new File(ClientProxy.configFolder, "keys.json"));
    }

    private void applyFilter() {
        filteredKeys.clear();
        String lower = searchText.toLowerCase();
        for (String key : allEmoteKeys) {
            if (key.contains(lower) || formatEmoteName(key).toLowerCase().contains(lower)) {
                filteredKeys.add(key);
            }
        }
        scrollOffset = 0;
        selectedIndex = -1;
    }

    private void playPreview(String key) {
        if (this.controller == null || this.controller.animation == null || this.controller.config == null)
            return;
        try {
            ActionConfig actionConfig = this.controller.config.config.actions
                    .getConfig("emote_" + key);
            if (actionConfig != null) {
                this.controller.setEmote(this.controller.animation.createAction(null, actionConfig, true));
            }
        } catch (Exception e) {
        }
    }

    private void updateSlotButton(int idx) {
        if (idx < slotButtons.size()) {
            slotButtons.get(idx).displayString = (idx + 1) + ": " + formatEmoteName(this.keys.emotes.get(idx));
        }
    }

    public static String formatEmoteName(String key) {
        if (key == null || key.isEmpty())
            return "";

        String langKey = "emoticons.emote." + key;
        String translated = net.minecraft.client.resources.I18n.format(langKey);
        if (!translated.equals(langKey)) {
            return translated;
        }

        StringBuilder sb = new StringBuilder();
        for (String part : key.split("_")) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1)).append(' ');
            }
        }
        return sb.toString().trim();
    }
}
