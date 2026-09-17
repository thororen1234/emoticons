package mchorse.emoticons;

import mchorse.emoticons.client.ClientEventHandler;
import mchorse.emoticons.client.EmoteSounds;
import mchorse.emoticons.client.KeyboardHandler;
import mchorse.emoticons.network.EmoteCatalog;
import mchorse.emoticons.network.EmoteNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Emoticons.MOD_ID)
public final class Emoticons
{
    public static final String MOD_ID = "emoticons";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static boolean debug = false;
    public static String config;

    public Emoticons()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        modBus.register(EmoteSounds.class);

        /* The channel has to exist before the handshake, so it is built during
         * mod construction rather than in a setup event. */
        EmoteNetwork.init();

        MinecraftForge.EVENT_BUS.register(EmoteNetwork.class);
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        EmoteCatalog.loadCustom();
    }

    private void clientSetup(FMLClientSetupEvent event)
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            /* Key bindings have to land in GameSettings#keyBindings, which exists
             * by now. The rest of the client init is deferred to the first client
             * tick by ClientEventHandler, because Minecraft's EntityRendererManager
             * (needed by RenderLightmap) is only built after mod loading finishes. */
            KeyboardHandler.init();

            MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
            MinecraftForge.EVENT_BUS.register(KeyboardHandler.class);
        });
    }
}
