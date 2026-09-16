package mchorse.emoticons;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import mchorse.emoticons.client.EntityModelHandler;
import mchorse.emoticons.client.EmoteKeyboardHandler;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationEntry;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfigEntry;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJData;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;
import mchorse.mclib.client.render.RenderLightmap;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy {
    public static File configFolder;
    public static mchorse.emoticons.client.EmoteKeys keys;

    public void init() {
        configFolder = new File(Minecraft.getMinecraft().mcDataDir, "config/emoticons");
        configFolder.mkdirs();

        File keysFile = new File(configFolder, "keys.json");
        keys = mchorse.emoticons.client.EmoteKeys.fromFile(keysFile);

        if (keys == null) {
            keys = new mchorse.emoticons.client.EmoteKeys();
        }

        EntityModelHandler modelHandler = new EntityModelHandler();
        MinecraftForge.EVENT_BUS.register(modelHandler);
        FMLCommonHandler.instance().bus().register(modelHandler);
        FMLCommonHandler.instance().bus().register(new EmoteKeyboardHandler());
        RenderLightmap.create();
        Emotes.register();

        try {
            Class<? extends ClientProxy> proxyClass = this.getClass();
            AnimationManager animationManager = AnimationManager.INSTANCE;
            BOBJData props = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/props.bobj"));
            BOBJData propsSimple = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/props_simple.bobj"));
            BOBJData defaultData = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/default.bobj"));
            BOBJData slimData = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/slim.bobj"));
            BOBJData defaultSimpleData = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/default_simple.bobj"));
            BOBJData slimSimpleData = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/slim_simple.bobj"));
            BOBJData actions = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/actions.bobj"));
            BOBJData extra = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/extra.bobj"));
            BOBJData ragdoll = BOBJLoader.readData(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/ragdoll.bobj"));

            defaultData.actions.putAll(actions.actions); defaultData.actions.putAll(extra.actions); defaultData.actions.putAll(ragdoll.actions);
            slimData.actions.putAll(actions.actions); slimData.actions.putAll(extra.actions); slimData.actions.putAll(ragdoll.actions);
            defaultSimpleData.actions.putAll(actions.actions); defaultSimpleData.actions.putAll(extra.actions); defaultSimpleData.actions.putAll(ragdoll.actions);
            slimSimpleData.actions.putAll(actions.actions); slimSimpleData.actions.putAll(extra.actions); slimSimpleData.actions.putAll(ragdoll.actions);
            BOBJLoader.merge(props, ragdoll); BOBJLoader.merge(propsSimple, ragdoll);
            BOBJLoader.merge(defaultData, props); BOBJLoader.merge(slimData, props);
            BOBJLoader.merge(defaultSimpleData, propsSimple); BOBJLoader.merge(slimSimpleData, propsSimple);
            ragdoll.armatures.get("ArmatureRagdoll").copyOrder(defaultData.armatures.get("Armature"));
            ragdoll.initiateArmatures();

            Animation defaultAnimation = new Animation("default", defaultData); defaultAnimation.init();
            Animation slimAnimation = new Animation("slim", slimData); slimAnimation.init();
            Animation defaultSimpleAnimation = new Animation("default_simple", defaultSimpleData); defaultSimpleAnimation.init();
            Animation slimSimpleAnimation = new Animation("slim_simple", slimSimpleData); slimSimpleAnimation.init();
            animationManager.animations.put("default", new AnimationEntry(defaultAnimation, configFolder, 1L));
            animationManager.animations.put("slim", new AnimationEntry(slimAnimation, configFolder, 1L));
            animationManager.animations.put("default_simple", new AnimationEntry(defaultSimpleAnimation, configFolder, 1L));
            animationManager.animations.put("slim_simple", new AnimationEntry(slimSimpleAnimation, configFolder, 1L));

            animationManager.configs.put("default", config(proxyClass, "default", animationManager));
            animationManager.configs.put("slim", config(proxyClass, "slim", animationManager));
            animationManager.configs.put("default_simple", config(proxyClass, "default_simple", animationManager));
            animationManager.configs.put("slim_simple", config(proxyClass, "slim_simple", animationManager));
        } catch (Exception exception) {
            throw new RuntimeException("Couldn't load Emoticons assets", exception);
        }
    }

    private AnimatorConfigEntry config(Class<? extends ClientProxy> proxyClass, String name, AnimationManager manager) throws Exception {
        AnimatorConfig config = manager.gson.fromJson(IOUtils.toString(proxyClass.getResourceAsStream("/assets/emoticons/models/entity/" + name + ".json"), Charset.defaultCharset()), AnimatorConfig.class);
        return new AnimatorConfigEntry(config, 1L);
    }
}

