package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.living.LivingEntity;

import java.util.Random;

public class Emote {
	public final String key;
	public int duration;
	public boolean looping;
	public Random rand = new Random();
	public String customTitle = "";
	public String customDescription = "";

	public Emote(String string, int n, boolean bl) {
		this.key = string;
		this.duration = n;
		this.looping = bl;
	}

	public boolean shouldStopOnMove() {
		return false;
	}

	public final boolean shouldLimitLoop() {
		return this.loops() > 0;
	}

	public int loops() {
		return -1;
	}

	public void progressAnimation(LivingEntity livingBase, BOBJArmature armature,
			AnimatorEmoticonsController controller, int n, float f) {
	}

	public void startAnimation(AnimatorEmoticonsController controller) {
	}

	public void stopAnimation(AnimatorEmoticonsController controller) {
	}

	public Emote getDynamicEmote() {
		return this;
	}

	public Emote getDynamicEmote(String string) {
		return this;
	}

	public String getKey() {
		return this.key;
	}

	public float rand(float f) {
		return this.rand.nextFloat() * f - f / 2.0f;
	}
}
