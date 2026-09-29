package mchorse.emoticons.client.particles;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.model.ModelPart;

public class SaltParticle extends ModelParticle {
	private static final ModelPart SALT = createModel(0, 0);

	public SaltParticle(ClientWorld world, double x, double y, double z, double velocityY) {
		super(world, x, y, z, velocityY, 0.5F, SALT);
	}
}
