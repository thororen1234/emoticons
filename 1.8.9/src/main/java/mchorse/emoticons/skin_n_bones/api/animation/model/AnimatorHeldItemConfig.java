package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.mclib.utils.Interpolation;
import net.minecraft.nbt.NbtCompound;

public class AnimatorHeldItemConfig {
	public String boneName = "";
	public float x;
	public float y;
	public float z;
	public float scaleX = 1.0f;
	public float scaleY = 1.0f;
	public float scaleZ = 1.0f;
	public float rotateX;
	public float rotateY;
	public float rotateZ;

	public AnimatorHeldItemConfig(String string) {
		this.boneName = string;
	}

	public void interpolate(AnimatorHeldItemConfig a, AnimatorHeldItemConfig b, float f, Interpolation interp) {
		this.x = interp.interpolate(a.x, b.x, f);
		this.y = interp.interpolate(a.y, b.y, f);
		this.z = interp.interpolate(a.z, b.z, f);
		this.scaleX = interp.interpolate(a.scaleX, b.scaleX, f);
		this.scaleY = interp.interpolate(a.scaleY, b.scaleY, f);
		this.scaleZ = interp.interpolate(a.scaleZ, b.scaleZ, f);
		this.rotateX = interp.interpolate(a.rotateX, b.rotateX, f);
		this.rotateY = interp.interpolate(a.rotateY, b.rotateY, f);
		this.rotateZ = interp.interpolate(a.rotateZ, b.rotateZ, f);
	}

	public AnimatorHeldItemConfig clone() {
		AnimatorHeldItemConfig config = new AnimatorHeldItemConfig(this.boneName);
		config.x = this.x;
		config.y = this.y;
		config.z = this.z;
		config.scaleX = this.scaleX;
		config.scaleY = this.scaleY;
		config.scaleZ = this.scaleZ;
		config.rotateX = this.rotateX;
		config.rotateY = this.rotateY;
		config.rotateZ = this.rotateZ;
		return config;
	}

	public boolean equals(Object object) {
		if (object instanceof AnimatorHeldItemConfig) {
			AnimatorHeldItemConfig config = (AnimatorHeldItemConfig) object;
			boolean bl = config.x == this.x && config.y == this.y && config.z == this.z;
			bl = bl && config.scaleX == this.scaleX && config.scaleY == this.scaleY && config.scaleZ == this.scaleZ;
			bl = bl && config.rotateX == this.rotateX && config.rotateY == this.rotateY
					&& config.rotateZ == this.rotateZ;
			return bl;
		}
		return super.equals(object);
	}

	public void fromNBT(NbtCompound compound) {
		if (compound.contains("X")) {
			this.x = compound.getFloat("X");
		}
		if (compound.contains("Y")) {
			this.y = compound.getFloat("Y");
		}
		if (compound.contains("Z")) {
			this.z = compound.getFloat("Z");
		}
		if (compound.contains("SX")) {
			this.scaleX = compound.getFloat("SX");
		}
		if (compound.contains("SY")) {
			this.scaleY = compound.getFloat("SY");
		}
		if (compound.contains("SZ")) {
			this.scaleZ = compound.getFloat("SZ");
		}
		if (compound.contains("RX")) {
			this.rotateX = compound.getFloat("RX");
		}
		if (compound.contains("RY")) {
			this.rotateY = compound.getFloat("RY");
		}
		if (compound.contains("RZ")) {
			this.rotateZ = compound.getFloat("RZ");
		}
	}

	public NbtCompound toNBT(NbtCompound compound) {
		if (compound == null) {
			compound = new NbtCompound();
		}
		if (this.x != 0.0f) {
			compound.putFloat("X", this.x);
		}
		if (this.y != 0.0f) {
			compound.putFloat("Y", this.y);
		}
		if (this.z != 0.0f) {
			compound.putFloat("Z", this.z);
		}
		if (this.scaleX != 1.0f) {
			compound.putFloat("SX", this.scaleX);
		}
		if (this.scaleY != 1.0f) {
			compound.putFloat("SY", this.scaleY);
		}
		if (this.scaleZ != 1.0f) {
			compound.putFloat("SZ", this.scaleZ);
		}
		if (this.rotateX != 0.0f) {
			compound.putFloat("RX", this.rotateX);
		}
		if (this.rotateY != 0.0f) {
			compound.putFloat("RY", this.rotateY);
		}
		if (this.rotateZ != 0.0f) {
			compound.putFloat("RZ", this.rotateZ);
		}
		return compound;
	}

}
