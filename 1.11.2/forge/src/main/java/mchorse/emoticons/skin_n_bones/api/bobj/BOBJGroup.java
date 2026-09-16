package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.ArrayList;
import java.util.List;

public class BOBJGroup {
    public String name;
    public List<BOBJChannel> channels;

    public BOBJGroup(final String name) {
        this.channels = new ArrayList<>();
        this.name = name;
    }

    public void apply(final BOBJBone BOBJBone, final float n) {
        for (BOBJChannel channel : this.channels) {
            channel.apply(BOBJBone, n);
        }
    }

    public void applyInterpolate(final BOBJBone BOBJBone, final float n, final float n2) {
        for (BOBJChannel channel : this.channels) {
            channel.applyInterpolate(BOBJBone, n, n2);
        }
    }

    public int getDuration() {
        int max = 0;
        for (final BOBJChannel channel : this.channels) {
            final int size = channel.keyframes.size();
            if (size > 0) {
                max = Math.max(max, channel.keyframes.get(size - 1).frame);
            }
        }
        return max;
    }
}


