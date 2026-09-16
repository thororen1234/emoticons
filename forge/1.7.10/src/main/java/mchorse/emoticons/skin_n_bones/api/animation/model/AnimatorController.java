package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.mclib.client.render.RenderLightmap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;

public class AnimatorController {
    private static final FloatBuffer MATRIX_BUFFER = BufferUtils.createFloatBuffer(16);
    private static final float[] MATRIX_ARRAY = new float[16];
    public static final IAnimatorFactory DEFAULT_FACTORY = Animator::new;
    public static final float ENTITY_RENDER_Y_OFFSET = 24.0f * 0.0625f + 0.0078125f;

    public IAnimatorFactory factory;
    public Animation animation;
    public IAnimator animator;
    public ActionPlayback emote;
    public AnimatorConfigEntry config;
    public AnimatorConfig userConfig;
    public long lastModified;
    public int checkConfig;
    public String animationName;
    public NBTTagCompound userData;
    private final Minecraft mc;
    private final Vector4f result;
    private final Matrix4f rotate;

    public AnimatorController(String name, NBTTagCompound data) {
        this.factory = DEFAULT_FACTORY;
        this.userConfig = new AnimatorConfig();
        this.result = new Vector4f();
        this.rotate = new Matrix4f();
        this.refresh(name, data);
        this.mc = Minecraft.getMinecraft();
    }

    public Vector4f calcPosition(EntityLivingBase livingBase, BOBJBone bone, float x, float y, float z,
                                 float partialTicks) {
        this.result.set(x, y, z, 1.0f);
        bone.mat.transform(this.result);
        this.rotate.setIdentity();
        this.rotate.rotY((180.0f - livingBase.renderYawOffset + 180.0f) / 180.0f * (float) Math.PI);
        this.rotate.transform(this.result);
        this.result.scale(0.9375f);

        float x2 = (float) (livingBase.lastTickPosX + (livingBase.posX - livingBase.lastTickPosX) * partialTicks);
        float y2 = (float) (livingBase.lastTickPosY + (livingBase.posY - livingBase.lastTickPosY) * partialTicks);
        float z2 = (float) (livingBase.lastTickPosZ + (livingBase.posZ - livingBase.lastTickPosZ) * partialTicks);

        this.result.x += x2;
        this.result.y += y2;
        this.result.z += z2;

        return this.result;
    }

    public void setEmote(ActionPlayback playback) {
        this.emote = playback;
        if (this.animator != null) {
            this.animator.setEmote(playback);
        }
    }

