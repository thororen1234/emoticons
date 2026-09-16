package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BOBJMesh {
    public String name;
    public List faces;
    public String armatureName;
    public BOBJArmature armature;

    public BOBJMesh(final String name) {
        this.faces = new ArrayList();
        this.name = name;
    }

    public BOBJMesh add(final int n, final int n2, final int n3) {
        final BOBJMesh BOBJMesh = new BOBJMesh(this.name);
        BOBJMesh.armatureName = this.armatureName;
        BOBJMesh.armature = this.armature;
        final Iterator iterator = this.faces.iterator();
        while (iterator.hasNext()) {
            BOBJMesh.faces.add(((Face) iterator.next()).add(n, n2, n3));
        }
        return BOBJMesh;
    }
}


