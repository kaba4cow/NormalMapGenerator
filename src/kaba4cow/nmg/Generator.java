package kaba4cow.nmg;

public final class Generator {

	private static boolean INVERT_HEIGHT;
	private static boolean INVERT_BIAS;
	private static float BIAS;
	private static float HEIGHT;

	private Generator() {

	}

	public static Image generate(Image diffuse, float height, float bias, boolean invertHeight, boolean invertBias,
			int blur) {
		INVERT_HEIGHT = invertHeight;
		INVERT_BIAS = invertBias;
		BIAS = bias;
		HEIGHT = 2f / height;

		int imageWidth = diffuse.getWidth();
		int imageHeight = diffuse.getHeight();

		Image normal = new Image(imageWidth, imageHeight);

		int x, y;
		float heightL, heightR, heightD, heightU;
		float nX, nY, nZ;
		float invLength;
		int r, g, b, a;
		int pixel;

		for (x = 0; x < imageWidth; x++)
			for (y = 0; y < imageHeight; y++) {
				heightL = getHeight(diffuse, x - 1, y);
				heightR = getHeight(diffuse, x + 1, y);
				heightD = getHeight(diffuse, x, y - 1);
				heightU = getHeight(diffuse, x, y + 1);

				nX = heightL - heightR;
				nY = HEIGHT;
				nZ = heightD - heightU;
				invLength = 1f / length(nX, nY, nZ);
				nX = convert(nX * invLength);
				nY = convert(nY * invLength);
				nZ = convert(nZ * invLength);

				r = (int) (0xFF * nX);
				g = (int) (0xFF * nZ);
				b = (int) (0xFF * nY);
				a = diffuse.getA(x, y);

				pixel = Util.create(r, g, b, a);
				normal.setPixel(x, y, pixel);
			}

		return normal.blur(blur);
	}

	private static float length(float x, float y, float z) {
		float dist = x * x + y * y + z * z;
		return (float) Math.sqrt(dist);
	}

	private static float getHeight(Image img, int x, int y) {
		int pixel = img.getPixel(x, y);
		float r = Util.getR(pixel);
		float g = Util.getG(pixel);
		float b = Util.getB(pixel);
		float value = (r + g + b) / (3f * 255f);
		return processHeight(value);
	}

	private static float processHeight(float value) {
		if (INVERT_HEIGHT)
			value = 1f - value;
		if (INVERT_BIAS)
			value = 1f - Util.bias(1f - value, BIAS);
		else
			value = Util.bias(value, BIAS);
		return value;
	}

	private static float convert(float value) {
		return 0.5f * (value + 1f);
	}

}
