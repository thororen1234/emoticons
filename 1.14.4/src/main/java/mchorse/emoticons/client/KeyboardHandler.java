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
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.keybinds.api.KeybindRegistry;
import net.ornithemc.osl.keybinds.api.KeybindEvents;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;

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

		int KEY_O = 24;
		int KEY_NUMPAD1 = 79;
		int KEY_NUMPAD2 = 80;
		int KEY_NUMPAD3 = 81;
		int KEY_NUMPAD4 = 75;
		int KEY_NUMPAD5 = 76;
		int KEY_NUMPAD6 = 77;
		int KEY_P = 25;
		int KEY_NONE = 0;

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

		KeybindEvents.REGISTER_KEYBINDS.register(() -> {
			KeybindRegistry.register(random);
			KeybindRegistry.register(emote1);
			KeybindRegistry.register(emote2);
			KeybindRegistry.register(emote3);
			KeybindRegistry.register(emote4);
			KeybindRegistry.register(emote5);
			KeybindRegistry.register(emote6);
			KeybindRegistry.register(emotes);
			KeybindRegistry.register(stopEmote);
			KeybindRegistry.register(reloadEmotes);
		});

		MinecraftClientEvents.TICK_END.register(client -> {
			if (client.player != null && client.screen == null && !client.isPaused()) {
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

		if (random.consumeClick()) {
			List<String> keys = new ArrayList<>();
			keys.addAll(Emotes.EMOTES.keySet());
			if (!keys.isEmpty()) {
				key = keys.get((int) (keys.size() * Math.random()));
			}
		}

		if (emotesObj != null && emotesObj.emotes.size() >= 6) {
			if (emote1.consumeClick())
				key = emotesObj.emotes.get(0);
			if (emote2.consumeClick())
				key = emotesObj.emotes.get(1);
			if (emote3.consumeClick())
				key = emotesObj.emotes.get(2);
			if (emote4.consumeClick())
				key = emotesObj.emotes.get(3);
			if (emote5.consumeClick())
				key = emotesObj.emotes.get(4);
			if (emote6.consumeClick())
				key = emotesObj.emotes.get(5);
		}

		double dist = Math.abs(player.m_94091929().x) + Math.abs(player.m_94091929().z);

		if (player.onGround && dist < 0.05 && key != null && !key.isEmpty()) {
			EmoteAPI.setEmoteClient(key, player);
		}

		if (stopEmote.consumeClick() && emote != null) {
			EmoteAPI.setEmoteClient("", player);
		}

		if (emotes.consumeClick()) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen == null) {
				try {
					mc.openScreen(new GuiEmotes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (reloadEmotes.consumeClick()) {
			mchorse.emoticons.ClientConfig.load();
			ClientProxy.reloadActions();
		}
	}
}
