package mchorse.emoticons.common.emotes;

import mchorse.emoticons.utils.Time;

import java.util.HashMap;
import java.util.Map;

public class Emotes {
    public static final Map EMOTES = new HashMap();

    public static boolean has(String string) {
        if (string.contains(":")) {
            string = string.split(":")[0];
        }
        return EMOTES.containsKey(string);
    }

    public static Emote get(String string) {
        if (string != null && string.contains(":")) {
            String[] stringArray = string.split(":");
            Emote emote = (Emote) EMOTES.get(stringArray[0]);
            return emote == null ? null : emote.getDynamicEmote(stringArray[1]);
        }
        Emote emote = (Emote) EMOTES.get(string);
        return emote == null ? null : emote.getDynamicEmote();
    }

    public static void register() {
        Emotes.register(new Emote("best_mates", 11, true));
        Emotes.register(new Emote("boneless", 40, true));
        Emotes.register(new Emote("default", 139, true));
        Emotes.register(new Emote("disco_fever", 175, true));
        Emotes.register(new Emote("electro_shuffle", 169, true));
        Emotes.register(new Emote("floss", 32, true));
        Emotes.register(new Emote("fresh", 101, true));
        Emotes.register(new Emote("gangnam_style", 18, true));
        Emotes.register(new Emote("hype", 68, true));
        Emotes.register(new Emote("infinite_dab", 19, true));
        Emotes.register(new Emote("orange_justice", 130, true));
        Emotes.register(new Emote("skibidi", 16, true));
        Emotes.register(new Emote("squat_kick", 232, true));
        Emotes.register((Emote) new StarPowerEmote("star_power", 160, true));
        Emotes.register(new Emote("l_dance", 16, true));
        Emotes.register(new Emote("take_the_l", 16, true));
        Emotes.register(new Emote("tidy", 104, true));
        Emotes.register(new Emote("free_flow", 158, true));
        Emotes.register(new Emote("shimmer", 156, true));
        Emotes.register(new Emote("get_funky", 172, true));
        Emotes.register(new Emote("gun_lean", 144, true));
        Emotes.register(new Emote("boy", 29, false));
        Emotes.register(new Emote("bow", 43, false));
        Emotes.register(new Emote("calculated", 33, false));
        Emotes.register(new Emote("chicken", 19, true));
        Emotes.register(new Emote("clapping", 15, true));
        Emotes.register(new Emote("club", 20, true));
        Emotes.register(new Emote("confused", 140, false));
        Emotes.register((Emote) new CryingEmote("crying", 27, true));
        Emotes.register(new Emote("dab", 23, false));
        Emotes.register(new Emote("facepalm", 104, false));
        Emotes.register(new Emote("fist", 53, false));
        Emotes.register(new Emote("laughing", 15, true));
        Emotes.register(new Emote("no", 30, false));
        Emotes.register(new Emote("pointing", 33, false));
        Emotes.register((Emote) new PopcornEmote("popcorn", 102, true));
        Emotes.register((Emote) new PureSaltEmote("pure_salt", 104, false));
        Emotes.register(new RockPaperScissorsEmote("rock_paper_scissors", 60, false));
        Emotes.register(new Emote("salute", 50, false));
        Emotes.register(new Emote("shrug", 50, false));
        Emotes.register(new Emote("t_pose", 80, true));
        Emotes.register(new Emote("thinking", 100, true));
        Emotes.register((Emote) new TwerkEmote("twerk", 14, true));
        Emotes.register(new Emote("wave", 40, false));
        Emotes.register(new Emote("yes", 23, false));
        Emotes.register(new Emote("bitchslap", Time.toTicks(100), false));
        Emotes.register(new Emote("bongo_cat", Time.toTicks(238), false));
        Emotes.register(new Emote("breathtaking", Time.toTicks(154), false));
        Emotes.register((Emote) new DisgustedEmote("disgusted", Time.toTicks(200), false));
        Emotes.register(new Emote("exhausted", Time.toTicks(330), true));
        Emotes.register(new Emote("punch", Time.toTicks(58), false));
        Emotes.register((Emote) new SneezeEmote("sneeze", Time.toTicks(200), false));
        Emotes.register(new Emote("threatening", Time.toTicks(70), false));
        Emotes.register(new Emote("woah", Time.toTicks(66), false));
        Emotes.register(new Emote("stick_bug", Time.toTicks(25), true));
        Emotes.register(new Emote("am_stuff", Time.toTicks(80), false));
        Emotes.register(new Emote("slow_clap", Time.toTicks(200), false));
        Emotes.register(new Emote("hell_yeah", Time.toTicks(70), false));
        Emotes.register(new Emote("paranoid", Time.toTicks(315), false));
        Emotes.register(new Emote("scared", Time.toTicks(50), true));
        Emotes.register(new Emote("tada", Time.toTicks(90), false));
        Emotes.register(new Emote("smug_dance", Time.toTicks(29), true));
        Emotes.register(new Emote("nope", Time.toTicks(101), false));
        Emotes.register(new Emote("ragdoll_1", Time.toTicks(135), false));
        Emotes.register(new Emote("ragdoll_2", Time.toTicks(150), false));
        Emotes.register(new Emote("ragdoll_3", Time.toTicks(120), false));
    }

    public static void register(Emote emote) {
        EMOTES.put(emote.key, emote);
    }
}
