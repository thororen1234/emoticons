package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.mclib.client.render.RenderLightmap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;
import net.minecraft.entity.EquipmentSlot;

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
	public CompoundTag userData;
	private final MinecraftClient mc;
	private final Vector4f result;
	private final Matrix4f rotate;

	public AnimatorController(String name, CompoundTag data) {
		this.factory = DEFAULT_FACTORY;
		this.userConfig = new AnimatorConfig();
		this.result = new Vector4f();
		this.rotate = new Matrix4f();
		this.refresh(name, data);
		this.mc = MinecraftClient.getInstance();
	}

	public Vector4f calcPosition(LivingEntity livingBase, BOBJBone bone, float x, float y, float z,
			float partialTicks) {
		this.result.set(x, y, z, 1.0f);
		bone.mat.transform(this.result);
		this.rotate.setIdentity();
		this.rotate.rotY((180.0f - livingBase.bodyYaw + 180.0f) / 180.0f * (float) Math.PI);
		this.rotate.transform(this.result);
		this.result.scale(0.9375f);

		float x2 = (float) (livingBase.lastRenderX + (livingBase.getX() - livingBase.lastRenderX) * partialTicks);
		float y2 = (float) (livingBase.lastRenderY + (livingBase.getY() - livingBase.lastRenderY) * partialTicks);
		float z2 = (float) (livingBase.lastRenderZ + (livingBase.getZ() - livingBase.lastRenderZ) * partialTicks);

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

	public void refresh(String key, CompoundTag compound) {
		this.animation = null;
		this.animator = null;
		this.animationName = key;
		this.userData = compound;
	}

	public void renderOnScreen(PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, int x, int y, float scale, float partialTicks) {
		this.fetchAnimation();

		if (this.animation != null && this.animation.meshes.size() > 0) {

			GlStateManager.enableRescaleNormal();
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GlStateManager.enableDepthTest();
			GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

			float guiScale = this.userConfig.scaleGui;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x, (float) y, 0.0f);
			GL11.glScalef(scale * guiScale, scale * guiScale, scale * guiScale);

			float prevYawHead = player.headYaw;
			float prevPrevYawHead = player.prevHeadYaw;
			float lastPitch = player.pitch;
			float prevPrevPitch = player.prevPitch;

			player.prevHeadYaw = 0.0f;
			player.headYaw = 0.0f;
			player.prevPitch = 0.0f;
			player.pitch = 0.0f;

			this.renderAnimation(player, matrices, vertexConsumers, light, this.animation.meshes.get(0), 0.0f, 0.0f);

			player.headYaw = prevYawHead;
			player.prevHeadYaw = prevPrevYawHead;
			player.pitch = lastPitch;
			player.prevPitch = prevPrevPitch;

			GL11.glPopMatrix();
			GlStateManager.disableDepthTest();
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			DiffuseLighting.disable();
			GlStateManager.disableRescaleNormal();
		}
	}

	/*
	 * 1.15 moved entity rendering onto MatrixStack/VertexConsumerProvider, so the
	 * old (x, y, z) translation arguments are gone: the caller's MatrixStack is
	 * already positioned at the entity.
	 */
	public void render(LivingEntity livingBase, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, float entityYaw, float partialTicks) {
		if (this.animation != null && this.animation.meshes.size() > 0) {
			GlStateManager.disableCull();
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			float yaw = livingBase.prevBodyYaw + (livingBase.bodyYaw - livingBase.prevBodyYaw) * partialTicks;

			if (livingBase.hasVehicle()) {
				Entity ridingEntity = livingBase.getRootVehicle();

				if (ridingEntity instanceof LivingEntity) {
					LivingEntity riddenLiving = (LivingEntity) ridingEntity;
					yaw = riddenLiving.prevBodyYaw + (riddenLiving.bodyYaw - riddenLiving.prevBodyYaw) * partialTicks;
				} else {
					yaw = 0;
				}

				if (ridingEntity instanceof AbstractMinecartEntity) {
					yaw += 90.0f;
				}
			}

			float scale = this.userConfig.scale;
			matrices.push();
			matrices.scale(scale, scale, scale);

			if (livingBase.isSleeping()) {
				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
			} else {
				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - (yaw - 180.0f)));
			}

			this.renderAnimation(livingBase, matrices, vertexConsumers, light, this.animation.meshes.get(0), yaw,
					partialTicks);
			matrices.pop();
			GlStateManager.enableCull();
		}
	}

	public void renderAnimation(LivingEntity livingBase, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, AnimationMesh mesh, float yaw, float partialTicks) {
		BOBJArmature armature = mesh.armature;
		if (this.emote != null && this.emote.action.name.startsWith("emote_ragdoll_")) {
			armature = mchorse.emoticons.ClientProxy.ragdoll;
		}
		this.setupBoneMatrices(livingBase, armature, yaw, partialTicks);

		for (AnimationMesh animationMesh : this.animation.meshes) {
			animationMesh.currentArmature = armature;
			animationMesh.updateMesh();
		}

		/*
		 * The BOBJ meshes still draw through legacy immediate-mode GL, which no longer
		 * sees the entity transform in 1.15 (the modelview matrix is identity while the
		 * world renders). Pushing the MatrixStack's model matrix onto the GL stack is
		 * exactly what vanilla does for its own unbatched passes, e.g.
		 * ParticleManager#renderParticles.
		 */
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrices.peek().getModel());
		GlStateManager.enableRescaleNormal();
		DiffuseLighting.enable();
		boolean lit = RenderLightmap.set(livingBase, partialTicks);
		this.animation.render(this.userConfig.meshes);
		if (lit) {
			RenderLightmap.unset();
		}
		DiffuseLighting.disable();
		GlStateManager.disableRescaleNormal();
		RenderSystem.popMatrix();

		/* Items go through the new pipeline, so they use the MatrixStack directly. */
		this.renderItems(livingBase, matrices, vertexConsumers, light, armature);
		this.renderHead(livingBase, matrices, vertexConsumers, light, armature.bones.get(this.userConfig.head));
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
			float yawHead = livingBase.prevHeadYaw + (livingBase.headYaw - livingBase.prevHeadYaw) * partialTicks;
			float pitch = livingBase.prevPitch + (livingBase.pitch - livingBase.prevPitch) * partialTicks;

			yawHead = (yaw - yawHead) / 180.0f * (float) Math.PI;

			head.rotateX = pitch / 180.0f * (float) Math.PI;
			head.rotateY = yawHead;
		}

		if (this.animator != null) {
			this.animator.applyActions(armature, partialTicks);
		}
	}

	protected void renderHead(LivingEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, BOBJBone head) {
		ItemStack stack = entity.getEquippedStack(EquipmentSlot.HEAD);

		if (stack != null && head != null && vertexConsumers != null) {
			Item item = stack.getItem();

			if (!(item instanceof ArmorItem)) {
				matrices.push();
				applyBoneMatrix(matrices, head.mat);
				matrices.translate(0.0f, 0.25f, 0.0f);
				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
				matrices.scale(0.625f, 0.625f, 0.625f);
				/*
				 * 1.15: ItemRenderer#renderHeldItem(ItemStack, LivingEntity, Type, boolean) is
				 * now renderItem(LivingEntity, ItemStack, Mode, boolean, MatrixStack,
				 * VertexConsumerProvider, World, light, overlay), and
				 * ModelTransformation.Type was renamed to ModelTransformation.Mode.
				 */
				this.mc.getItemRenderer().renderItem(entity, stack, ModelTransformation.Mode.HEAD, false, matrices,
						vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV);
				matrices.pop();
			}
		}
	}

	protected void renderItems(LivingEntity livingBase, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, BOBJArmature armature) {
		if (!this.userConfig.renderHeldItems) {
			return;
		}

		float scale = this.userConfig.scaleItems;
		ItemStack mainItem = livingBase.getEquippedStack(EquipmentSlot.MAINHAND);

		if (mainItem != null && this.userConfig.rightHands != null) {
			for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
				this.renderItem(livingBase, matrices, vertexConsumers, light, mainItem, armature, config, null, scale);
			}
		}
	}

	public void renderItem(LivingEntity livingBase, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, ItemStack stack, BOBJArmature armature, AnimatorHeldItemConfig config, Object type,
			float scale) {
		BOBJBone bone = armature.bones.get(config.boneName);
		if (bone == null || vertexConsumers == null)
			return;
		matrices.push();
		try {
			applyBoneMatrix(matrices, bone.mat);
			matrices.translate(config.x, config.y, config.z);
			matrices.scale(scale * config.scaleX, scale * config.scaleY, scale * config.scaleZ);
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(config.rotateX));
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(config.rotateY));
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(config.rotateZ));
			mc.getItemRenderer().renderItem(livingBase, stack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, false,
					matrices, vertexConsumers, livingBase.world, light, OverlayTexture.DEFAULT_UV);
		} finally {
			matrices.pop();
		}
	}

	/*
	 * MatrixStack only exposes translate/scale/multiply(Quaternion), and 1.15's own
	 * Matrix4f has no public element accessors, so an arbitrary bone matrix has to
	 * be decomposed before it can be pushed onto the stack.
	 */
	public static void applyBoneMatrix(MatrixStack matrices, Matrix4f matrix) {
		javax.vecmath.Vector3f translation = new javax.vecmath.Vector3f();
		matrix.get(translation);

		Quat4f rotation = new Quat4f();
		matrix.get(rotation);

		float scale = matrix.getScale();

		matrices.translate(translation.x, translation.y, translation.z);
		matrices.multiply(new Quaternion(rotation.x, rotation.y, rotation.z, rotation.w));
		matrices.scale(scale, scale, scale);
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
		GL11.glMultMatrixf(MATRIX_BUFFER);
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
