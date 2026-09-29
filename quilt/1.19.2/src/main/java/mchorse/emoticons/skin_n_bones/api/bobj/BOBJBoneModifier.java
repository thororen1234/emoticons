package mchorse.emoticons.skin_n_bones.api.bobj;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class BOBJBoneModifier {
	public BOBJBone target;
	public int chain = 0;
	public boolean stick;
	private Vector4f global = new Vector4f();
	private Vector4f local = new Vector4f();
	private Matrix4f inverse = new Matrix4f();

	public BOBJBoneModifier(BOBJBone bone, int n, boolean bl) {
		this.target = bone;
		this.chain = n;
		this.stick = bl;
	}

	public void apply(BOBJBone bone) {
		if (this.chain == 0 || this.target == null) {
			return;
		}
		this.global.set(0.0f, 0.0f, 0.0f, 1.0f);
		this.target.mat.transform(this.global);
		this.local.set(0.0f, 0.0f, 0.0f, 1.0f);
		bone.mat.transform(this.local);
		this.local.sub(this.global);
		float f = this.local.length();
		this.inverse.set(bone.mat);
		this.inverse.invert();
		this.local.set(this.global);
		this.inverse.transform(this.local);
		Vector3f vector3f = new Vector3f(this.local.x, this.local.y, this.local.z);
		vector3f.normalize();
		this.local.set(0.0f, 0.0f, 1.0f, 1.0f);
		this.target.mat.transform(this.local);
		Vector3f vector3f2 = new Vector3f(0.0f, 1.0f, 0.0f);
		vector3f2.normalize();
		vector3f2.cross(vector3f, vector3f2);
		vector3f2.normalize();
		Vector3f vector3f3 = new Vector3f(0.0f, 0.0f, 0.0f);
		vector3f3.cross(vector3f2, vector3f);
		vector3f3.normalize();
		this.inverse.setIdentity();
		this.inverse.m00 = vector3f2.x;
		this.inverse.m10 = vector3f2.y;
		this.inverse.m20 = vector3f2.z;
		this.inverse.m01 = vector3f.x;
		this.inverse.m11 = vector3f.y;
		this.inverse.m21 = vector3f.z;
		this.inverse.m02 = vector3f3.x;
		this.inverse.m12 = vector3f3.y;
		this.inverse.m22 = vector3f3.z;
		if (this.stick) {
			this.local.set(0.0f, f - bone.length, 0.0f, 1.0f);
			this.inverse.transform(this.local);
			this.inverse.m03 = this.local.x;
			this.inverse.m13 = this.local.y;
			this.inverse.m23 = this.local.z;
		}
		Matrix4f matrix4f = new Matrix4f();
		bone.mat.set(bone.relBoneMat);
		bone.applyTransformations();
		bone.mat.mul(this.inverse);
		if (bone.parentBone != null) {
			matrix4f = new Matrix4f(bone.parentBone.mat);
		}
		matrix4f.mul(bone.mat);
		bone.mat.set(matrix4f);
	}
}
