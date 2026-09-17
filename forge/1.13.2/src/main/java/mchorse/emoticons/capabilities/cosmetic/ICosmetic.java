package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.entity.EntityLivingBase;

public interface ICosmetic {
	public CosmeticMode getMode();

	public void setMode(CosmeticMode var1);

	public void setEmote(Emote var1, EntityLivingBase var2);

	public Emote getEmote();

	public void update(EntityLivingBase var1);

	public boolean render(EntityLivingBase var1, double var2, double var4, double var6, float var8);
}
