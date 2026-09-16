package mchorse.emoticons.mixin;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin {
	@Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDFF)V", at = @At("HEAD"), cancellable = true)
	private void emoticons$render(AbstractClientPlayerEntity entity, double x, double y, double z, float yaw, float delta, CallbackInfo ci) {
		if (entity.isSpectator() || entity.isInvisible() || entity.isSleeping()) return;
		ICosmetic state = EmoteController.get(entity);
		if ((state.getEmote() != null || !ClientConfig.instance.disableAnimations) && state.render(entity, x, y, z, delta)) {
			mchorse.mclib.client.render.RenderLightmap.renderNameplate(entity, entity.getName().getString(), x, y, z);
			ci.cancel();
		}
	}
}
