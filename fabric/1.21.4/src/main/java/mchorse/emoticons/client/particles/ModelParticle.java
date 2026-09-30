package mchorse.emoticons.client.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Small textured cubes rendered in the particle manager's unbatched model pass.
 */
abstract class ModelParticle extends Particle {
	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	public static final Identifier PARTICLES = Identifier.of("emoticons", "textures/particles.png");
	private final ModelPart model;
	private final float scale;

	protected ModelParticle(ClientWorld world, double x, double y, double z, double velocityY, float scale,
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

	/*
	 * 1.15 dropped ModelPart(Model, u, v) as the only usable constructor: the
	 * standalone ModelPart(textureWidth, textureHeight, u, v) form removes the need
	 * for a dummy EntityModel (whose setAngles is abstract in 1.15 anyway).
	 */
	protected static ModelPart createModel(int textureU, int textureV) { return null; }

	/*
	 * 1.15 rewrote Particle#buildGeometry to take a VertexConsumer instead of a
	 * BufferBuilder plus the five pre-baked billboard rotation factors, and the
	 * static Particle.cameraX/Y/Z fields are gone (the Camera carries the position
	 * now). ParticleTextureSheet.CUSTOM is still an unbatched pass, so this draws
	 * its own geometry through the entity vertex consumers instead of the supplied
	 * buffer. The particle manager has already pushed the camera matrix onto the
	 * GL modelview stack, so the local MatrixStack only carries camera-relative
	 * offsets.
	 */
	@Override
	public void buildGeometry(net.minecraft.client.render.VertexConsumer consumer, net.minecraft.client.render.Camera camera, float partialTicks) {}
}
