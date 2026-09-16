package mchorse.emoticons.skin_n_bones.api.animation.model;

public class AnimatorConfigEntry {
	public AnimatorConfig config;
	public long lastModified;

	public AnimatorConfigEntry(AnimatorConfig config, long l) {
		this.config = config;
		this.lastModified = l;
	}
}
