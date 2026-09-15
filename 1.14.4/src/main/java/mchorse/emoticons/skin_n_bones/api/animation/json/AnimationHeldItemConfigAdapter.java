package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;

import java.lang.reflect.Type;

public class AnimationHeldItemConfigAdapter
		implements JsonDeserializer<AnimatorHeldItemConfig> {
	public AnimatorHeldItemConfig deserialize(JsonElement jsonElement, Type type,
											  JsonDeserializationContext jsonDeserializationContext) {
		if (!jsonElement.isJsonObject()) {
			return null;
		}
		JsonObject jsonObject = (JsonObject) jsonElement;
		AnimatorHeldItemConfig config = new AnimatorHeldItemConfig("");
		if (jsonObject.has("x")) {
			config.x = jsonObject.get("x").getAsFloat();
		}
		if (jsonObject.has("y")) {
			config.y = jsonObject.get("y").getAsFloat();
		}
		if (jsonObject.has("z")) {
			config.z = jsonObject.get("z").getAsFloat();
		}
		if (jsonObject.has("sx")) {
			config.scaleX = jsonObject.get("sx").getAsFloat();
		}
		if (jsonObject.has("sy")) {
			config.scaleY = jsonObject.get("sy").getAsFloat();
		}
		if (jsonObject.has("sz")) {
			config.scaleZ = jsonObject.get("sz").getAsFloat();
		}
		if (jsonObject.has("rx")) {
			config.rotateX = jsonObject.get("rx").getAsFloat();
		}
		if (jsonObject.has("ry")) {
			config.rotateY = jsonObject.get("ry").getAsFloat();
		}
		if (jsonObject.has("rz")) {
			config.rotateZ = jsonObject.get("rz").getAsFloat();
		}
		return config;
	}

}
