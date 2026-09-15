package mchorse.mclib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.client.resource.Identifier;

public class RenderLightmap extends LivingEntityRenderer {
	private static RenderLightmap instance;

	public static void create() {
		instance = new RenderLightmap(null, 0.0f);
	}

	public static boolean canRenderNamePlate(LivingEntity livingBase) {
		if (instance == null) return false;
		return instance.shouldRenderNameTag(livingBase);
	}

	public static boolean set(LivingEntity livingBase, float f) {
		if (instance == null) return false;
		return false;
	}

	public static void unset() {
	}

	public static void renderNameplate(LivingEntity livingBase, String name, double x, double y, double z) {
		if (instance == null) return;
		instance.renderNameTag(livingBase, x, y, z, name, 0.0f, 64.0);
	}

	public RenderLightmap(Model model, float shadowSize) {
		super(model, shadowSize);
	}

	@Override
	protected int getOverlayColor(LivingEntity entity, float f, float g) {
		return 0;
	}

	public void render(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
	}

	protected Identifier getTextureLocation(Entity entity) {
		return null;
	}
}

