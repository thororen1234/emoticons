package mchorse.emoticons.network.client;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.network.common.PacketEmote;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientHandlerEmote implements IMessageHandler<PacketEmote, IMessage>
{
    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketEmote message, MessageContext ctx)
    {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.id);

        if (entity instanceof EntityLivingBase && message.emote != null)
        {
            EmoteController.get((EntityLivingBase) entity).setEmote(message.emote, (EntityLivingBase) entity);
        }

        return null;
    }
}
