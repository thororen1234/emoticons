package mchorse.emoticons.skin_n_bones.api.animation.model;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class AnimatorActionsConfig {
	public Map<Object, ActionConfig> actions;

	public AnimatorActionsConfig() {
		this.actions = new HashMap<>();
	}

	public void copy(final AnimatorActionsConfig config) {
		this.actions.clear();
		this.actions.putAll(config.actions);
	}

	public void fromNBT(final CompoundNBT CompoundNBT) {
		this.actions.clear();
		for (final String s : CompoundNBT.keySet()) {
			final INBT base = CompoundNBT.get(s);
			final String key = this.toKey(s);
			final ActionConfig config = new ActionConfig(key);
			config.fromNBT(base);
			this.actions.put(key, config);
		}
	}

	public CompoundNBT toNBT(CompoundNBT CompoundNBT) {
		if (this.actions.isEmpty()) {
			return null;
		}
		if (CompoundNBT == null) {
			CompoundNBT = new CompoundNBT();
		}
		for (final Map.Entry<Object, ActionConfig> entry : this.actions.entrySet()) {
			final ActionConfig config = entry.getValue();
			final String s = (String) entry.getKey();
			if (!s.equals(config.name) || !config.isDefault()) {
				CompoundNBT.put(s, config.toNBT());
			}
		}
		return CompoundNBT;
	}

	public ActionConfig getConfig(final String s) {
		final ActionConfig config = this.actions.get(s);
		return (config == null) ? new ActionConfig(s) : config;
	}

	public String toKey(final String s) {
		return s.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
	}
}

