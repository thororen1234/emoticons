package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.mclib.client.render.RenderLightmap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

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
	public NbtCompound userData;
	private final Minecraft mc;
	private final Vector4f result;
	private final Matrix4f rotate;

	public AnimatorController(String name, NbtCompound data) {
		this.factory = DEFAULT_FACTORY;
		this.userConfig = new AnimatorConfig();
		this.result = new Vector4f();
		this.rotate = new Matrix4f();
		this.refresh(name, data);
		this.mc = Minecraft.getInstance();
	}

	public Vector4f calcPosition(LivingEntity livingBase, BOBJBone bone, float x, float y, float z,
			float partialTicks) {
		this.result.set(x, y, z, 1.0f);
		bone.mat.transform(this.result);
		this.rotate.setIdentity();
		this.rotate.rotY((180.0f - livingBase.bodyYaw + 180.0f) / 180.0f * (float) Math.PI);
		this.rotate.transform(this.result);
		this.result.scale(0.9375f);

		float x2 = (float) (livingBase.prevX + (livingBase.x - livingBase.prevX) * partialTicks);
		float y2 = (float) (livingBase.prevY + (livingBase.y - livingBase.prevY) * partialTicks);
		float z2 = (float) (livingBase.prevZ + (livingBase.z - livingBase.prevZ) * partialTicks);

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

	public void refresh(String key, NbtCompound compound) {
		this.animation = null;
		this.animator = null;
		this.animationName = key;
		this.userData = compound;
	}

	public void renderOnScreen(PlayerEntity player, int x, int y, float scale, float partialTicks) {
		this.fetchAnimation();

		if (this.animation != null && this.animation.meshes.size() > 0) {

			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlphaTest();
			GlStateManager.enableDepthTest();
			GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

			float guiScale = this.userConfig.scaleGui;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x, (float) y, 0.0f);
			GL11.glScalef(scale * guiScale, -scale * guiScale, scale * guiScale);
			GL11.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);

			float prevYawHead = player.headYaw;
			float prevPrevYawHead = player.lastHeadYaw;
			float lastPitch = player.pitch;
			float prevPrevPitch = player.lastPitch;

			player.lastHeadYaw = 0.0f;
			player.headYaw = 0.0f;
			player.lastPitch = 0.0f;
			player.pitch = 0.0f;

			this.renderAnimation(player, this.animation.meshes.get(0), 0.0f, 0.0f);

			player.headYaw = prevYawHead;
			player.lastHeadYaw = prevPrevYawHead;
			player.pitch = lastPitch;
			player.lastPitch = prevPrevPitch;

			GL11.glPopMatrix();
			GlStateManager.disableDepthTest();
			GlStateManager.disableAlphaTest();
			Lighting.turnOff();
			GlStateManager.disableRescaleNormal();
		}
	}

	public void render(LivingEntity livingBase, double x, double y, double z, float entityYaw, float partialTicks) {
		if (this.animation != null && this.animation.meshes.size() > 0) {
			GlStateManager.disableCull();
			GlStateManager.enableAlphaTest();

			float yaw = livingBase.lastBodyYaw + (livingBase.bodyYaw - livingBase.lastBodyYaw) * partialTicks;

			if (livingBase.isRiding()) {
				Entity ridingEntity = livingBase.vehicle;

				if (ridingEntity instanceof LivingEntity) {
					LivingEntity riddenLiving = (LivingEntity) ridingEntity;
					yaw = riddenLiving.lastBodyYaw + (riddenLiving.bodyYaw - riddenLiving.lastBodyYaw) * partialTicks;
				} else {
					yaw = 0;
				}

				if (ridingEntity instanceof MinecartEntity) {
					yaw += 90.0f;
				}
			}

			float scale = this.userConfig.scale;
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glScalef(scale, scale, scale);

			if (livingBase.isSleeping()) {
				GlStateManager.rotatef(((PlayerEntity) livingBase).isSleeping() ? 0.0f : 0.0f, 0.0f, 1.0f, 0.0f);
				GlStateManager.rotatef(270.0f, 0.0f, 1.0f, 0.0f);
			} else {
				GL11.glRotatef(180.0f - (yaw - 180.0f), 0.0f, 1.0f, 0.0f);
			}

			this.renderAnimation(livingBase, this.animation.meshes.get(0), yaw, partialTicks);
			GL11.glPopMatrix();
			GlStateManager.enableCull();
		}
	}

	public void renderAnimation(LivingEntity livingBase, AnimationMesh mesh, float yaw, float partialTicks) {
		BOBJArmature armature = mesh.armature;
		if (this.emote != null && this.emote.action.name.startsWith("emote_ragdoll_")) {
			armature = mchorse.emoticons.ClientProxy.ragdoll;
		}
		this.setupBoneMatrices(livingBase, armature, yaw, partialTicks);

		for (AnimationMesh animationMesh : this.animation.meshes) {
			animationMesh.currentArmature = armature;
			animationMesh.updateMesh();
		}

		GlStateManager.enableRescaleNormal();
		boolean lit = RenderLightmap.set(livingBase, partialTicks);
		this.animation.render(this.userConfig.meshes);
		if (lit) {
			RenderLightmap.unset();
		}
		GlStateManager.disableRescaleNormal();

		this.renderItems(livingBase, armature);
		this.renderHead(livingBase, armature.bones.get(this.userConfig.head));
	}

	public void setupBoneMatrices(LivingEntity livingBase, BOBJArmature armature, float yaw, float partialTicks) {
		for (BOBJBone bone : armature.orderedBones) {
			bone.reset();
		}

		this.setupBoneTransformations(livingBase, armature, yaw, partialTicks);

		for (BOBJBone bone : armature.orderedBones) {
			armature.matrices[bone.index] = bone.compute();
		}
	}

	protected void setupBoneTransformations(LivingEntity livingBase, BOBJArmature armature, float yaw,
			float partialTicks) {
		BOBJBone head = armature.bones.get(this.userConfig.head);

		if (head != null) {
			float yawHead = livingBase.lastHeadYaw + (livingBase.headYaw - livingBase.lastHeadYaw) * partialTicks;
			float pitch = livingBase.lastPitch + (livingBase.pitch - livingBase.lastPitch) * partialTicks;

			yawHead = (yaw - yawHead) / 180.0f * (float) Math.PI;

			head.rotateX = pitch / 180.0f * (float) Math.PI;
			head.rotateY = yawHead;
		}

		if (this.animator != null) {
			this.animator.applyActions(armature, partialTicks);
		}
	}

	protected void renderHead(LivingEntity entity, BOBJBone head) {
		ItemStack stack = entity.getEquipment(4);

		if (stack != null && head != null) {
			Item item = stack.getItem();

			if (!(item instanceof ArmorItem)) {
				GlStateManager.pushMatrix();
				this.setupMatrix(head);
				GlStateManager.translatef(0.0f, 0.25f, 0.0f);
				GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
				GlStateManager.scalef(0.625f, 0.625f, 0.625f);
				this.mc.getItemRenderer().renderItemInHand(stack, entity,
						net.minecraft.client.render.model.block.ModelTransformations.Type.HEAD);
				GlStateManager.popMatrix();
			}
		}
	}

	protected void renderItems(LivingEntity livingBase, BOBJArmature armature) {
		if (!this.userConfig.renderHeldItems) {
			return;
		}

		float scale = this.userConfig.scaleItems;
		ItemStack mainItem = livingBase.getEquipment(0);

		if (mainItem != null && this.userConfig.rightHands != null) {
			for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
				this.renderItem(livingBase, mainItem, armature, config, null, scale);
			}
		}
	}

	public void renderItem(LivingEntity livingBase, ItemStack stack, BOBJArmature armature,
			AnimatorHeldItemConfig config, Object type, float scale) {
		BOBJBone bone = armature.bones.get(config.boneName);
		if (bone == null)
			return;
		GlStateManager.pushMatrix();
		try {
			setupMatrix(bone);
			GlStateManager.translatef(config.x, config.y, config.z);
			GlStateManager.scalef(scale * config.scaleX, scale * config.scaleY, scale * config.scaleZ);
			GlStateManager.rotatef(config.rotateX, 1, 0, 0);
			GlStateManager.rotatef(config.rotateY, 0, 1, 0);
			GlStateManager.rotatef(config.rotateZ, 0, 0, 1);
			mc.getItemRenderer().renderItemInHand(stack, livingBase,
					net.minecraft.client.render.model.block.ModelTransformations.Type.THIRD_PERSON);
		} finally {
			GlStateManager.popMatrix();
		}
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

	public void update(LivingEntity entity) {
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
