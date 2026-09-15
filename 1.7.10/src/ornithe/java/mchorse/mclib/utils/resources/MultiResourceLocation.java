package mchorse.mclib.utils.resources;

import com.google.common.base.Objects;
import net.minecraft.client.resource.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MultiResourceLocation
		extends Identifier {
	public List<Identifier> children = new ArrayList();

	public MultiResourceLocation(String string) {
		super("it_would_be_very_ironic:if_this_would_match_with_regular_rls");
		this.children.add(RLUtils.create(string));
	}

	public MultiResourceLocation(String string, String string2) {
		super("it_would_be_very_ironic", "if_this_would_match_with_regular_rls");
		this.children.add(RLUtils.create(string, string2));
	}
	public String getResourceDomain() {
		return this.children.isEmpty() ? "" : this.children.get(0).getNamespace();
	}
	public String getPath() {
		return this.children.isEmpty() ? "" : this.children.get(0).getPath();
	}
	public String toString() {
		return this.getNamespace() + ":" + this.getPath();
	}
	public boolean equals(Object object) {
		if (object instanceof MultiResourceLocation) {
			MultiResourceLocation other = (MultiResourceLocation) object;
			boolean equal = this.children.size() == other.children.size();

			if (equal) {
				for (int i = 0; i < this.children.size(); ++i) {
					equal = equal && Objects.equal(this.children.get(i), other.children.get(i));
				}
			}

			return equal;
		}
		return super.equals(object);
	}
	public int hashCode() {
		int n = super.hashCode();
		int n2 = this.children.size();
		for (int i = 0; i < n2; ++i) {
			n = 31 * n + this.children.hashCode();
		}
		return n;
	}
}

