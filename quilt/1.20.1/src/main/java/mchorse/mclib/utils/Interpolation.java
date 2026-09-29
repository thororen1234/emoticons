package mchorse.mclib.utils;

public enum Interpolation {
	LINEAR("LINEAR", 0, "linear") {
		public float interpolate(final float n, final float n2, final float n3) {
			return Interpolations.lerp(n, n2, n3);
		}
	},
	QUAD_IN("QUAD_IN", 1, "quad_in") {
		public float interpolate(final float n, final float n2, final float n3) {
			return n + (n2 - n) * n3 * n3;
		}
	},
	QUAD_OUT("QUAD_OUT", 2, "quad_out") {
		public float interpolate(final float n, final float n2, final float n3) {
			return n - (n2 - n) * n3 * (n3 - 2.0f);
		}
	},
	QUAD_INOUT("QUAD_INOUT", 3, "quad_inout") {
		public float interpolate(final float n, final float n2, float n3) {
			n3 *= 2.0f;
			if (n3 < 1.0f) {
				return n + (n2 - n) / 2.0f * n3 * n3;
			}
			--n3;
			return n - (n2 - n) / 2.0f * (n3 * (n3 - 2.0f) - 1.0f);
		}
	},
	CUBIC_IN("CUBIC_IN", 4, "cubic_in") {
		public float interpolate(final float n, final float n2, final float n3) {
			return n + (n2 - n) * n3 * n3 * n3;
		}
	},
	CUBIC_OUT("CUBIC_OUT", 5, "cubic_out") {
		public float interpolate(final float n, final float n2, float n3) {
			--n3;
			return n + (n2 - n) * (n3 * n3 * n3 + 1.0f);
		}
	},
	CUBIC_INOUT("CUBIC_INOUT", 6, "cubic_inout") {
		public float interpolate(final float n, final float n2, float n3) {
			n3 *= 2.0f;
			if (n3 < 1.0f) {
				return n + (n2 - n) / 2.0f * n3 * n3 * n3;
			}
			n3 -= 2.0f;
			return n + (n2 - n) / 2.0f * (n3 * n3 * n3 + 2.0f);
		}
	},
	EXP_IN("EXP_IN", 7, "exp_in") {
		public float interpolate(final float n, final float n2, final float n3) {
			return n + (n2 - n) * (float) Math.pow(2.0, 10.0f * (n3 - 1.0f));
		}
	},
	EXP_OUT("EXP_OUT", 8, "exp_out") {
		public float interpolate(final float n, final float n2, final float n3) {
			return n + (n2 - n) * (float) (-Math.pow(2.0, -10.0f * n3) + 1.0);
		}
	},
	EXP_INOUT("EXP_INOUT", 9, "exp_inout") {
		public float interpolate(final float n, final float n2, float n3) {
			if (n3 == 0.0f) {
				return n;
			}
			if (n3 == 1.0f) {
				return n2;
			}
			n3 *= 2.0f;
			if (n3 < 1.0f) {
				return n + (n2 - n) / 2.0f * (float) Math.pow(2.0, 10.0f * (n3 - 1.0f));
			}
			--n3;
			return n + (n2 - n) / 2.0f * (float) (-Math.pow(2.0, -10.0f * n3) + 2.0);
		}
	};

	public final String key;

	private Interpolation(final String name, final int ordinal, final String key) {
		this.key = key;
	}

	public abstract float interpolate(final float p0, final float p1, final float p2);
}
