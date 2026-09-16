package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.emoticons.skin_n_bones.api.bobj.CompiledData;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.resource.Identifier;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AnimationMesh {
	public Animation owner;
	public Identifier texture;
	public String name;
	public CompiledData data;
	public BOBJArmature armature;
	public BOBJArmature currentArmature;
	public FloatBuffer vertices;
	public FloatBuffer normals;
	public FloatBuffer textcoords;
	public IntBuffer indices;
	public int vertexBuffer;
	public int normalBuffer;
	public int texCoordBuffer;
	public int indexBuffer;

	public AnimationMesh(final Animation animation, final String name, final CompiledData compiledData) {
		this.owner = animation;
		this.name = name;
		this.data = compiledData;
		(this.armature = this.data.mesh.armature).initArmature();
		this.initBuffers();
	}

	private void initBuffers() {
		this.vertices = BufferUtils.createFloatBuffer(this.data.posData.length);
		this.vertices.put(this.data.posData).flip();
		this.normals = BufferUtils.createFloatBuffer(this.data.normData.length);
		this.normals.put(this.data.normData).flip();
		this.textcoords = BufferUtils.createFloatBuffer(this.data.texData.length);
		this.textcoords.put(this.data.texData).flip();
		this.indices = BufferUtils.createIntBuffer(this.data.indexData.length);
		this.indices.put(this.data.indexData).flip();
		GL15.glBindBuffer(34962, this.vertexBuffer = GL15.glGenBuffers());
		GL15.glBufferData(34962, this.vertices, 35048);
		GL15.glBindBuffer(34962, this.normalBuffer = GL15.glGenBuffers());
		GL15.glBufferData(34962, this.normals, 35044);
		GL15.glBindBuffer(34962, this.texCoordBuffer = GL15.glGenBuffers());
		GL15.glBufferData(34962, this.textcoords, 35044);
		GL15.glBindBuffer(34963, this.indexBuffer = GL15.glGenBuffers());
		GL15.glBufferData(34963, this.indices, 35044);
		GL15.glBindBuffer(34962, 0);
		GL15.glBindBuffer(34963, 0);
	}

	public void setFiltering(final int n) {
		GL11.glTexParameteri(3553, 10241, n);
		GL11.glTexParameteri(3553, 10240, n);
	}

	public int getFiltering() {
		return GL11.glGetTexParameteri(3553, 10241);
	}

	public void delete() {
		GL15.glDeleteBuffers(this.vertexBuffer);
		GL15.glDeleteBuffers(this.normalBuffer);
		GL15.glDeleteBuffers(this.texCoordBuffer);
		GL15.glDeleteBuffers(this.indexBuffer);
		this.vertices = null;
		this.normals = null;
		this.textcoords = null;
		this.indices = null;
	}

	public void updateMesh() {
		final Vector4f vector4f = new Vector4f();
		final Vector4f vector4f2 = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
		final Vector3f vector3f = new Vector3f();
		final Vector3f vector3f2 = new Vector3f(0.0f, 0.0f, 0.0f);
		final float[] posData = this.data.posData;
		final float[] array = new float[posData.length];
		final float[] normData = this.data.normData;
		final float[] array2 = new float[normData.length];
		final Matrix4f[] matrices = this.armature.matrices;
		if (currentArmature != null && currentArmature != armature) {
			for (BOBJBone original : armature.orderedBones) {
				BOBJBone replacement = currentArmature.bones.get(original.name);
				if (replacement != null) matrices[original.index] = currentArmature.matrices[replacement.index];
			}
		}
		for (int i = 0; i < array.length / 4; ++i) {
			int n = 0;
			for (int j = 0; j < 4; ++j) {
				final float n2 = this.data.weightData[i * 4 + j];
				if (n2 > 0.0f) {
					final int n3 = this.data.boneIndexData[i * 4 + j];
					vector4f.set(posData[i * 4], posData[i * 4 + 1], posData[i * 4 + 2], 1.0f);
					matrices[n3].transform(vector4f);
					vector4f.scale(n2);
					vector4f2.add(vector4f);
					vector3f.set(normData[i * 3], normData[i * 3 + 1], normData[i * 3 + 2]);
					matrices[n3].transform(vector3f);
					vector3f.scale(n2);
					vector3f2.add(vector3f);
					++n;
				}
			}
			if (n == 0) {
				vector3f2.set(normData[i * 3], normData[i * 3 + 1], normData[i * 3 + 2]);
				vector4f2.set(posData[i * 4], posData[i * 4 + 1], posData[i * 4 + 2], 1.0f);
			}
			array[i * 4] = vector4f2.x;
			array[i * 4 + 1] = vector4f2.y;
			array[i * 4 + 2] = vector4f2.z;
			array[i * 4 + 3] = vector4f2.w;
			array2[i * 3] = vector3f2.x;
			array2[i * 3 + 1] = vector3f2.y;
			array2[i * 3 + 2] = vector3f2.z;
			vector4f2.set(0.0f, 0.0f, 0.0f, 0.0f);
			vector3f2.set(0.0f, 0.0f, 0.0f);
		}
		this.updateVertices(array);
		this.updateNormals(array2);
	}

	public void updateVertices(final float[] src) {
		this.vertices.clear();
		this.vertices.put(src).flip();
		GL15.glBindBuffer(34962, this.vertexBuffer);
		GL15.glBufferData(34962, this.vertices, 35048);
	}

	public void updateNormals(final float[] src) {
		this.normals.clear();
		this.normals.put(src).flip();
		GL15.glBindBuffer(34962, this.normalBuffer);
		GL15.glBufferData(34962, this.normals, 35048);
	}

	public void render(final Minecraft minecraft, final AnimationMeshConfig AnimationMeshConfig) {
		if (AnimationMeshConfig != null && !AnimationMeshConfig.visible) {
			return;
		}
		final Identifier texture = this.getTexture(AnimationMeshConfig);
		final boolean b = AnimationMeshConfig != null && AnimationMeshConfig.smooth;
		final boolean b2 = AnimationMeshConfig != null && AnimationMeshConfig.normals;
		final boolean b3 = AnimationMeshConfig == null || AnimationMeshConfig.lighting;
		if (texture != null) {
			GlStateManager.enableBoundTexture();
			minecraft.getTextureManager().bind(texture);
			if (AnimationMeshConfig != null) {
				this.setFiltering(AnimationMeshConfig.filtering);
			}
		}
		if (b && b2) {
			GL11.glShadeModel(7425);
		}
		if (!b2) {
			Lighting.turnOff();
		}
		if (!b3) {
			// OpenGlHelper.setLightmapTextureCoords(33985, 240.0f, 240.0f);
		}
		final int n = (AnimationMeshConfig != null) ? AnimationMeshConfig.color : 16777215;
		GlStateManager.color((n >> 16 & 0xFF) / 255.0f,  (n >> 8 & 0xFF) / 255.0f,  (n & 0xFF) / 255.0f, 1.0f);

		GlStateManager.enableRescaleNormal();
		GL15.glBindBuffer(34962, this.vertexBuffer);
		GL11.glVertexPointer(4, 5126, 0, 0L);
		GL15.glBindBuffer(34962, this.normalBuffer);
		GL11.glNormalPointer(5126, 0, 0L);
		GL15.glBindBuffer(34962, this.texCoordBuffer);
		GL11.glTexCoordPointer(2, 5126, 0, 0L);
		GL11.glEnableClientState(32884);
		GL11.glEnableClientState(32885);
		GL11.glEnableClientState(32888);
		GL15.glBindBuffer(34963, this.indexBuffer);

		GlStateManager.disableBlend();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.99F);
		GL11.glDrawElements(4, this.data.indexData.length, 5125, 0L);

		GlStateManager.enableBlend();
		org.lwjgl.opengl.GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.3F);
		GlStateManager.depthMask(false);
		GL11.glDrawElements(4, this.data.indexData.length, 5125, 0L);
		GlStateManager.depthMask(true);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

		GL15.glBindBuffer(34963, 0);
		GL15.glBindBuffer(34962, 0);
		GL11.glDisableClientState(32884);
		GL11.glDisableClientState(32885);
		GL11.glDisableClientState(32888);
		if (b && b2) {
			GL11.glShadeModel(7424);
		}
		if (!b2) {
			Lighting.turnOn();
		}
		if (!b3) {
			// OpenGlHelper.setLightmapTextureCoords(33985, prevLightmapS, prevLightmapT);
		}
		GlStateManager.disableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		if (minecraft.options.debugEnabled && !minecraft.options.reducedDebugInfo) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.disableBoundTexture();
			for (final BOBJBone BOBJBone : this.data.mesh.armature.orderedBones) {
				final Vector4f vec = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
				final Vector4f vec2 = new Vector4f(0.0f, BOBJBone.length, 0.0f, 1.0f);
				final Vector4f vec3 = new Vector4f(0.1f, 0.0f, 0.0f, 1.0f);
				final Vector4f vec4 = new Vector4f(0.0f, 0.1f, 0.0f, 1.0f);
				final Vector4f vec5 = new Vector4f(0.0f, 0.0f, 0.1f, 1.0f);
				final Matrix4f boneMatrix = BOBJBone.mat;
				boneMatrix.transform(vec);
				boneMatrix.transform(vec2);
				boneMatrix.transform(vec3);
				boneMatrix.transform(vec4);
				boneMatrix.transform(vec5);
				GL11.glPointSize(5.0f);
				GL11.glBegin(0);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glVertex3f(vec.x, vec.y, vec.z);
				GL11.glEnd();
				GL11.glLineWidth(1.0f);
				GL11.glBegin(1);
				GlStateManager.color(0.9f,  0.9f,  0.9f, 1.0f);
				GL11.glVertex3f(vec.x, vec.y, vec.z);
				GL11.glVertex3f(vec2.x, vec2.y, vec2.z);
				GL11.glEnd();
				GL11.glLineWidth(2.0f);
				GL11.glBegin(1);
				GlStateManager.color(1.0f,  0.0f,  0.0f, 1.0f);
				GL11.glVertex3f(vec.x, vec.y, vec.z);
				GL11.glVertex3f(vec3.x, vec3.y, vec3.z);
				GL11.glEnd();
				GL11.glBegin(1);
				GlStateManager.color(0.0f,  1.0f,  0.0f, 1.0f);
				GL11.glVertex3f(vec.x, vec.y, vec.z);
				GL11.glVertex3f(vec4.x, vec4.y, vec4.z);
				GL11.glEnd();
				GL11.glBegin(1);
				GlStateManager.color(0.0f, 0.0f, 1.0f, 1.0f);
				GL11.glVertex3f(vec.x, vec.y, vec.z);
				GL11.glVertex3f(vec5.x, vec5.y, vec5.z);
				GL11.glEnd();
			}
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glLineWidth(1.0f);
			GlStateManager.enableDepth();
			GlStateManager.enableLighting();
			GlStateManager.enableBoundTexture();
		}
	}

	private Identifier getTexture(final AnimationMeshConfig AnimationMeshConfig) {
		if (AnimationMeshConfig == null) {
			return this.texture;
		}
		return (AnimationMeshConfig.texture == null) ? this.texture : AnimationMeshConfig.texture;
	}
}
