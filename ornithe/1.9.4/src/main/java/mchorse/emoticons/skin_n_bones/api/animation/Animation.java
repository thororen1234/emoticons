package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJAction;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJData;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;
import mchorse.emoticons.skin_n_bones.api.bobj.CompiledData;
import net.minecraft.client.Minecraft;
import net.minecraft.resource.Identifier;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Animation {
	public String name;
	public BOBJData data;
	public List<AnimationMesh> meshes;
	public Minecraft mc;

	public Animation(final String name, final BOBJData data) {
		this.name = name;
		this.data = data;
		this.mc = Minecraft.getInstance();
		this.meshes = new ArrayList<>();
	}

	public void reload(final BOBJData data) {
		this.data = data;
		this.delete();
		this.init();
	}

	public ActionPlayback createAction(final ActionPlayback playback, final ActionConfig config, final boolean b) {
		return this.createAction(playback, config, b, 1);
	}

	public ActionPlayback createAction(final ActionPlayback playback, final ActionConfig config, final boolean b,
									   final int n) {
		final BOBJAction action = this.data.actions.get(config.name);
		if (action == null) {
			return null;
		}
		if (playback != null && playback.action == action) {
			playback.config = config;
			playback.setSpeed(1.0f);
			return playback;
		}
		return new ActionPlayback(action, config, b, n);
	}

	public void init() {
		for (final Map.Entry<String, CompiledData> entry : BOBJLoader.loadMeshes(this.data).entrySet()) {
			final AnimationMesh AnimationMesh = new AnimationMesh(this, entry.getKey(),
					(CompiledData) entry.getValue());

			AnimationMesh.texture = new Identifier("minecraft", "textures/entity/steve.png");
			this.meshes.add(AnimationMesh);
		}
	}

	public void delete() {
		for (AnimationMesh mesh : this.meshes) {
			mesh.delete();
		}
		this.meshes.clear();
	}

	public void render(final Map<?, ?> map) {
		for (final AnimationMesh AnimationMesh : this.meshes) {
			AnimationMesh.render(this.mc, (map == null) ? null : ((AnimationMeshConfig) map.get(AnimationMesh.name)));
		}
		GL15.glBindBuffer(34962, 0);
		GL15.glBindBuffer(34963, 0);
	}
}
