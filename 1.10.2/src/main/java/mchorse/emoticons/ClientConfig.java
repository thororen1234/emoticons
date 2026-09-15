package mchorse.emoticons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Client preferences stored independently of Minecraft's key bindings. */
public final class ClientConfig {
	public static ClientConfig instance = new ClientConfig();
	public boolean disableAnimations = false;
	public boolean stopOnMove = true;
	public boolean thirdPerson = true;
	public boolean sounds = true;
	public float volume = 1.0F;
	public String model = "default";

	public static void load() {
		File file = new File(ClientProxy.configFolder, "config.json");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		if (file.isFile()) {
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				ClientConfig config = gson.fromJson(reader, ClientConfig.class);
				if (config != null) instance = config;
			} catch (Exception e) {
				Emoticons.LOGGER.warn("Unable to read emote preferences", e);
			}
		} else {
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
				gson.toJson(instance, writer);
			} catch (IOException e) {
				Emoticons.LOGGER.warn("Unable to save emote preferences", e);
			}
		}
	}
}
