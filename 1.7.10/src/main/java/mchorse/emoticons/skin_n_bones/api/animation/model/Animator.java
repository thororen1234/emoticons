package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
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
    public double prevX;
    public double prevZ;
    public double prevMY;
    public boolean wasOnGround;
    public boolean wasShooting;
    public boolean wasConsuming;
    public AnimatorController controller;

    public Animator(final AnimatorController controller) {
        this.actions = new ArrayList();
        this.prevX = 3.4028234663852886E38;
        this.prevZ = 3.4028234663852886E38;
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
    public void update(final EntityLivingBase livingBase) {
        if (this.prevX == 3.4028234663852886E38) {
            this.prevX = livingBase.posX;
            this.prevZ = livingBase.posZ;
        }
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

    protected void controlActions(final EntityLivingBase livingBase) {
        final double a = livingBase.posX - this.prevX;
        final double a2 = livingBase.posZ - this.prevZ;
        final boolean b = livingBase instanceof EntityPlayer && ((EntityPlayer) livingBase).capabilities.isFlying;
        final boolean inWater = livingBase.isInWater();
        final float n = b ? 0.1f : (inWater ? 0.025f : 0.01f);
        final boolean b2 = Math.abs(a) > n || Math.abs(a2) > n;
        if (this.emote != null) {
            this.setActiveAction(this.emote);
        } else if (livingBase.getHealth() <= 0.0f) {
            this.setActiveAction(this.dying);
        } else if (livingBase.isPlayerSleeping()) {
            this.setActiveAction(this.sleeping);
        } else if (inWater) {
            this.setActiveAction(b2 ? this.swimming : this.swimmingIdle);
        } else if (livingBase.isRiding()) {
            final Entity entity = livingBase.ridingEntity;
            final boolean b3 = Math.abs(entity.posX - this.prevX) > n || Math.abs(entity.posZ - this.prevZ) > n;
            this.prevX = entity.posX;
            this.prevZ = entity.posZ;
            this.setActiveAction(b3 ? this.riding : this.ridingIdle);
        } else if (!b) {
            final float n2 = (float) (Math.round(Math.sqrt(a * a + a2 * a2) * 1000.0) / 1000.0);
            if (livingBase.isSneaking()) {
                final float n3 = n2 / 0.065f;
                this.setActiveAction(b2 ? this.crouching : this.crouchingIdle);
                if (this.crouching != null) {
                    this.crouching.setSpeed((livingBase.moveForward > 0.0f) ? n3 : (-n3));
                }
            } else if (!livingBase.onGround && livingBase.motionY < 0.0 && livingBase.fallDistance > 1.25) {
                this.setActiveAction(this.falling);
            } else if (livingBase.isSprinting() && this.sprinting != null) {
                this.setActiveAction(this.sprinting);
                this.sprinting.setSpeed(n2 / 0.281f);
            } else {
                this.setActiveAction(b2 ? this.running : this.idle);
                final float n4 = n2 / 0.216f;
                if (this.running != null) {
                    this.running.setSpeed((livingBase.moveForward >= 0.0f) ? n4 : (-n4));
                }
                if (this.walking != null) {
                    this.walking.setSpeed((livingBase.moveForward > 0.0f) ? n4 : (-n4));
                }
            }
            if (!livingBase.onGround || this.wasOnGround || livingBase.isSprinting() || this.prevMY < -0.5) {
            }
        } else {
            this.setActiveAction(b2 ? this.flying : this.flyingIdle);
        }
        if (!livingBase.onGround && this.wasOnGround && Math.abs(livingBase.motionY) > 0.20000000298023224) {
            this.wasOnGround = false;
        }
        final boolean wasShooting = this.wasShooting;
        final boolean wasConsuming = this.wasConsuming;
        final ItemStack heldItem = livingBase.getHeldItem();
        if (heldItem != null) {
            if (((EntityPlayer) livingBase).getItemInUseCount() > 0) {
                final EnumAction action = heldItem.getItemUseAction();
                if (action == EnumAction.bow) {
                    if (!this.actions.contains(this.shoot)) {
                    }
                    this.wasShooting = true;
                } else if (action == EnumAction.drink || action == EnumAction.eat) {
                    if (!this.actions.contains(this.consume)) {
                    }
                    this.wasConsuming = true;
                }
            } else {
                this.wasShooting = false;
                this.wasConsuming = false;
            }
        } else {
            this.wasShooting = false;
            this.wasConsuming = false;
        }
        if (wasShooting && !this.wasShooting && this.shoot != null) {
            this.shoot.fade();
        }
        if (wasConsuming && !this.wasConsuming && this.consume != null) {
            this.consume.fade();
        }
        if (livingBase.hurtTime == livingBase.maxHurtTime - 1) {
        }
        if (!livingBase.isSwingInProgress || livingBase.swingProgress != 0.0f || !livingBase.isPlayerSleeping()) {
        }
        this.prevX = livingBase.posX;
        this.prevZ = livingBase.posZ;
        this.prevMY = livingBase.motionY;
        this.wasOnGround = livingBase.onGround;
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

    @Override
    public BOBJArmature useArmature(BOBJArmature armature) {
        if (this.active != null && this.active.customArmature != null) {
            return this.active.customArmature;
        }
        return armature;
    }
}


