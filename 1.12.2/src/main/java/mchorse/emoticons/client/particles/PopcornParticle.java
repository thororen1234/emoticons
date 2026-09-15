package mchorse.emoticons.client.particles;

import net.minecraft.world.World;

public class PopcornParticle extends ModelParticle {
	public PopcornParticle(World world, double x, double y, double z, double velocityY) {
		super(world, x, y, z, velocityY, 0.75F, 1 + world.random.nextInt(3));
		if (velocityY != 0) this.velocityY += random.nextDouble() * 0.1F;
	}
}
