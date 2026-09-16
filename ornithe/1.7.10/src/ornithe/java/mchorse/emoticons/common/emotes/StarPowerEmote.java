package mchorse.emoticons.common.emotes;

import javax.vecmath.Vector4f;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;

import net.minecraft.entity.living.LivingEntity;

public class StarPowerEmote extends Emote {
	public StarPowerEmote(String name, int duration, boolean looping) {
		super(name, duration, looping);
	}

	@Override
	
	public void progressAnimation(LivingEntity entity, BOBJArmature armature, AnimatorEmoticonsController animator, int tick, float partial) {
		if (tick == 30) {
			BOBJBone hand = armature.bones.get("low_right_arm.end");
			Vector4f result = animator.calcPosition(entity, hand, 0, 0.15F, 0, partial);

			for (int i = 0, c = 15; i < c; i++) {
				entity.world.addParticle("fireworksSpark", result.x, result.y, result.z,
						this.rand(0.05F), this.rand(0.05F), this.rand(0.05F));
			}
		}

		if (tick >= 33 && tick < 43) {
			BOBJBone hand = armature.bones.get("low_right_arm.end");
			Vector4f result = animator.calcPosition(entity, hand, 0, 0.15F, 0, partial);

			float r = 1;
			float g = 0;
			float b = 0;
			float p = (tick - 33) / (float) (43 - 33);

			if (p < 0.2) {}
			else if (p < 0.35) {
				g = 0.5F;
			}
			else if (p < 0.45) {
				g = 1;
			}
			else if (p < 0.65) {
				r = 0.25F;
				g = 1;
			}
			else if (p < 0.85) {
				r = 0;
				g = 0.75F;
				b = 1;
			}
			else {
				r = 0;
				g = 0;
				b = 1;
			}

			for (int i = 0, c = 7; i < c; i++) {
				entity.world.addParticle("mobSpell", result.x + this.rand.nextDouble() * 0.05 - 0.025, result.y + this.rand.nextDouble() * 0.05 - 0.025, result.z + this.rand.nextDouble() * 0.05 - 0.025, r, g, b);
			}
		}
	}
}