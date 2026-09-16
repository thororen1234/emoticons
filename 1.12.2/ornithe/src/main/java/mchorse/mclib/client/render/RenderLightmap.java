package mchorse.mclib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.resource.Identifier;

public class RenderLightmap extends LivingEntityRenderer<LivingEntity> {
	private static RenderLightmap instance;

	public static void create() {
		instance = new RenderLightmap(Minecraft.getInstance().getEntityRenderDispatcher(), null, 0.0f);
	}

	public static boolean canRenderNamePlate(LivingEntity livingBase) {
		if (instance == null) return false;
		return instance.shouldRenderNameTag(livingBase);
	}

	public static boolean set(LivingEntity livingBase, float f) {
		if (instance == null) return false;
		return instance.setupOverlayColor(livingBase, f);
	}

	public static void unset() {
		if (instance == null) return;
		instance.tearDownOverlayColor();
	}

	public static void renderNameplate(LivingEntity livingBase, String name, double x, double y, double z) {
		if (instance == null) return;
		instance.renderNameTag(livingBase, name, x, y, z, 64);
	}

	public RenderLightmap(EntityRenderDispatcher dispatcher, Model model, float shadowSize) {
		super(dispatcher, model, shadowSize);
	}

	@Override
	protected int getOverlayColor(LivingEntity entity, float f, float g) {
		return 0;
	}

	@Override
	protected Identifier getTextureLocation(LivingEntity entity) {
		return null;
	}
}
