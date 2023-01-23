package kaba4cow.nmg;

public final class Util {

	private Util() {

	}

	public static int create(int red, int green, int blue, int alpha) {
		return ((alpha << 24) & 0xFF000000) | ((red << 16) & 0xFF0000) | ((green << 8) & 0xFF00) | ((blue << 0) & 0xFF);
	}

	public static int getA(int color) {
		return (color >> 24) & 0xFF;
	}

	public static int getR(int color) {
		return (color >> 16) & 0xFF;
	}

	public static int getG(int color) {
		return (color >> 8) & 0xFF;
	}

	public static int getB(int color) {
		return (color >> 0) & 0xFF;
	}

	public static float bias(float x, float bias) {
		if (bias < 0f)
			bias = 0f;
		else if (bias > 1f)
			bias = 1f;
		float k = (float) Math.pow(1f - bias, 3f);
		return (x * k) / (x * k - x + 1f);
	}

	public static boolean randomBoolean() {
		return randomFloat(0f, 1f) < 0.5f;
	}

	public static int randomInt(int min, int max) {
		return (int) randomFloat(min, max);
	}

	public static float randomFloat(float min, float max) {
		return min + (max - min) * ((float) Math.random());
	}

}
