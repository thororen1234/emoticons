package mchorse.emoticons.skin_n_bones.api.bobj;

public class Face {
	public IndexGroup[] idxGroups = new IndexGroup[3];

	public Face(String string, String string2, String string3) {
		this.idxGroups[0] = this.parseLine(string);
		this.idxGroups[1] = this.parseLine(string2);
		this.idxGroups[2] = this.parseLine(string3);
	}

	public Face() {
	}

	private IndexGroup parseLine(String string) {
		IndexGroup indexGroup = new IndexGroup();
		String[] stringArray = string.split("/");
		int n = stringArray.length;
		indexGroup.idxPos = Integer.parseInt(stringArray[0]) - 1;
		if (n > 1) {
			String string2 = stringArray[1];
			if (!string2.isEmpty()) {
				indexGroup.idxTextCoord = Integer.parseInt(string2) - 1;
			}
			if (n > 2) {
				indexGroup.idxVecNormal = Integer.parseInt(stringArray[2]) - 1;
			}
		}
		return indexGroup;
	}

	public Face add(int n, int n2, int n3) {
		Face face = new Face();
		for (int i = 0; i < face.idxGroups.length; ++i) {
			IndexGroup group = this.idxGroups[i];
			face.idxGroups[i] = new IndexGroup(group.idxPos + n, group.idxTextCoord + n3, group.idxVecNormal + n2);
		}
		return face;
	}
}
