package mchorse.emoticons.client.particles;

import net.minecraft.world.World;
import net.minecraft.client.model.ModelPart;

public class PopcornParticle extends ModelParticle {
	/* Matches the two randomly selected kernel textures from the Forge build. */
	private static final ModelPart KERNEL_ONE = createModel(0, 2);
	private static final ModelPart KERNEL_TWO = createModel(0, 4);

	public PopcornParticle(World world, double x, double y, double z, double velocityY) {
		super(world, x, y, z, velocityY, 0.75F, world.random.nextBoolean() ? KERNEL_ONE : KERNEL_TWO);
		if (velocityY != 0) this.velocityY += random.nextDouble() * 0.1F;
	}
}
