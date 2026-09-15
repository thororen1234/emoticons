package mchorse.emoticons.skin_n_bones.api.bobj;

import org.apache.commons.lang3.ArrayUtils;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BOBJLoader {
	public static void merge(final BOBJData to, final BOBJData from) {
		final int size = to.vertices.size();
		final int size2 = to.normal.size();
		final int size3 = to.textures.size();
		to.vertices.addAll(from.vertices);
		to.normal.addAll(from.normal);
		to.textures.addAll(from.textures);
		final Iterator iterator = from.meshes.iterator();
		while (iterator.hasNext()) {
			final BOBJMesh newMesh = ((BOBJMesh) iterator.next()).add(size, size2, size3);
			newMesh.armature = (BOBJArmature) to.armatures.get(newMesh.armatureName);
			to.meshes.add(newMesh);
		}
	}

	public static List<String> readAllLines(final InputStream in) {
		final ArrayList<String> list = new ArrayList<>();
		try {
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
			bufferedReader.close();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public static BOBJData readData(final InputStream inputStream) {
		final List<String> lines = readAllLines(inputStream);
		List<Vertex> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<BOBJMesh> objects = new ArrayList<>();

		Map<String, BOBJAction> actions = new HashMap<>();
		Map<String, BOBJArmature> armatures = new HashMap<>();

		BOBJMesh mesh = null;
		BOBJAction action = null;
		BOBJGroup group = null;
		BOBJChannel channel = null;
		BOBJArmature armature = null;
		BOBJBone bone = null;
		Vertex vertex = null;
		int n = 0;
		final Iterator iterator = lines.iterator();
		while (iterator.hasNext()) {
			final String[] split = ((String) iterator.next()).split("\\s");
			final String s = split[0];
			if (s.equals("o")) {
				objects.add(mesh = new BOBJMesh(split[1]));
				armature = null;
				vertex = null;
			} else if (s.equals("o_arm")) {
				mesh.armatureName = split[1];
			} else if (s.equals("v")) {
				if (vertex != null) {
					vertex.eliminateTinyWeights();
				}
				vertices.add(vertex = new Vertex(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
			} else if (s.equals("vw")) {
				final float float1 = Float.parseFloat(split[2]);
				if (float1 == 0.0f) {
					continue;
				}
				vertex.weights.add(new Weight(split[1], float1));
			} else if (s.equals("vt")) {
				textures.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
			} else if (s.equals("vn")) {
				normals.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
			} else if (s.equals("f")) {
				mesh.faces.add(new Face(split[1], split[2], split[3]));
			} else if (s.equals("arm_name")) {
				n = 0;
				bone = null;
				armature = new BOBJArmature(split[1]);
				armatures.put(armature.name, armature);
			} else if (s.equals("arm_action")) {
				armature.action = split[1];
			} else if (s.equals("arm_bone")) {
				final Vector3f vector3f = new Vector3f(Float.parseFloat(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
				final Matrix4f matrix4f = new Matrix4f();
				final float[] m = new float[16];
				for (int i = 6; i < 22; ++i) {
					m[i - 6] = Float.parseFloat(split[i]);
				}
				matrix4f.set(m);
				bone = new BOBJBone(n++, split[1], split[2], vector3f, matrix4f);
				armature.addBone(bone);
			} else if (s.equals("arm_ik") && split.length >= 2) {
				final BOBJBone tail = armature.bones.get(split[1]);
				if (bone == null) {
					System.out.println("Found IK modifier in BOBJ, but bone " + split[1] + " doesn't exist...");
				} else {
					bone.addModifier(new BOBJBoneModifier(tail, (split.length >= 3) ? Integer.parseInt(split[2]) : 1, split.length >= 4 && split[3].equals("true")));
				}
			} else if (s.equals("an")) {
				actions.put(split[1], action = new BOBJAction(split[1]));
			} else if (s.equals("ao")) {
				action.groups.put(split[1], group = new BOBJGroup(split[1]));
			} else if (s.equals("ag")) {
				group.channels.add(channel = new BOBJChannel(split[1], Integer.parseInt(split[2])));
			} else {
				if (!s.equals("kf")) {
					continue;
				}
				channel.keyframes.add(BOBJKeyframe.parse(split));
			}
		}
		if (vertex != null) {
			vertex.eliminateTinyWeights();
		}
		return new BOBJData(vertices, textures, normals, objects, actions, armatures);
	}

	public static Map<String, CompiledData> loadMeshes(final BOBJData data) {
		final Map<String, CompiledData> hashMap = new HashMap<>();
		for (final BOBJMesh BOBJMesh : data.meshes) {
			final List<Integer> indices = new ArrayList();
			final List<Face> faces = BOBJMesh.faces;
			final int[] a = new int[faces.size() * 3 * 4];
			final float[] a2 = new float[faces.size() * 3 * 4];
			final float[] array = new float[faces.size() * 3 * 4];
			final float[] array2 = new float[faces.size() * 3 * 2];
			final float[] array3 = new float[faces.size() * 3 * 3];
			Arrays.fill(a, -1);
			Arrays.fill(a2, -1.0f);
			int n = 0;
			for (Face face : faces) {
				final IndexGroup[] indValue = face.idxGroups;
				for (int length = indValue.length, i = 0; i < length; ++i) {
					processFaceVertex(n, indValue[i], BOBJMesh, data, indices, array, array2, array3, a2, a);
					++n;
				}
			}
			hashMap.put(BOBJMesh.name, new CompiledData(array, array2, array3, a2, a, ArrayUtils.toPrimitive((Integer[]) indices.toArray(new Integer[0])), BOBJMesh));
		}
		return hashMap;
	}

	public static CompiledData loadMesh(final BOBJData data) {
		final List<Integer> indices = new ArrayList();
		final List<Face> faces = new ArrayList();
		for (BOBJMesh bobjMesh : data.meshes) {
			faces.addAll(bobjMesh.faces);
		}
		final float[] array = new float[faces.size() * 3 * 4];
		final float[] array2 = new float[faces.size() * 3 * 2];
		final float[] array3 = new float[faces.size() * 3 * 3];
		int n = 0;
		for (Face face : faces) {
			final IndexGroup[] indValue = face.idxGroups;
			for (int length = indValue.length, i = 0; i < length; ++i) {
				processFaceVertex(n, indValue[i], null, data, indices, array, array2, array3, null, null);
				++n;
			}
		}
		return new CompiledData(array, array2, array3, null, null, ArrayUtils.toPrimitive((Integer[]) indices.toArray(new Integer[0])), null);
	}

	private static void processFaceVertex(final int index, final IndexGroup indices, final BOBJMesh mesh, final BOBJData data,
										  final List<Integer> indicesList,
										  final float[] posArr, final float[] texCoordArr, final float[] normArr,
										  final float[] weightsArr, final int[] boneIndicesArr) {
		indicesList.add(index);
		if (indices.idxPos >= 0) {
			final Vertex vec = data.vertices.get(indices.idxPos);
			posArr[index * 4] = vec.x;
			posArr[index * 4 + 1] = vec.y;
			posArr[index * 4 + 2] = vec.z;
			posArr[index * 4 + 3] = 1.0f;
			if (mesh != null) {
				for (int j = 0; j < Math.min(vec.weights.size(), 4); ++j) {
					final Weight weight = vec.weights.get(j);
					final BOBJBone bone = mesh.armature.bones.get(weight.factor);
					weightsArr[index * 4 + j] = ((bone == null) ? 0.0f : weight.index);
					boneIndicesArr[index * 4 + j] = ((bone == null) ? -1 : bone.index);
				}
			}
		}
		if (indices.idxTextCoord >= 0) {
			final Vector2f vector2f = data.textures.get(indices.idxTextCoord);
			texCoordArr[index * 2] = vector2f.x;
			texCoordArr[index * 2 + 1] = 1.0f - vector2f.y;
		}
		if (indices.idxVecNormal >= 0) {
			final Vector3f vector3f = data.normal.get(indices.idxVecNormal);
			normArr[index * 3] = vector3f.x;
			normArr[index * 3 + 1] = vector3f.y;
			normArr[index * 3 + 2] = vector3f.z;
		}
	}
}

