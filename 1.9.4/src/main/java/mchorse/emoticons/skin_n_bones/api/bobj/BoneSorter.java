package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.Comparator;

public class BoneSorter
        implements Comparator<BOBJBone> {
    final /* synthetic */ BOBJArmature armature;

    BoneSorter(BOBJArmature armature) {
        this.armature = armature;
    }

    public int compare(BOBJBone o1, BOBJBone o2) {
        return o1.index - o2.index;
    }

}

