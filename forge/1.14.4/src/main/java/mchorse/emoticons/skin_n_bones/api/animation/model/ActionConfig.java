package mchorse.emoticons.skin_n_bones.api.animation.model;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;

public class ActionConfig {
	public String name = "";
	public boolean clamp = true;
	public boolean reset = true;
	public float speed = 1.0f;
	public float fade = 5.0f;
	public int tick = 0;

	public ActionConfig() {
	}

	public ActionConfig(String string) {
		this.name = string;
	}

	public ActionConfig copy() {
		ActionConfig config = new ActionConfig(this.name);
		config.clamp = this.clamp;
		config.reset = this.reset;
		config.speed = this.speed;
		config.fade = this.fade;
		config.tick = this.tick;
		return config;
	}

	public void fromNBT(INBT base) {
		if (base instanceof CompoundNBT) {
			CompoundNBT compound = (CompoundNBT) base;
			if (compound.contains("Name")) {
				this.name = compound.getString("Name");
			}
			if (compound.contains("Clamp")) {
				this.clamp = compound.getBoolean("Clamp");
			}
			if (compound.contains("Reset")) {
				this.reset = compound.getBoolean("Reset");
			}
			if (compound.contains("Speed")) {
				this.speed = compound.getFloat("Speed");
			}
			if (compound.contains("Fade")) {
				this.fade = compound.getInt("Fade");
			}
			if (compound.contains("Tick")) {
				this.tick = compound.getInt("Tick");
			}
		} else if (base instanceof StringNBT) {
			this.name = ((StringNBT) base).getString();
		}
	}

	public INBT toNBT() {
		if (!this.name.isEmpty() && this.isDefault()) {
			return new StringNBT(this.name);
		}
		CompoundNBT compound = new CompoundNBT();
		if (!this.name.isEmpty()) {
			compound.putString("Name", this.name);
		}
		if (!this.clamp) {
			compound.putBoolean("Clamp", this.clamp);
		}
		if (!this.reset) {
			compound.putBoolean("Reset", this.reset);
		}
		if (this.speed != 1.0f) {
			compound.putFloat("Speed", this.speed);
		}
		if (this.fade != 5.0f) {
			compound.putInt("Fade", (int) this.fade);
		}
		if (this.tick != 0) {
			compound.putInt("Tick", this.tick);
		}
		return compound;
	}

	public boolean isDefault() {
		return this.clamp && this.reset && this.speed == 1.0f && this.fade == 5.0f && this.tick == 0;
	}

	public /* synthetic */ Object clone() {
		return this.copy();
	}
}
