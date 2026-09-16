package mchorse.emoticons.client.particles;

import net.minecraft.world.World;
import net.minecraft.client.model.ModelPart;

public class SaltParticle extends ModelParticle {
	private static final ModelPart SALT = createModel(0, 0);

	public SaltParticle(World world, double x, double y, double z, double velocityY) {
		super(world, x, y, z, velocityY, 0.5F, SALT);
	}
}
