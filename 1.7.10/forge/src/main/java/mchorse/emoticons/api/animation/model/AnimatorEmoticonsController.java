package mchorse.emoticons.api.animation.model;

import com.google.common.collect.Maps;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorController;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class AnimatorEmoticonsController extends AnimatorController {

    private static final int SLOT_FEET = 1;
    private static final int SLOT_LEGS = 2;
    private static final int SLOT_CHEST = 3;
    private static final int SLOT_HEAD = 4;

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    public ItemStack itemSlot;
    public float itemSlotScale;

    public AnimatorEmoticonsController(String name, NBTTagCompound data) {
        super(name, data);
        this.itemSlot = null;
        this.itemSlotScale = 0.0f;
    }

    @Override
    public void renderAnimation(EntityLivingBase livingBase, AnimationMesh mesh, float yaw, float partialTicks) {
        this.updateArmor(livingBase);
        super.renderAnimation(livingBase, mesh, yaw, partialTicks);
    }

    @Override
    protected void renderItems(EntityLivingBase livingBase, BOBJArmature armature) {
        if (!this.userConfig.renderHeldItems) {
            return;
        }

        float scaleItems = this.userConfig.scaleItems;
        ItemStack heldItem = livingBase.getHeldItem();

        if (this.itemSlot != null) {
            if (this.itemSlotScale > 0.0f) {
                for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
                    this.renderItem(livingBase, this.itemSlot, armature, config,
                            0, scaleItems * this.itemSlotScale);
                }
            }
        } else if (heldItem != null && this.userConfig.rightHands != null) {
            for (AnimatorHeldItemConfig config : this.userConfig.rightHands.values()) {
                this.renderItem(livingBase, heldItem, armature, config,
                        0, scaleItems);
            }
        }
    }

    private void updateArmor(EntityLivingBase livingBase) {
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

    private void updateArmorSlot(AnimationMeshConfig config, EntityLivingBase livingBase, int slot) {
        ItemStack stack = livingBase.getEquipmentInSlot(slot);

        if (stack != null && stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            config.visible = true;
            config.texture = this.getArmorResource(livingBase, stack, slot, null);
            config.color = -1;

            if (armor.hasColor(stack)) {
                config.color = armor.getColor(stack);
            }
        } else {
            config.visible = false;
            config.color = -1;
        }
    }

    private static final String[] ARMOR_TEXTURE_PREFIX = new String[]{"leather", "chainmail", "iron", "diamond", "gold"};

    private ResourceLocation getArmorResource(Entity entity, ItemStack stack, int slot, String suffix) {
        String texture = ARMOR_TEXTURE_PREFIX[((ItemArmor) stack.getItem()).renderIndex];

        String path = String.format("minecraft:textures/models/armor/%s_layer_%d%s.png", texture,
                this.isLegSlot(slot) ? 2 : 1, suffix == null ? "" : String.format("_%s", suffix));

        ResourceLocation location = ARMOR_TEXTURE_RES_MAP.get(path);

        if (location == null) {
            location = new ResourceLocation(path);
            ARMOR_TEXTURE_RES_MAP.put(path, location);
        }

        return location;
    }

    private boolean isLegSlot(int slot) {
        return slot == SLOT_LEGS;
    }
}
