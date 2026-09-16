package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;

import java.lang.reflect.Type;

public class ActionConfigAdapter
        implements JsonDeserializer<ActionConfig> {
    public ActionConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        ActionConfig config = new ActionConfig();
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("name")) {
                config.name = jsonObject.get("name").getAsString();
            }
            if (jsonObject.has("clamp")) {
                config.clamp = jsonObject.get("clamp").getAsBoolean();
            }
            if (jsonObject.has("reset")) {
                config.reset = jsonObject.get("reset").getAsBoolean();
            }
            if (jsonObject.has("speed")) {
                config.speed = jsonObject.get("speed").getAsFloat();
            }
            if (jsonObject.has("fade")) {
                config.fade = jsonObject.get("fade").getAsInt();
            }
            if (jsonObject.has("tick")) {
                config.tick = jsonObject.get("tick").getAsInt();
            }
        } else if (jsonElement.isJsonPrimitive()) {
            config.name = jsonElement.getAsString();
        }
        return config;
    }

}

