package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.mclib.client.render.RenderLightmap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Vector4f;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EmoteController implements ICosmetic {
    public static final Map<UUID, EmoteController> cache = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> FIXED_SKINS = new HashMap<>();

    public AnimatorEmoticonsController controller;
    public ActionPlayback emoteAction;
    public Emote emote;
    public CosmeticMode mode;
    private mchorse.emoticons.client.EmoteSound sound;
    private int emoteTimer;
    private int effectTick;
    private double lastX;
    private double lastY;
    private double lastZ;

    public EmoteController() {
        this.mode = CosmeticMode.CLIENT;
    }

    public static ICosmetic get(Entity entity) {
        return get(entity.getUniqueID());
    }

    public static ICosmetic get(UUID key) {
        return cache.computeIfAbsent(key, k -> new EmoteController());
    }

    public static void postUpdate(EntityPlayer player) {
        EmoteController controller = cache.get(player.getUniqueID());

        if (controller != null) {
            controller.update(player);
        }
    }

    @Override
    public CosmeticMode getMode() {
        return this.mode;
    }

    @Override
    public void setMode(CosmeticMode mode) {
        this.mode = mode;
    }

    @Override
    public void setEmote(Emote emote, EntityLivingBase livingBase) {
        boolean client = livingBase.worldObj.isRemote;

        if (client) {
            this.stopAction(livingBase);
        }

        this.emote = emote;
        this.emoteTimer = 0;

        if (client) {
            this.setActionEmote(emote, livingBase);
        }
    }

    @Override
    public Emote getEmote() {
        return this.emote;
    }

    @Override
    public void update(EntityLivingBase livingBase) {
        if (livingBase.worldObj.isRemote) {
            this.updateClient(livingBase);
        } else {
            if (this.emote != null) {
                if (this.shouldStopEmote(livingBase) && livingBase instanceof AbstractClientPlayer) {
                    this.setEmote(null, livingBase);
                }
                ++this.emoteTimer;
            }

            this.lastX = livingBase.posX;
            this.lastY = livingBase.posY;
            this.lastZ = livingBase.posZ;
        }
    }

    public int loopsDone() {
        if (this.emoteTimer < this.emote.duration) {
            return 0;
        }

        return (int) Math.floor(this.emoteTimer / (double) this.emote.duration);
    }

    private boolean shouldStopEmote(EntityLivingBase livingBase) {
        boolean moved = ClientConfig.instance.stopOnMove && this.emote.shouldStopOnMove()
                && Math.abs(livingBase.posX - this.lastX + (livingBase.posY - this.lastY)
                + (livingBase.posZ - this.lastZ)) > 0.015;

        return moved
                || (this.emote.shouldLimitLoop() && this.loopsDone() > this.emote.loops())
                || (!this.emote.looping && this.emoteTimer >= this.emote.duration);
    }

    private void updateClient(EntityLivingBase livingBase) {
        if (this.mode != CosmeticMode.SERVER) {
            if (this.emote != null && this.shouldStopEmote(livingBase)) {
                this.setEmote(null, livingBase);
            }

            this.lastX = livingBase.posX;
            this.lastY = livingBase.posY;
            this.lastZ = livingBase.posZ;
        }

        if (this.emote != null && this.emoteAction != null) {
            ++this.emoteTimer;
        }

        if (this.controller != null) {
            this.controller.update(livingBase);
        }
    }

    private void stopAction(EntityLivingBase livingBase) {
        if (this.sound != null) {
            this.sound.finish();
            this.sound = null;
        }

        if (this.emote != null) {
            this.emote.stopAnimation(this.controller);
            this.emote = null;
        }
    }

    private void setActionEmote(Emote emote, EntityLivingBase livingBase) {
        if (this.controller == null) {
            this.setupAnimator(livingBase);
        }

        try {
            if (emote != null) {
                ActionConfig actionConfig = this.controller.config.config.actions.getConfig("emote_" + emote.key);

                if (emote.key.equals("gun_lean")) {
                    actionConfig.tick = 8;
                }

                this.emoteAction = this.controller.animation.createAction(null, actionConfig, emote.looping);
                this.controller.setEmote(this.emoteAction);
                emote.startAnimation(this.controller);
                
                if (livingBase instanceof net.minecraft.client.entity.AbstractClientPlayer) {
                    this.sound = mchorse.emoticons.client.EmoteSound.play(livingBase, emote);
                }
            } else {
                this.emoteAction = null;
                this.controller.setEmote((ActionPlayback) null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean render(EntityLivingBase livingBase, double x, double y, double z, float partialTicks) {
        if (ClientConfig.instance.disableAnimations) {
            return false;
        }

        if (this.controller == null) {
            this.setupAnimator(livingBase);
        }

        boolean shouldRender = this.controller != null;

        if (shouldRender) {
            if (livingBase instanceof AbstractClientPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer) livingBase;

                if (this.controller.animation == null) {
                    this.controller.fetchAnimation();
                }

                if (this.controller.animation == null) {
                    return false;
                }

                this.controller.userConfig.meshes.get("body").texture = getSkin(player.getLocationSkin());
            }

            if (this.controller.animation == null) {
                return false;
            }

            this.controller.render(livingBase, x, y, z, 0.0f, partialTicks);

            BOBJArmature armature = this.controller.animation.meshes.get(0).armature;

            if (RenderLightmap.canRenderNamePlate(livingBase)) {
                RenderManager renderManager = RenderManager.instance;
                Vector4f position = this.controller.calcPosition(livingBase, armature.bones.get("head"), 0.0f, 0.0f,
                        0.0f, partialTicks);
                float nameX = position.x - (float) renderManager.viewerPosX;
                float nameY = position.y - (float) renderManager.viewerPosY + 0.20f
                        - mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorController.ENTITY_RENDER_Y_OFFSET;
                float nameZ = position.z - (float) renderManager.viewerPosZ;

                RenderLightmap.renderNameplate(livingBase, livingBase.getCommandSenderName(), nameX, nameY, nameZ);
            }

            if (this.emote != null && this.emoteAction != null && this.effectTick != this.emoteTimer && !Minecraft.getMinecraft().isGamePaused()) {
                this.effectTick = this.emoteTimer;
                this.emote.progressAnimation(livingBase, armature, this.controller,
                        (int) this.emoteAction.getTick(0.0f), partialTicks);
            }
        }

        return shouldRender;
    }

    /**
     * Available model styles, matching the models shipped in this module's
     * assets folder (1.7.10 ships neither the 3d nor the simple_plus models)
     */
    public static final String[] MODELS = {"default", "simple"};

    /**
     * Get the animation name based upon the model style picked in the
     * client configuration. 1.7.10 has no slim (Alex) skin type, so the
     * skin part is always "default"
     */
    public static String model(EntityLivingBase livingBase) {
        return "simple".equals(ClientConfig.instance.model) ? "default_simple" : "default";
    }

    public void setupAnimator(EntityLivingBase livingBase) {
        AbstractClientPlayer player = (AbstractClientPlayer) livingBase;
        this.controller = new AnimatorEmoticonsController(model(livingBase), new NBTTagCompound());

        NBTTagCompound meshCompound = new NBTTagCompound();
        NBTTagCompound bodyCompound = new NBTTagCompound();
        meshCompound.setTag("body", bodyCompound);
        bodyCompound.setString("Texture", getSkin(player.getLocationSkin()).toString());

        this.controller.userData.setTag("Meshes", meshCompound);
        this.controller.fetchAnimation();
    }

    private static boolean isGamePaused() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft.isSingleplayer() && minecraft.currentScreen != null
                && minecraft.currentScreen.doesGuiPauseGame();
    }

    public static ResourceLocation getSkin(ResourceLocation skin) {
        if (FIXED_SKINS.containsKey(skin)) return FIXED_SKINS.get(skin);

        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(skin);
            if (resource != null) {
                BufferedImage image = ImageIO.read(resource.getInputStream());
                if (image != null && image.getWidth() == 64 && image.getHeight() == 32) {
                    BufferedImage newImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
                    Graphics graphics = newImage.getGraphics();
                    graphics.drawImage(image, 0, 0, null);

                    copyMirror(newImage, image, 4, 16, 20, 48, 4, 4); // Top Leg
                    copyMirror(newImage, image, 8, 16, 24, 48, 4, 4); // Bottom Leg
                    copyMirror(newImage, image, 0, 20, 24, 52, 4, 12); // Outer Leg
                    copyMirror(newImage, image, 4, 20, 20, 52, 4, 12); // Front Leg
                    copyMirror(newImage, image, 8, 20, 16, 52, 4, 12); // Inner Leg
                    copyMirror(newImage, image, 12, 20, 28, 52, 4, 12); // Back Leg
                    copyMirror(newImage, image, 44, 16, 36, 48, 4, 4); // Top Arm
                    copyMirror(newImage, image, 48, 16, 40, 48, 4, 4); // Bottom Arm
                    copyMirror(newImage, image, 40, 20, 40, 52, 4, 12); // Outer Arm
                    copyMirror(newImage, image, 44, 20, 36, 52, 4, 12); // Front Arm
                    copyMirror(newImage, image, 48, 20, 32, 52, 4, 12); // Inner Arm
                    copyMirror(newImage, image, 52, 20, 44, 52, 4, 12); // Back Arm

                    graphics.dispose();

                    DynamicTexture texture = new DynamicTexture(newImage);
                    ResourceLocation newSkin = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("fixed_skin_" + UUID.randomUUID().toString(), texture);
                    FIXED_SKINS.put(skin, newSkin);
                    return newSkin;
                }
            }
        } catch (IOException e) {
            // Ignore
        }

        FIXED_SKINS.put(skin, skin);
        return skin;
    }

    private static void copyMirror(BufferedImage to, BufferedImage from, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                to.setRGB(dstX + (width - 1 - x), dstY + y, from.getRGB(srcX + x, srcY + y));
            }
        }
    }
}
