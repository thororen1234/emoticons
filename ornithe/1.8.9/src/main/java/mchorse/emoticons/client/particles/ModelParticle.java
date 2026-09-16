package mchorse.emoticons.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.particle.Particle;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.render.platform.GLX;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Identifier;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/** Small textured cubes rendered in the particle manager's unbatched model pass. */
abstract class ModelParticle extends Particle {
	public static final Identifier PARTICLES = new Identifier("emoticons", "textures/particles.png");
	private static final ModelPart[] MODELS = createModels();
	private final ModelPart model;
	private final float scale;

	protected ModelParticle(World world, double x, double y, double z, double velocityY, float scale, int texture) {
		super(world, x, y, z);
		gravity = 0.5F;
		lifetime = 20 + random.nextInt(10);
		velocityX = random.nextFloat() * 0.05F;
		velocityZ = random.nextFloat() * 0.05F;
		this.velocityY = velocityY;
		this.scale = scale;
		model = MODELS[texture];
	}

	private static ModelPart[] createModels() {
		Model base = new Model() {};
		base.textureWidth = 64;
		base.textureHeight = 64;
		ModelPart[] models = new ModelPart[4];
		for (int i = 0; i < models.length; i++) {
			models[i] = new ModelPart(base, 0, i * 2);
			models[i].addBox(-0.5F, -0.5F, 0.5F, 1, 1, 1);
		}
		return models;
	}

	@Override
	public int getAtlasType() {
		return 3;
	}

	@Override
	public void render(BufferBuilder buffer, Entity camera, float partialTicks,
			float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float remaining = lifetime - age - partialTicks;
		float shrink = Math.max(0, Math.min(1, remaining / 5F));
		if (shrink == 0) return;

		int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
		boolean rescaleNormal = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
		int light = getLightLevel(partialTicks);
		GLX.multiTexCoord2f(GLX.GL_TEXTURE1, light & 65535, light >> 16);
		Minecraft.getInstance().getTextureManager().bind(PARTICLES);

		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.pushMatrix();
		try {
			GlStateManager.translated(
					lastX + (x - lastX) * partialTicks - lerpCameraX,
					lastY + (y - lastY) * partialTicks - lerpCameraY,
					lastZ + (z - lastZ) * partialTicks - lerpCameraZ);
			float size = scale * shrink;
			GlStateManager.scalef(size, size, size);
			GlStateManager.enableRescaleNormal();
			Lighting.turnOn();
			model.render(1 / 16F);
		} finally {
			if (!lighting) Lighting.turnOff();
			if (!rescaleNormal) GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.bindTexture(previousTexture);
		}
	}
}
