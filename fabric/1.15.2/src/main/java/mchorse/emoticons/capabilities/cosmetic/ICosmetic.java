package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.common.emotes.Emote;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface ICosmetic {
	public CosmeticMode getMode();

	public void setMode(CosmeticMode var1);

	public void setEmote(Emote var1, LivingEntity var2);

	public Emote getEmote();

	public void update(LivingEntity var1);

	public boolean render(LivingEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, float yaw, float tickDelta);
}
