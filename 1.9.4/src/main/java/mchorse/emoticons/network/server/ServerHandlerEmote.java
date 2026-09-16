package mchorse.emoticons.network.server;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.network.Dispatcher;
import mchorse.emoticons.network.common.PacketEmote;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerHandlerEmote implements IMessageHandler<PacketEmote, IMessage>
{
    @Override
    public IMessage onMessage(final PacketEmote message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        
        ((net.minecraft.world.WorldServer) player.worldObj).addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (message.emote != null)
                {
                    message.id = player.getEntityId();
                    Dispatcher.sendToTracked(player, message);
                    EmoteController.get(player).setEmote(message.emote, player);
                }
            }
        });

        return null;
    }
}
