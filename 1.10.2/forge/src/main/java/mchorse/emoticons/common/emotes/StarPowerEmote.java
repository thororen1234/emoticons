package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

import javax.vecmath.Vector4f;

public class StarPowerEmote extends Emote {
    public StarPowerEmote(String key, int duration, boolean looping) {
        super(key, duration, looping);
    }

    @Override
    public void progressAnimation(EntityLivingBase livingBase, BOBJArmature armature, AnimatorEmoticonsController controller, int tick, float partialTicks) {
        if (tick == 30) {
            BOBJBone bone = armature.bones.get("low_right_arm.end");
            Vector4f position = controller.calcPosition(livingBase, bone, 0.0f, 0.15f, 0.0f, partialTicks);

            for (int i = 0; i < 15; ++i) {
                livingBase.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
                        position.x, position.y, position.z,
                        this.rand.nextDouble() * 0.05 - 0.025,
                        this.rand.nextDouble() * 0.05 - 0.025,
                        this.rand.nextDouble() * 0.05 - 0.025);
            }
        }

        if (tick >= 33 && tick < 43) {
            BOBJBone bone = armature.bones.get("low_right_arm.end");
            Vector4f position = controller.calcPosition(livingBase, bone, 0.0f, 0.15f, 0.0f, partialTicks);

            float red = 1.0f;
            float green = 0.0f;
            float blue = 0.0f;
            float progress = (tick - 33) / 10.0f;

            if (progress >= 0.2) {
                if (progress < 0.35) {
                    green = 0.5f;
                } else if (progress < 0.45) {
                    green = 1.0f;
                } else if (progress < 0.65) {
                    red = 0.25f;
                    green = 1.0f;
                } else if (progress < 0.85) {
                    red = 0.0f;
                    green = 0.75f;
                    blue = 1.0f;
                } else {
                    red = 0.0f;
                    green = 0.0f;
                    blue = 1.0f;
                }
            }

            for (int i = 0; i < 7; ++i) {
                livingBase.world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                        position.x + this.rand.nextDouble() * 0.05 - 0.025,
                        position.y + this.rand.nextDouble() * 0.05 - 0.025,
                        position.z + this.rand.nextDouble() * 0.05 - 0.025,
                        red, green, blue);
            }
        }
    }
}
