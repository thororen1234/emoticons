package mchorse.emoticons.skin_n_bones.api.animation.json;

import com.google.gson.*;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorActionsConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnimationConfigAdapter
        implements JsonDeserializer<AnimatorConfig> {
    public AnimatorConfig deserialize(JsonElement jsonElement, Type type,
                                      JsonDeserializationContext jsonDeserializationContext) {
        if (!jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        AnimatorConfig config = new AnimatorConfig();
        if (jsonObject.has("name")) {
            config.name = jsonObject.get("name").getAsString();
        }
        if (jsonObject.has("primaryMesh")) {
            config.primaryMesh = jsonObject.get("primaryMesh").getAsString();
        }
        if (jsonObject.has("scale")) {
            config.scale = jsonObject.get("scale").getAsFloat();
        }
        if (jsonObject.has("scaleGui")) {
            config.scaleGui = jsonObject.get("scaleGui").getAsFloat();
        }
        if (jsonObject.has("scaleItems")) {
            config.scaleItems = jsonObject.get("scaleItems").getAsFloat();
        }
        if (jsonObject.has("renderHeldItems")) {
            config.renderHeldItems = jsonObject.get("renderHeldItems").getAsBoolean();
        }
        if (jsonObject.has("leftHands")) {
            this.addHeldConfig(config.leftHands, jsonObject.get("leftHands"), jsonDeserializationContext);
        }
        if (jsonObject.has("rightHands")) {
            this.addHeldConfig(config.rightHands, jsonObject.get("rightHands"), jsonDeserializationContext);
        }
        if (jsonObject.has("head")) {
            config.head = jsonObject.get("head").getAsString();
        }
        if (jsonObject.has("actions")) {
            config.actions = jsonDeserializationContext.deserialize(jsonObject.get("actions"),
                    AnimatorActionsConfig.class);
        }
        if (jsonObject.has("meshes")) {
            HashMap<String, AnimationMeshConfig> hashMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("meshes").entrySet()) {
                hashMap.put(entry.getKey(),
                        jsonDeserializationContext.deserialize(entry.getValue(), AnimationMeshConfig.class));
            }
            config.meshes = hashMap;
        }
        return config;
    }

    private void addHeldConfig(Map<String, AnimatorHeldItemConfig> list, JsonElement element,
                               JsonDeserializationContext context) {
        list.clear();

        if (element.isJsonArray()) {
            for (String bone : toStringArray(element.getAsJsonArray())) {
                list.put(bone, new AnimatorHeldItemConfig(bone));
            }
        } else if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet()) {
                AnimatorHeldItemConfig item = context.deserialize(entry.getValue(), AnimatorHeldItemConfig.class);

                item.boneName = entry.getKey();
                list.put(item.boneName, item);
            }
        }
    }

    public static String[] toStringArray(JsonArray jsonArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        int n = jsonArray.size();
        for (int i = 0; i < n; ++i) {
            JsonElement jsonElement = jsonArray.get(i);
            if (!jsonElement.isJsonPrimitive())
                continue;
            arrayList.add(jsonElement.getAsString());
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }

}
