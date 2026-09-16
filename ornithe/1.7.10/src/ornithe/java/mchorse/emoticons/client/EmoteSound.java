package mchorse.emoticons.client;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.instance.AbstractTickableSoundInstance;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.client.resource.Identifier;

public final class EmoteSound extends AbstractTickableSoundInstance {
	private final LivingEntity player;
	private EmoteSound(LivingEntity player, Emote emote) {
		super(new Identifier("emoticons", emote.key));
		this.player = player;
		looping = emote.looping;
		volume = Math.max(0, Math.min(1, ClientConfig.instance.volume));
		tick();
	}
	public static EmoteSound play(LivingEntity player, Emote emote) {
		if (!ClientConfig.instance.sounds) return null;
		Minecraft mc = Minecraft.getInstance();
		if (mc.getSoundManager().get(new Identifier("emoticons", emote.key)) == null) return null;
		EmoteSound sound = new EmoteSound(player, emote);
		mc.getSoundManager().play(sound);
		return sound;
	}
	public void tick() {
		x = (float) player.x; y = (float) player.y; z = (float) player.z;
		if (!player.isAlive()) finish();
	}
	public void finish() {
		done = true;
		Minecraft.getInstance().getSoundManager().stop(this);
	}
}

