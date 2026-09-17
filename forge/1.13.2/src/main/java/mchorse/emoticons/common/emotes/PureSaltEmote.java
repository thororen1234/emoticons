package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.client.particles.SaltParticle;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

import javax.vecmath.Vector4f;

public class PureSaltEmote extends Emote {
	public PureSaltEmote(String key, int duration, boolean looping) {
		super(key, duration, looping);
	}

	@Override
	public void progressAnimation(EntityLivingBase livingBase, BOBJArmature armature, AnimatorEmoticonsController controller, int tick, float partialTicks) {
		if (tick > 18 && tick <= 78 && tick % 2 == 0) {
			BOBJBone bone = armature.bones.get("low_right_arm.end");
			Vector4f position = controller.calcPosition(livingBase, bone, 0.0f, 0.15f, 0.0f, partialTicks);
			int count = tick == 78 ? 12 : 1;

			for (int i = 0; i < count; ++i) {
				SaltParticle particle = new SaltParticle(livingBase.world, position.x, position.y, position.z, 0);
				Minecraft.getInstance().particles.addEffect(particle);
			}
		}
	}
}
