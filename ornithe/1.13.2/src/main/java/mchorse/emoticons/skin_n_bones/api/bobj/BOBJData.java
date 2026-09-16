package mchorse.emoticons.skin_n_bones.api.bobj;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.List;
import java.util.Map;

public class BOBJData {
	public List<Vertex> vertices;
	public List<Vector2f> textures;
	public List<Vector3f> normal;
	public List<BOBJMesh> meshes;
	public Map<String, BOBJAction> actions;
	public Map<String, BOBJArmature> armatures;

	public BOBJData(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normal,
					List<BOBJMesh> meshes, Map<String, BOBJAction> actions, Map<String, BOBJArmature> armatures) {
		this.vertices = vertices;
		this.textures = textures;
		this.normal = normal;
		this.meshes = meshes;
		this.actions = actions;
		this.armatures = armatures;

		for (BOBJMesh mesh : meshes) {
			mesh.armature = armatures.get(mesh.armatureName);
		}
	}

	public boolean hasMeshes() {
		return !this.meshes.isEmpty();
	}

	public void clear() {
		this.vertices.clear();
		this.textures.clear();
		this.normal.clear();
		this.meshes.clear();
	}
}
