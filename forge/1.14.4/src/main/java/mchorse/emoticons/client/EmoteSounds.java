package mchorse.emoticons.client;

import java.util.HashMap;
import java.util.Map;

import mchorse.emoticons.Emoticons;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Bundled emote sounds, registered once during the registry phase.
 *
 * <p>Registries are frozen after loading in 1.13+, so this deliberately has
 * nothing to do with the runtime "reload emotes" keybind, which only rebuilds
 * {@link mchorse.emoticons.common.emotes.Emotes#EMOTES}. Custom user emotes fall
 * back to an unregistered {@link SoundEvent} in {@link EmoteSound}, which is fine
 * because playback only needs a {@code sounds.json} entry, not a registry entry.
 */
public final class EmoteSounds
{
    /** Bundled sounds, matching {@code assets/emoticons/sounds.json}. */
    private static final String[] NAMES = {
        "best_mates", "boneless", "default", "disco_fever", "electro_shuffle",
        "floss", "fresh", "gangnam_style", "hype", "infinite_dab",
        "orange_justice", "skibidi", "squat_kick", "star_power", "take_the_l",
        "tidy"
    };

    private static final Map<String, SoundEvent> SOUNDS = new HashMap<String, SoundEvent>();

    private EmoteSounds()
    {}

    @SubscribeEvent
    public static void register(RegistryEvent.Register<SoundEvent> event)
    {
        for (String name : NAMES)
        {
            ResourceLocation location = new ResourceLocation(Emoticons.MOD_ID, name);
            SoundEvent sound = new SoundEvent(location);

            sound.setRegistryName(location);
            event.getRegistry().register(sound);
            SOUNDS.put(name, sound);
        }
    }

    /**
     * Returns the registered sound for an emote key, or an ad hoc one for custom
     * emotes shipped by the user in their config folder.
     */
    public static SoundEvent get(String key)
    {
        SoundEvent sound = SOUNDS.get(key);

        return sound == null ? new SoundEvent(new ResourceLocation(Emoticons.MOD_ID, key)) : sound;
    }
}
