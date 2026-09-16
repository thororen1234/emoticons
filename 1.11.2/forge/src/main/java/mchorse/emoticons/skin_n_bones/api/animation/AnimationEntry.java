package mchorse.emoticons.skin_n_bones.api.animation;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJData;

import java.io.File;

public class AnimationEntry {
    public Animation animation;
    public File directory;
    public long lastModified;

    public AnimationEntry(Animation animation, File directory, long lastModified) {
        this.animation = animation;
        this.directory = directory;
        this.lastModified = lastModified;
    }

    public void reloadAnimation(BOBJData data, long lastModified) {
        this.animation.reload(data);
        this.lastModified = lastModified;
    }
}
