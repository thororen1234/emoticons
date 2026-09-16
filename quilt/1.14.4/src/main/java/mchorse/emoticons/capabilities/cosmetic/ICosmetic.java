package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.entity.LivingEntity;

public interface ICosmetic {
	public CosmeticMode getMode();

	public void setMode(CosmeticMode var1);

	public void setEmote(Emote var1, LivingEntity var2);

	public Emote getEmote();

	public void update(LivingEntity var1);

	public boolean render(LivingEntity var1, double var2, double var4, double var6, float var8);
}
