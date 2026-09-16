package mchorse.emoticons.client;

import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.capabilities.cosmetic.CosmeticMode;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

        if (cap != null && cap.render(player, event.getX(), event.getY(), event.getZ(), event.getPartialRenderTick()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerDisconnects(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        ClientProxy.mode = CosmeticMode.CLIENT;
    }
}
