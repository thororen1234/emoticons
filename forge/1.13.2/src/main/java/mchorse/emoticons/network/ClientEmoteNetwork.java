package mchorse.emoticons.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public final class ClientEmoteNetwork
{
    private static final Map<UUID, Pending> STATES = new HashMap<UUID, Pending>();

    private ClientEmoteNetwork()
    {}

    public static void clear()
    {
        STATES.clear();
    }

    public static void handle(PacketEmote message)
    {
        Minecraft mc = Minecraft.getInstance();
        UUID id = message.id;
        String key = message.key;
        int age = Math.max(0, message.age);

        if (key.isEmpty())
        {
            STATES.remove(id);

            EmoteController state = EmoteController.cache.get(id);

            if (state != null && mc.world != null)
            {
                for (EntityPlayer player : mc.world.playerEntities)
                {
                    if (id.equals(player.getUniqueID()))
                    {
                        state.setEmote(null, player);
                    }
                }
            }
        }
        else if (Emotes.has(key))
        {
            STATES.put(id, new Pending(key, age));
        }
    }

    public static void send(String key)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() != null)
        {
            EmoteNetwork.channel().sendToServer(new PacketEmote(EmoteNetwork.NIL, key, 0));
        }
    }

    public static void tick(Minecraft client)
    {
        for (Pending state : STATES.values())
        {
            state.age ++;
        }

        for (EntityPlayer player : client.world.playerEntities)
        {
            Pending pending = STATES.get(player.getUniqueID());

            if (pending == null || pending.applied == player)
            {
                continue;
            }

            Emote emote = Emotes.get(pending.key);

            if (emote == null || (!emote.looping && pending.age >= emote.duration))
            {
                continue;
            }

            EmoteController state = (EmoteController) EmoteController.get(player);

            /* The local action already began before the server echo arrived. */
            if (player != client.player || state.getEmote() == null || !state.getEmote().getKey().equals(pending.key))
            {
                state.setEmote(emote, player);
                state.seek(pending.age);
            }

            pending.applied = player;
        }
    }

    private static final class Pending
    {
        final String key;

        int age;
        EntityPlayer applied;

        Pending(String key, int age)
        {
            this.key = key;
            this.age = age;
        }
    }
}
