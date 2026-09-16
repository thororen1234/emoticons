package mchorse.emoticons.network;

import mchorse.emoticons.Emoticons;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import mchorse.emoticons.utils.Time;

/** Server metadata has no dependency on client emote or renderer classes. */
public final class EmoteCatalog {
	private static final Map<String, int[]> ENTRIES = new HashMap<>();
	static {
		ENTRIES.put("best_mates", new int[] { 11, 1 });
		ENTRIES.put("boneless", new int[] { 40, 1 });
		ENTRIES.put("default", new int[] { 139, 1 });
		ENTRIES.put("disco_fever", new int[] { 175, 1 });
		ENTRIES.put("electro_shuffle", new int[] { 169, 1 });
		ENTRIES.put("floss", new int[] { 32, 1 });
		ENTRIES.put("fresh", new int[] { 101, 1 });
		ENTRIES.put("gangnam_style", new int[] { 18, 1 });
		ENTRIES.put("hype", new int[] { 68, 1 });
		ENTRIES.put("infinite_dab", new int[] { 19, 1 });
		ENTRIES.put("orange_justice", new int[] { 130, 1 });
		ENTRIES.put("skibidi", new int[] { 16, 1 });
		ENTRIES.put("squat_kick", new int[] { 232, 1 });
		ENTRIES.put("star_power", new int[] { 160, 1 });
		ENTRIES.put("take_the_l", new int[] { 16, 1 });
		ENTRIES.put("tidy", new int[] { 104, 1 });
		ENTRIES.put("free_flow", new int[] { 158, 1 });
		ENTRIES.put("shimmer", new int[] { 156, 1 });
		ENTRIES.put("get_funky", new int[] { 172, 1 });
		ENTRIES.put("boy", new int[] { 29, 0 });
		ENTRIES.put("bow", new int[] { 43, 0 });
		ENTRIES.put("calculated", new int[] { 33, 0 });
		ENTRIES.put("chicken", new int[] { 19, 1 });
		ENTRIES.put("clapping", new int[] { 15, 1 });
		ENTRIES.put("club", new int[] { 20, 1 });
		ENTRIES.put("confused", new int[] { 140, 0 });
		ENTRIES.put("crying", new int[] { 27, 1 });
		ENTRIES.put("dab", new int[] { 23, 0 });
		ENTRIES.put("facepalm", new int[] { 104, 0 });
		ENTRIES.put("fist", new int[] { 53, 0 });
		ENTRIES.put("laughing", new int[] { 15, 1 });
		ENTRIES.put("no", new int[] { 30, 0 });
		ENTRIES.put("pointing", new int[] { 33, 0 });
		ENTRIES.put("popcorn", new int[] { 102, 1 });
		ENTRIES.put("pure_salt", new int[] { 104, 0 });
		ENTRIES.put("rock_paper_scissors", new int[] { 60, 0 });
		ENTRIES.put("salute", new int[] { 50, 0 });
		ENTRIES.put("shrug", new int[] { 50, 0 });
		ENTRIES.put("t_pose", new int[] { 80, 1 });
		ENTRIES.put("thinking", new int[] { 100, 1 });
		ENTRIES.put("twerk", new int[] { 14, 1 });
		ENTRIES.put("wave", new int[] { 40, 0 });
		ENTRIES.put("yes", new int[] { 23, 0 });
		ENTRIES.put("bitchslap", new int[] { Time.toTicks(100), 0 });
		ENTRIES.put("bongo_cat", new int[] { Time.toTicks(238), 0 });
		ENTRIES.put("breathtaking", new int[] { Time.toTicks(154), 0 });
		ENTRIES.put("disgusted", new int[] { Time.toTicks(200), 0 });
		ENTRIES.put("exhausted", new int[] { Time.toTicks(330), 1 });
		ENTRIES.put("punch", new int[] { Time.toTicks(58), 0 });
		ENTRIES.put("sneeze", new int[] { Time.toTicks(200), 0 });
		ENTRIES.put("threatening", new int[] { Time.toTicks(70), 0 });
		ENTRIES.put("woah", new int[] { Time.toTicks(66), 0 });
		ENTRIES.put("stick_bug", new int[] { Time.toTicks(25), 1 });
		ENTRIES.put("am_stuff", new int[] { Time.toTicks(80), 0 });
		ENTRIES.put("slow_clap", new int[] { Time.toTicks(200), 0 });
		ENTRIES.put("hell_yeah", new int[] { Time.toTicks(70), 0 });
		ENTRIES.put("paranoid", new int[] { Time.toTicks(315), 0 });
		ENTRIES.put("scared", new int[] { Time.toTicks(50), 1 });
		ENTRIES.put("tada", new int[] { Time.toTicks(90), 0 });
		ENTRIES.put("smug_dance", new int[] { Time.toTicks(29), 1 });
		ENTRIES.put("nope", new int[] { Time.toTicks(101), 0 });
		ENTRIES.put("ragdoll_1", new int[] { Time.toTicks(135), 0 });
		ENTRIES.put("ragdoll_2", new int[] { Time.toTicks(150), 0 });
		ENTRIES.put("ragdoll_3", new int[] { Time.toTicks(120), 0 });
	}

	public static void loadCustom() {
		File folder = FabricLoader.getInstance().getConfigDir().resolve("emoticons/emotes").toFile();
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
		if (files == null)
			return;
		for (File file : files) {
			try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				for (Map.Entry<String, JsonElement> entry : JsonParser.parseReader(reader).getAsJsonObject()
						.entrySet()) {
					JsonObject info = entry.getValue().getAsJsonObject();
					int duration = info.has("duration") ? info.get("duration").getAsInt() : 1200;
					ENTRIES.put(entry.getKey(), new int[] { Math.max(1, duration),
							info.has("looping") && info.get("looping").getAsBoolean() ? 1 : 0 });
				}
			} catch (Exception e) {
				Emoticons.LOGGER.warn("Unable to read server emotes from " + file, e);
			}
		}
	}

	public static int[] get(String key) {
		return ENTRIES.get(key.split(":", 2)[0]);
	}

	public static boolean valid(String key) {
		String[] parts = key.split(":", 2);
		if (!ENTRIES.containsKey(parts[0]))
			return false;
		return parts.length == 1 || (parts[0].equals("rock_paper_scissors")
				&& Arrays.asList("rock", "paper", "scissors").contains(parts[1]));
	}
}
