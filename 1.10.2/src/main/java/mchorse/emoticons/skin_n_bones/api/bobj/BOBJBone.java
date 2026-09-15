package mchorse.emoticons.skin_n_bones.api.bobj;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class BOBJBone {
	public int index;
	public String name;
	public String parent;
	public BOBJBone parentBone;
	public List<BOBJBoneModifier> modifiers;
	public Vector3f head;
	public Vector3f tail;
	public float length;
	public float x;
	public float y;
	public float z;
	public float rotateX;
	public float rotateY;
	public float rotateZ;
	public float scaleX;
	public float scaleY;
	public float scaleZ;
	public Matrix4f mat;
	public Matrix4f boneMat;
	public Matrix4f invBoneMat;
	public Matrix4f relBoneMat;
	public Matrix4f tempMat;

	public BOBJBone(final int index, final String name, final String parent, final Vector3f tail, final Matrix4f matrix4f) {
		this.scaleX = 1.0f;
		this.scaleY = 1.0f;
		this.scaleZ = 1.0f;
		this.mat = new Matrix4f();
		this.invBoneMat = new Matrix4f();
		this.relBoneMat = new Matrix4f();
		this.tempMat = new Matrix4f();
		this.index = index;
		this.name = name;
		this.parent = parent;
		this.boneMat = matrix4f;
		this.head = new Vector3f(matrix4f.m03, matrix4f.m13, matrix4f.m23);
		this.tail = tail;
		final Vector3f vector3f = new Vector3f(this.tail);
		vector3f.sub(this.head);
		this.length = vector3f.length();
		this.invBoneMat.set(matrix4f);
		this.invBoneMat.invert();
		this.relBoneMat.setIdentity();
	}

	public void addModifier(final BOBJBoneModifier modifier) {
		if (this.modifiers == null) {
			this.modifiers = new ArrayList<>();
		}
		this.modifiers.add(modifier);
	}

	public Matrix4f compute() {
		final Matrix4f matrix = this.computeMatrix(new Matrix4f());
		this.mat.set(matrix);
		this.applyModifiers();
		matrix.set(this.mat);
		matrix.mul(this.invBoneMat);

		return matrix;
	}

	public boolean hasModifiers() {
		return this.modifiers != null;
	}

	public void applyModifiers() {
		if (this.hasModifiers()) {
			for (BOBJBoneModifier modifier : this.modifiers) {
				modifier.apply(this);
			}
		}
	}

	public Matrix4f computeMatrix(Matrix4f matrix4f) {
		matrix4f.setIdentity();
		this.mat.set(this.relBoneMat);
		this.applyTransformations();
		if (this.parentBone != null) {
			matrix4f = new Matrix4f(this.parentBone.mat);
		}
		matrix4f.mul(this.mat);
		return matrix4f;
	}

	public void applyTransformations() {
		this.tempMat.setIdentity();
		this.tempMat.m03 = this.x;
		this.tempMat.m13 = this.y;
		this.tempMat.m23 = this.z;
		this.mat.mul(this.tempMat);
		this.tempMat.setIdentity();
		this.tempMat.m00 = this.scaleX;
		this.tempMat.m11 = this.scaleY;
		this.tempMat.m22 = this.scaleZ;
		this.mat.mul(this.tempMat);
		if (!this.hasModifiers()) {
			if (this.rotateZ != 0.0f) {
				this.tempMat.rotZ(this.rotateZ);
				this.mat.mul(this.tempMat);
			}
			if (this.rotateY != 0.0f) {
				this.tempMat.rotY(this.rotateY);
				this.mat.mul(this.tempMat);
			}
			if (this.rotateX != 0.0f) {
				this.tempMat.rotX(this.rotateX);
				this.mat.mul(this.tempMat);
			}
		}
	}

	public void reset() {
		this.z = 0.0f;
		this.y = 0.0f;
		this.x = 0.0f;
		this.rotateZ = 0.0f;
		this.rotateY = 0.0f;
		this.rotateX = 0.0f;
		this.scaleZ = 1.0f;
		this.scaleY = 1.0f;
		this.scaleX = 1.0f;
	}
}


