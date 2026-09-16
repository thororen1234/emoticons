package mchorse.emoticons.network;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import net.ornithemc.osl.networking.api.client.ClientConnectionEvents;
import java.util.*;

public final class ClientEmoteNetwork {
	private static final Map<UUID, Pending> STATES = new HashMap<>();
	public static void init() {
		ClientConnectionEvents.DISCONNECT.register(client -> STATES.clear());
		ClientPlayNetworking.registerListener(EmoteNetwork.CHANNEL, (ctx, buffer) -> {
			ctx.ensureOnMainThread();
			UUID id = buffer.readUuid();
			String key = buffer.readString(128);
			int age = Math.max(0, buffer.readInt());
			if (key.isEmpty()) {
				STATES.remove(id);
				EmoteController state = EmoteController.cache.get(id);
				if (state != null && ctx.minecraft().world != null) {
					for (Object object : ctx.minecraft().world.players) {
						PlayerEntity player = (PlayerEntity) object;
						if (id.equals(player.getUuid())) state.setEmote(null, player);
					}
				}
			} else if (Emotes.has(key)) {
				STATES.put(id, new Pending(key, age));
			}
		});
	}
	public static void send(String key) {
		if (ClientPlayNetworking.isPlayReady(EmoteNetwork.CHANNEL)) {
			ClientPlayNetworking.send(EmoteNetwork.CHANNEL, buffer -> buffer.writeString(key, 128));
		}
	}
	public static void tick(Minecraft client) {
		for (Pending state : STATES.values()) state.age++;
		for (Object object : client.world.players) {
			PlayerEntity player = (PlayerEntity) object;
			Pending pending = STATES.get(player.getUuid());
			if (pending == null || pending.applied == player) continue;
			Emote emote = Emotes.get(pending.key);
			if (emote == null || (!emote.looping && pending.age >= emote.duration)) continue;
			EmoteController state = (EmoteController) EmoteController.get(player);
			// The local action already began before the server echo arrived.
			if (player != client.player || state.getEmote() == null || !state.getEmote().getKey().equals(pending.key)) {
				state.setEmote(emote, player);
				state.seek(pending.age);
			}
			pending.applied = player;
		}
	}
	private static final class Pending {
		final String key;
		int age;
		PlayerEntity applied;
		Pending(String key, int age) { this.key = key; this.age = age; }
	}
}
