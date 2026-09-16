package mchorse.emoticons.network;

import mchorse.emoticons.Emoticons;
import mchorse.emoticons.network.client.ClientHandlerEmote;
import mchorse.emoticons.network.common.PacketEmote;
import mchorse.emoticons.network.server.ServerHandlerEmote;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Dispatcher
{
    public static final SimpleNetworkWrapper DISPATCHER = NetworkRegistry.INSTANCE.newSimpleChannel(Emoticons.MOD_ID);

    public static void register()
    {
        DISPATCHER.registerMessage(ServerHandlerEmote.class, PacketEmote.class, 0, Side.SERVER);
        DISPATCHER.registerMessage(ClientHandlerEmote.class, PacketEmote.class, 1, Side.CLIENT);
    }

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        if (entity.world instanceof WorldServer)
        {
            EntityTracker tracker = ((WorldServer) entity.world).getEntityTracker();

            for (Object player : tracker.getTrackingPlayers(entity))
            {
                DISPATCHER.sendTo(message, (EntityPlayerMP) player);
            }
        }
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }
}
