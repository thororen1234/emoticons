package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.entity.EntityLivingBase;

import javax.vecmath.Vector4f;

public class CryingEmote extends Emote {
    public CryingEmote(String key, int duration, boolean looping) {
        super(key, duration, looping);
    }

    @Override
    public void progressAnimation(EntityLivingBase livingBase, BOBJArmature armature, AnimatorEmoticonsController controller, int tick, float partialTicks) {
        if (tick % 2 == 0) {
            BOBJBone bone = armature.bones.get("head");
            Vector4f position = controller.calcPosition(livingBase, bone, 0.0f, 0.5f, 0.35f, partialTicks);
            position.y -= mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorController.ENTITY_RENDER_Y_OFFSET;
            livingBase.worldObj.spawnParticle("splash", position.x, position.y, position.z, 1.0, -1.0, 1.0);
        }
    }
}
