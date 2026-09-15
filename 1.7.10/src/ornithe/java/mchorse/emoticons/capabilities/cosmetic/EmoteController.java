package mchorse.emoticons.capabilities.cosmetic;

import mchorse.emoticons.ClientConfig;
import mchorse.emoticons.Emoticons;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.client.EmoteSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.resource.Identifier;
import net.minecraft.client.resource.Resource;
import net.minecraft.client.render.texture.DynamicTexture;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

public class EmoteController implements ICosmetic {
	public static final Map<UUID, EmoteController> cache = new HashMap<>();
	public AnimatorEmoticonsController controller;
	public ActionPlayback emoteAction;
	public Emote emote;
	public CosmeticMode mode = CosmeticMode.CLIENT;
	private LivingEntity owner;
	private EmoteSound sound;
	private int emoteTimer;
	private int effectTick = -1;
	private double lastX, lastY, lastZ;

	public static ICosmetic get(Entity entity) {
		return get(entity.getUuid());
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
		lastX = entity.x;
		lastY = entity.y;
		lastZ = entity.z;
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
	}

	public void update(LivingEntity entity) {
		if (owner != null && owner != entity) {
			stop();
			controller = null;
		}
		owner = entity;
		if (emote != null) {
			double dx = entity.x - lastX, dy = entity.y - lastY, dz = entity.z - lastZ;
			boolean moved = ClientConfig.instance.stopOnMove && dx * dx + dy * dy + dz * dz > 0.000225;
			if (!entity.isAlive() || entity.isSleeping() || moved || (!emote.looping && emoteTimer >= emote.duration)) {
				boolean local = entity == Minecraft.getInstance().player;
				stop();
				if (local)
					mchorse.emoticons.network.ClientEmoteNetwork.send("");
			} else {
				emoteTimer++;
			}
		}
		lastX = entity.x;
		lastY = entity.y;
		lastZ = entity.z;
		if (controller == null && !ClientConfig.instance.disableAnimations)
			setupAnimator(entity);
		if (controller != null)
			controller.update(entity);
	}

	private String model(LivingEntity entity) {
		String skin = "default";
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
		controller = new AnimatorEmoticonsController(model, new NbtCompound());
		controller.fetchAnimation();
		controller.setEmote(emoteAction);
	}

	public boolean render(LivingEntity entity, double x, double y, double z, float delta) {
		setupAnimator(entity);
		if (controller.animation == null || controller.animation.meshes.isEmpty())
			return false;
		if (entity instanceof ClientPlayerEntity && controller.userConfig.meshes.containsKey("body")) {
			controller.userConfig.meshes.get("body").texture = getFixedSkin(
					((ClientPlayerEntity) entity).getSkinTextureLocation());
		}
		controller.render(entity, x, y, z, 0, delta);
		if (emote != null && emoteAction != null && effectTick != emoteTimer && !Minecraft.getInstance().isPaused()) {
			effectTick = emoteTimer;
			BOBJArmature armature = controller.animation.meshes.get(0).armature;
			emote.progressAnimation(entity, armature, controller, (int) emoteAction.getTick(0), delta);
		}
		return true;
	}

	private static final Map<Identifier, Identifier> FIXED_SKINS = new HashMap<>();

	public static Identifier getFixedSkin(Identifier skin) {
		if (skin == null)
			return null;
		if (FIXED_SKINS.containsKey(skin))
			return FIXED_SKINS.get(skin);

		try {
			Resource resource = Minecraft.getInstance().getResourceManager().getResource(skin);
			if (resource != null) {
				BufferedImage image = ImageIO.read(resource.asStream());
				if (image != null && image.getWidth() == 64 && image.getHeight() == 32) {
					BufferedImage newImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
					Graphics graphics = newImage.getGraphics();
					graphics.drawImage(image, 0, 0, null);

					copyMirror(newImage, image, 4, 16, 20, 48, 4, 4); // Top
					copyMirror(newImage, image, 8, 16, 24, 48, 4, 4); // Bottom
					copyMirror(newImage, image, 0, 20, 24, 52, 4, 12); // Right -> Left
					copyMirror(newImage, image, 4, 20, 20, 52, 4, 12); // Front -> Front
					copyMirror(newImage, image, 8, 20, 16, 52, 4, 12); // Left -> Right
					copyMirror(newImage, image, 12, 20, 28, 52, 4, 12); // Back -> Back

					copyMirror(newImage, image, 44, 16, 36, 48, 4, 4); // Top
					copyMirror(newImage, image, 48, 16, 40, 48, 4, 4); // Bottom
					copyMirror(newImage, image, 40, 20, 40, 52, 4, 12); // Right -> Left
					copyMirror(newImage, image, 44, 20, 36, 52, 4, 12); // Front -> Front
					copyMirror(newImage, image, 48, 20, 32, 52, 4, 12); // Left -> Right
					copyMirror(newImage, image, 52, 20, 44, 52, 4, 12); // Back -> Back

					graphics.dispose();

					DynamicTexture dynamicTexture = new DynamicTexture(newImage);
					Identifier newLocation = new Identifier("emoticons", "fixed_skin_" + UUID.randomUUID().toString());
					Minecraft.getInstance().getTextureManager().register(newLocation, dynamicTexture);
					FIXED_SKINS.put(skin, newLocation);
					return newLocation;
				}
			}
		} catch (Exception e) {
		}

		FIXED_SKINS.put(skin, skin);
		return skin;
	}

	private static void copyMirror(BufferedImage to, BufferedImage from, int srcX, int srcY, int dstX, int dstY,
			int width, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				to.setRGB(dstX + (width - 1 - x), dstY + y, from.getRGB(srcX + x, srcY + y));
			}
		}
	}
}
