package mchorse.emoticons.skin_n_bones.api.animation.model;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

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

    public void fromNBT(NBTBase base) {
        if (base instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) base;
            if (compound.hasKey("Name")) {
                this.name = compound.getString("Name");
            }
            if (compound.hasKey("Clamp")) {
                this.clamp = compound.getBoolean("Clamp");
            }
            if (compound.hasKey("Reset")) {
                this.reset = compound.getBoolean("Reset");
            }
            if (compound.hasKey("Speed")) {
                this.speed = compound.getFloat("Speed");
            }
            if (compound.hasKey("Fade")) {
                this.fade = compound.getInteger("Fade");
            }
            if (compound.hasKey("Tick")) {
                this.tick = compound.getInteger("Tick");
            }
        } else if (base instanceof NBTTagString) {
            this.name = ((NBTTagString) base).func_150285_a_();
        }
    }

    public NBTBase toNBT() {
        if (!this.name.isEmpty() && this.isDefault()) {
            return new NBTTagString(this.name);
        }
        NBTTagCompound compound = new NBTTagCompound();
        if (!this.name.isEmpty()) {
            compound.setString("Name", this.name);
        }
        if (!this.clamp) {
            compound.setBoolean("Clamp", this.clamp);
        }
        if (!this.reset) {
            compound.setBoolean("Reset", this.reset);
        }
        if (this.speed != 1.0f) {
            compound.setFloat("Speed", this.speed);
        }
        if (this.fade != 5.0f) {
            compound.setInteger("Fade", (int) this.fade);
        }
        if (this.tick != 0) {
            compound.setInteger("Tick", this.tick);
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
