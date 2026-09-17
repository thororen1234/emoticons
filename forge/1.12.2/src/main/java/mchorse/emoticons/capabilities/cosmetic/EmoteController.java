package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.ClientProxy;
import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.mclib.client.render.RenderLightmap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Emote controller
 *
 * Static-map based replacement for the removed Capability API based
 * {@code Cosmetic}/{@code CosmeticProvider} pair (matches the pattern used
 * by the other standalone ports of this mod).
 */
public class EmoteController implements ICosmetic
{
    public static final Map<UUID, EmoteController> cache = new ConcurrentHashMap<UUID, EmoteController>();

    @SideOnly(Side.CLIENT)
    public AnimatorEmoticonsController animator;

    @SideOnly(Side.CLIENT)
    public ActionPlayback emoteAction;

    public Emote emote;

    /* Trackers */
    private int emoteTimer;
    private double lastX;
    private double lastY;
    private double lastZ;
    private long lastUpdate = System.currentTimeMillis();

    /**
     * Perspective the player was in before the emote forced third person
     * view (-1 means the perspective wasn't changed by this mod)
     */
    @SideOnly(Side.CLIENT)
    private int previousPerspective = -1;

    public static ICosmetic get(Entity entity)
    {
        return cache.computeIfAbsent(entity.getUniqueID(), (key) -> new EmoteController());
    }

    /**
     * Advance this player's emote state by one tick. Must be driven from a
     * per-tick hook (see {@code EntityModelHandler#onPlayerTick}) - nothing
     * else calls {@link #update(EntityLivingBase)} during actual gameplay,
     * so without this the emote animation never advances past frame 0.
     */
    @SideOnly(Side.CLIENT)
    public static void postUpdate(EntityPlayer player)
    {
        EmoteController controller = (EmoteController) cache.get(player.getUniqueID());

        if (controller != null)
        {
            controller.update(player);
        }
    }

    @Override
    public void setEmote(Emote emote, EntityLivingBase target)
    {
        if (target.world.isRemote)
        {
            this.stopAction(target);
        }

        this.emote = emote;
        this.emoteTimer = 0;

        if (target.world.isRemote)
        {
            this.setActionEmote(emote, target);
        }
    }

    @Override
    public Emote getEmote()
    {
        return this.emote;
    }

    @Override
    public void update(EntityLivingBase target)
    {
        if (target.world.isRemote)
        {
            this.updateClient(target);
        }
        else
        {
            if (this.emote != null)
            {
                /* Turn off emote when player moves */
                double diff = Math.abs((target.posX - this.lastX) + (target.posY - this.lastY) + (target.posZ - this.lastZ));

                if (diff > 0.015 || (!this.emote.looping && this.emoteTimer >= this.emote.duration))
                {
                    EmoteAPI.setEmote("", (EntityPlayerMP) target);
                }

                this.emoteTimer++;
            }

            this.lastX = target.posX;
            this.lastY = target.posY;
            this.lastZ = target.posZ;
        }
    }

    /* Client side code */

    @SideOnly(Side.CLIENT)
    private void updateClient(EntityLivingBase target)
    {
        /* On servers without Emoticons mod, reset the emote when
         * finished */
        if (this.emote != null)
        {
            /* Turn off emote when player moves */
            double diff = Math.abs((target.posX - this.lastX) + (target.posY - this.lastY) + (target.posZ - this.lastZ));
            boolean moved = ClientConfig.instance.stopOnMove && diff > 0.015;

            if (moved || (!this.emote.looping && this.emoteTimer >= this.emote.duration))
            {
                this.setEmote(null, target);
            }
        }

        this.lastX = target.posX;
        this.lastY = target.posY;
        this.lastZ = target.posZ;

        if (this.emote != null && this.emoteAction != null)
        {
            if (this.emote.sound != null && this.emoteAction.getTick(0) == 0 && ClientConfig.instance.sounds)
            {
                float volume = Math.max(0.0F, Math.min(1.0F, ClientConfig.instance.volume));

                if (volume > 0.0F)
                {
                    target.world.playSound(target.posX, target.posY, target.posZ, this.emote.sound, net.minecraft.util.SoundCategory.MASTER, volume, 1, false);
                }
            }

            this.emote.updateEmote(target, this.animator, (int) this.emoteAction.getTick(0));
            this.emoteTimer++;
        }

        if (ClientProxy.lastUpdate > this.lastUpdate)
        {
            this.setupAnimator(target);
            this.lastUpdate = ClientProxy.lastUpdate;
        }

        if (this.animator != null)
        {
            this.animator.update(target);
        }
    }

