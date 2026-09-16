package mchorse.emoticons.skin_n_bones.api.bobj;

import mchorse.mclib.utils.Interpolations;
import net.minecraft.util.math.MathHelper;

public enum Interpolation {
    CONSTANT {
        public float interpolate(BOBJKeyframe from, float progress, BOBJKeyframe to) {
            return from.value;
        }
    },
    LINEAR {
        public float interpolate(BOBJKeyframe from, float progress, BOBJKeyframe to) {
            return BOBJKeyframe.lerp(from.value, to.value, progress);
        }
    },
    BEZIER {
        public float interpolate(BOBJKeyframe from, float progress, BOBJKeyframe to) {
            if (progress <= 0.0f) {
                return from.value;
            }
            if (progress >= 1.0f) {
                return to.value;
            }

            float frameSpan = (float) (to.frame - from.frame);
            float valueSpan = to.value - from.value;
            if (valueSpan == 0.0f) {
                valueSpan = 1.0E-5f;
            }

            float rightX = (from.rightX - from.frame) / frameSpan;
            float rightY = (from.rightY - from.value) / valueSpan;
            float leftX = (to.leftX - from.frame) / frameSpan;
            float leftY = (to.leftY - from.value) / valueSpan;

            float epsilon = 5.0E-4f;
            epsilon = (valueSpan == 0.0f) ? epsilon : Math.max(Math.min(epsilon, 1.0f / valueSpan * epsilon), 1.0E-5f);
            rightX = MathHelper.clamp(rightX, 0.0f, 1.0f);
            leftX = MathHelper.clamp(leftX, 0.0f, 1.0f);

            return Interpolations.bezier(0.0f, rightY, leftY, 1.0f, Interpolations.bezierX(rightX, leftX, progress, epsilon)) * valueSpan + from.value;
        }
    };

    public abstract float interpolate(BOBJKeyframe from, float progress, BOBJKeyframe to);
}
