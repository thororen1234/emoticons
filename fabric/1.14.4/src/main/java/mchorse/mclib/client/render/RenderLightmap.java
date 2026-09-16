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

	@Override
	protected int getOverlayColor(LivingEntity entity, float f, float g) {
		return 0;
	}

	@Override
	public Identifier getTexture(LivingEntity entity) {
		return null;
	}
}
