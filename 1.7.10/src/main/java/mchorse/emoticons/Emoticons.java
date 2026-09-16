package mchorse.emoticons;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = Emoticons.MOD_ID, name = "Emoticons", version = Emoticons.VERSION)
public final class Emoticons {
    public static final String MOD_ID = "emoticons";
    public static final String VERSION = "@VERSION@";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mchorse.emoticons.network.Dispatcher.register();
        new ClientProxy().init();
    }
}
