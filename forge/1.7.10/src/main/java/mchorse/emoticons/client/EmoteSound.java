package mchorse.emoticons.client;

import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class EmoteSound extends MovingSound {
    private final EntityLivingBase player;

    public EmoteSound(EntityLivingBase player, Emote emote) {
        super(new ResourceLocation("emoticons", emote.key));
        this.player = player;
        this.repeat = emote.looping;
        this.volume = Math.max(0.0F, Math.min(1.0F, ClientProxy.keys.volume));
        this.update();
    }

    public static EmoteSound play(EntityLivingBase player, Emote emote) {
        if (ClientProxy.keys.volume <= 0.0F) return null;
        Minecraft mc = Minecraft.getMinecraft();
        
        EmoteSound sound = new EmoteSound(player, emote);
        mc.getSoundHandler().playSound(sound);
        return sound;
    }

    @Override
    public void update() {
        this.xPosF = (float) this.player.posX;
        this.yPosF = (float) this.player.posY;
        this.zPosF = (float) this.player.posZ;

        if (this.player.isDead) {
            this.finish();
        }
    }

    public void finish() {
        this.donePlaying = true;
    }
}
