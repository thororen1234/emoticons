package mchorse.emoticons.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.network.ClientEmoteNetwork;
import mchorse.mclib.client.render.RenderLightmap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Gameplay-side client listeners: the player render hook, the per-tick emote
 * state pump, connection teardown, and the {@code /emote} chat command.
 */
public final class ClientEventHandler
{
    private static boolean initialized;
    private static boolean connected;

    private ClientEventHandler()
    {}

    /**
     * Replaces the Ornithe port's {@code PlayerRendererMixin}: Forge has a
     * first-class cancellable event for exactly this.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        EntityPlayer entity = event.getEntityPlayer();

        if (entity.isSpectator() || entity.isInvisible() || entity.isPlayerSleeping())
        {
            return;
        }

        ICosmetic state = EmoteController.get(entity);

        if (state.getEmote() == null && ClientConfig.instance.disableAnimations)
        {
            return;
        }

        if (state.render(entity, event.getX(), event.getY(), event.getZ(), event.getPartialRenderTick()))
        {
            RenderLightmap.renderNameplate(entity, entity.getName().getString(),
                event.getX(), event.getY(), event.getZ());

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
        {
            return;
        }

        if (!initialized)
        {
            /* Deferred from FMLClientSetupEvent: RenderLightmap needs Minecraft's
             * RenderManager, which does not exist during mod loading. */
            initialized = true;
            new ClientProxy().init();
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.world == null)
        {
            /* Forge 25 (1.13.2) has no ClientPlayerNetworkEvent — that event was
             * only added in 1.14's Forge. Losing the world is the same signal, so
             * the per-connection caches are torn down here instead. */
            if (connected)
            {
                connected = false;

                ClientEmoteNetwork.clear();
                EmoteController.clear();
            }

            return;
        }

        connected = true;

        if (mc.isGamePaused())
        {
            return;
        }

        ClientEmoteNetwork.tick(mc);

        Set<UUID> present = new HashSet<UUID>();

        /* 1.13.2 World#getPlayers takes a class + predicate; the plain player list
         * is the public playerEntities field instead. */
        for (EntityPlayer player : mc.world.playerEntities)
        {
            present.add(player.getUniqueID());
            EmoteController.postUpdate(player);
        }

        EmoteController.retain(present);
    }

    /**
     * Forge has no client-side Brigadier hook at 1.13.2, but every chat line the
     * player submits — including {@code /}-prefixed ones — funnels through
     * {@code GuiScreen.sendChatMessage}, which fires this cancellable event before the
     * text is handed to {@code EntityPlayerSP#sendChatMessage}. Cancelling it
     * therefore keeps {@code /emote} from ever reaching the server.
     */
    @SubscribeEvent
    public static void onClientChat(ClientChatEvent event)
    {
        String message = event.getMessage().trim();

        if (!message.equals("/emote") && !message.startsWith("/emote "))
        {
            return;
        }

        String[] args = message.split("\\s+");
        String emote = args.length >= 2 ? args[1] : "";

        if (!Emotes.has(emote))
        {
            emote = "";
        }

        EmoteAPI.setEmoteClient(emote, Minecraft.getInstance().player);
        event.setCanceled(true);
    }
}
