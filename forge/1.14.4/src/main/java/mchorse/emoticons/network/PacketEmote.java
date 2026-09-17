package mchorse.emoticons.network;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Version 1 protocol, carrying the same payload shape the Ornithe port validated:
 * C2S only uses the key, S2C uses all three fields (owner UUID, emote key and the
 * number of ticks the emote has already been playing for).
 */
public class PacketEmote
{
    public UUID id;
    public String key;
    public int age;

    public PacketEmote(UUID id, String key, int age)
    {
        this.id = id;
        this.key = key;
        this.age = age;
    }

    public static void encode(PacketEmote message, PacketBuffer buffer)
    {
        buffer.writeUniqueId(message.id == null ? EmoteNetwork.NIL : message.id);
        buffer.writeString(message.key, 128);
        buffer.writeInt(message.age);
    }

    public static PacketEmote decode(PacketBuffer buffer)
    {
        return new PacketEmote(buffer.readUniqueId(), buffer.readString(128), buffer.readInt());
    }

    public static void handle(PacketEmote message, Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() ->
        {
            if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER)
            {
                EmoteNetwork.handle(context.getSender(), message.key);
            }
            else
            {
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientEmoteNetwork.handle(message));
            }
        });

        context.setPacketHandled(true);
    }
}
