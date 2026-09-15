package mchorse.emoticons.mixin;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
	@Inject(method = "render(Lnet/minecraft/client/entity/living/player/ClientPlayerEntity;DDDFF)V", at = @At("HEAD"), cancellable = true)
	private void emoticons$render(ClientPlayerEntity entity, double x, double y, double z, float yaw, float delta, CallbackInfo ci) {
		if (entity.isInvisible() || entity.isSleeping()) return;
		ICosmetic state = EmoteController.get(entity);
		if ((state.getEmote() != null || !ClientConfig.instance.disableAnimations) && state.render(entity, x, y - entity.eyeHeight, z, delta)) {

			ci.cancel();
		}
	}
}
