package mchorse.emoticons.network;

import mchorse.emoticons.common.EmoteAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ClientEmoteNetwork {
	public static void init() {
		// Networking disabled for 1.21.1
	}

	public static void send(String key) {
		// Networking disabled for 1.21.1
	}

	public static void tick(MinecraftClient client) {
		// Networking disabled for 1.21.1
	}
}