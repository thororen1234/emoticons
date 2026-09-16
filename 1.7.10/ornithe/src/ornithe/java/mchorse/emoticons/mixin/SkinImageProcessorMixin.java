package mchorse.emoticons.mixin;

import net.minecraft.client.render.texture.SkinImageProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

@Mixin(SkinImageProcessor.class)
public class SkinImageProcessorMixin {
	@Inject(method = "process", at = @At("RETURN"), cancellable = true)
	private void emoticons(BufferedImage image, CallbackInfoReturnable<BufferedImage> cir) {
		BufferedImage original = cir.getReturnValue();
		if (original != null && original.getWidth() == 64 && original.getHeight() == 32) {
			BufferedImage newImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = newImage.getGraphics();
			graphics.drawImage(original, 0, 0, null);

			// 1.8 skin conversion logic to map 1.7 right arm/leg to left arm/leg
			// Left leg (16, 48) <- Right leg (0, 16)
			copyMirror(newImage, original, 4, 16, 20, 48, 4, 4); // Top
			copyMirror(newImage, original, 8, 16, 24, 48, 4, 4); // Bottom
			copyMirror(newImage, original, 0, 20, 24, 52, 4, 12); // Right -> Left
			copyMirror(newImage, original, 4, 20, 20, 52, 4, 12); // Front -> Front
			copyMirror(newImage, original, 8, 20, 16, 52, 4, 12); // Left -> Right
			copyMirror(newImage, original, 12, 20, 28, 52, 4, 12); // Back -> Back

			// Left arm (32, 48) <- Right arm (40, 16)
			copyMirror(newImage, original, 44, 16, 36, 48, 4, 4); // Top
			copyMirror(newImage, original, 48, 16, 40, 48, 4, 4); // Bottom
			copyMirror(newImage, original, 40, 20, 40, 52, 4, 12); // Right -> Left
			copyMirror(newImage, original, 44, 20, 36, 52, 4, 12); // Front -> Front
			copyMirror(newImage, original, 48, 20, 32, 52, 4, 12); // Left -> Right
			copyMirror(newImage, original, 52, 20, 44, 52, 4, 12); // Back -> Back

			graphics.dispose();
			cir.setReturnValue(newImage);
		}
	}

	private void copyMirror(BufferedImage to, BufferedImage from, int srcX, int srcY, int dstX, int dstY, int width,
			int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				to.setRGB(dstX + (width - 1 - x), dstY + y, from.getRGB(srcX + x, srcY + y));
			}
		}
	}
}
