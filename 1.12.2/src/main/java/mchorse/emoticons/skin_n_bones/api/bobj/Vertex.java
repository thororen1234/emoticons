package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
	public float x;
	public float y;
	public float z;
	public List<Weight> weights;

	public Vertex(final float x, final float y, final float z) {
		this.weights = new ArrayList<>();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void eliminateTinyWeights() {
		this.weights.removeIf(weight -> weight.index < 0.05);
		if (this.weights.size() > 0) {
			float n = 0.0f;
			for (Weight weight : this.weights) {
				n += weight.index;
			}
			if (n < 1.0f) {
				final Weight Weight = this.weights.get(this.weights.size() - 1);
				Weight.index += 1.0f - n;
			}
		}
	}
}


