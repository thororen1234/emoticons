package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public class AnimatorConfig {
    public String name;
    public String primaryMesh;
    public float scale;
    public float scaleGui;
    public float scaleItems;
    public boolean renderHeldItems;
    public Map<String, AnimatorHeldItemConfig> leftHands;
    public Map<String, AnimatorHeldItemConfig> rightHands;
    public String head;
    public AnimatorActionsConfig actions;
    public Map<String, AnimationMeshConfig> meshes;

    public AnimatorConfig() {
        this.name = "";
        this.primaryMesh = "";
        this.scale = 1.0f;
        this.scaleGui = 1.0f;
        this.scaleItems = 1.0f;
        this.renderHeldItems = true;
        this.leftHands = new HashMap();
        this.rightHands = new HashMap();
        this.head = "head";
        this.actions = new AnimatorActionsConfig();
        this.meshes = new HashMap();
    }

    public void copy(final AnimatorConfig config) {
        this.name = config.name;
        this.primaryMesh = config.primaryMesh;
        this.scale = config.scale;
        this.scaleGui = config.scaleGui;
        this.scaleItems = config.scaleItems;
        this.renderHeldItems = config.renderHeldItems;
        this.head = config.head;
        this.actions.copy(config.actions);
        this.leftHands.clear();
        this.rightHands.clear();
        this.meshes.clear();
        for (final Map.Entry<String, AnimatorHeldItemConfig> entry : config.leftHands.entrySet()) {
            this.leftHands.put(entry.getKey(), entry.getValue().clone());
        }
        for (final Map.Entry<String, AnimatorHeldItemConfig> entry2 : config.rightHands.entrySet()) {
            this.rightHands.put(entry2.getKey(), entry2.getValue().clone());
        }
        for (final Map.Entry<String, AnimationMeshConfig> entry3 : config.meshes.entrySet()) {
            this.meshes.put(entry3.getKey(), entry3.getValue().copy());
        }
    }

    public void fromNBT(final NBTTagCompound NBTTagCompound) {
        if (NBTTagCompound.hasKey("Name")) {
            this.name = NBTTagCompound.getString("Name");
        }
        if (NBTTagCompound.hasKey("Scale")) {
            this.scale = NBTTagCompound.getFloat("Scale");
        }
        if (NBTTagCompound.hasKey("ScaleGUI")) {
            this.scaleGui = NBTTagCompound.getFloat("ScaleGUI");
        }
        if (NBTTagCompound.hasKey("ScaleItems")) {
            this.scaleItems = NBTTagCompound.getFloat("ScaleItems");
        }
        if (NBTTagCompound.hasKey("RenderHeldItems")) {
            this.renderHeldItems = NBTTagCompound.getBoolean("RenderHeldItems");
        }
        if (NBTTagCompound.hasKey("LeftHands")) {
            this.readHandsFromNBT(this.leftHands, NBTTagCompound.getTag("LeftHands"));
        }
        if (NBTTagCompound.hasKey("RightHands")) {
            this.readHandsFromNBT(this.rightHands, NBTTagCompound.getTag("RightHands"));
        }
        if (NBTTagCompound.hasKey("Head")) {
            this.head = NBTTagCompound.getString("Head");
        }
        if (NBTTagCompound.hasKey("Actions")) {
            this.actions.fromNBT(NBTTagCompound.getCompoundTag("Actions"));
        }
        if (NBTTagCompound.hasKey("Meshes")) {
            final NBTTagCompound meshesCompound = NBTTagCompound.getCompoundTag("Meshes");
            for (final String s : meshesCompound.getKeySet()) {
                final NBTBase mesh = meshesCompound.getTag(s);
                AnimationMeshConfig AnimationMeshConfig = this.meshes.get(s);
                if (AnimationMeshConfig == null) {
                    this.meshes.put(s, AnimationMeshConfig = new AnimationMeshConfig());
                }
                AnimationMeshConfig.fromNBT((NBTTagCompound) mesh);
            }
        }
    }

    public NBTTagCompound toNBT(NBTTagCompound NBTTagCompound) {
        if (NBTTagCompound == null) {
            NBTTagCompound = new NBTTagCompound();
        }
        if (!this.name.isEmpty()) {
            NBTTagCompound.setString("Name", this.name);
        }
        if (this.scale != 1.0f) {
            NBTTagCompound.setFloat("Scale", this.scale);
        }
        if (this.scaleGui != 1.0f) {
            NBTTagCompound.setFloat("ScaleGUI", this.scaleGui);
        }
        if (this.scaleItems != 1.0f) {
            NBTTagCompound.setFloat("ScaleItems", this.scaleItems);
        }
        if (!this.renderHeldItems) {
            NBTTagCompound.setBoolean("RenderHeldItems", this.renderHeldItems);
        }
        if (!this.head.equals("head")) {
            NBTTagCompound.setString("Head", this.head);
        }
        if (!this.leftHands.isEmpty()) {
            NBTTagCompound.setTag("LeftHands", this.writeHandsToNBT(this.leftHands));
        }
        if (!this.rightHands.isEmpty()) {
            NBTTagCompound.setTag("RightHands", this.writeHandsToNBT(this.rightHands));
        }
        final NBTTagCompound actionCompouind = this.actions.toNBT((NBTTagCompound) null);
        if (actionCompouind != null && !actionCompouind.hasNoTags()) {
            NBTTagCompound.setTag("Actions", actionCompouind);
        }
        if (!this.meshes.isEmpty()) {
            final NBTTagCompound meshesCompound = new NBTTagCompound();
            for (final Map.Entry<String, AnimationMeshConfig> entry : this.meshes.entrySet()) {
                meshesCompound.setTag(entry.getKey(), entry.getValue().toNBT(null));
            }
            NBTTagCompound.setTag("Meshes", meshesCompound);
        }
        return NBTTagCompound;
    }

    private void readHandsFromNBT(final Map<String, AnimatorHeldItemConfig> map, final NBTBase base) {
        map.clear();
        if (base instanceof NBTTagList) {
            final NBTTagList listCompound = (NBTTagList) base;
            for (int i = 0; i < listCompound.tagCount(); ++i) {
                final String key = listCompound.getStringTagAt(i);
                map.put(key, new AnimatorHeldItemConfig(key));
            }
        } else if (base instanceof NBTTagCompound) {
            final NBTTagCompound NBTTagCompound = (NBTTagCompound) base;
            for (final String s : NBTTagCompound.getKeySet()) {
                AnimatorHeldItemConfig config = map.get(s);
                if (config == null) {
                    map.put(s, config = new AnimatorHeldItemConfig(s));
                }
                config.fromNBT(NBTTagCompound.getCompoundTag(s));
            }
        }
    }

    private NBTTagCompound writeHandsToNBT(final Map<String, AnimatorHeldItemConfig> map) {
        final NBTTagCompound NBTTagCompound = new NBTTagCompound();
        for (final Map.Entry<String, AnimatorHeldItemConfig> entry : map.entrySet()) {
            NBTTagCompound.setTag(entry.getKey(), entry.getValue().toNBT(null));
        }
        return NBTTagCompound;
    }
}