    public void refresh(String key, NBTTagCompound compound) {
        this.animation = null;
        this.animator = null;
        this.animationName = key;
        this.userData = compound;
    }

    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float partialTicks) {
        this.fetchAnimation();

        if (this.animation != null && this.animation.meshes.size() > 0) {
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            float guiScale = this.userConfig.scaleGui;
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, 0.0f);
            GL11.glScalef(scale * guiScale, -scale * guiScale, scale * guiScale);
            GL11.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);

            float prevYawHead = player.rotationYawHead;
            float prevPrevYawHead = player.prevRotationYawHead;
            float prevPitch = player.rotationPitch;
            float prevPrevPitch = player.prevRotationPitch;

            player.prevRotationYawHead = 0.0f;
            player.rotationYawHead = 0.0f;
            player.prevRotationPitch = 0.0f;
            player.rotationPitch = 0.0f;

            this.renderAnimation(player, this.animation.meshes.get(0), 0.0f, 0.0f);

            player.rotationYawHead = prevYawHead;
            player.prevRotationYawHead = prevPrevYawHead;
            player.rotationPitch = prevPitch;
            player.prevRotationPitch = prevPrevPitch;

            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public void render(EntityLivingBase livingBase, double x, double y, double z, float entityYaw, float partialTicks) {
        if (this.animation != null && this.animation.meshes.size() > 0) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            RenderHelper.enableStandardItemLighting();

            float yaw = livingBase.prevRenderYawOffset
                    + (livingBase.renderYawOffset - livingBase.prevRenderYawOffset) * partialTicks;

            if (livingBase.isRiding()) {
                Entity ridingEntity = livingBase.ridingEntity;

                if (ridingEntity instanceof EntityLivingBase) {
                    EntityLivingBase riddenLiving = (EntityLivingBase) ridingEntity;
                    yaw = riddenLiving.prevRenderYawOffset
                            + (riddenLiving.renderYawOffset - riddenLiving.prevRenderYawOffset) * partialTicks;
                } else {
                    yaw = ridingEntity.prevRotationYaw
                            + (ridingEntity.rotationYaw - ridingEntity.prevRotationYaw) * partialTicks;
                }

                if (ridingEntity instanceof EntityMinecart) {
                    yaw += 90.0f;
                }
            }

            float scale = this.userConfig.scale;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glScalef(scale, scale, scale);

            if (livingBase.isPlayerSleeping()) {
                GL11.glRotatef(((EntityPlayer) livingBase).getBedOrientationInDegrees(), 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
            } else {
                GL11.glRotatef(180.0f - (yaw - 180.0f), 0.0f, 1.0f, 0.0f);
            }

            this.renderAnimation(livingBase, this.animation.meshes.get(0), yaw, partialTicks);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    public void renderAnimation(EntityLivingBase livingBase, AnimationMesh mesh, float yaw, float partialTicks) {
        BOBJArmature armature = mesh.armature;
        if (this.animator != null) {
            armature = this.animator.useArmature(armature);
        }
        this.setupBoneMatrices(livingBase, armature, yaw, partialTicks);

        for (AnimationMesh animationMesh : this.animation.meshes) {
            animationMesh.currentArmature = armature;
            animationMesh.updateMesh();
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        boolean lit = RenderLightmap.set(livingBase, partialTicks);
        this.animation.render(this.userConfig.meshes);
        if (lit) {
            RenderLightmap.unset();
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        this.renderItems(livingBase, armature);
        this.renderHead(livingBase, armature.bones.get(this.userConfig.head));
    }

    public void setupBoneMatrices(EntityLivingBase livingBase, BOBJArmature armature, float yaw, float partialTicks) {
        for (BOBJBone bone : armature.orderedBones) {
            bone.reset();
        }

        this.setupBoneTransformations(livingBase, armature, yaw, partialTicks);

        for (BOBJBone bone : armature.orderedBones) {
            armature.matrices[bone.index] = bone.compute();
        }
    }

    protected void setupBoneTransformations(EntityLivingBase livingBase, BOBJArmature armature, float yaw,
                                            float partialTicks) {
        BOBJBone head = armature.bones.get(this.userConfig.head);

        if (head != null) {
            float yawHead = livingBase.prevRotationYawHead
                    + (livingBase.rotationYawHead - livingBase.prevRotationYawHead) * partialTicks;
            float pitch = livingBase.prevRotationPitch
                    + (livingBase.rotationPitch - livingBase.prevRotationPitch) * partialTicks;

            yawHead = net.minecraft.util.MathHelper.wrapAngleTo180_float(yaw - yawHead) / 180.0f * (float) Math.PI;

            head.rotateX = pitch / 180.0f * (float) Math.PI;
            head.rotateY = yawHead;
        }

        if (this.animator != null) {
            this.animator.applyActions(armature, partialTicks);
        }
    }

    protected void renderHead(EntityLivingBase entity, BOBJBone head) {
        ItemStack stack = entity.getEquipmentInSlot(4);

        if (stack != null && head != null) {
            Item item = stack.getItem();

            if (!(item instanceof ItemArmor)) {
                GL11.glPushMatrix();
                this.setupMatrix(head);
                GL11.glTranslatef(0.0f, 0.25f, 0.0f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(0.625f, 0.625f, 0.625f);
                RenderManager.instance.itemRenderer.renderItem(entity, stack, 0);
                GL11.glPopMatrix();
            }
        }
    }

    protected void renderItems(EntityLivingBase livingBase, BOBJArmature armature) {
        if (!this.userConfig.renderHeldItems) {
            return;
        }

        float scale = this.userConfig.scaleItems;
        ItemStack mainItem = livingBase.getHeldItem();

        if (mainItem != null && this.userConfig.rightHands != null) {
            for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
                this.renderItem(livingBase, mainItem, armature, config, 0, scale);
            }
        }
    }

    public void renderItem(EntityLivingBase livingBase, ItemStack stack, BOBJArmature armature,
                           AnimatorHeldItemConfig config, int type, float scale) {
    }

    public void setupMatrix(BOBJBone bone) {
        this.setupMatrix(bone.mat);
    }

    public void setupMatrix(Matrix4f matrix) {
        MATRIX_ARRAY[0] = matrix.m00;
        MATRIX_ARRAY[1] = matrix.m10;
        MATRIX_ARRAY[2] = matrix.m20;
        MATRIX_ARRAY[3] = matrix.m30;
        MATRIX_ARRAY[4] = matrix.m01;
        MATRIX_ARRAY[5] = matrix.m11;
        MATRIX_ARRAY[6] = matrix.m21;
        MATRIX_ARRAY[7] = matrix.m31;
        MATRIX_ARRAY[8] = matrix.m02;
        MATRIX_ARRAY[9] = matrix.m12;
        MATRIX_ARRAY[10] = matrix.m22;
        MATRIX_ARRAY[11] = matrix.m32;
        MATRIX_ARRAY[12] = matrix.m03;
        MATRIX_ARRAY[13] = matrix.m13;
        MATRIX_ARRAY[14] = matrix.m23;
        MATRIX_ARRAY[15] = matrix.m33;

        MATRIX_BUFFER.clear();
        MATRIX_BUFFER.put(MATRIX_ARRAY);
        MATRIX_BUFFER.flip();
        GL11.glMultMatrix(MATRIX_BUFFER);
    }

    public void update(EntityLivingBase entity) {
        this.fetchAnimation();

        if (this.animator != null) {
            this.watchConfig();
            this.animator.update(entity);
        }
    }

    protected void watchConfig() {
        ++this.checkConfig;

        if (this.checkConfig > 10) {
            this.checkConfig = 0;

            if (this.lastModified < this.config.lastModified) {
                this.animation = null;
                this.fetchAnimation();
            }
        }
    }

    public void fetchAnimation() {
        if (this.animation != null) {
            return;
        }

        Animation animation = AnimationManager.INSTANCE.getAnimation(this.animationName);

        if (animation != null) {
            this.animation = animation;
            this.config = AnimationManager.INSTANCE.getConfig(animation.name);
            this.userConfig.copy(this.config.config);
            this.userConfig.fromNBT(this.userData);
            this.animator = this.factory.createAnimator(this);
            this.animator.setEmote(this.emote);
            this.lastModified = this.config.lastModified;
        }
    }
}
