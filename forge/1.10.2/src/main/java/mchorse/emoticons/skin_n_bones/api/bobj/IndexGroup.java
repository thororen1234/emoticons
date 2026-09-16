package mchorse.emoticons.skin_n_bones.api.bobj;

public class IndexGroup {
    public static final int NO_VALUE = -1;
    public int idxPos = -1;
    public int idxTextCoord = -1;
    public int idxVecNormal = -1;

    public IndexGroup(int n, int n2, int n3) {
        this.idxPos = n;
        this.idxTextCoord = n2;
        this.idxVecNormal = n3;
    }

    public IndexGroup() {
    }
}
