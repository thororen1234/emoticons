package mchorse.emoticons.common.emotes;

import mchorse.emoticons.utils.Time;

import java.util.HashMap;
import java.util.Map;

public class Emotes {
	public static final Map<String, Emote> EMOTES = new HashMap<>();

	public static boolean has(String key) {
		return key != null && EMOTES.containsKey(key.split(":", 2)[0]);
	}

	public static Emote get(String key) {
		if (key == null || key.isEmpty()) return null;
		String[] parts = key.split(":", 2);
		Emote emote = EMOTES.get(parts[0]);
		if (emote == null) return null;
		return parts.length == 1 ? emote.getDynamicEmote() : emote.getDynamicEmote(parts[1]);
	}

	public static void register() {
		EMOTES.clear();
		register(new Emote("best_mates", 11, true));
		register(new Emote("boneless", 40, true));
		register(new Emote("default", 139, true));
		register(new Emote("disco_fever", 175, true));
		register(new Emote("electro_shuffle", 169, true));
		register(new Emote("floss", 32, true));
		register(new Emote("fresh", 101, true));
		register(new Emote("gangnam_style", 18, true));
		register(new Emote("hype", 68, true));
		register(new Emote("infinite_dab", 19, true));
		register(new Emote("orange_justice", 130, true));
		register(new Emote("skibidi", 16, true));
		register(new Emote("squat_kick", 232, true));
		register(new StarPowerEmote("star_power", 160, true));
		register(new Emote("take_the_l", 16, true));
		register(new Emote("tidy", 104, true));
		register(new Emote("free_flow", 158, true));
		register(new Emote("shimmer", 156, true));
		register(new Emote("get_funky", 172, true));
		register(new Emote("boy", 29, false));
		register(new Emote("bow", 43, false));
		register(new Emote("calculated", 33, false));
		register(new Emote("chicken", 19, true));
		register(new Emote("clapping", 15, true));
		register(new Emote("club", 20, true));
		register(new Emote("confused", 140, false));
		register(new CryingEmote("crying", 27, true));
		register(new Emote("dab", 23, false));
		register(new Emote("facepalm", 104, false));
		register(new Emote("fist", 53, false));
		register(new Emote("laughing", 15, true));
		register(new Emote("no", 30, false));
		register(new Emote("pointing", 33, false));
		register(new PopcornEmote("popcorn", 102, true));
		register(new PureSaltEmote("pure_salt", 104, false));
		register(new RockPaperScissorsEmote("rock_paper_scissors", 60, false));
		register(new Emote("salute", 50, false));
		register(new Emote("shrug", 50, false));
		register(new Emote("t_pose", 80, true));
		register(new Emote("thinking", 100, true));
		register(new Emote("twerk", 14, true));
		register(new Emote("wave", 40, false));
		register(new Emote("yes", 23, false));
		register(new Emote("bitchslap", Time.toTicks(100), false));
		register(new Emote("bongo_cat", Time.toTicks(238), false));
		register(new Emote("breathtaking", Time.toTicks(154), false));
		register(new DisgustedEmote("disgusted", Time.toTicks(200), false));
		register(new Emote("exhausted", Time.toTicks(330), true));
		register(new Emote("punch", Time.toTicks(58), false));
		register(new SneezeEmote("sneeze", Time.toTicks(200), false));
		register(new Emote("threatening", Time.toTicks(70), false));
		register(new Emote("woah", Time.toTicks(66), false));
		register(new Emote("stick_bug", Time.toTicks(25), true));
		register(new Emote("am_stuff", Time.toTicks(80), false));
		register(new Emote("slow_clap", Time.toTicks(200), false));
		register(new Emote("hell_yeah", Time.toTicks(70), false));
		register(new Emote("paranoid", Time.toTicks(315), false));
		register(new Emote("scared", Time.toTicks(50), true));
		register(new Emote("tada", Time.toTicks(90), false));
		register(new Emote("smug_dance", Time.toTicks(29), true));
		register(new Emote("nope", Time.toTicks(101), false));
		register(new Emote("ragdoll_1", Time.toTicks(135), false));
		register(new Emote("ragdoll_2", Time.toTicks(150), false));
		register(new Emote("ragdoll_3", Time.toTicks(120), false));
	}

	public static void register(Emote emote) {
		EMOTES.put(emote.key, emote);
	}
}
