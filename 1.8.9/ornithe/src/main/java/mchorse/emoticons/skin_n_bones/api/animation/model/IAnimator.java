package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.living.LivingEntity;

public interface IAnimator {
	public void refresh();

	public void setEmote(ActionPlayback var1);

	public void update(LivingEntity var1);

	public void applyActions(BOBJArmature var1, float var2);
}
