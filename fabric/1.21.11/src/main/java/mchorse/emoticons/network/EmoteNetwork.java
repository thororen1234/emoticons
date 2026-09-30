package mchorse.emoticons.network;

import mchorse.emoticons.common.EmoteAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmoteNetwork {
	public static final Identifier CHANNEL = Identifier.of("emoticons", "emote");
	public static final Map<UUID, Integer> ACTIVE = new HashMap<>();

	public static void init() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ACTIVE.put(handler.getPlayer().getUuid(), 0);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ACTIVE.remove(handler.getPlayer().getUuid());
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> ACTIVE.clear());
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Map.Entry<UUID, Integer> entry : ACTIVE.entrySet()) {
				entry.setValue(entry.getValue() + 1);
			}
		});
	}

	public static void send(ServerPlayerEntity player, String key) {
		send(player, player.getUuid(), key, 0);
	}

	private static void send(ServerPlayerEntity player, UUID id, String key, int age) {
		// Networking disabled for 1.21.1
	}

	public static void broadcast(ServerPlayerEntity player, String key) {
		// Networking disabled for 1.21.1
	}
}