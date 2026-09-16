package mchorse.emoticons.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mchorse.emoticons.capabilities.cosmetic.EmoteController;
import mchorse.emoticons.capabilities.cosmetic.ICosmetic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class EntityModelHandler {
    private int previousThirdPersonView = -1;
    private mchorse.emoticons.common.emotes.Emote currentEmote = null;

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!(event.entityPlayer instanceof AbstractClientPlayer)) return;
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        ICosmetic cosmetic = EmoteController.get(player);
        
        if (cosmetic != null && cosmetic.getEmote() != null) {
            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialRenderTick - RenderManager.renderPosX;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialRenderTick - RenderManager.renderPosY;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialRenderTick - RenderManager.renderPosZ;
            
            if (player == Minecraft.getMinecraft().thePlayer) {
                y -= player.yOffset;
            }
            
            if (cosmetic.render(player, x, y, z, event.partialRenderTick)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player.worldObj.isRemote
                && event.player instanceof AbstractClientPlayer) {
            EmoteController.postUpdate(event.player);
            this.updateCamera((AbstractClientPlayer) event.player);
        }
    }

    private void updateCamera(AbstractClientPlayer player) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (player != minecraft.thePlayer) {
            return;
        }

        mchorse.emoticons.common.emotes.Emote playingEmote = EmoteController.get(player).getEmote();
        boolean emoting = playingEmote != null;

        if (emoting && playingEmote != this.currentEmote) {
            if (minecraft.gameSettings.thirdPersonView == 0) {
                this.previousThirdPersonView = 0;
                minecraft.gameSettings.thirdPersonView = 1;
            }
        }

        this.currentEmote = playingEmote;

        if (!emoting && this.previousThirdPersonView >= 0) {
            if (minecraft.gameSettings.thirdPersonView == 1) {
                minecraft.gameSettings.thirdPersonView = this.previousThirdPersonView;
            }

            this.previousThirdPersonView = -1;
        }
    }
}
