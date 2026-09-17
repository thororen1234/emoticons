package mchorse.emoticons.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.EmoteAPI;
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

/**
 * Emote selection GUI
 *
 * Vanilla-GUI-API replacement for the removed 3-file McLib-framework-based
 * split (client/gui/GuiEmotes.java, GuiEmotesList.java,
 * GuiEmoticonsModelRenderer.java), matching the standalone ports'
 * single-file pattern.
 */
public class GuiEmotes extends GuiScreen
{
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

    private int settingsX;
    private int settingsY;

    private float previewRotation = 0;
    private boolean isDragging = false;
    private int lastMouseX = 0;

    public GuiEmotes()
    {
        Minecraft mc = Minecraft.getMinecraft();
        this.keys = ClientProxy.keys;

        EmoteController ec = (EmoteController) EmoteController.get(mc.player);

        if (ec != null)
        {
            if (ec.animator == null)
            {
                ec.setupAnimator(mc.player);
            }

            if (ec.animator != null)
            {
                this.controller = new AnimatorEmoticonsController(ec.animator.animationName, ec.animator.userData);
                this.controller.fetchAnimation();

                if (mc.player instanceof AbstractClientPlayer)
                {
                    ResourceLocation skinTex = ((AbstractClientPlayer) mc.player).getLocationSkin();

                    if (this.controller.userConfig != null && this.controller.userConfig.meshes.containsKey("body"))
                    {
                        this.controller.userConfig.meshes.get("body").texture = skinTex;
                    }
                }
            }
        }

        for (Object key : Emotes.EMOTES.keySet())
        {
            allEmoteKeys.add((String) key);
        }

        Collections.sort(allEmoteKeys, String.CASE_INSENSITIVE_ORDER);
        filteredKeys.addAll(allEmoteKeys);
    }

