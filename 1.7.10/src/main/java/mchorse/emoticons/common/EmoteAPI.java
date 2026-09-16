package mchorse.emoticons.common;

import mchorse.emoticons.capabilities.cosmetic.CosmeticMode;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.entity.player.EntityPlayer;

public class EmoteAPI {
    public static void setEmoteClient(String string, EntityPlayer player) {
        ICosmetic cosmetic = EmoteController.get(player);
        if (cosmetic == null) {
            return;
        }
        Emote emote = Emotes.get(string);
        CosmeticMode mod = cosmetic.getMode();
        if (mod == CosmeticMode.CLIENT || emote == null) {
            cosmetic.setEmote(emote, player);
        }
    }
}
