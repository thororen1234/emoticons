package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Animator implements IAnimator {
	public ActionPlayback idle;
	public ActionPlayback walking;
	public ActionPlayback running;
	public ActionPlayback sprinting;
	public ActionPlayback crouching;
	public ActionPlayback crouchingIdle;
	public ActionPlayback swimming;
	public ActionPlayback swimmingIdle;
	public ActionPlayback flying;
	public ActionPlayback flyingIdle;
	public ActionPlayback riding;
	public ActionPlayback ridingIdle;
	public ActionPlayback dying;
	public ActionPlayback falling;
	public ActionPlayback sleeping;
	public ActionPlayback jump;
	public ActionPlayback swipe;
	public ActionPlayback hurt;
	public ActionPlayback land;
	public ActionPlayback shoot;
	public ActionPlayback consume;
	public ActionPlayback emote;
	public ActionPlayback active;
	public ActionPlayback lastActive;
	public List<ActionPlayback> actions;
	public double prevX = Double.NaN;
	public double prevZ;
	public double prevMY;
	public boolean wasOnGround;
	public boolean wasShooting;
	public boolean wasConsuming;
	public AnimatorController controller;

	public Animator(final AnimatorController controller) {
		this.actions = new ArrayList();
		// 0;
		// 0;
		this.wasOnGround = true;
		this.wasShooting = false;
		this.wasConsuming = false;
		this.controller = controller;
		this.refresh();
	}

	@Override
	public void refresh() {
		final AnimatorActionsConfig config = this.controller.userConfig.actions;
		final Animation animation = this.controller.animation;
		this.idle = animation.createAction(this.idle, config.getConfig("idle"), true);
		this.walking = animation.createAction(this.walking, config.getConfig("walking"), true);
		this.running = animation.createAction(this.running, config.getConfig("running"), true);
		this.sprinting = animation.createAction(this.sprinting, config.getConfig("sprinting"), true);
		this.crouching = animation.createAction(this.crouching, config.getConfig("crouching"), true);
		this.crouchingIdle = animation.createAction(this.crouchingIdle, config.getConfig("crouching_idle"), true);
		this.swimming = animation.createAction(this.swimming, config.getConfig("swimming"), true);
		this.swimmingIdle = animation.createAction(this.swimmingIdle, config.getConfig("swimming_idle"), true);
		this.flying = animation.createAction(this.flying, config.getConfig("flying"), true);
		this.flyingIdle = animation.createAction(this.flyingIdle, config.getConfig("flying_idle"), true);
		this.riding = animation.createAction(this.riding, config.getConfig("riding"), true);
		this.ridingIdle = animation.createAction(this.ridingIdle, config.getConfig("riding_idle"), true);
		this.dying = animation.createAction(this.dying, config.getConfig("dying"), false);
		this.falling = animation.createAction(this.falling, config.getConfig("falling"), true);
		this.sleeping = animation.createAction(this.sleeping, config.getConfig("sleeping"), true);
		this.swipe = animation.createAction(this.swipe, config.getConfig("swipe"), false);
		this.jump = animation.createAction(this.jump, config.getConfig("jump"), false, 2);
		this.hurt = animation.createAction(this.hurt, config.getConfig("hurt"), false, 3);
		this.land = animation.createAction(this.land, config.getConfig("land"), false);
		this.shoot = animation.createAction(this.shoot, config.getConfig("shoot"), true);
		this.consume = animation.createAction(this.consume, config.getConfig("consume"), true);
	}

	@Override
	public void setEmote(final ActionPlayback playback) {
		if (playback != null) {
			this.emote = playback;
		} else if (this.emote != null) {
			this.emote = null;
		}
	}

	@Override
	public void update(final LivingEntity livingBase) {
		if (Double.isNaN(prevX)) { prevX = livingBase.x; prevZ = livingBase.z; }
		this.controlActions(livingBase);
		if (this.active != null) {
			this.active.update();
		}
		if (this.lastActive != null) {
			this.lastActive.update();
		}
		final Iterator<ActionPlayback> iterator = this.actions.iterator();
		while (iterator.hasNext()) {
			final ActionPlayback playback = iterator.next();
			playback.update();
			if (playback.finishedFading()) {
				playback.unfade();
				iterator.remove();
			}
		}
	}

	protected void controlActions(LivingEntity target) {
		double dx = target.x - prevX, dz = target.z - prevZ;
		boolean flying = target instanceof PlayerEntity && ((PlayerEntity) target).abilities.flying;
		boolean wet = target.isInWater();
		float threshold = flying ? 0.1F : wet ? 0.025F : 0.01F;
		boolean moves = Math.abs(dx) > threshold || Math.abs(dz) > threshold;
		float speed = (float) Math.sqrt(dx * dx + dz * dz);
		if (emote != null) setActiveAction(emote);
		else if (target.getHealth() <= 0) setActiveAction(dying);
		else if (target.isSleeping()) setActiveAction(sleeping);
		else if (wet) setActiveAction(moves ? swimming : swimmingIdle);
		else if (target.isRiding()) setActiveAction(moves ? riding : ridingIdle);
		else if (flying) setActiveAction(moves ? this.flying : flyingIdle);
		else if (target.isSneaking()) {
			setActiveAction(moves ? crouching : crouchingIdle);
			if (crouching != null) crouching.setSpeed((target.forwardSpeed >= 0 ? speed : -speed) / 0.065F);
		} else if (!target.onGround && target.velocityY < 0 && target.fallDistance > 1.25) setActiveAction(falling);
		else if (target.isSprinting() && sprinting != null) {
			setActiveAction(sprinting); sprinting.setSpeed(speed / 0.281F);
		} else {
			setActiveAction(moves ? running : idle);
			if (running != null) running.setSpeed((target.forwardSpeed >= 0 ? speed : -speed) / 0.216F);
		}
		if (target.onGround && !wasOnGround && !target.isSprinting() && prevMY < -0.5) addAction(land);
		if (!target.onGround && wasOnGround && Math.abs(target.velocityY) > 0.2) addAction(jump);
		boolean shooting = wasShooting, consuming = wasConsuming;
		wasShooting = false; wasConsuming = false;
		ItemStack held = target.getEquipment(0);
		if (held != null && target instanceof PlayerEntity && ((PlayerEntity) target).hasItemInUse()) {
			net.minecraft.item.UseAction action = held.getUseAction();
			if (action == net.minecraft.item.UseAction.BOW) {
				if (!actions.contains(shoot)) addAction(shoot);
				wasShooting = true;
			} else if (action == net.minecraft.item.UseAction.EAT || action == net.minecraft.item.UseAction.DRINK) {
				if (!actions.contains(consume)) addAction(consume);
				wasConsuming = true;
			}
		}
		if (shooting && !wasShooting && shoot != null) shoot.fade();
		if (consuming && !wasConsuming && consume != null) consume.fade();
		if (target.damagedTimer == target.damagedTime - 1) addAction(hurt);
		if (target.armSwinging && target.armSwingingTicks == 0 && !target.isSleeping()) addAction(swipe);
		prevX = target.x; prevZ = target.z; prevMY = target.velocityY;
		wasOnGround = target.onGround;
	}

	public void setActiveAction(final ActionPlayback playback) {
		if (this.active == playback || playback == null) {
			return;
		}
		if (this.active != null && playback.priority < this.active.priority) {
			return;
		}
		if (this.active != null) {
			(this.lastActive = this.active).fade();
		}
		(this.active = playback).reset();
	}

	public void addAction(final ActionPlayback playback) {
		if (playback == null) {
			return;
		}
		if (this.actions.contains(playback)) {
			playback.reset();
			return;
		}
		playback.reset();
		this.actions.add(playback);
	}

	@Override
	public void applyActions(final BOBJArmature armature, final float n) {
		if (this.active != null) {
			this.active.apply(armature, n);
		}
		if (this.lastActive != null && this.lastActive.isFading()) {
			this.lastActive.applyInactive(armature, n, 1.0f - this.lastActive.getFadeFactor(n));
		}
		for (final ActionPlayback playback : this.actions) {
			if (playback.isFading()) {
				playback.applyInactive(armature, n, 1.0f - playback.getFadeFactor(n));
			} else {
				playback.apply(armature, n);
			}
		}
	}
}

