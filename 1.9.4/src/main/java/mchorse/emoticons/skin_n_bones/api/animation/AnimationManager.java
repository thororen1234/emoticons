package mchorse.emoticons.skin_n_bones.api.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mchorse.emoticons.skin_n_bones.api.animation.json.*;
import mchorse.emoticons.skin_n_bones.api.animation.model.*;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    public Map<String, AnimationEntry> animations = new HashMap<>();
    public Map<String, AnimatorConfigEntry> configs = new HashMap<>();
    public AnimatorConfigEntry defaultConfig = new AnimatorConfigEntry(new AnimatorConfig(), 0L);
    public Gson gson;
    public static final AnimationManager INSTANCE = new AnimationManager();

    private AnimationManager() {
        this.defaultConfig.config.rightHands.put("right_hand", new AnimatorHeldItemConfig("right_hand"));
        this.defaultConfig.config.leftHands.put("left_hand", new AnimatorHeldItemConfig("left_hand"));
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AnimationMeshConfig.class, new AnimationMeshConfigAdapter());
        gsonBuilder.registerTypeAdapter(AnimatorConfig.class, new AnimationConfigAdapter());
        gsonBuilder.registerTypeAdapter(AnimatorActionsConfig.class, new AnimationActionsConfigAdapter());
        gsonBuilder.registerTypeAdapter(AnimatorHeldItemConfig.class, new AnimationHeldItemConfigAdapter());
        gsonBuilder.registerTypeAdapter(ActionConfig.class, new ActionConfigAdapter());
        this.gson = gsonBuilder.create();
    }

    public Animation getAnimation(String string) {
        AnimationEntry entry = this.animations.get(string);
        return entry == null ? null : entry.animation;
    }

    public AnimatorConfigEntry getConfig(String string) {
        AnimatorConfigEntry entry = this.configs.get(string);
        return entry == null ? this.defaultConfig : entry;
    }
}
