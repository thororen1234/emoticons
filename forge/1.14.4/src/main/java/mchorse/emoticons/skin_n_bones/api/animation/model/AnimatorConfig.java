package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

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

	public void fromNBT(final CompoundNBT CompoundNBT) {
		if (CompoundNBT.contains("Name")) {
			this.name = CompoundNBT.getString("Name");
		}
		if (CompoundNBT.contains("Scale")) {
			this.scale = CompoundNBT.getFloat("Scale");
		}
		if (CompoundNBT.contains("ScaleGUI")) {
			this.scaleGui = CompoundNBT.getFloat("ScaleGUI");
		}
		if (CompoundNBT.contains("ScaleItems")) {
			this.scaleItems = CompoundNBT.getFloat("ScaleItems");
		}
		if (CompoundNBT.contains("RenderHeldItems")) {
			this.renderHeldItems = CompoundNBT.getBoolean("RenderHeldItems");
		}
		if (CompoundNBT.contains("LeftHands")) {
			this.readHandsFromNBT(this.leftHands, CompoundNBT.get("LeftHands"));
		}
		if (CompoundNBT.contains("RightHands")) {
			this.readHandsFromNBT(this.rightHands, CompoundNBT.get("RightHands"));
		}
		if (CompoundNBT.contains("Head")) {
			this.head = CompoundNBT.getString("Head");
		}
		if (CompoundNBT.contains("Actions")) {
			this.actions.fromNBT(CompoundNBT.getCompound("Actions"));
		}
		if (CompoundNBT.contains("Meshes")) {
			final CompoundNBT meshesCompound = CompoundNBT.getCompound("Meshes");
			for (final String s : meshesCompound.keySet()) {
				final INBT mesh = meshesCompound.get(s);
				AnimationMeshConfig AnimationMeshConfig = this.meshes.get(s);
				if (AnimationMeshConfig == null) {
					this.meshes.put(s, AnimationMeshConfig = new AnimationMeshConfig());
				}
				AnimationMeshConfig.fromNBT((CompoundNBT) mesh);
			}
		}
	}

	public CompoundNBT toNBT(CompoundNBT CompoundNBT) {
		if (CompoundNBT == null) {
			CompoundNBT = new CompoundNBT();
		}
		if (!this.name.isEmpty()) {
			CompoundNBT.putString("Name", this.name);
		}
		if (this.scale != 1.0f) {
			CompoundNBT.putFloat("Scale", this.scale);
		}
		if (this.scaleGui != 1.0f) {
			CompoundNBT.putFloat("ScaleGUI", this.scaleGui);
		}
		if (this.scaleItems != 1.0f) {
			CompoundNBT.putFloat("ScaleItems", this.scaleItems);
		}
		if (!this.renderHeldItems) {
			CompoundNBT.putBoolean("RenderHeldItems", this.renderHeldItems);
		}
		if (!this.head.equals("head")) {
			CompoundNBT.putString("Head", this.head);
		}
		if (!this.leftHands.isEmpty()) {
			CompoundNBT.put("LeftHands", this.writeHandsToNBT(this.leftHands));
		}
		if (!this.rightHands.isEmpty()) {
			CompoundNBT.put("RightHands", this.writeHandsToNBT(this.rightHands));
		}
		final CompoundNBT actionCompouind = this.actions.toNBT((CompoundNBT) null);
		if (actionCompouind != null && !actionCompouind.isEmpty()) {
			CompoundNBT.put("Actions", actionCompouind);
		}
		if (!this.meshes.isEmpty()) {
			final CompoundNBT meshesCompound = new CompoundNBT();
			for (final Map.Entry<String, AnimationMeshConfig> entry : this.meshes.entrySet()) {
				meshesCompound.put(entry.getKey(), entry.getValue().toNBT(null));
			}
			CompoundNBT.put("Meshes", meshesCompound);
		}
		return CompoundNBT;
	}

	private void readHandsFromNBT(final Map<String, AnimatorHeldItemConfig> map, final INBT base) {
		map.clear();
		if (base instanceof ListNBT) {
			final ListNBT listCompound = (ListNBT) base;
			for (int i = 0; i < listCompound.size(); ++i) {
				final String key = listCompound.getString(i);
				map.put(key, new AnimatorHeldItemConfig(key));
			}
		} else if (base instanceof CompoundNBT) {
			final CompoundNBT CompoundNBT = (CompoundNBT) base;
			for (final String s : CompoundNBT.keySet()) {
				AnimatorHeldItemConfig config = map.get(s);
				if (config == null) {
					map.put(s, config = new AnimatorHeldItemConfig(s));
				}
				config.fromNBT(CompoundNBT.getCompound(s));
			}
		}
	}

	private CompoundNBT writeHandsToNBT(final Map<String, AnimatorHeldItemConfig> map) {
		final CompoundNBT CompoundNBT = new CompoundNBT();
		for (final Map.Entry<String, AnimatorHeldItemConfig> entry : map.entrySet()) {
			CompoundNBT.put(entry.getKey(), entry.getValue().toNBT(null));
		}
		return CompoundNBT;
	}
}

