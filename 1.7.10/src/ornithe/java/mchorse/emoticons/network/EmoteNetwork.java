package mchorse.emoticons.network;

import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.core.api.util.NamespacedIdentifiers;
import net.ornithemc.osl.networking.api.ChannelRegistry;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;
import net.ornithemc.osl.lifecycle.api.server.MinecraftServerEvents;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import java.util.*;

/** Version 1 protocol: C2S key; S2C UUID, key and elapsed ticks. */
public final class EmoteNetwork {
	public static final NamespacedIdentifier CHANNEL = NamespacedIdentifiers.parse("emoticons:emote");
	private static final Map<UUID, State> ACTIVE = new HashMap<>();
	private static final Set<ServerPlayerEntity> PLAYERS = new HashSet<>();

	public static void init() {
		ChannelRegistry.register(CHANNEL);
		ServerPlayNetworking.registerListener(CHANNEL, (ctx, buffer) -> {
			ctx.ensureOnMainThread();
			String key = buffer.readString(128);
			ServerPlayerEntity player = ctx.player();
			if (!key.isEmpty() && (!EmoteCatalog.valid(key) || !player.isAlive() || !player.onGround)) {
				send(player, player.getUuid(), "", 0);
				return;
			}
			if (key.isEmpty()) ACTIVE.remove(player.getUuid());
			else ACTIVE.put(player.getUuid(), new State(player, key));
			broadcast(player.getUuid(), key, 0);
		});
		ServerConnectionEvents.PLAY_READY.register((server, player) -> {
			PLAYERS.add(player);
			for (Map.Entry<UUID, State> entry : ACTIVE.entrySet()) {
				State state = entry.getValue();
				send(player, entry.getKey(), state.key, state.age);
			}
		});
		ServerConnectionEvents.DISCONNECT.register((server, player) -> {
			PLAYERS.remove(player);
			if (ACTIVE.remove(player.getUuid()) != null) broadcast(player.getUuid(), "", 0);
		});
		MinecraftServerEvents.STOP.register(server -> {
			ACTIVE.clear();
			PLAYERS.clear();
		});
		MinecraftServerEvents.TICK_END.register(server -> {
			Iterator<Map.Entry<UUID, State>> iterator = ACTIVE.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<UUID, State> entry = iterator.next();
				State state = entry.getValue();
				if (state.expired()) {
					iterator.remove();
					broadcast(entry.getKey(), "", 0);
				} else state.age++;
			}
		});
	}
	private static void send(ServerPlayerEntity player, UUID id, String key, int age) {
		ServerPlayNetworking.send(player, CHANNEL, buffer -> {
			buffer.writeUuid(id); buffer.writeString(key, 128); buffer.writeInt(age);
		});
	}
	private static void broadcast(UUID id, String key, int age) {
		for (ServerPlayerEntity p : PLAYERS) {
			send(p, id, key, age);
		}
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
			x = player.x; y = player.y; z = player.z; world = player.world;
		}
		boolean expired() {
			double dx = player.x - x, dy = player.y - y, dz = player.z - z;
			return !player.isAlive() || player.world != world || player.isSleeping()
					|| dx * dx + dy * dy + dz * dz > 0.01 || (!looping && age >= duration);
		}
	}
}