    @SideOnly(Side.CLIENT)
    private void stopAction(EntityLivingBase target)
    {
        if (this.emote != null)
        {
            this.emote.stopAnimation(this.animator);
        }

        this.restorePerspective();
    }

    /**
     * Switch the camera into third person view, so that the player can
     * actually see the emote they're playing
     */
    @SideOnly(Side.CLIENT)
    private void switchPerspective(EntityLivingBase target)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (target == mc.player && ClientConfig.instance.thirdPerson && mc.gameSettings.thirdPersonView == 0)
        {
            this.previousPerspective = 0;
            mc.gameSettings.thirdPersonView = 1;
        }
    }

    @SideOnly(Side.CLIENT)
    private void restorePerspective()
    {
        if (this.previousPerspective != -1)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.gameSettings.thirdPersonView == 1)
            {
                mc.gameSettings.thirdPersonView = this.previousPerspective;
            }

            this.previousPerspective = -1;
        }
    }

    @SideOnly(Side.CLIENT)
    private void setActionEmote(Emote emote, EntityLivingBase target)
    {
        if (this.animator == null)
        {
            this.setupAnimator(target);
        }

        if (emote != null)
        {
            ActionConfig config = this.animator.config.config.actions.getConfig("emote_" + emote.name);

            this.emoteAction = this.animator.animation.createAction(null, config, emote.looping);
            this.animator.setEmote(this.emoteAction);

            emote.startAnimation(this.animator);

            this.switchPerspective(target);
        }
        else
        {
            this.emoteAction = null;
            this.animator.setEmote(null);
        }
    }

    /**
     * Render the animator controller based on given entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean render(EntityLivingBase entity, double x, double y, double z, float partialTicks)
    {
        /* Load animator with texture */
        if (this.animator == null)
        {
            this.setupAnimator(entity);
        }

        boolean render = this.animator != null;

        if (render)
        {
            /* Just in case */
            if (entity instanceof AbstractClientPlayer)
            {
                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                String type = model(player);

                if (!type.equals(this.animator.animationName))
                {
                    this.animator.animationName = type;
                    this.animator.animation = null;
                    this.animator.fetchAnimation();
                }

                this.animator.userConfig.meshes.get("body").texture = player.getLocationSkin();
            }

            this.animator.render(this.emote, entity, x, y, z, 0, partialTicks);

            BOBJArmature armature = this.animator.animation.meshes.get(0).getCurrentArmature();
            Minecraft mc = Minecraft.getMinecraft();

            if (RenderLightmap.canRenderNamePlate(entity))
            {
                RenderManager manager = mc.getRenderManager();
                Vector4f vec = this.animator.calcPosition(entity, armature.bones.get("head"), 0F, 0F, 0F, partialTicks);
                float pYaw = manager.playerViewY;
                float pPitch = manager.playerViewX;
                boolean frontal = mc.gameSettings.thirdPersonView == 2;

                float nx = vec.x - (float) manager.viewerPosX;
                float ny = vec.y - (float) manager.viewerPosY + 0.7F;
                float nz = vec.z - (float) manager.viewerPosZ;

                EntityRenderer.drawNameplate(mc.fontRenderer, entity.getDisplayName().getFormattedText(), nx, ny, nz, -6, pYaw, pPitch, frontal, entity.isSneaking());
            }

            if (this.emote != null && this.emoteAction != null && !Minecraft.getMinecraft().isGamePaused())
            {
                int tick = (int) this.emoteAction.getTick(0);

                this.emote.progressAnimation(entity, armature, this.animator, tick, partialTicks);
            }
        }

        return render;
    }

    /**
     * Available model styles, matching the models shipped in this module's
     * assets folder
     */
    public static final String[] MODELS = {"default", "3d", "simple", "simple_plus"};

    /**
     * Get the animation name for given player, based upon their skin type
     * and the model style picked in the client configuration
     */
    @SideOnly(Side.CLIENT)
    public static String model(AbstractClientPlayer player)
    {
        String skin = player.getSkinType();
        String style = ClientConfig.instance.model;

        if ("3d".equals(style)) return skin + "_3d";
        if ("simple".equals(style)) return skin + "_simple";
        if ("simple_plus".equals(style)) return skin + "_simple_plus";

        return skin;
    }

    @SideOnly(Side.CLIENT)
    public void setupAnimator(EntityLivingBase entity)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) entity;

        this.animator = new AnimatorEmoticonsController(model(player), new NBTTagCompound());

        NBTTagCompound meshes = new NBTTagCompound();
        NBTTagCompound body = new NBTTagCompound();

        meshes.setTag("body", body);
        body.setString("Texture", player.getLocationSkin().toString());

        this.animator.userData.setTag("Meshes", meshes);
        this.animator.fetchAnimation();
    }
}
