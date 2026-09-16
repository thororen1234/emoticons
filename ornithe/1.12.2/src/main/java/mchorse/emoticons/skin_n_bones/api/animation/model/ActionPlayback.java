package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJAction;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJGroup;

public class ActionPlayback {
	public BOBJAction action;
	public ActionConfig config;
	private int fade;
	private float ticks;
	private int duration;
	private float speed;
	private boolean looping;
	private boolean fading;
	public boolean playing;
	public int priority;

	public ActionPlayback(final BOBJAction action, final ActionConfig config) {
		this(action, config, true);
	}

	public ActionPlayback(final BOBJAction action, final ActionConfig config, final boolean looping) {
		this.speed = 1.0f;
		this.looping = false;
		this.fading = false;
		this.playing = true;
		this.action = action;
		this.config = config;
		this.duration = action.getDuration();
		this.looping = looping;
		this.setSpeed(1.0f);
	}

	public ActionPlayback(final BOBJAction action, final ActionConfig config, final boolean looping, final int priority) {
		this(action, config, looping);
		this.priority = priority;
	}

	public void seek(int ticks) {
		this.ticks = this.looping ? Math.max(0, ticks) % Math.max(1, duration) : Math.max(0, ticks);
	}

	public void reset() {
		if (this.config.reset) {
			this.ticks = ((Math.copySign(1.0f, this.speed) < 0.0f) ? ((float) this.duration) : 0.0f);
		}
		this.unfade();
	}

	public boolean finishedFading() {
		return this.fading && this.fade <= 0;
	}

	public boolean isFading() {
		return this.fading && this.fade > 0;
	}

	public void fade() {
		this.fade = (int) this.config.fade;
		this.fading = true;
	}

	public void unfade() {
		this.fade = 0;
		this.fading = false;
	}

	public float getFadeFactor(final float n) {
		return (this.fade - n) / this.config.fade;
	}

	public void setSpeed(final float n) {
		this.speed = n * this.config.speed;
	}

	public void update() {
		if (this.fading && this.fade > 0) {
			--this.fade;
			return;
		}
		if (!this.playing) {
			return;
		}
		this.ticks += this.speed;
		if (!this.looping && !this.fading && this.ticks >= this.duration) {
			this.fade();
		}
		if (this.looping) {
			if (this.ticks >= this.duration && this.speed > 0.0f && this.config.clamp) {
				this.ticks -= this.duration;
				this.ticks += this.config.tick;
			} else if (this.ticks < 0.0f && this.speed < 0.0f && this.config.clamp) {
				this.ticks += this.duration;
				this.ticks -= this.config.tick;
			}
		}
	}

	public float getTick(final float n) {
		float n2 = this.ticks + n * this.speed;
		if (this.looping) {
			if (n2 >= this.duration && this.speed > 0.0f && this.config.clamp) {
				n2 -= this.duration;
			} else if (this.ticks < 0.0f && this.speed < 0.0f && this.config.clamp) {
				n2 += this.duration;
			}
		}
		return n2;
	}

	public void apply(final BOBJArmature armature, final float n) {
		for (final BOBJGroup BOBJGroup : this.action.groups.values()) {
			final BOBJBone BOBJBone = armature.bones.get(BOBJGroup.name);
			if (BOBJBone != null) {
				BOBJGroup.apply(BOBJBone, this.getTick(n));
			}
		}
	}

	public void applyInactive(final BOBJArmature armature, final float n, final float n2) {
		for (final BOBJGroup BOBJGroup : this.action.groups.values()) {
			final BOBJBone BOBJBone = armature.bones.get(BOBJGroup.name);
			if (BOBJBone != null) {
				BOBJGroup.applyInterpolate(BOBJBone, this.ticks, n2);
			}
		}
	}
}

