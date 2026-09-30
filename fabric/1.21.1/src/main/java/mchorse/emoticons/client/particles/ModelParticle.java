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
	protected static ModelPart createModel(int textureU, int textureV) {
		java.util.List<ModelPart.Cuboid> cuboids = new java.util.ArrayList<>();
		cuboids.add(new ModelPart.Cuboid(textureU, textureV, -0.5F, -0.5F, 0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, false, 64.0F, 64.0F));
		return new ModelPart(cuboids, java.util.Collections.emptyMap());
	}

	@Override
	public void buildGeometry(VertexConsumer buffer, Camera camera, float partialTicks) {
		float remaining = maxAge - age - partialTicks;
		float shrink = Math.max(0, Math.min(1, remaining / 5F));
		if (shrink == 0) return;

		Vec3d cameraPos = camera.getPos();
		double dx = MathHelper.lerp((double) partialTicks, prevPosX, x) - cameraPos.getX();
		double dy = MathHelper.lerp((double) partialTicks, prevPosY, y) - cameraPos.getY();
		double dz = MathHelper.lerp((double) partialTicks, prevPosZ, z) - cameraPos.getZ();

		MinecraftClient minecraft = MinecraftClient.getInstance();
		VertexConsumerProvider.Immediate immediate = minecraft.getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer consumer = immediate.getBuffer(RenderLayer.getEntityCutoutNoCull(PARTICLES));

		MatrixStack matrices = new MatrixStack();
		matrices.push();
		matrices.translate(dx, dy, dz);
		float size = scale * shrink;
		matrices.scale(size, size, size);
		model.render(matrices, consumer, getBrightness(partialTicks), OverlayTexture.DEFAULT_UV);
		matrices.pop();

		immediate.draw();
	}
}
