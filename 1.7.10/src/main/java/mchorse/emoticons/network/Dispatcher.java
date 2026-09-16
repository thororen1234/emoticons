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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class Dispatcher
{
    public static final SimpleNetworkWrapper DISPATCHER = NetworkRegistry.INSTANCE.newSimpleChannel(Emoticons.MOD_ID);

    public static void register()
    {
        DISPATCHER.registerMessage(ServerHandlerEmote.class, PacketEmote.class, 0, Side.SERVER);
        DISPATCHER.registerMessage(ClientHandlerEmote.class, PacketEmote.class, 1, Side.CLIENT);
    }

    public static void sendToTracked(Entity entity, IMessage message)
    {
        if (entity.worldObj instanceof WorldServer)
        {
            EntityTracker tracker = ((WorldServer) entity.worldObj).getEntityTracker();

            for (Object player : tracker.getTrackingPlayers(entity))
            {
                DISPATCHER.sendTo(message, (EntityPlayerMP) player);
            }
        }
    }

    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }
}
