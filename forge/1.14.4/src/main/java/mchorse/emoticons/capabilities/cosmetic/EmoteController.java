package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.network.ClientEmoteNetwork;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.Emoticons;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.client.EmoteSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client animation state. Server synchronization deliberately uses separate
 * state.
 */
public class EmoteController implements ICosmetic {
	public static final Map<UUID, EmoteController> cache = new ConcurrentHashMap<>();
	public AnimatorEmoticonsController controller;
	public ActionPlayback emoteAction;
	public Emote emote;
	public CosmeticMode mode = CosmeticMode.CLIENT;
	private LivingEntity owner;
	private EmoteSound sound;
	private int emoteTimer;
	private int effectTick = -1;
	private double lastX, lastY, lastZ;
	private int previousPerspective = -1;

	public static ICosmetic get(Entity entity) {
		return get(entity.getUniqueID());
	}

	public static ICosmetic get(UUID id) {
		return cache.computeIfAbsent(id, k -> new EmoteController());
	}

	public static void postUpdate(PlayerEntity player) {
		EmoteController state = (EmoteController) get(player);
		state.update(player);
	}

	public static void clear() {
		for (EmoteController state : cache.values())
			state.stop();
		cache.clear();
	}

	public static void retain(Set<UUID> players) {
		Iterator<Map.Entry<UUID, EmoteController>> iterator = cache.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, EmoteController> entry = iterator.next();
			if (!players.contains(entry.getKey())) {
				entry.getValue().stop();
				iterator.remove();
			}
		}
	}

	public CosmeticMode getMode() {
		return mode;
	}

	public void setMode(CosmeticMode mode) {
		this.mode = mode;
	}

	public Emote getEmote() {
		return emote;
	}

	public void setEmote(Emote next, LivingEntity entity) {
		stop();
		owner = entity;
		lastX = entity.posX;
		lastY = entity.posY;
		lastZ = entity.posZ;
		emoteTimer = 0;
		effectTick = -1;
		if (next == null)
			return;
		setupAnimator(entity);
		if (controller.animation == null)
			return;
		emoteAction = controller.animation.createAction(null,
				controller.userConfig.actions.getConfig("emote_" + next.key), next.looping);
		if (emoteAction == null) {
			Emoticons.LOGGER.warn("No animation for emote " + next.key);
			return;
		}
		emote = next;
		controller.setEmote(emoteAction);
		next.startAnimation(controller);
		Minecraft mc = Minecraft.getInstance();
		if (entity == mc.player && ClientConfig.instance.thirdPerson && mc.gameSettings.thirdPersonView == 0) {
			previousPerspective = 0;
			mc.gameSettings.thirdPersonView = 1;
		}
		sound = EmoteSound.play(entity, next);
	}

	public void seek(int ticks) {
		emoteTimer = Math.max(0, ticks);
		if (emoteAction != null)
			emoteAction.seek(ticks);
	}

	public int loopsDone() {
		return emote == null ? 0 : emoteTimer / Math.max(1, emote.duration);
	}

	private void stop() {
		if (emote != null && controller != null)
			emote.stopAnimation(controller);
		emote = null;
		emoteAction = null;
		if (controller != null)
			controller.setEmote(null);
		if (sound != null) {
			sound.finish();
			sound = null;
		}
		if (previousPerspective != -1) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.gameSettings.thirdPersonView == 1) {
				mc.gameSettings.thirdPersonView = previousPerspective;
			}
			previousPerspective = -1;
		}
	}

	public void update(LivingEntity entity) {
		if (owner != null && owner != entity) {
			stop();
			controller = null;
		}
		owner = entity;
		if (emote != null) {
			double dx = entity.posX - lastX, dy = entity.posY - lastY, dz = entity.posZ - lastZ;
			boolean moved = ClientConfig.instance.stopOnMove && dx * dx + dy * dy + dz * dz > 0.000225;
			if (!entity.isAlive() || entity.isSleeping() || moved || (!emote.looping && emoteTimer >= emote.duration)) {
				boolean local = entity == Minecraft.getInstance().player;
				stop();
				if (local)
					ClientEmoteNetwork.send("");
			} else {
				emoteTimer++;
			}
		}
		lastX = entity.posX;
		lastY = entity.posY;
		lastZ = entity.posZ;
		if (controller == null && !ClientConfig.instance.disableAnimations)
			setupAnimator(entity);
		if (controller != null)
			controller.update(entity);
	}

	private String model(LivingEntity entity) {
		String skin = entity instanceof AbstractClientPlayerEntity ? ((AbstractClientPlayerEntity) entity).getSkinType() : "default";
		String style = ClientConfig.instance.model;
		if ("3d".equals(style))
			return skin + "_3d";
		if ("simple".equals(style))
			return skin + "_simple";
		if ("simple_plus".equals(style))
			return skin + "_simple_plus";
		return skin;
	}

	public void setupAnimator(LivingEntity entity) {
		String model = model(entity);
		if (controller != null && model.equals(controller.animationName))
			return;
		controller = new AnimatorEmoticonsController(model, new CompoundNBT());
		controller.fetchAnimation();
		controller.setEmote(emoteAction);
	}

	public boolean render(LivingEntity entity, double x, double y, double z, float delta) {
		setupAnimator(entity);
		if (controller.animation == null || controller.animation.meshes.isEmpty())
			return false;
		if (entity instanceof AbstractClientPlayerEntity && controller.userConfig.meshes.containsKey("body")) {
			controller.userConfig.meshes.get("body").texture = ((AbstractClientPlayerEntity) entity).getLocationSkin();
		}
		controller.render(entity, x, y, z, 0, delta);
		// Emit effects at most once per simulation tick, regardless of frame rate or
		// render passes.
		if (emote != null && emoteAction != null && effectTick != emoteTimer && !Minecraft.getInstance().isGamePaused()) {
			effectTick = emoteTimer;
			BOBJArmature armature = controller.animation.meshes.get(0).armature;
			emote.progressAnimation(entity, armature, controller, (int) emoteAction.getTick(0), delta);
		}
		return true;
	}
}
