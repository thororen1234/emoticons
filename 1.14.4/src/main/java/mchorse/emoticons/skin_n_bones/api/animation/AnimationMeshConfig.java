package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.CompoundTag;

public class AnimationMeshConfig {
	public Identifier texture;
	public int filtering = 9728;
	public boolean normals = false;
	public boolean smooth = false;
	public boolean visible = true;
	public boolean lighting = true;
	public int color = 0xFFFFFF;

	public AnimationMeshConfig copy() {
		AnimationMeshConfig config = new AnimationMeshConfig();
		config.texture = this.texture;
		config.filtering = this.filtering;
		config.normals = this.normals;
		config.smooth = this.smooth;
		config.visible = this.visible;
		config.lighting = this.lighting;
		config.color = this.color;
		return config;
	}

	public void fromNBT(CompoundTag compound) {
		if (compound.contains("Texture")) {
			this.texture = RLUtils.create(compound.get("Texture"));
		}
		if (compound.contains("Filtering")) {
			this.filtering = compound.getString("Filtering").equalsIgnoreCase("linear") ? 9729 : 9728;
		}
		if (compound.contains("Normals")) {
			this.normals = compound.getBoolean("Normals");
		}
		if (compound.contains("Smooth")) {
			this.smooth = compound.getBoolean("Smooth");
		}
		if (compound.contains("Visible")) {
			this.visible = compound.getBoolean("Visible");
		}
		if (compound.contains("Lighting")) {
			this.lighting = compound.getBoolean("Lighting");
		}
		if (compound.contains("Color")) {
			this.color = compound.getInt("Color");
		}
	}

	public CompoundTag toNBT(CompoundTag compound) {
		if (compound == null) {
			compound = new CompoundTag();
		}
		if (this.texture != null) {
			compound.put("Texture", RLUtils.writeNbt(this.texture));
		}
		compound.putString("Filtering", this.filtering == 9728 ? "nearest" : "linear");
		compound.putBoolean("Normals", this.normals);
		compound.putBoolean("Smooth", this.smooth);
		compound.putBoolean("Visible", this.visible);
		compound.putBoolean("Lighting", this.lighting);
		compound.putInt("Color", this.color);
		return compound;
	}

	public /* synthetic */ Object clone() {
		return this.copy();
	}
}
