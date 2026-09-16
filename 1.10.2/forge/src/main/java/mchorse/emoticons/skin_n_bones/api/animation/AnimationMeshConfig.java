package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class AnimationMeshConfig {
    public ResourceLocation texture;
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

    public void fromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Texture")) {
            this.texture = RLUtils.create(compound.getTag("Texture"));
        }
        if (compound.hasKey("Filtering")) {
            int n = this.filtering = compound.getString("Filtering").equalsIgnoreCase("linear") ? 9729 : 9728;
        }
        if (compound.hasKey("Normals")) {
            this.normals = compound.getBoolean("Normals");
        }
        if (compound.hasKey("Smooth")) {
            this.smooth = compound.getBoolean("Smooth");
        }
        if (compound.hasKey("Visible")) {
            this.visible = compound.getBoolean("Visible");
        }
        if (compound.hasKey("Lighting")) {
            this.lighting = compound.getBoolean("Lighting");
        }
        if (compound.hasKey("Color")) {
            this.color = compound.getInteger("Color");
        }
    }

    public NBTTagCompound toNBT(NBTTagCompound compound) {
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        if (this.texture != null) {
            compound.setTag("Texture", RLUtils.writeNbt(this.texture));
        }
        compound.setString("Filtering", this.filtering == 9728 ? "nearest" : "linear");
        compound.setBoolean("Normals", this.normals);
        compound.setBoolean("Smooth", this.smooth);
        compound.setBoolean("Visible", this.visible);
        compound.setBoolean("Lighting", this.lighting);
        compound.setInteger("Color", this.color);
        return compound;
    }

    public /* synthetic */ Object clone() {
        return this.copy();
    }
}
