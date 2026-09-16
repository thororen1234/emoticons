package mchorse.emoticons.common.emotes;

import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RockPaperScissorsEmote
        extends Emote {
    public String suffix = "";

    public RockPaperScissorsEmote(String string, int n, boolean bl) {
        super(string, n, bl);
    }

    public RockPaperScissorsEmote(String string, int n, boolean bl, String string2) {
        super(string, n, bl);
        this.suffix = string2;
    }

    public Emote getDynamicEmote() {
        int n = this.rand.nextInt(30);
        String string = "";
        if (n <= 10) {
            string = "rock";
        } else if (n <= 20) {
            string = "paper";
        } else if (n <= 30) {
            string = "scissors";
        }
        return this.getDynamicEmote(string);
    }

    public Emote getDynamicEmote(String string) {
        return new RockPaperScissorsEmote(this.key, this.duration, this.looping, string);
    }

    public String getKey() {
        return this.key + (this.suffix.isEmpty() ? "" : ":" + this.suffix);
    }

    public void startAnimation(AnimatorEmoticonsController controller) {
        if (this.suffix.equals("rock")) {
            controller.itemSlot = new ItemStack(Blocks.STONE, 1);
        } else if (this.suffix.equals("paper")) {
            controller.itemSlot = new ItemStack(Items.PAPER, 1);
        } else if (this.suffix.equals("scissors")) {
            controller.itemSlot = new ItemStack((Item) Items.SHEARS, 1);
        }
        controller.itemSlotScale = 0.0f;
    }

    public void progressAnimation(EntityLivingBase livingBase, BOBJArmature armature, AnimatorEmoticonsController controller, int n, float f) {
        controller.itemSlotScale = n > 25 && n < 55 ? (n < 30 ? ((float) (n - 25) + f) / 5.0f : (n >= 50 ? 1.0f - ((float) (n - 50) + f) / 5.0f : 1.0f)) : 0.0f;
    }

    public void stopAnimation(AnimatorEmoticonsController controller) {
        controller.itemSlot = null;
        controller.itemSlotScale = 0.0f;
    }
}
