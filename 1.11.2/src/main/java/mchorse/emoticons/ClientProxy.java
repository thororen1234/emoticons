package mchorse.emoticons;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mchorse.emoticons.client.EmoteKeys;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.skin_n_bones.api.animation.*;
import mchorse.emoticons.skin_n_bones.api.animation.model.*;
import mchorse.emoticons.skin_n_bones.api.bobj.*;
import mchorse.mclib.client.render.RenderLightmap;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClientProxy {
	public static File configFolder;
	public static EmoteKeys keys = new EmoteKeys();
	public static BOBJArmature ragdoll;
	public static final Map<String, BOBJAction> actionMap = new HashMap<>();

	public void init() {
		configFolder = FabricLoader.getInstance().getConfigDir().resolve("emoticons").toFile();
		Emoticons.config = configFolder.getAbsolutePath();
		EmoteKeys saved = EmoteKeys.fromFile(new File(configFolder, "keys.json"));
		if (saved == null) {
			// Preserve preferences from the earlier unfinished port.
			saved = EmoteKeys.fromFile(new File(net.minecraft.client.Minecraft.getInstance().gameDir,
					"cheatbreaker/emoticons/keys.json"));
		}
		if (saved != null) keys = saved;
		EmoteKeys.toFile(keys, new File(configFolder, "keys.json"));
		ClientConfig.load();
		RenderLightmap.create();
		reloadActions();
		AnimationManager manager = AnimationManager.INSTANCE;
		for (String model : new String[]{"default", "slim", "default_3d", "slim_3d",
				"default_simple", "slim_simple", "default_simple_plus", "slim_simple_plus"}) {
			try {
				BOBJData data = readModel(model);
				BOBJLoader.merge(data, readModel(model.contains("simple") ? "props_simple" : "props"));
				data.actions = actionMap;
				Animation animation = new Animation(model, data);
				animation.init();
				manager.animations.put(model, new AnimationEntry(animation, configFolder, 1L));
				String config = model.replace("_3d", "").replace("_plus", "");
				try (Reader reader = new InputStreamReader(requireResource(config + ".json"), StandardCharsets.UTF_8)) {
					manager.configs.put(model, new AnimatorConfigEntry(manager.gson.fromJson(reader, AnimatorConfig.class), 1L));
				}
			} catch (Exception e) {
				throw new IllegalStateException("Unable to load Emoticons model " + model, e);
			}
		}
	}

	private static InputStream requireResource(String name) throws IOException {
		InputStream stream = ClientProxy.class.getResourceAsStream("/assets/emoticons/models/entity/" + name);
		if (stream == null) throw new FileNotFoundException(name);
		return stream;
	}

	private static BOBJData readModel(String name) throws IOException {
		try (InputStream stream = requireResource(name + ".bobj")) {
			return BOBJLoader.readData(stream);
		}
	}

	public static void reloadActions() {
		EmoteController.clear();
		Emotes.register();
		try {
			Map<String, BOBJAction> loaded = new HashMap<>(readModel("actions").actions);
			BOBJData ragdollData = readModel("ragdoll");
			loaded.putAll(ragdollData.actions);
			ragdoll = ragdollData.armatures.get("ArmatureRagdoll");
			ragdoll.initArmature();
			File folder = new File(configFolder, "emotes");
			folder.mkdirs();
			File[] files = folder.listFiles((dir, name) -> name.endsWith(".bobj"));
			if (files != null) {
				Arrays.sort(files);
				for (File file : files) {
					try (InputStream stream = new FileInputStream(file)) {
						BOBJData data = BOBJLoader.readData(stream);
						loaded.putAll(data.actions);
						File metadata = new File(folder, file.getName().replaceFirst("\\.bobj$", ".json"));
						if (!metadata.isFile()) continue;
						try (Reader reader = new InputStreamReader(new FileInputStream(metadata), StandardCharsets.UTF_8)) {
							JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
							for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
								BOBJAction action = data.actions.get("emote_" + entry.getKey());
								if (action == null || !entry.getValue().isJsonObject()) continue;
								JsonObject info = entry.getValue().getAsJsonObject();
								Emote emote = new Emote(entry.getKey(), action.getDuration(),
										info.has("looping") && info.get("looping").getAsBoolean());
								if (info.has("title")) emote.customTitle = info.get("title").getAsString();
								if (info.has("description")) emote.customDescription = info.get("description").getAsString();
								Emotes.register(emote);
							}
						}
					} catch (Exception e) {
						Emoticons.LOGGER.error("Unable to load custom emotes from " + file, e);
					}
				}
			}
			actionMap.clear();
			actionMap.putAll(loaded);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load bundled emotes", e);
		}
	}
}
