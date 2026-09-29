package mchorse.emoticons.mixin;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin {
	@Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	private void emoticons$render(AbstractClientPlayerEntity entity, float yaw, float delta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (entity.isSpectator() || entity.isInvisible() || entity.isSleeping()) return;
		ICosmetic state = EmoteController.get(entity);
		if ((state.getEmote() != null || !ClientConfig.instance.disableAnimations)
				&& state.render(entity, matrices, vertexConsumers, light, yaw, delta)) {
			ci.cancel();
		}
	}
}
