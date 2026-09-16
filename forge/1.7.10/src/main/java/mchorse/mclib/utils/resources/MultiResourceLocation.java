package mchorse.mclib.utils.resources;

import com.google.common.base.Objects;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MultiResourceLocation
        extends ResourceLocation {
    public List<ResourceLocation> children = new ArrayList();

    public MultiResourceLocation(String string) {
        super("it_would_be_very_ironic:if_this_would_match_with_regular_rls");
        this.children.add(RLUtils.create(string));
    }

    public MultiResourceLocation(String string, String string2) {
        super("it_would_be_very_ironic", "if_this_would_match_with_regular_rls");
        this.children.add(RLUtils.create(string, string2));
    }

    @Override
    public String getResourceDomain() {
        return this.children.isEmpty() ? "" : this.children.get(0).getResourceDomain();
    }

    @Override
    public String getResourcePath() {
        return this.children.isEmpty() ? "" : this.children.get(0).getResourcePath();
    }

    @Override
    public String toString() {
        return this.getResourceDomain() + ":" + this.getResourcePath();
    }

    @Override
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

    @Override
    public int hashCode() {
        int n = super.hashCode();
        int n2 = this.children.size();
        for (int i = 0; i < n2; ++i) {
            n = 31 * n + this.children.hashCode();
        }
        return n;
    }
}
