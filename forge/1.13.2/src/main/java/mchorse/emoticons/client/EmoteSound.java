package mchorse.emoticons.client;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.Emoticons;
import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public final class EmoteSound extends MovingSound
{
    private final EntityLivingBase player;

    private EmoteSound(EntityLivingBase player, Emote emote)
    {
        super(EmoteSounds.get(emote.key), SoundCategory.PLAYERS);

        this.player = player;
        this.repeat = emote.looping;
        this.volume = Math.max(0, Math.min(1, ClientConfig.instance.volume));

        this.tick();
    }

    public static EmoteSound play(EntityLivingBase player, Emote emote)
    {
        if (!ClientConfig.instance.sounds)
        {
            return null;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.getSoundHandler().getAccessor(new ResourceLocation(Emoticons.MOD_ID, emote.key)) == null)
        {
            return null;
        }

        EmoteSound sound = new EmoteSound(player, emote);

        mc.getSoundHandler().play(sound);

        return sound;
    }

    @Override
    public void tick()
    {
        this.x = (float) this.player.posX;
        this.y = (float) this.player.posY;
        this.z = (float) this.player.posZ;

        if (!this.player.isAlive())
        {
            this.finish();
        }
    }

    public void finish()
    {
        this.donePlaying = true;
        Minecraft.getInstance().getSoundHandler().stop(this);
    }
}
