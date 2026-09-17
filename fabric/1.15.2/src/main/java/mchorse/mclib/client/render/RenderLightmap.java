package mchorse.mclib.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;

public class RenderLightmap extends LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> {

	public static void create() {
		new RenderLightmap(MinecraftClient.getInstance().getEntityRenderManager(), null, 0.0f);
	}

	public static boolean canRenderNamePlate(LivingEntity livingBase) {
		return false;
	}

	public static boolean set(LivingEntity livingBase, float f) {
		return false;
	}

	public static void unset() {
	}

	public static void renderNameplate(LivingEntity livingBase, String name, double x, double y, double z) {
	}

	public RenderLightmap(EntityRenderDispatcher dispatcher, EntityModel<LivingEntity> model, float shadowSize) {
		super(dispatcher, model, shadowSize);
	}

	/*
	 * 1.15 replaced LivingEntityRenderer#getOverlayColor(T, float, float) with the
	 * overlay-texture pipeline; the closest hook is getWhiteOverlayProgress.
	 */
	@Override
	protected float getWhiteOverlayProgress(LivingEntity entity, float tickDelta) {
		return 0.0F;
	}

	@Override
	public Identifier getTexture(LivingEntity entity) {
		return null;
	}
}
