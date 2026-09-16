package mchorse.emoticons;

import net.fabricmc.api.ModInitializer;
import mchorse.emoticons.network.EmoteNetwork;
import mchorse.emoticons.network.EmoteCatalog;

public class MainEntrypoint implements ModInitializer {
	public void onInitialize() {
		EmoteCatalog.loadCustom();
		EmoteNetwork.init();
	}
}
