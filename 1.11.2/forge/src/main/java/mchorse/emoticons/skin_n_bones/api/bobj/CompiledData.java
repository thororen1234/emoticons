package mchorse.emoticons.skin_n_bones.api.bobj;

public class CompiledData {
    public float[] posData;
    public float[] texData;
    public float[] normData;
    public float[] weightData;
    public int[] boneIndexData;
    public int[] indexData;
    public BOBJMesh mesh;

    public CompiledData(float[] posData, float[] texData, float[] normData, float[] weightData,
                        int[] boneIndexData, int[] indexData, BOBJMesh mesh) {
        this.posData = posData;
        this.texData = texData;
        this.normData = normData;
        this.weightData = weightData;
        this.boneIndexData = boneIndexData;
        this.indexData = indexData;
        this.mesh = mesh;
    }
}
