package mchorse.emoticons.client;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/** Client-only controls for the standalone 1.7.10 Forge port. */
@SideOnly(Side.CLIENT)
public class EmoteKeyboardHandler {
    public final KeyBinding random = new KeyBinding("emoticons.keys.random", Keyboard.KEY_O,
            "emoticons.keys.category");
    public final KeyBinding menu = new KeyBinding("emoticons.keys.emotes", Keyboard.KEY_P,
            "emoticons.keys.category");
    public final KeyBinding[] emotes = new KeyBinding[6];

    public EmoteKeyboardHandler() {
        ClientRegistry.registerKeyBinding(this.random);
        ClientRegistry.registerKeyBinding(this.menu);
        for (int i = 0; i < 6; i++) {
            this.emotes[i] = new KeyBinding("emoticons.keys.emote" + (i + 1), 0, "emoticons.keys.category");
            ClientRegistry.registerKeyBinding(this.emotes[i]);
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;

        if (player == null) {
            return;
        }

        if (this.random.isPressed()) {
            this.playRandom(player);
        }

        if (this.menu.isPressed()) {
            minecraft.displayGuiScreen(new GuiEmotes());
        }

        for (int i = 0; i < 6; i++) {
            if (this.emotes[i].isPressed()) {
                String key = mchorse.emoticons.ClientProxy.keys.emotes.get(i);
                if (key != null && !key.isEmpty()) {
                    play(Emotes.get(key), player);
                }
            }
        }
    }

    private void playRandom(EntityPlayerSP player) {
        List<String> keys = new ArrayList<String>();

        for (Object key : Emotes.EMOTES.keySet()) {
            keys.add((String) key);
        }

        if (!keys.isEmpty()) {
            play(Emotes.get(keys.get((int) (Math.random() * keys.size()))), player);
        }
    }

    public static void play(Emote emote, EntityPlayerSP player) {
        if (emote == null || !player.onGround || Math.abs(player.motionX + player.motionZ) >= 0.05D) {
            return;
        }

        mchorse.emoticons.network.Dispatcher.sendToServer(new mchorse.emoticons.network.common.PacketEmote(player.getEntityId(), emote));
        EmoteController.get(player).setEmote(emote, player);
    }
}