    @Override
    public void initGui()
    {
        this.slotButtons.clear();
        this.buttonList.clear();

        this.settingsX = LIST_WIDTH + 5;
        this.settingsY = 34;

        this.addSetting(13, animLabel(), "Anim: Off");
        this.addSetting(14, stopOnMoveLabel(), "Stop on Move: Off");
        this.addSetting(15, thirdPersonLabel(), "3rd Person: Off");
        this.addSetting(16, soundsLabel(), "Sounds: Off");
        this.addSetting(12, volumeLabel(), "Volume: 100%");
        this.addSetting(17, modelLabel(), "Model: simple_plus");

        int playY = this.settingsY + BTN_HEIGHT + 4;

        this.buttonList.add(new GuiButton(10, LIST_WIDTH + 5, playY, 60, 20, "Play"));
        this.buttonList.add(new GuiButton(11, LIST_WIDTH + 70, playY, 60, 20, "Stop"));

        int btnW = Math.max(30, (this.width - LIST_WIDTH - 10 - 5 * 5) / 6);
        int btnY = this.height - BOTTOM_HEIGHT + (BOTTOM_HEIGHT - BTN_HEIGHT) / 2;

        for (int i = 0; i < 6; i++)
        {
            String emoteName = this.keys.emotes.get(i);
            String label = (i + 1) + ": " + formatEmoteName(emoteName);
            int btnX = LIST_WIDTH + 5 + i * (btnW + 5);
            GuiButton btn = new GuiButton(i, btnX, btnY, btnW, BTN_HEIGHT, label);

            this.slotButtons.add(btn);
            this.buttonList.add(btn);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        int id = button.id;

        if (id == 10 && selectedIndex >= 0 && selectedIndex < filteredKeys.size())
        {
            EmoteAPI.setEmoteClient(filteredKeys.get(selectedIndex), this.mc.player);
            this.mc.displayGuiScreen(null);
        }

        if (id == 11)
        {
            EmoteAPI.setEmoteClient("", this.mc.player);
        }

        if (id == 12)
        {
            ClientConfig.instance.volume -= 0.2F;

            if (ClientConfig.instance.volume < -0.01F)
            {
                ClientConfig.instance.volume = 1.0F;
            }

            button.displayString = volumeLabel();
            ClientConfig.save();
        }

        if (id == 13)
        {
            ClientConfig.instance.disableAnimations = !ClientConfig.instance.disableAnimations;
            button.displayString = animLabel();
            ClientConfig.save();
        }

        if (id == 14)
        {
            ClientConfig.instance.stopOnMove = !ClientConfig.instance.stopOnMove;
            button.displayString = stopOnMoveLabel();
            ClientConfig.save();
        }

        if (id == 15)
        {
            ClientConfig.instance.thirdPerson = !ClientConfig.instance.thirdPerson;
            button.displayString = thirdPersonLabel();
            ClientConfig.save();
        }

        if (id == 16)
        {
            ClientConfig.instance.sounds = !ClientConfig.instance.sounds;
            button.displayString = soundsLabel();
            ClientConfig.save();
        }

        if (id == 17)
        {
            ClientConfig.instance.model = nextModel();
            button.displayString = modelLabel();
            ClientConfig.save();
            this.refreshModel();
        }

        if (id >= 0 && id < 6)
        {
            this.slotIndex = id;
            playPreview(this.keys.emotes.get(slotIndex));
        }
    }

    @Override
    public void updateScreen()
    {
        if (this.controller != null)
        {
            this.controller.update(Minecraft.getMinecraft().player);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        Minecraft mc = Minecraft.getMinecraft();
        int listH = this.height - BOTTOM_HEIGHT - 32;
        int rowH = 12;
        int visibleRows = listH / rowH;
        int startY = 32;

        Gui.drawRect(0, 0, LIST_WIDTH, this.height, 0xCC000000);
        this.drawString(this.fontRenderer, "Search:", 5, 5, 0xAAAAAA);
        Gui.drawRect(5, 15, LIST_WIDTH - 5, 29, 0xFF555555);
        Gui.drawRect(6, 16, LIST_WIDTH - 6, 28, 0xFF222222);

        String displaySearch = searchText + (searchFocused ? "_" : "");
        this.drawString(this.fontRenderer, displaySearch, 8, 18, 0xFFFFFF);

        for (int i = 0; i < visibleRows; i++)
        {
            int idx = i + scrollOffset;

            if (idx >= filteredKeys.size())
            {
                break;
            }

            String key = filteredKeys.get(idx);
            boolean isSelected = (idx == selectedIndex);
            boolean isHovered = mouseX < LIST_WIDTH && mouseY >= startY + i * rowH && mouseY < startY + (i + 1) * rowH;
            int bg = isSelected ? 0xFF4444AA : (isHovered ? 0xFF333355 : 0x00000000);

            if (bg != 0)
            {
                Gui.drawRect(0, startY + i * rowH, LIST_WIDTH, startY + (i + 1) * rowH, bg);
            }

            this.drawString(this.fontRenderer, formatEmoteName(key), 5, startY + i * rowH + 2, isSelected ? 0xFFFFFF : 0xCCCCCC);
        }

        if (filteredKeys.size() > visibleRows)
        {
            int scrollH = Math.max(10, (int) ((float) visibleRows / filteredKeys.size() * listH));
            int scrollY = startY + (int) ((float) scrollOffset / Math.max(1, filteredKeys.size() - visibleRows) * (listH - scrollH));

            Gui.drawRect(LIST_WIDTH - 4, scrollY, LIST_WIDTH - 1, scrollY + scrollH, 0xFF888888);
        }

        this.drawGradientRect(LIST_WIDTH, this.height - BOTTOM_HEIGHT - 20, this.width, this.height - BOTTOM_HEIGHT, 0x00000000, 0x88000000);
        Gui.drawRect(LIST_WIDTH, this.height - BOTTOM_HEIGHT, this.width, this.height, 0x99000000);

        if (this.controller != null && this.controller.animation != null)
        {
            int previewX = LIST_WIDTH + (this.width - LIST_WIDTH) / 2;
            int previewY = this.height / 2 + 20;

            if (!Mouse.isButtonDown(0))
            {
                isDragging = false;
            }

            if (isDragging)
            {
                previewRotation += (mouseX - lastMouseX);
                lastMouseX = mouseX;
            }

            drawModel(mc, previewX, previewY, 50, partialTicks);
        }

        if (selectedIndex >= 0 && selectedIndex < filteredKeys.size())
        {
            String name = formatEmoteName(filteredKeys.get(selectedIndex));
            int cx = LIST_WIDTH + (this.width - LIST_WIDTH) / 2;
            int nameW = this.fontRenderer.getStringWidth(name);

            GL11.glPushMatrix();
            GL11.glTranslatef(cx - nameW, this.height - BOTTOM_HEIGHT - 32, 0);
            GL11.glScalef(2.0f, 2.0f, 2.0f);
            this.drawString(this.fontRenderer, name, 0, 0, 0xFFFFFF);
            GL11.glPopMatrix();
        }

        this.drawGradientRect(LIST_WIDTH, 0, this.width, 32, 0x88000000, 0x00000000);

        String title = "Emotes ";
        String subtitle = "(" + Emotes.EMOTES.size() + " total)";

        this.drawString(this.fontRenderer, title, LIST_WIDTH + 5, 10, 0xFFFFFF);
        this.drawString(this.fontRenderer, subtitle, LIST_WIDTH + 5 + this.fontRenderer.getStringWidth(title), 10, 0xAAAAAA);
        this.drawString(this.fontRenderer, "Editing slot: " + (slotIndex + 1), LIST_WIDTH + 5, this.height - BOTTOM_HEIGHT + 5, 0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawModel(Minecraft mc, int x, int y, int scale, float partialTicks)
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 100.0f);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(previewRotation, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        this.controller.render(mc.player, 0, 0, 0, 0.0f, partialTicks);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException
    {
        try
        {
            super.mouseClicked(mouseX, mouseY, button);
        }
        catch (Exception e)
        {}

        if (mouseX >= 5 && mouseX < LIST_WIDTH - 5 && mouseY >= 15 && mouseY < 29)
        {
            searchFocused = true;

            return;
        }

        searchFocused = false;

        if (button == 0 && mouseX >= LIST_WIDTH && mouseY < this.height - BOTTOM_HEIGHT)
        {
            isDragging = true;
            lastMouseX = mouseX;
        }

        int startY = 32;
        int rowH = 12;

        if (button == 0 && mouseX >= 0 && mouseX < LIST_WIDTH && mouseY >= startY && mouseY < this.height - BOTTOM_HEIGHT)
        {
            int idx = (mouseY - startY) / rowH + scrollOffset;

            if (idx >= 0 && idx < filteredKeys.size())
            {
                this.selectedIndex = idx;

                String key = filteredKeys.get(idx);

                if (slotIndex >= 0 && slotIndex < 6)
                {
                    this.keys.emotes.set(slotIndex, key);
                    updateSlotButton(slotIndex);
                }

                playPreview(key);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int wheel = Mouse.getEventDWheel();

        if (wheel != 0)
        {
            int listH = this.height - BOTTOM_HEIGHT - 32;
            int rowH = 12;
            int visibleRows = listH / rowH;
            int delta = wheel > 0 ? -3 : 3;

            scrollOffset = Math.max(0, Math.min(Math.max(0, filteredKeys.size() - visibleRows), scrollOffset + delta));
        }
    }

    @Override
    protected void keyTyped(char chr, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen(null);

            return;
        }

        if (searchFocused)
        {
            if (keyCode == 14 && !searchText.isEmpty())
            {
                searchText = searchText.substring(0, searchText.length() - 1);
                applyFilter();
            }
            else if (chr >= 32 && chr < 127)
            {
                searchText += chr;
                applyFilter();
            }
        }
        else
        {
            super.keyTyped(chr, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        EmoteKeys.toFile(this.keys, new File(ClientProxy.configFolder, "keys.json"));
        ClientConfig.save();
    }

    private void applyFilter()
    {
        filteredKeys.clear();

        String lower = searchText.toLowerCase();

        for (String key : allEmoteKeys)
        {
            if (key.contains(lower) || formatEmoteName(key).toLowerCase().contains(lower))
            {
                filteredKeys.add(key);
            }
        }

        scrollOffset = 0;
        selectedIndex = -1;
    }

    private void playPreview(String key)
    {
        if (this.controller == null || this.controller.animation == null || this.controller.config == null)
        {
            return;
        }

        try
        {
            ActionConfig actionConfig = this.controller.config.config.actions.getConfig("emote_" + key);

            if (actionConfig != null)
            {
                this.controller.setEmote(this.controller.animation.createAction(null, actionConfig, true));
            }
        }
        catch (Exception e)
        {}
    }

    private void updateSlotButton(int idx)
    {
        if (idx < slotButtons.size())
        {
            slotButtons.get(idx).displayString = (idx + 1) + ": " + formatEmoteName(this.keys.emotes.get(idx));
        }
    }

    /**
     * Lay out a settings button, wrapping onto the next row when it doesn't
     * fit anymore. The width is derived from the longest label the button
     * can ever show, so that it doesn't jump around when toggled
     */
    private void addSetting(int id, String label, String widest)
    {
        int w = this.fontRenderer.getStringWidth(widest) + 12;

        if (this.settingsX + w > this.width - 5)
        {
            this.settingsX = LIST_WIDTH + 5;
            this.settingsY += BTN_HEIGHT + 4;
        }

        this.buttonList.add(new GuiButton(id, this.settingsX, this.settingsY, w, BTN_HEIGHT, label));

        this.settingsX += w + 5;
    }

    /**
     * Rebuild the preview (and the player's own animator) after the model
     * style was changed
     */
    private void refreshModel()
    {
        Minecraft mc = Minecraft.getMinecraft();
        EmoteController ec = (EmoteController) EmoteController.get(mc.player);

        if (ec == null)
        {
            return;
        }

        ec.setupAnimator(mc.player);

        if (ec.animator == null)
        {
            return;
        }

        this.controller = new AnimatorEmoticonsController(ec.animator.animationName, ec.animator.userData);
        this.controller.fetchAnimation();

        if (mc.player instanceof AbstractClientPlayer)
        {
            ResourceLocation skinTex = ((AbstractClientPlayer) mc.player).getLocationSkin();

            if (this.controller.userConfig != null && this.controller.userConfig.meshes.containsKey("body"))
            {
                this.controller.userConfig.meshes.get("body").texture = skinTex;
            }
        }
    }

    private static String nextModel()
    {
        String[] models = EmoteController.MODELS;

        for (int i = 0; i < models.length; i ++)
        {
            if (models[i].equals(ClientConfig.instance.model))
            {
                return models[(i + 1) % models.length];
            }
        }

        return models[0];
    }

    private static String onOff(boolean value)
    {
        return value ? "On" : "Off";
    }

    private static String animLabel()
    {
        return "Anim: " + onOff(!ClientConfig.instance.disableAnimations);
    }

    private static String stopOnMoveLabel()
    {
        return "Stop on Move: " + onOff(ClientConfig.instance.stopOnMove);
    }

    private static String thirdPersonLabel()
    {
        return "3rd Person: " + onOff(ClientConfig.instance.thirdPerson);
    }

    private static String soundsLabel()
    {
        return "Sounds: " + onOff(ClientConfig.instance.sounds);
    }

    private static String volumeLabel()
    {
        return "Volume: " + (int) (ClientConfig.instance.volume * 100) + "%";
    }

    private static String modelLabel()
    {
        return "Model: " + ClientConfig.instance.model;
    }

    public static String formatEmoteName(String key)
    {
        if (key == null || key.isEmpty())
        {
            return "";
        }

        String langKey = "emoticons.emote." + key;
        String translated = net.minecraft.client.resources.I18n.format(langKey);

        if (!translated.equals(langKey))
        {
            return translated;
        }

        StringBuilder sb = new StringBuilder();

        for (String part : key.split("_"))
        {
            if (!part.isEmpty())
            {
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1)).append(' ');
            }
        }

        return sb.toString().trim();
    }
}
