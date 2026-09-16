package mchorse.emoticons.api.animation.model;

import com.google.common.collect.Maps;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorController;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;

import java.util.Map;

public class AnimatorEmoticonsController extends AnimatorController {

	private static final int SLOT_FEET = 1;
	private static final int SLOT_LEGS = 2;
	private static final int SLOT_CHEST = 3;
	private static final int SLOT_HEAD = 4;

	private static final Map<String, Identifier> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

	public ItemStack itemSlot;
	public float itemSlotScale;

	public AnimatorEmoticonsController(String name, NbtCompound data) {
		super(name, data);
		this.itemSlot = null;
		this.itemSlotScale = 0.0f;
	}

	@Override
	public void renderAnimation(LivingEntity livingBase, AnimationMesh mesh, float yaw, float partialTicks) {
		this.updateArmor(livingBase);
		super.renderAnimation(livingBase, mesh, yaw, partialTicks);
	}

	@Override
	protected void renderItems(LivingEntity livingBase, BOBJArmature armature) {
		if (!this.userConfig.renderHeldItems) {
			return;
		}

		float scaleItems = this.userConfig.scaleItems;
		ItemStack heldItem = livingBase.getEquipment(0);

		if (this.itemSlot != null) {
			if (this.itemSlotScale > 0.0f) {
				for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
					this.renderItem(livingBase, this.itemSlot, armature, config,
							null, scaleItems * this.itemSlotScale);
				}
			}
		} else if (heldItem != null && this.userConfig.rightHands != null) {
			for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
				this.renderItem(livingBase, heldItem, armature, config,
						null, scaleItems);
			}
		}
	}

	private void updateArmor(LivingEntity livingBase) {
		AnimationMeshConfig helmet = this.userConfig.meshes.get("armor_helmet");
		AnimationMeshConfig chestplate = this.userConfig.meshes.get("armor_chest");
		AnimationMeshConfig leggings = this.userConfig.meshes.get("armor_leggings");
		AnimationMeshConfig feet = this.userConfig.meshes.get("armor_feet");

		if (helmet != null) {
			this.updateArmorSlot(helmet, livingBase, SLOT_HEAD);
		}
		if (chestplate != null) {
			this.updateArmorSlot(chestplate, livingBase, SLOT_CHEST);
		}
		if (leggings != null) {
			this.updateArmorSlot(leggings, livingBase, SLOT_LEGS);
		}
		if (feet != null) {
			this.updateArmorSlot(feet, livingBase, SLOT_FEET);
		}
	}

	private void updateArmorSlot(AnimationMeshConfig config, LivingEntity livingBase, int slot) {
		ItemStack stack = livingBase.getEquipment(slot);

		if (stack != null && stack.getItem() instanceof ArmorItem) {
			ArmorItem armor = (ArmorItem) stack.getItem();
			config.visible = true;
			config.texture = this.getArmorResource(livingBase, stack, slot, null);
			config.color = -1;

			if (armor.hasColor(stack)) {
				config.color = 0xFF000000 | armor.getColor(stack);
			}
		} else {
			config.visible = false;
			config.color = -1;
		}
	}

	private Identifier getArmorResource(Entity entity, ItemStack stack, int slot, String suffix) {
		String texture = ((ArmorItem) stack.getItem()).getTier().getKey();
		String namespace = "minecraft";
		int index = texture.indexOf(':');

		if (index != -1) {
			namespace = texture.substring(0, index);
			texture = texture.substring(index + 1);
		}

		String path = String.format("%s:textures/models/armor/%s_layer_%d%s.png", namespace, texture,
				this.isLegSlot(slot) ? 2 : 1, suffix == null ? "" : String.format("_%s", suffix));

		Identifier location = ARMOR_TEXTURE_RES_MAP.get(path);

		if (location == null) {
			location = new Identifier(path);
			ARMOR_TEXTURE_RES_MAP.put(path, location);
		}

		return location;
	}

	private boolean isLegSlot(int slot) {
		return slot == SLOT_LEGS;
	}
}
