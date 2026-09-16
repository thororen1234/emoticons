package mchorse.emoticons.network.server;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.network.Dispatcher;
import mchorse.emoticons.network.common.PacketEmote;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ServerHandlerEmote implements IMessageHandler<PacketEmote, IMessage>
{
    @Override
    public IMessage onMessage(PacketEmote message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        
        if (message.emote != null)
        {
            message.id = player.getEntityId();
            Dispatcher.sendToTracked(player, message);
            EmoteController.get(player).setEmote(message.emote, player);
        }

        return null;
    }
}
