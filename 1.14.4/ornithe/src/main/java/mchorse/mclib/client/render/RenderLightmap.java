package mchorse.mclib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.resource.Identifier;

public class RenderLightmap extends LivingEntityRenderer<LivingEntity, Model<LivingEntity>> {

	public static void create() {
		new RenderLightmap(Minecraft.getInstance().getEntityRenderDispatcher(), null, 0.0f);
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

	public RenderLightmap(EntityRenderDispatcher dispatcher, Model<LivingEntity> model, float shadowSize) {
		super(dispatcher, model, shadowSize);
	}

	@Override
	protected int getOverlayColor(LivingEntity entity, float f, float g) {
		return 0;
	}

	@Override
	public Identifier getTextureLocation(LivingEntity entity) {
		return null;
	}
}
