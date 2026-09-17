package mchorse.emoticons.network;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import java.util.*;
import net.minecraft.util.PacketByteBuf;

public final class ClientEmoteNetwork {
	private static final Map<UUID, Pending> STATES = new HashMap<>();
	public static void init() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> STATES.clear());
		ClientPlayNetworking.registerGlobalReceiver(EmoteNetwork.CHANNEL, (client, handler, buffer, responseSender) -> {
			UUID id = buffer.readUuid();
			String key = buffer.readString(128);
			int age = Math.max(0, buffer.readInt());
			client.execute(() -> {
			if (key.isEmpty()) {
				STATES.remove(id);
				EmoteController state = EmoteController.cache.get(id);
				if (state != null && client.world != null) {
					for (Object object : client.world.getPlayers()) {
						PlayerEntity player = (PlayerEntity) object;
						if (id.equals(player.getUuid())) state.setEmote(null, player);
					}
				}
			} else if (Emotes.has(key)) {
				STATES.put(id, new Pending(key, age));
			}
			});
		});
	}
	public static void send(String key) {
		if (ClientPlayNetworking.canSend(EmoteNetwork.CHANNEL)) {
			PacketByteBuf buffer = PacketByteBufs.create();
			buffer.writeString(key, 128);
			ClientPlayNetworking.send(EmoteNetwork.CHANNEL, buffer);
		}
	}
	public static void tick(MinecraftClient client) {
		for (Pending state : STATES.values()) state.age++;
		for (Object object : client.world.getPlayers()) {
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
