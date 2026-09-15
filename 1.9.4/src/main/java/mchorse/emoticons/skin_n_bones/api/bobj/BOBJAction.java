package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.HashMap;
import java.util.Map;

public class BOBJAction {
	public String name;
	public Map<Object, BOBJGroup> groups;

	public BOBJAction(final String name) {
		this.groups = new HashMap<>();
		this.name = name;
	}

	public int getDuration() {
		int max = 0;
		for (BOBJGroup bobjGroup : this.groups.values()) {
			max = Math.max(max, bobjGroup.getDuration());
		}
		return max;
	}
}


