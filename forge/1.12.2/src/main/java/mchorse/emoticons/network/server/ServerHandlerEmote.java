package mchorse.emoticons.network.server;

import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.network.common.PacketEmote;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerHandlerEmote implements IMessageHandler<PacketEmote, IMessage>
{
    @Override
    public IMessage onMessage(final PacketEmote message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().player;

        ((WorldServer) player.world).addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                EmoteAPI.setEmote(message.emote, player);
            }
        });

        return null;
    }
}
