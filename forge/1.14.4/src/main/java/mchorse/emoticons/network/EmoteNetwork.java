package mchorse.emoticons.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import mchorse.emoticons.Emoticons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/** Version 1 protocol: C2S key; S2C UUID, key and elapsed ticks. */
public final class EmoteNetwork
{
    public static final ResourceLocation CHANNEL = new ResourceLocation(Emoticons.MOD_ID, "main");
    public static final String PROTOCOL = "1";
    public static final UUID NIL = new UUID(0L, 0L);

    private static final Map<UUID, State> ACTIVE = new HashMap<UUID, State>();

    private static SimpleChannel channel;

    private EmoteNetwork()
    {}

    public static SimpleChannel channel()
    {
        return channel;
    }

    public static void init()
    {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL)
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

        channel.registerMessage(0, PacketEmote.class, PacketEmote::encode, PacketEmote::decode, PacketEmote::handle);
    }

    /* Server side */

    public static void handle(ServerPlayerEntity player, String key)
    {
        if (player == null)
        {
            return;
        }

        if (!key.isEmpty() && (!EmoteCatalog.valid(key) || !player.isAlive() || !player.onGround))
        {
            send(player, player.getUniqueID(), "", 0);

            return;
        }

        if (key.isEmpty())
        {
            ACTIVE.remove(player.getUniqueID());
        }
        else
        {
            ACTIVE.put(player.getUniqueID(), new State(player, key));
        }

        broadcast(player, player.getUniqueID(), key, 0);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();

        if (!(player instanceof ServerPlayerEntity))
        {
            return;
        }

        for (Map.Entry<UUID, State> entry : ACTIVE.entrySet())
        {
            State state = entry.getValue();

            send((ServerPlayerEntity) player, entry.getKey(), state.key, state.age);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerEntity player = event.getPlayer();

        if (player instanceof ServerPlayerEntity && ACTIVE.remove(player.getUniqueID()) != null)
        {
            /* The leaving player's tracker list is already being torn down, so the
             * clear has to go out to everyone rather than to its trackers. */
            channel.send(PacketDistributor.ALL.noArg(), new PacketEmote(player.getUniqueID(), "", 0));
        }
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event)
    {
        ACTIVE.clear();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
        {
            return;
        }

        Iterator<Map.Entry<UUID, State>> iterator = ACTIVE.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<UUID, State> entry = iterator.next();
            State state = entry.getValue();

            if (state.expired())
            {
                iterator.remove();
                broadcast(state.player, entry.getKey(), "", 0);
            }
            else
            {
                state.age ++;
            }
        }
    }

    private static void send(ServerPlayerEntity player, UUID id, String key, int age)
    {
        channel.send(PacketDistributor.PLAYER.with(() -> player), new PacketEmote(id, key, age));
    }

    /**
     * Server to every client that can see the emoting player, including the
     * emoting player itself.
     */
    private static void broadcast(ServerPlayerEntity player, UUID id, String key, int age)
    {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new PacketEmote(id, key, age));
    }

    private static final class State
    {
        final ServerPlayerEntity player;
        final String key;
        final int duration;
        final boolean looping;
        final double x, y, z;
        final Object world;

        int age;

        State(ServerPlayerEntity player, String key)
        {
            this.player = player;
            this.key = key;

            int[] definition = EmoteCatalog.get(key);

            this.duration = definition[0];
            this.looping = definition[1] != 0;
            this.x = player.posX;
            this.y = player.posY;
            this.z = player.posZ;
            this.world = player.world;
        }

        boolean expired()
        {
            double dx = this.player.posX - this.x;
            double dy = this.player.posY - this.y;
            double dz = this.player.posZ - this.z;

            return !this.player.isAlive() || this.player.world != this.world || this.player.isSleeping()
                || dx * dx + dy * dy + dz * dz > 0.01 || (!this.looping && this.age >= this.duration);
        }
    }
}
