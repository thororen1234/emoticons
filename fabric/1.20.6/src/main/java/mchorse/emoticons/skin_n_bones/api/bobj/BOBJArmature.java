package mchorse.emoticons.skin_n_bones.api.bobj;

import javax.vecmath.Matrix4f;
import java.util.*;

public class BOBJArmature {
	public String name;
	public String action;
	public Map<String, BOBJBone> bones;
	public List<BOBJBone> orderedBones;
	public List<BOBJBone> ikBones;
	public Matrix4f[] matrices;
	private boolean initialized;

	public BOBJArmature(final String name) {
		this.action = "";
		this.bones = new HashMap<>();
		this.orderedBones = new ArrayList<>();
		this.ikBones = new ArrayList<>();
		this.name = name;
	}

	public void addBone(final BOBJBone bone) {
		this.bones.put(bone.name, bone);
		this.orderedBones.add(bone);
	}

	public void initArmature() {
		if (!this.initialized) {
			final ArrayList<BOBJBone> bones = new ArrayList<>();
			for (final BOBJBone bone : this.bones.values()) {
				if (bone.hasModifiers()) {
					bones.add(bone);
				}
				if (!bone.parent.isEmpty()) {
					bone.parentBone = this.bones.get(bone.parent);
					bone.relBoneMat.set(bone.parentBone.boneMat);
					bone.relBoneMat.invert();
					bone.relBoneMat.mul(bone.boneMat);
				} else {
					bone.relBoneMat.set(bone.boneMat);
				}
			}
			if (!bones.isEmpty()) {
				this.ikBones = bones;
			}
			Collections.sort(this.orderedBones, new BoneSorter(this));
			this.matrices = new Matrix4f[this.orderedBones.size()];
			this.initialized = true;
		}
	}

	public void setupMatrices() {
		for (final BOBJBone bone : this.orderedBones) {
			this.matrices[bone.index] = bone.compute();
		}
	}
}

