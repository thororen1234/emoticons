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
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector4f;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EmoteController implements ICosmetic {
    public static final Map<UUID, EmoteController> cache = new ConcurrentHashMap<>();

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
        boolean client = livingBase.world.isRemote;

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
        if (livingBase.world.isRemote) {
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

    public static ResourceLocation getSkin(ResourceLocation skin) {
        return skin;
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
                String skinType = model(player);

                if (!skinType.equals(this.controller.animationName)) {
                    this.controller.animationName = skinType;
                    this.controller.animation = null;
                    this.controller.fetchAnimation();
                }

                if (this.controller.animation == null) {
                    return false;
                }

                this.controller.userConfig.meshes.get("body").texture = player.getLocationSkin();
            }

            if (this.controller.animation == null) {
                return false;
            }

            this.controller.render(livingBase, x, y, z, 0.0f, partialTicks);

            BOBJArmature armature = this.controller.animation.meshes.get(0).armature;
            Minecraft minecraft = Minecraft.getMinecraft();

            if (RenderLightmap.canRenderNamePlate(livingBase)) {
                RenderManager renderManager = minecraft.getRenderManager();
                Vector4f position = this.controller.calcPosition(livingBase, armature.bones.get("head"), 0.0f, 0.0f,
                        0.0f, partialTicks);
                float nameX = position.x - (float) renderManager.viewerPosX;
                float nameY = position.y - (float) renderManager.viewerPosY + 0.20f
                        - mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorController.ENTITY_RENDER_Y_OFFSET;
                float nameZ = position.z - (float) renderManager.viewerPosZ;

                RenderLightmap.renderNameplate(livingBase, livingBase.getDisplayName().getFormattedText(), nameX, nameY,
                        nameZ);
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
     * assets folder (this version ships neither the 3d nor the simple_plus
     * models)
     */
    public static final String[] MODELS = {"default", "simple"};

    /**
     * Get the animation name for given player, based upon their skin type
     * and the model style picked in the client configuration
     */
    public static String model(AbstractClientPlayer player) {
        String skin = player.getSkinType();

        return "simple".equals(ClientConfig.instance.model) ? skin + "_simple" : skin;
    }

    public void setupAnimator(EntityLivingBase livingBase) {
        AbstractClientPlayer player = (AbstractClientPlayer) livingBase;
        this.controller = new AnimatorEmoticonsController(model(player), new NBTTagCompound());

        NBTTagCompound meshCompound = new NBTTagCompound();
        NBTTagCompound bodyCompound = new NBTTagCompound();
        meshCompound.setTag("body", bodyCompound);
        bodyCompound.setString("Texture", player.getLocationSkin().toString());

        this.controller.userData.setTag("Meshes", meshCompound);
        this.controller.fetchAnimation();
    }
}
