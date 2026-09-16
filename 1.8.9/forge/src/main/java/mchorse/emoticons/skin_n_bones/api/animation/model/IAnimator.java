package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.EntityLivingBase;

public interface IAnimator {
    public void refresh();

    public void setEmote(ActionPlayback var1);

    public void update(EntityLivingBase var1);
    public void applyActions(BOBJArmature var1, float var2);

    public BOBJArmature useArmature(BOBJArmature armature);
}
