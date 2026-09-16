package mchorse.emoticons.network.client;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.network.common.PacketEmote;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerEmote implements IMessageHandler<PacketEmote, IMessage>
{
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketEmote message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.id);

                if (entity instanceof EntityLivingBase && message.emote != null)
                {
                    EmoteController.get((EntityLivingBase) entity).setEmote(message.emote, (EntityLivingBase) entity);
                }
            }
        });

        return null;
    }
}
