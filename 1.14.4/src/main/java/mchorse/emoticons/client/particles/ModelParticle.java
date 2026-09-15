package mchorse.emoticons.client.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.model.ModelPart;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;

/**
 * Small textured cubes rendered in the particle manager's unbatched model pass.
 */
abstract class ModelParticle extends Particle {
	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	public static final Identifier PARTICLES = new Identifier("emoticons", "textures/particles.png");
	private final ModelPart model;
	private final float scale;

	protected ModelParticle(World world, double x, double y, double z, double velocityY, float scale,
			ModelPart model) {
		super(world, x, y, z);
		gravityStrength = 0.5F;
		maxAge = 20 + random.nextInt(10);
		velocityX = random.nextFloat() * 0.05F;
		velocityZ = random.nextFloat() * 0.05F;
		this.velocityY = velocityY;
		this.scale = scale;
		this.model = model;
	}

	protected static ModelPart createModel(int textureU, int textureV) {
		EntityModel<Entity> base = new EntityModel<Entity>() {
		};
		base.textureWidth = 64;
		base.textureHeight = 64;
		ModelPart model = new ModelPart(base, textureU, textureV);
		model.addCuboid(-0.5F, -0.5F, 0.5F, 1, 1, 1);

		return model;
	}

	@Override
	public void buildGeometry(BufferBuilder buffer, Camera camera, float partialTicks,
			float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float remaining = maxAge - age - partialTicks;
		float shrink = Math.max(0, Math.min(1, remaining / 5F));
		if (shrink == 0)
			return;

		int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
		boolean rescaleNormal = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.getTextureManager().bindTexture(PARTICLES);
		boolean lightmapDisabled = false;

		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.pushMatrix();
		try {
			GlStateManager.translated(
					prevPosX + (x - prevPosX) * partialTicks - cameraX,
					prevPosY + (y - prevPosY) * partialTicks - cameraY,
					prevPosZ + (z - prevPosZ) * partialTicks - cameraZ);
			float size = scale * shrink;
			GlStateManager.scalef(size, size, size);
			GlStateManager.enableRescaleNormal();
			// ModelPart's display list has no per-particle light coordinates. Leaving
			// the lightmap active can multiply it by black when the camera is high up.
			minecraft.gameRenderer.disableLightmap();
			lightmapDisabled = true;
			DiffuseLighting.enable();
			model.render(1 / 16F);
		} finally {
			if (lightmapDisabled)
				minecraft.gameRenderer.enableLightmap();
			if (!lighting)
				DiffuseLighting.disable();
			if (!rescaleNormal)
				GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.bindTexture(previousTexture);
		}
	}
}
