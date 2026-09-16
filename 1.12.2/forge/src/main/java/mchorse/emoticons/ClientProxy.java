package mchorse.emoticons;

import mchorse.emoticons.api.animation.model.AnimationSimple;
import mchorse.emoticons.capabilities.cosmetic.CosmeticMode;
import mchorse.emoticons.client.EmoteKeys;
import mchorse.emoticons.client.EntityModelHandler;
import mchorse.emoticons.client.KeyboardHandler;
import mchorse.emoticons.client.NetworkHandler;
import mchorse.emoticons.commands.CommandEmote;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager.AnimationEntry;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfig.AnimatorConfigEntry;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJAction;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader.BOBJData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClientProxy
{
    public static File configFolder;

    /**
     * Emote keys configuration
     */
    public static EmoteKeys keys;

    /**
     * Cosmetic mode determines how emote packet will be sent from the client
     */
    public static CosmeticMode mode = CosmeticMode.CLIENT;

    /**
     * Last time actions were reloaded
     */
    public static long lastUpdate;

    /**
     * Action map
     */
    public static Map<String, BOBJAction> actionMap = new HashMap<String, BOBJAction>();

    public static BOBJData ragdoll;

    /**
     * Custom payload channel, allows external plugins to trigger emotes
     * without the mod being installed on the server
     */
    public static FMLEventChannel channel;

    public static void reloadActions()
    {
        try
        {
            BOBJData actions = BOBJLoader.readData(ClientProxy.class.getResourceAsStream("/assets/emoticons/models/entity/actions.bobj"));

            actionMap.clear();
            actionMap.putAll(actions.actions);
            actionMap.putAll(ragdoll.actions);

            lastUpdate = System.currentTimeMillis();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void init()
    {
        configFolder = new File(Minecraft.getMinecraft().mcDataDir, "config/emoticons");
        configFolder.mkdirs();

        /* Load emote keys configuration */
        File file = new File(configFolder, "keys.json");
        keys = EmoteKeys.fromFile(file);

        if (keys == null)
        {
            keys = new EmoteKeys();
            EmoteKeys.toFile(keys, file);
        }

        /* Registering an event channel for custom payload */
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("Emoticons");
        channel.register(new NetworkHandler());

        Emotes.register();

        /* Register event handlers */
        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new EntityModelHandler());

        ClientCommandHandler.instance.registerCommand(new CommandEmote());

        /* Load default models, manually... */
        try
        {
            Class loader = this.getClass();
            AnimationManager manager = AnimationManager.INSTANCE;

            ragdoll = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/ragdoll.bobj"));
            ragdoll.initiateArmatures();

            BOBJData propData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/props.bobj"));
            BOBJData propSimpleData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/props_simple.bobj"));

            BOBJData steveData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default.bobj"));
            BOBJData steve3DData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default_3d.bobj"));
            BOBJData alexData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim.bobj"));
            BOBJData alex3DData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_3d.bobj"));
            BOBJData steveSimpleData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default_simple.bobj"));
            BOBJData alexSimpleData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_simple.bobj"));
            BOBJData steveSimplePlusData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default_simple_plus.bobj"));
            BOBJData alexSimplPluseData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_simple_plus.bobj"));

            reloadActions();

            steveData.actions = actionMap;
            steve3DData.actions = actionMap;
            alexData.actions = actionMap;
            alex3DData.actions = actionMap;
            steveSimpleData.actions = actionMap;
            alexSimpleData.actions = actionMap;
            steveSimplePlusData.actions = actionMap;
            alexSimplPluseData.actions = actionMap;

            BOBJLoader.merge(propData, ragdoll);
            BOBJLoader.merge(propSimpleData, ragdoll);

            BOBJLoader.merge(steveData, propData);
            BOBJLoader.merge(steve3DData, propData);
            BOBJLoader.merge(alexData, propData);
            BOBJLoader.merge(alex3DData, propData);
            BOBJLoader.merge(steveSimpleData, propSimpleData);
            BOBJLoader.merge(alexSimpleData, propSimpleData);
            BOBJLoader.merge(steveSimplePlusData, propSimpleData);
            BOBJLoader.merge(alexSimplPluseData, propSimpleData);

            ragdoll.armatures.get("ArmatureRagdoll").copyOrder(steveData.armatures.get("Armature"));

            Animation steve = new Animation("default", steveData);
            Animation steve3d = new Animation("default_3d", steve3DData);
            Animation alex = new Animation("slim", alexData);
            Animation alex3d = new Animation("slim_3d", alex3DData);
            Animation steveSimple = new Animation("default_simple", steveSimpleData);
            Animation alexSimple = new Animation("slim_simple", alexSimpleData);
            Animation steveSimplePlus = new AnimationSimple("default_simple_plus", steveSimplePlusData);
            Animation alexSimplePlus = new AnimationSimple("slim_simple_plus", alexSimplPluseData);

            steve.init();
            steve3d.init();
            alex.init();
            alex3d.init();
            steveSimple.init();
            alexSimple.init();
            steveSimplePlus.init();
            alexSimplePlus.init();

            manager.animations.put("default", new AnimationEntry(steve, configFolder, 1));
            manager.animations.put("default_3d", new AnimationEntry(steve3d, configFolder, 1));
            manager.animations.put("slim", new AnimationEntry(alex, configFolder, 1));
            manager.animations.put("slim_3d", new AnimationEntry(alex3d, configFolder, 1));
            manager.animations.put("default_simple", new AnimationEntry(steveSimple, configFolder, 1));
            manager.animations.put("slim_simple", new AnimationEntry(alexSimple, configFolder, 1));
            manager.animations.put("default_simple_plus", new AnimationEntry(steveSimplePlus, configFolder, 1));
            manager.animations.put("slim_simple_plus", new AnimationEntry(alexSimplePlus, configFolder, 1));

            AnimatorConfig steveConfig = manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/default.json"), StandardCharsets.UTF_8), AnimatorConfig.class);
            AnimatorConfig alexConfig = manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/slim.json"), StandardCharsets.UTF_8), AnimatorConfig.class);
            AnimatorConfig steveSimpleConfig = manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/default_simple.json"), StandardCharsets.UTF_8), AnimatorConfig.class);
            AnimatorConfig alexSimpleConfig = manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_simple.json"), StandardCharsets.UTF_8), AnimatorConfig.class);

            manager.configs.put("default", new AnimatorConfigEntry(steveConfig, 1));
            manager.configs.put("default_3d", new AnimatorConfigEntry(steveConfig, 1));
            manager.configs.put("slim", new AnimatorConfigEntry(alexConfig, 1));
            manager.configs.put("slim_3d", new AnimatorConfigEntry(alexConfig, 1));
            manager.configs.put("default_simple", new AnimatorConfigEntry(steveSimpleConfig, 1));
            manager.configs.put("slim_simple", new AnimatorConfigEntry(alexSimpleConfig, 1));
            manager.configs.put("default_simple_plus", new AnimatorConfigEntry(steveSimpleConfig, 1));
            manager.configs.put("slim_simple_plus", new AnimatorConfigEntry(alexSimpleConfig, 1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
