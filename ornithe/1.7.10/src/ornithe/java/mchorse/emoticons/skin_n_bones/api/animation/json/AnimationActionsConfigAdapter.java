package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorActionsConfig;

import java.lang.reflect.Type;
import java.util.Map;

public class AnimationActionsConfigAdapter
		implements JsonDeserializer<AnimatorActionsConfig> {
	public AnimatorActionsConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
		if (!jsonElement.isJsonObject()) {
			return null;
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		AnimatorActionsConfig config = new AnimatorActionsConfig();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			final JsonElement element = entry.getValue();
			final String string = config.toKey(entry.getKey());
			if (element.isJsonObject()) {
				((JsonObject) element).addProperty("name", string);
			}
			config.actions.put(string, jsonDeserializationContext.deserialize(element, ActionConfig.class));
		}
		return config;
	}

}

