package mchorse.emoticons.client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import mchorse.emoticons.client.gui.GuiEmotes;
import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.common.emotes.Emotes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyboardHandler
{
    public static KeyBinding random;
    public static KeyBinding emote1;
    public static KeyBinding emote2;
    public static KeyBinding emote3;
    public static KeyBinding emote4;
    public static KeyBinding emote5;
    public static KeyBinding emote6;
    public static KeyBinding emotes;
    public static KeyBinding stopEmote;
    public static KeyBinding reloadEmotes;

    /** Must run inside {@code FMLClientSetupEvent}, registries are per-dist. */
    public static void init()
    {
        String pre = "emoticons.keys.";

        random = key(pre + "random", GLFW.GLFW_KEY_O, pre);
        emote1 = key(pre + "emote1", GLFW.GLFW_KEY_KP_1, pre);
        emote2 = key(pre + "emote2", GLFW.GLFW_KEY_KP_2, pre);
        emote3 = key(pre + "emote3", GLFW.GLFW_KEY_KP_3, pre);
        emote4 = key(pre + "emote4", GLFW.GLFW_KEY_KP_4, pre);
        emote5 = key(pre + "emote5", GLFW.GLFW_KEY_KP_5, pre);
        emote6 = key(pre + "emote6", GLFW.GLFW_KEY_KP_6, pre);
        emotes = key(pre + "emotes", GLFW.GLFW_KEY_P, pre);
        stopEmote = key(pre + "stop_emote", InputMappings.INPUT_INVALID.getKeyCode(), pre);
        reloadEmotes = key(pre + "reload_emotes", InputMappings.INPUT_INVALID.getKeyCode(), pre);
    }

    private static KeyBinding key(String description, int code, String prefix)
    {
        KeyBinding binding = new KeyBinding(description, code, prefix + "category");

        ClientRegistry.registerKeyBinding(binding);

        return binding;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.currentScreen != null || mc.isGamePaused())
        {
            return;
        }

        ICosmetic cap = EmoteController.get(mc.player);

        if (cap != null)
        {
            processKeybind(mc.player, cap);
        }
    }

    private static void processKeybind(PlayerEntity player, ICosmetic cap)
    {
        Emote emote = cap.getEmote();
        String key = null;
        EmoteKeys emotesObj = ClientProxy.keys;

        if (random.isPressed())
        {
            List<String> keys = new ArrayList<String>(Emotes.EMOTES.keySet());

            if (!keys.isEmpty())
            {
                key = keys.get((int) (keys.size() * Math.random()));
            }
        }

        if (emotesObj != null && emotesObj.emotes.size() >= 6)
        {
            if (emote1.isPressed()) key = emotesObj.emotes.get(0);
            if (emote2.isPressed()) key = emotesObj.emotes.get(1);
            if (emote3.isPressed()) key = emotesObj.emotes.get(2);
            if (emote4.isPressed()) key = emotesObj.emotes.get(3);
            if (emote5.isPressed()) key = emotesObj.emotes.get(4);
            if (emote6.isPressed()) key = emotesObj.emotes.get(5);
        }

        double dist = Math.abs(player.getMotion().x) + Math.abs(player.getMotion().z);

        if (player.onGround && dist < 0.05 && key != null && !key.isEmpty())
        {
            EmoteAPI.setEmoteClient(key, player);
        }

        if (stopEmote.isPressed() && emote != null)
        {
            EmoteAPI.setEmoteClient("", player);
        }

        if (emotes.isPressed())
        {
            Minecraft mc = Minecraft.getInstance();

            if (mc.currentScreen == null)
            {
                try
                {
                    mc.displayGuiScreen(new GuiEmotes());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (reloadEmotes.isPressed())
        {
            ClientConfig.load();
            ClientProxy.reloadActions();
        }
    }
}
