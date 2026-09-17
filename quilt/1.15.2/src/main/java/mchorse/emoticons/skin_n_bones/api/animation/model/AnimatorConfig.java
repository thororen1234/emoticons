package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
		this.leftHands = new HashMap<>();
		this.rightHands = new HashMap<>();
		this.head = "head";
		this.actions = new AnimatorActionsConfig();
		this.meshes = new HashMap<>();
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

	public void fromNBT(final CompoundTag NbtCompound) {
		if (NbtCompound.contains("Name")) {
			this.name = NbtCompound.getString("Name");
		}
		if (NbtCompound.contains("Scale")) {
			this.scale = NbtCompound.getFloat("Scale");
		}
		if (NbtCompound.contains("ScaleGUI")) {
			this.scaleGui = NbtCompound.getFloat("ScaleGUI");
		}
		if (NbtCompound.contains("ScaleItems")) {
			this.scaleItems = NbtCompound.getFloat("ScaleItems");
		}
		if (NbtCompound.contains("RenderHeldItems")) {
			this.renderHeldItems = NbtCompound.getBoolean("RenderHeldItems");
		}
		if (NbtCompound.contains("LeftHands")) {
			this.readHandsFromNBT(this.leftHands, NbtCompound.get("LeftHands"));
		}
		if (NbtCompound.contains("RightHands")) {
			this.readHandsFromNBT(this.rightHands, NbtCompound.get("RightHands"));
		}
		if (NbtCompound.contains("Head")) {
			this.head = NbtCompound.getString("Head");
		}
		if (NbtCompound.contains("Actions")) {
			this.actions.fromNBT(NbtCompound.getCompound("Actions"));
		}
		if (NbtCompound.contains("Meshes")) {
			final CompoundTag meshesCompound = NbtCompound.getCompound("Meshes");
			for (final String s : meshesCompound.getKeys()) {
				final Tag mesh = meshesCompound.get(s);
				AnimationMeshConfig AnimationMeshConfig = this.meshes.get(s);
				if (AnimationMeshConfig == null) {
					this.meshes.put(s, AnimationMeshConfig = new AnimationMeshConfig());
				}
				AnimationMeshConfig.fromNBT((CompoundTag) mesh);
			}
		}
	}

	public CompoundTag toNBT(CompoundTag NbtCompound) {
		if (NbtCompound == null) {
			NbtCompound = new CompoundTag();
		}
		if (!this.name.isEmpty()) {
			NbtCompound.putString("Name", this.name);
		}
		if (this.scale != 1.0f) {
			NbtCompound.putFloat("Scale", this.scale);
		}
		if (this.scaleGui != 1.0f) {
			NbtCompound.putFloat("ScaleGUI", this.scaleGui);
		}
		if (this.scaleItems != 1.0f) {
			NbtCompound.putFloat("ScaleItems", this.scaleItems);
		}
		if (!this.renderHeldItems) {
			NbtCompound.putBoolean("RenderHeldItems", this.renderHeldItems);
		}
		if (!this.head.equals("head")) {
			NbtCompound.putString("Head", this.head);
		}
		if (!this.leftHands.isEmpty()) {
			NbtCompound.put("LeftHands", this.writeHandsToNBT(this.leftHands));
		}
		if (!this.rightHands.isEmpty()) {
			NbtCompound.put("RightHands", this.writeHandsToNBT(this.rightHands));
		}
		final CompoundTag actionCompouind = this.actions.toNBT((CompoundTag) null);
		if (actionCompouind != null && !actionCompouind.isEmpty()) {
			NbtCompound.put("Actions", actionCompouind);
		}
		if (!this.meshes.isEmpty()) {
			final CompoundTag meshesCompound = new CompoundTag();
			for (final Map.Entry<String, AnimationMeshConfig> entry : this.meshes.entrySet()) {
				meshesCompound.put(entry.getKey(), entry.getValue().toNBT(null));
			}
			NbtCompound.put("Meshes", meshesCompound);
		}
		return NbtCompound;
	}

	private void readHandsFromNBT(final Map<String, AnimatorHeldItemConfig> map, final Tag base) {
		map.clear();
		if (base instanceof ListTag) {
			final ListTag listCompound = (ListTag) base;
			for (int i = 0; i < listCompound.size(); ++i) {
				final String key = listCompound.getString(i);
				map.put(key, new AnimatorHeldItemConfig(key));
			}
		} else if (base instanceof CompoundTag) {
			final CompoundTag NbtCompound = (CompoundTag) base;
			for (final String s : NbtCompound.getKeys()) {
				AnimatorHeldItemConfig config = map.get(s);
				if (config == null) {
					map.put(s, config = new AnimatorHeldItemConfig(s));
				}
				config.fromNBT(NbtCompound.getCompound(s));
			}
		}
	}

	private CompoundTag writeHandsToNBT(final Map<String, AnimatorHeldItemConfig> map) {
		final CompoundTag NbtCompound = new CompoundTag();
		for (final Map.Entry<String, AnimatorHeldItemConfig> entry : map.entrySet()) {
			NbtCompound.put(entry.getKey(), entry.getValue().toNBT(null));
		}
		return NbtCompound;
	}
}

