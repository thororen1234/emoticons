package mchorse.emoticons.network;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import java.util.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.PacketByteBuf;

/** Version 1 protocol: C2S key; S2C UUID, key and elapsed ticks. */
public final class EmoteNetwork {
	public static final Identifier CHANNEL = new Identifier("emoticons", "emote");
	private static final Map<UUID, State> ACTIVE = new HashMap<>();

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(CHANNEL, (server, player, handler, buffer, responseSender) -> {
			String key = buffer.readString(128);
			server.execute(() -> {
			if (!key.isEmpty() && (!EmoteCatalog.valid(key) || !player.isAlive() || !player.onGround)) {
				send(player, player.getUuid(), "", 0);
				return;
			}
			if (key.isEmpty()) ACTIVE.remove(player.getUuid());
			else ACTIVE.put(player.getUuid(), new State(player, key));
			broadcast(server, player.getUuid(), key, 0);
			});
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			for (Map.Entry<UUID, State> entry : ACTIVE.entrySet()) {
				State state = entry.getValue();
				send(player, entry.getKey(), state.key, state.age);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.player;
			if (ACTIVE.remove(player.getUuid()) != null) broadcast(server, player.getUuid(), "", 0);
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> ACTIVE.clear());
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			Iterator<Map.Entry<UUID, State>> iterator = ACTIVE.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<UUID, State> entry = iterator.next();
				State state = entry.getValue();
				if (state.expired()) {
					iterator.remove();
					broadcast(server, entry.getKey(), "", 0);
				} else state.age++;
			}
		});
	}
	private static void send(ServerPlayerEntity player, UUID id, String key, int age) {
		PacketByteBuf buffer = PacketByteBufs.create();
		buffer.writeUuid(id); buffer.writeString(key, 128); buffer.writeInt(age);
		ServerPlayNetworking.send(player, CHANNEL, buffer);
	}
	private static void broadcast(MinecraftServer server, UUID id, String key, int age) {
		for (ServerPlayerEntity player : PlayerLookup.all(server)) send(player, id, key, age);
	}
	private static final class State {
		final ServerPlayerEntity player;
		final String key;
		final int duration;
		final boolean looping;
		final double x, y, z;
		final Object world;
		int age;
		State(ServerPlayerEntity player, String key) {
			this.player = player; this.key = key;
			int[] definition = EmoteCatalog.get(key);
			duration = definition[0]; looping = definition[1] != 0;
			x = player.getX(); y = player.getY(); z = player.getZ(); world = player.world;
		}
		boolean expired() {
			double dx = player.getX() - x, dy = player.getY() - y, dz = player.getZ() - z;
			return !player.isAlive() || player.world != world || player.isSleeping()
					|| dx * dx + dy * dy + dz * dz > 0.01 || (!looping && age >= duration);
		}
	}
}
