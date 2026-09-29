package mchorse.emoticons.common;

import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.network.ClientEmoteNetwork;
import net.minecraft.entity.player.PlayerEntity;

public final class EmoteAPI {
	public static void setEmoteClient(String key, PlayerEntity player) {
		if (player == null || key == null || (!key.isEmpty() && !Emotes.has(key))) return;
		Emote emote = Emotes.get(key);
		if (!key.isEmpty() && emote == null) return;
		EmoteController.get(player).setEmote(emote, player);
		ClientEmoteNetwork.send(emote == null ? "" : emote.getKey());
	}
}
