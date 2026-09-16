package mchorse.emoticons.skin_n_bones.api.bobj;


public class BOBJKeyframe {
    public int frame;
    public float value;
    public Interpolation interpolation = Interpolation.LINEAR;
    public float leftX;
    public float leftY;
    public float rightX;
    public float rightY;

    public static BOBJKeyframe parse(String[] stringArray) {
        if (stringArray.length == 8) {
            float leftX = Float.parseFloat(stringArray[4]);
            float leftY = Float.parseFloat(stringArray[5]);
            float rightX = Float.parseFloat(stringArray[6]);
            float rightY = Float.parseFloat(stringArray[7]);
            return new BOBJKeyframe(Integer.parseInt(stringArray[1]), Float.parseFloat(stringArray[2]), stringArray[3], leftX, leftY, rightX, rightY);
        }
        if (stringArray.length == 4) {
            return new BOBJKeyframe(Integer.parseInt(stringArray[1]), Float.parseFloat(stringArray[2]), stringArray[3]);
        }
        if (stringArray.length == 3) {
            return new BOBJKeyframe(Integer.parseInt(stringArray[1]), Float.parseFloat(stringArray[2]));
        }
        return null;
    }

    public static Interpolation interpolationFromString(String string) {
        if (string.equals("CONSTANT")) {
            return Interpolation.CONSTANT;
        }
        if (string.equals("BEZIER")) {
            return Interpolation.BEZIER;
        }
        return Interpolation.LINEAR;
    }

    public static float lerp(float f, float f2, float f3) {
        return f + (f2 - f) * f3;
    }

    public BOBJKeyframe(int n, float f) {
        this.frame = n;
        this.value = f;
    }

    public BOBJKeyframe(int n, float f, String string) {
        this(n, f);
        this.interpolation = BOBJKeyframe.interpolationFromString(string);
    }

    public BOBJKeyframe(int n, float f, String string, float f2, float f3, float f4, float f5) {
        this(n, f, string);
        this.leftX = f2;
        this.leftY = f3;
        this.rightX = f4;
        this.rightY = f5;
    }

    public float interpolate(float f, BOBJKeyframe keyframe) {
        return this.interpolation.interpolate(this, f, keyframe);
    }
}
