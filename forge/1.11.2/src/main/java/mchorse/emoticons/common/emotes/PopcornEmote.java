package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.client.particles.PopcornParticle;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

import javax.vecmath.Vector4f;

public class PopcornEmote extends Emote {
    public PopcornEmote(String key, int duration, boolean looping) {
        super(key, duration, looping);
    }

    @Override
    public void progressAnimation(EntityLivingBase livingBase, BOBJArmature armature, AnimatorEmoticonsController controller, int tick, float partialTicks) {
        if (tick == 8 || tick == 32 || tick == 56 || tick == 86) {
            BOBJBone bone = armature.bones.get("low_right_arm.end");
            Vector4f position = controller.calcPosition(livingBase, bone, 0.0f, 0.15f, 0.0f, partialTicks);

            for (int i = 0; i < 15; ++i) {
                PopcornParticle particle = new PopcornParticle(livingBase.world, position.x, position.y, position.z, 0.1);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
        }
    }

    @Override
    public void startAnimation(AnimatorEmoticonsController controller) {
        controller.userConfig.meshes.get("popcorn").visible = true;
    }

    @Override
    public void stopAnimation(AnimatorEmoticonsController controller) {
        controller.userConfig.meshes.get("popcorn").visible = false;
    }
}
