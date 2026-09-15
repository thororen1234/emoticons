package mchorse.emoticons;

import net.fabricmc.api.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import net.ornithemc.osl.networking.api.client.ClientConnectionEvents;
import mchorse.emoticons.client.KeyboardHandler;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.network.ClientEmoteNetwork;
import net.minecraft.entity.living.player.PlayerEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientEntrypoint implements ClientModInitializer {
	private boolean initialized;
	public void onInitializeClient() {
		MinecraftClientEvents.TICK_START.register(client -> {
			if (!initialized) { new ClientProxy().init(); initialized = true; }
		});
		KeyboardHandler.init();
		ClientEmoteNetwork.init();
		ClientConnectionEvents.DISCONNECT.register(client -> EmoteController.clear());
		MinecraftClientEvents.TICK_END.register(client -> {
			if (client.world == null || client.isPaused()) return;
			ClientEmoteNetwork.tick(client);
			Set<UUID> present = new HashSet<>();
			for (Object object : client.world.players) {
				PlayerEntity player = (PlayerEntity) object;
				present.add(player.getUuid());
				EmoteController.postUpdate(player);
			}
			EmoteController.retain(present);
		});
	}
}
