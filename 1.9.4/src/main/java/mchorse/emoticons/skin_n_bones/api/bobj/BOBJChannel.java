package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.ArrayList;
import java.util.List;

public class BOBJChannel {
    public String path;
    public int index;
    public List<BOBJKeyframe> keyframes = new ArrayList<>();

    public BOBJChannel(String path, int index) {
        this.path = path;
        this.index = index;
    }

    public float calculate(float f) {
        int n = this.keyframes.size();
        if (n <= 0) {
            return 0.0f;
        }
        if (n == 1) {
            return this.keyframes.get(0).value;
        }
        BOBJKeyframe keyframe = this.keyframes.get(0);
        if ((float) keyframe.frame > f) {
            return keyframe.value;
        }
        for (int i = 0; i < n; ++i) {
            keyframe = this.keyframes.get(i);
            if (!((float) keyframe.frame > f) || i == 0) continue;
            BOBJKeyframe frame = this.keyframes.get(i - 1);
            float f2 = (f - (float) frame.frame) / (float) (keyframe.frame - frame.frame);
            return frame.interpolate(f2, keyframe);
        }
        return keyframe.value;
    }

    public BOBJKeyframe get(float f, boolean bl) {
        int n = this.keyframes.size();
        if (n == 0) {
            return null;
        }
        if (n == 1) {
            return this.keyframes.get(0);
        }
        BOBJKeyframe keyframe = null;
        for (int i = 0; i < n; ++i) {
            keyframe = this.keyframes.get(i);
            if (!((float) keyframe.frame > f) || i == 0) continue;
            return bl ? keyframe : this.keyframes.get(i - 1);
        }
        return keyframe;
    }

    public void apply(BOBJBone bone, float f) {
        if (this.path.equals("location")) {
            if (this.index == 0) {
                bone.x = this.calculate(f);
            } else if (this.index == 1) {
                bone.y = this.calculate(f);
            } else if (this.index == 2) {
                bone.z = this.calculate(f);
            }
        } else if (this.path.equals("rotation")) {
            if (this.index == 0) {
                bone.rotateX = this.calculate(f);
            } else if (this.index == 1) {
                bone.rotateY = this.calculate(f);
            } else if (this.index == 2) {
                bone.rotateZ = this.calculate(f);
            }
        } else if (this.path.equals("scale")) {
            if (this.index == 0) {
                bone.scaleX = this.calculate(f);
            } else if (this.index == 1) {
                bone.scaleY = this.calculate(f);
            } else if (this.index == 2) {
                bone.scaleZ = this.calculate(f);
            }
        }
    }

    public void applyInterpolate(BOBJBone bone, float f, float f2) {
        float f3 = this.calculate(f);
        if (this.path.equals("location")) {
            if (this.index == 0) {
                bone.x = f3 + (bone.x - f3) * f2;
            } else if (this.index == 1) {
                bone.y = f3 + (bone.y - f3) * f2;
            } else if (this.index == 2) {
                bone.z = f3 + (bone.z - f3) * f2;
            }
        } else if (this.path.equals("rotation")) {
            if (this.index == 0) {
                bone.rotateX = f3 + (bone.rotateX - f3) * f2;
            } else if (this.index == 1) {
                bone.rotateY = f3 + (bone.rotateY - f3) * f2;
            } else if (this.index == 2) {
                bone.rotateZ = f3 + (bone.rotateZ - f3) * f2;
            }
        } else if (this.path.equals("scale")) {
            if (this.index == 0) {
                bone.scaleX = f3 + (bone.scaleX - f3) * f2;
            } else if (this.index == 1) {
                bone.scaleY = f3 + (bone.scaleY - f3) * f2;
            } else if (this.index == 2) {
                bone.scaleZ = f3 + (bone.scaleZ - f3) * f2;
            }
        }
    }
}
