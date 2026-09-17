package mchorse.emoticons.client;

import mchorse.emoticons.client.gui.GuiEmotes;

import java.util.ArrayList;
import java.util.List;

import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;

import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler {
	public static KeyBinding random;
	public static KeyBinding emote1;
	public static KeyBinding emote2;
	public static KeyBinding emote3;
	public static KeyBinding emote4;
	public static KeyBinding emote5;
	public static KeyBinding emote6;
	public static KeyBinding emotes;
	public static KeyBinding stopEmote;
	public static KeyBinding reloadEmotes;

	public static void init() {
		String pre = "emoticons.keys.";

		// NOTE: KeyBinding takes a raw GLFW key token here (not a legacy LWJGL2
		// org.lwjgl.input.Keyboard scancode, which used a different numbering scheme).
		int KEY_O = GLFW.GLFW_KEY_O;
		int KEY_NUMPAD1 = GLFW.GLFW_KEY_KP_1;
		int KEY_NUMPAD2 = GLFW.GLFW_KEY_KP_2;
		int KEY_NUMPAD3 = GLFW.GLFW_KEY_KP_3;
		int KEY_NUMPAD4 = GLFW.GLFW_KEY_KP_4;
		int KEY_NUMPAD5 = GLFW.GLFW_KEY_KP_5;
		int KEY_NUMPAD6 = GLFW.GLFW_KEY_KP_6;
		int KEY_P = GLFW.GLFW_KEY_P;
		int KEY_NONE = GLFW.GLFW_KEY_UNKNOWN;

		random = new KeyBinding(pre + "random", KEY_O, pre + "category");
		emote1 = new KeyBinding(pre + "emote1", KEY_NUMPAD1, pre + "category");
		emote2 = new KeyBinding(pre + "emote2", KEY_NUMPAD2, pre + "category");
		emote3 = new KeyBinding(pre + "emote3", KEY_NUMPAD3, pre + "category");
		emote4 = new KeyBinding(pre + "emote4", KEY_NUMPAD4, pre + "category");
		emote5 = new KeyBinding(pre + "emote5", KEY_NUMPAD5, pre + "category");
		emote6 = new KeyBinding(pre + "emote6", KEY_NUMPAD6, pre + "category");
		emotes = new KeyBinding(pre + "emotes", KEY_P, pre + "category");
		stopEmote = new KeyBinding(pre + "stop_emote", KEY_NONE, pre + "category");
		reloadEmotes = new KeyBinding(pre + "reload_emotes", KEY_NONE, pre + "category");

		random = KeyBindingHelper.registerKeyBinding(random);
		emote1 = KeyBindingHelper.registerKeyBinding(emote1);
		emote2 = KeyBindingHelper.registerKeyBinding(emote2);
		emote3 = KeyBindingHelper.registerKeyBinding(emote3);
		emote4 = KeyBindingHelper.registerKeyBinding(emote4);
		emote5 = KeyBindingHelper.registerKeyBinding(emote5);
		emote6 = KeyBindingHelper.registerKeyBinding(emote6);
		emotes = KeyBindingHelper.registerKeyBinding(emotes);
		stopEmote = KeyBindingHelper.registerKeyBinding(stopEmote);
		reloadEmotes = KeyBindingHelper.registerKeyBinding(reloadEmotes);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.currentScreen == null && !client.isPaused()) {
				onKeyPress(client.player);
			}
		});
	}

	private static void onKeyPress(PlayerEntity player) {
		ICosmetic cap = EmoteController.get(player);

		if (cap != null) {
			processKeybind(player, cap);
		}
	}

	private static void processKeybind(PlayerEntity player, ICosmetic cap) {
		Emote emote = cap.getEmote();
		String key = null;
		EmoteKeys emotesObj = ClientProxy.keys;

		if (random.wasPressed()) {
			List<String> keys = new ArrayList<>();
			keys.addAll(Emotes.EMOTES.keySet());
			if (!keys.isEmpty()) {
				key = keys.get((int) (keys.size() * Math.random()));
			}
		}

		if (emotesObj != null && emotesObj.emotes.size() >= 6) {
			if (emote1.wasPressed())
				key = emotesObj.emotes.get(0);
			if (emote2.wasPressed())
				key = emotesObj.emotes.get(1);
			if (emote3.wasPressed())
				key = emotesObj.emotes.get(2);
			if (emote4.wasPressed())
				key = emotesObj.emotes.get(3);
			if (emote5.wasPressed())
				key = emotesObj.emotes.get(4);
			if (emote6.wasPressed())
				key = emotesObj.emotes.get(5);
		}

		double dist = Math.abs(player.getVelocity().x) + Math.abs(player.getVelocity().z);

		if (player.onGround && dist < 0.05 && key != null && !key.isEmpty()) {
			EmoteAPI.setEmoteClient(key, player);
		}

		if (stopEmote.wasPressed() && emote != null) {
			EmoteAPI.setEmoteClient("", player);
		}

		if (emotes.wasPressed()) {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.currentScreen == null) {
				try {
					mc.openScreen(new GuiEmotes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (reloadEmotes.wasPressed()) {
			mchorse.emoticons.ClientConfig.load();
			ClientProxy.reloadActions();
		}
	}
}
