package mchorse.emoticons.client.gui;

import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import mchorse.emoticons.client.EmoteKeys;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.entity.living.player.PlayerEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiEmotes extends Screen {

	private static final int LIST_WIDTH = 120;
	private static final int BOTTOM_HEIGHT = 50;
	private static final int BTN_HEIGHT = 20;

	private EmoteKeys keys;
	private AnimatorEmoticonsController controller;

	private List<String> allEmoteKeys = new ArrayList<>();
	private List<String> filteredKeys = new ArrayList<>();

	private int scrollOffset = 0;
	private int selectedIndex = -1;
	private int slotIndex = 0;

	/** Model variants that have asset files under assets/emoticons/models/entity. */
	private static final String[] MODELS = {"default", "3d", "simple", "simple_plus"};
	private static final int SETTINGS_Y = 34;
	private static final int PLAY_Y = 58;

	private List<ButtonWidget> slotButtons = new ArrayList<>();
	private List<ButtonWidget> settingButtons = new ArrayList<>();
	private String searchText = "";
	private boolean searchFocused = false;

	private float previewRotation = 0;
	private boolean isDragging = false;
	private int lastMouseX = 0;

	public GuiEmotes() {
		super(new net.minecraft.text.LiteralText(""));
		Minecraft mc = Minecraft.getInstance();
		this.keys = ClientProxy.keys;

		PlayerEntity player = mc.player;
		ICosmetic cap = EmoteController.get(player);
		EmoteController ec = (EmoteController) cap;

		if (ec.controller == null) {
			ec.setupAnimator(player);
		}

		if (ec.controller != null) {
			this.controller = new AnimatorEmoticonsController(
					ec.controller.animationName,
					ec.controller.userData
			);
			this.controller.fetchAnimation();
			// fetchAnimation makes an independent copy for the preview.

			// Apply actual player skin to preview
			if (player instanceof net.minecraft.client.entity.living.player.ClientPlayerEntity) {
				net.minecraft.resource.Identifier skinTex =
						((net.minecraft.client.entity.living.player.ClientPlayerEntity) player).getSkinTextureLocation();
				if (this.controller.userConfig != null && this.controller.userConfig.meshes.containsKey("body")) {
					this.controller.userConfig.meshes.get("body").texture = skinTex;
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
	public void init() {
		this.slotButtons.clear();
		this.settingButtons.clear();
		this.buttons.clear();

		this.addButton(new ButtonWidget(LIST_WIDTH + 5, PLAY_Y, 60, 20, "Play") {
			@Override
			public void m_15978786(double mouseX, double mouseY) {
				super.m_15978786(mouseX, mouseY);
				handleButtonClick(this);
			}
		});
		this.addButton(new ButtonWidget(LIST_WIDTH + 70, PLAY_Y, 60, 20, "Stop") {
			@Override
			public void m_15978786(double mouseX, double mouseY) {
				super.m_15978786(mouseX, mouseY);
				handleButtonClick(this);
			}
		});

		int setX = LIST_WIDTH + 5;
		int available = this.width - LIST_WIDTH - 10 - 5 * 5;
		int[] setWidths = new int[6];
		int setTotal = 0;

		for (int i = 0; i < 6; i++) {
			setWidths[i] = this.textRenderer.getWidth(settingWidestLabel(i)) + 10;
			setTotal += setWidths[i];
		}

		for (int i = 0; i < 6; i++) {
			final int index = i;
			int setW = setTotal > available ? Math.max(20, available * setWidths[i] / setTotal) : setWidths[i];
			ButtonWidget setBtn = new ButtonWidget(setX, SETTINGS_Y, setW, BTN_HEIGHT, settingLabel(i)) {
				@Override
				public void m_15978786(double mouseX, double mouseY) {
					super.m_15978786(mouseX, mouseY);
					toggleSetting(index);
				}
			};

			this.settingButtons.add(setBtn);
			this.addButton(setBtn);
			setX += setW + 5;
		}

		int btnW = Math.max(30, (this.width - LIST_WIDTH - 10 - 5 * 5) / 6);
		int btnY = this.height - BOTTOM_HEIGHT + (BOTTOM_HEIGHT - BTN_HEIGHT) / 2;

		for (int i = 0; i < 6; i++) {
			String emoteName = this.keys.emotes.get(i);
			String label = (i + 1) + ": " + formatEmoteName(emoteName);
			int btnX = LIST_WIDTH + 5 + i * (btnW + 5);
			ButtonWidget btn = new ButtonWidget(btnX, btnY, btnW, BTN_HEIGHT, label) {
				@Override
				public void m_15978786(double mouseX, double mouseY) {
					super.m_15978786(mouseX, mouseY);
					handleButtonClick(this);
				}
			};
			this.slotButtons.add(btn);
			this.buttons.add(btn);
		}
	}

	protected void handleButtonClick(ButtonWidget button) {
		int id = slotButtons.indexOf(button);
		if (id != -1) {
			this.slotIndex = id;
			playPreview(this.keys.emotes.get(slotIndex));
		}
	}

	@Override
	public void tick() {
		if (this.controller != null) {
			this.controller.update(Minecraft.getInstance().player);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();

		Minecraft mc = Minecraft.getInstance();
		int listH = this.height - BOTTOM_HEIGHT - 32;
		int rowH = 12;
		int visibleRows = listH / rowH;
		int startY = 32;

		// Left panel
		GuiElement.fill(0, 0, LIST_WIDTH, this.height, 0xCC000000);
		this.textRenderer.drawWithShadow("Search:", 5, 5, 0xAAAAAA);
		GuiElement.fill(5, 15, LIST_WIDTH - 5, 29, 0xFF555555);
		GuiElement.fill(6, 16, LIST_WIDTH - 6, 28, 0xFF222222);
		String displaySearch = searchText + (searchFocused ? "_" : "");
		this.textRenderer.drawWithShadow(displaySearch, 8, 18, 0xFFFFFF);

		for (int i = 0; i < visibleRows; i++) {
			int idx = i + scrollOffset;
			if (idx >= filteredKeys.size()) break;
			String key = filteredKeys.get(idx);
			boolean isSelected = (idx == selectedIndex);
			boolean isHovered = mouseX < LIST_WIDTH && mouseY >= startY + i * rowH && mouseY < startY + (i + 1) * rowH;
			int bg = isSelected ? 0xFF4444AA : (isHovered ? 0xFF333355 : 0x00000000);
			if (bg != 0) GuiElement.fill(0, startY + i * rowH, LIST_WIDTH, startY + (i + 1) * rowH, bg);
			this.textRenderer.drawWithShadow(formatEmoteName(key), 5, startY + i * rowH + 2, isSelected ? 0xFFFFFF : 0xCCCCCC);
		}

		if (filteredKeys.size() > visibleRows) {
			int scrollH = Math.max(10, (int) ((float) visibleRows / filteredKeys.size() * listH));
			int scrollY = startY + (int) ((float) scrollOffset / Math.max(1, filteredKeys.size() - visibleRows) * (listH - scrollH));
			GuiElement.fill(LIST_WIDTH - 4, scrollY, LIST_WIDTH - 1, scrollY + scrollH, 0xFF888888);
		}

		// Bottom bar
		this.fillGradient(LIST_WIDTH, this.height - BOTTOM_HEIGHT - 20, this.width, this.height - BOTTOM_HEIGHT, 0x00000000, 0x88000000);
		GuiElement.fill(LIST_WIDTH, this.height - BOTTOM_HEIGHT, this.width, this.height, 0x99000000);

		// Model preview
		if (this.controller != null && this.controller.animation != null) {
			int previewX = LIST_WIDTH + (this.width - LIST_WIDTH) / 2;
			int previewY = this.height / 2 + 20;

			if (isDragging) {
				previewRotation += (mouseX - lastMouseX);
				lastMouseX = mouseX;
			}

			drawModel(mc, previewX, previewY, 50, partialTicks);
		}

		// Emote name overlay
		if (selectedIndex >= 0 && selectedIndex < filteredKeys.size()) {
			String name = formatEmoteName(filteredKeys.get(selectedIndex));
			int cx = LIST_WIDTH + (this.width - LIST_WIDTH) / 2;
			int nameW = this.textRenderer.getWidth(name);
			GlStateManager.pushMatrix();
			GlStateManager.translate(cx - nameW, this.height - BOTTOM_HEIGHT - 32, 0);
			GlStateManager.scale(2.0f, 2.0f, 2.0f);
			this.textRenderer.drawWithShadow(name, 0, 0, 0xFFFFFF);
			GlStateManager.popMatrix();
		}

		// Title bar
		this.fillGradient(LIST_WIDTH, 0, this.width, 32, 0x88000000, 0x00000000);
		String title = "Emotes ";
		String subtitle = "(" + Emotes.EMOTES.size() + " total)";
		this.textRenderer.drawWithShadow(title, LIST_WIDTH + 5, 10, 0xFFFFFF);
		this.textRenderer.drawWithShadow(subtitle, LIST_WIDTH + 5 + this.textRenderer.getWidth(title), 10, 0xAAAAAA);

		// Slot label
		this.textRenderer.drawWithShadow("Editing slot: " + (slotIndex + 1), LIST_WIDTH + 5, this.height - BOTTOM_HEIGHT + 5, 0xAAAAAA);

		super.render(mouseX, mouseY, partialTicks);
	}

	private void drawModel(Minecraft mc, int x, int y, int scale, float partialTicks) {
		GlStateManager.enableDepth();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 100.0f);
		GlStateManager.scale(-scale, scale, scale);
		GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
		GlStateManager.rotate(previewRotation, 0.0f, 1.0f, 0.0f);
		Lighting.turnOn();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		this.controller.render(mc.player, 0, 0, 0, 0.0f, partialTicks);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		Lighting.turnOff();
		GlStateManager.disableDepth();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (super.mouseScrolled(mouseX, mouseY, amount)) {
			return true;
		}
		if (mouseX >= 5 && mouseX < LIST_WIDTH - 5 && mouseY >= 15 && mouseY < 29) {
			searchFocused = true;
			return true;
		}
		if (amount != 0) {
			int listH = this.height - BOTTOM_HEIGHT - 32;
			int rowH = 12;
			int visibleRows = listH / rowH;
			int delta = amount > 0 ? -3 : 3;
			scrollOffset = Math.max(0, Math.min(Math.max(0, filteredKeys.size() - visibleRows), scrollOffset + delta));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		if (mouseX >= 5 && mouseX < LIST_WIDTH - 5 && mouseY >= 15 && mouseY < 29) {
			searchFocused = true;
			return true;
		}
		searchFocused = false;

		if (button == 0 && mouseX >= LIST_WIDTH && mouseY < this.height - BOTTOM_HEIGHT) {
			isDragging = true;
			lastMouseX = (int)mouseX;
		}

		int startY = 32;
		int rowH = 12;
		if (button == 0 && mouseX >= 0 && mouseX < LIST_WIDTH && mouseY >= startY && mouseY < this.height - BOTTOM_HEIGHT) {
			int idx = ((int)mouseY - startY) / rowH + scrollOffset;
			if (idx >= 0 && idx < filteredKeys.size()) {
				this.selectedIndex = idx;
				String key = filteredKeys.get(idx);
				if (slotIndex >= 0 && slotIndex < 6) {
					this.keys.emotes.set(slotIndex, key);
					updateSlotButton(slotIndex);
				}
				playPreview(key);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			isDragging = false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

@Override
	public boolean charTyped(char chr, int modifiers) {
		if (searchFocused && chr >= 32 && chr < 127) {
			searchText += chr;
			applyFilter();
			return true;
		}
		return super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) { // Escape
			this.minecraft.openScreen(null);
			return true;
		}
		if (searchFocused) {
			if (keyCode == 259 && !searchText.isEmpty()) { // Backspace
				searchText = searchText.substring(0, searchText.length() - 1);
				applyFilter();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public void removed() {
		EmoteKeys.toFile(this.keys, new File(ClientProxy.configFolder, "keys.json"));
		ClientConfig.save();
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
		if (this.controller == null || this.controller.animation == null || this.controller.config == null) return;
		try {
			ActionConfig actionConfig = this.controller.config.config.actions.getConfig("emote_" + key);
			if (actionConfig != null) {
				this.controller.setEmote(this.controller.animation.createAction(null, actionConfig, true));
			}
		} catch (Exception e) { /* preview not critical */ }
	}

	private void updateSlotButton(int idx) {
		if (idx < slotButtons.size()) {
			slotButtons.get(idx).setMessage((idx + 1) + ": " + formatEmoteName(this.keys.emotes.get(idx)));
		}
	}

	/** Widest label a setting button can ever show, so its width never jumps when clicked. */
	private static String settingWidestLabel(int index) {
		switch (index) {
			case 0: return "Anim: Off";
			case 1: return "Stop on Move: Off";
			case 2: return "3rd Person: Off";
			case 3: return "Sounds: Off";
			case 4: return "Volume: 100%";
			default: return "Model: simple_plus";
		}
	}

	private static String settingLabel(int index) {
		ClientConfig config = ClientConfig.instance;

		switch (index) {
			case 0: return "Anim: " + (config.disableAnimations ? "Off" : "On");
			case 1: return "Stop on Move: " + (config.stopOnMove ? "On" : "Off");
			case 2: return "3rd Person: " + (config.thirdPerson ? "On" : "Off");
			case 3: return "Sounds: " + (config.sounds ? "On" : "Off");
			case 4: return "Volume: " + Math.round(config.volume * 100) + "%";
			default: return "Model: " + config.model;
		}
	}

	private void toggleSetting(int index) {
		ClientConfig config = ClientConfig.instance;

		switch (index) {
			case 0:
				config.disableAnimations = !config.disableAnimations;
				break;
			case 1:
				config.stopOnMove = !config.stopOnMove;
				break;
			case 2:
				config.thirdPerson = !config.thirdPerson;
				break;
			case 3:
				config.sounds = !config.sounds;
				break;
			case 4:
				/* Same -20% wrapping cycle as the original 1.12.2 volume button. */
				config.volume -= 0.2F;
				if (config.volume < -0.01F) config.volume = 1.0F;
				break;
			default:
				int model = 0;
				for (int i = 0; i < MODELS.length; i++) {
					if (MODELS[i].equals(config.model)) model = i;
				}
				config.model = MODELS[(model + 1) % MODELS.length];
				break;
		}

		if (index < settingButtons.size()) {
			settingButtons.get(index).setMessage(settingLabel(index));
		}
	}

	public static String formatEmoteName(String key) {
		if (key == null || key.isEmpty()) return "";
		Emote emote = Emotes.EMOTES.get(key);
		if (emote != null && !emote.customTitle.isEmpty()) return emote.customTitle;
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

