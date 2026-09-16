package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.*;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.mclib.utils.resources.RLUtils;

import java.lang.reflect.Type;

public class AnimationMeshConfigAdapter
        implements JsonDeserializer<AnimationMeshConfig>, JsonSerializer<AnimationMeshConfig> {
    public AnimationMeshConfig deserialize(JsonElement jsonElement, Type type,
                                           JsonDeserializationContext jsonDeserializationContext) {
        if (!jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        AnimationMeshConfig config = new AnimationMeshConfig();
        if (jsonObject.has("texture")) {
            config.texture = RLUtils.create(jsonObject.get("texture"));
        }
        if (jsonObject.has("filtering")) {
            config.filtering = jsonObject.get("filtering").getAsString().equalsIgnoreCase("linear") ? 9729 : 9728;
        }
        if (jsonObject.has("normals")) {
            config.normals = jsonObject.get("normals").getAsBoolean();
        }
        if (jsonObject.has("smooth")) {
            config.smooth = jsonObject.get("smooth").getAsBoolean();
        }
        if (jsonObject.has("visible")) {
            config.visible = jsonObject.get("visible").getAsBoolean();
        }
        if (jsonObject.has("lighting")) {
            config.lighting = jsonObject.get("lighting").getAsBoolean();
        }
        if (jsonObject.has("color")) {
            config.color = jsonObject.get("color").getAsInt();
        }
        return config;
    }

    public JsonElement serialize(AnimationMeshConfig config, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        if (config.texture != null) {
            jsonObject.add("texture", RLUtils.writeJson(config.texture));
        }
        jsonObject.addProperty("filtering", config.filtering == 9729 ? "linear" : "nearest");
        jsonObject.addProperty("normals", Boolean.valueOf(config.normals));
        jsonObject.addProperty("smooth", Boolean.valueOf(config.smooth));
        jsonObject.addProperty("visible", Boolean.valueOf(config.visible));
        jsonObject.addProperty("lighting", Boolean.valueOf(config.lighting));
        jsonObject.addProperty("color", (Number) config.color);
        return jsonObject;
    }

}
