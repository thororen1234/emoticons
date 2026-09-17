package mchorse.emoticons.client;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.capabilities.cosmetic.CosmeticMode;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Entity model handler. This handler is responsible for rendering
 * models on player.
 */
@SideOnly(Side.CLIENT)
public class EntityModelHandler
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();

        if (player.isSpectator())
        {
            return;
        }

        ICosmetic cap = EmoteController.get(player);

        if (cap == null)
        {
            return;
        }

        /* Don't take over the vanilla model when idling, if the user
         * disabled the idle animations */
        if (cap.getEmote() == null && ClientConfig.instance.disableAnimations)
        {
            return;
        }

        if (cap.render(player, event.getX(), event.getY(), event.getZ(), event.getPartialRenderTick()))
        {
            event.setCanceled(true);
        }
    }

    /* Drives the emote animation forward. Nothing else calls
     * EmoteController#update(EntityLivingBase) during actual gameplay (the
     * GUI preview calls it independently, only while the screen is open),
     * so without this tick every emote gets stuck on its first frame. */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && event.player.world.isRemote)
        {
            EmoteController.postUpdate(event.player);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerDisconnects(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        ClientProxy.mode = CosmeticMode.CLIENT;
    }
}
