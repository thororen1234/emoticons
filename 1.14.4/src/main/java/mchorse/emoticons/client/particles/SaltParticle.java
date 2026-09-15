package mchorse.emoticons.client.particles;

import net.minecraft.world.World;

public class SaltParticle extends ModelParticle {
	public SaltParticle(World world, double x, double y, double z, double velocityY) {
		super(world, x, y, z, velocityY, 0.5F, 0);
	}
}
