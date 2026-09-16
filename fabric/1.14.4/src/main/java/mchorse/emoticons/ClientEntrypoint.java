package mchorse.emoticons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import mchorse.emoticons.client.KeyboardHandler;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.network.ClientEmoteNetwork;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientEntrypoint implements ClientModInitializer {
	private boolean initialized;
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (!initialized) { new ClientProxy().init(); initialized = true; }
		});
		KeyboardHandler.init();
		ClientEmoteNetwork.init();
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> EmoteController.clear());
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world == null || client.isPaused()) return;
			ClientEmoteNetwork.tick(client);
			Set<UUID> present = new HashSet<>();
			for (Object object : client.world.getPlayers()) {
				PlayerEntity player = (PlayerEntity) object;
				present.add(player.getUuid());
				EmoteController.postUpdate(player);
			}
			EmoteController.retain(present);
		});
	}
}
