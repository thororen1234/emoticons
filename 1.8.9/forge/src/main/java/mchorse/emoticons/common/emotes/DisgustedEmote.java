package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.utils.Time;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

import javax.vecmath.Vector4f;

public class DisgustedEmote extends Emote {
    public DisgustedEmote(String key, int duration, boolean looping) {
        super(key, duration, looping);
    }

    @Override
    public void progressAnimation(EntityLivingBase livingBase, BOBJArmature armature, AnimatorEmoticonsController controller, int tick, float partialTicks) {
        super.progressAnimation(livingBase, armature, controller, tick, partialTicks);

        if (tick >= Time.toTicks(117) && tick < Time.toTicks(140)) {
            for (int i = 0; i < 10; ++i) {
                Vector4f position = controller.calcPosition(livingBase, armature.bones.get("head"), 0.0f, 0.125f, 0.25f, partialTicks);
                livingBase.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK,
                        position.x + this.rand(0.1f), position.y, position.z + this.rand(0.1f),
                        this.rand(0.05f), -0.125, this.rand(0.05f), 351, 2);
            }
        }
    }
}
