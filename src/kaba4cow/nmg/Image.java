package kaba4cow.nmg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {

	protected int width;
	protected int height;
	protected int[] pixels;

	public Image(File file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = image.getWidth();
		height = image.getHeight();
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		image.flush();
	}

	public Image(int width, int height) {
		this.pixels = new int[width * height];
		this.width = width;
		this.height = height;
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0xFF000000;
	}

	public BufferedImage getBufferedImage() {
		BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		dest.setRGB(0, 0, width, height, pixels, 0, width);
		return dest;
	}

	public int getPixel(int x, int y) {
		x = width + (x % width);
		x %= width;
		y = height + (y % height);
		y %= height;
		int index = y * width + x;
		int color = pixels[index];
		return color;
	}

	public void setPixel(int x, int y, int color) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		int index = y * width + x;
		pixels[index] = color;
	}

	public void setPixel(int index, int color) {
		if (index < 0 || index >= pixels.length)
			return;
		pixels[index] = color;
	}

	public int getA(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 24) & 0xFF;
		return value;
	}

	public int getR(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 16) & 0xFF;
		return value;
	}

	public int getG(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 8) & 0xFF;
		return value;
	}

	public int getB(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 0) & 0xFF;
		return value;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public Image blur(int d) {
		if (d == 0)
			return this;
		Image res = new Image(width, height);

		int size = 2 * d + 1;
		float v = 1f / (float) (size * size);

		int srcColor = 0;
		int curColor = 0;
		float r = 0f;
		float g = 0f;
		float b = 0f;
		float a = 0f;
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				srcColor = getPixel(x, y);
				r = 0f;
				g = 0f;
				b = 0f;
				a = Util.getA(srcColor);

				for (int cy = y - d; cy <= y + d; cy++) {
					for (int cx = x - d; cx <= x + d; cx++) {
						curColor = getPixel(cx, cy);
						r += v * Util.getR(curColor);
						g += v * Util.getG(curColor);
						b += v * Util.getB(curColor);
					}
				}

				res.setPixel(x, y, Util.create((int) r, (int) g, (int) b, (int) a));
			}
		}
		return res;
	}

	public int sample(float sampleX, float sampleY) {
		sampleX = Math.abs(sampleX);
		sampleX = sampleX - (int) sampleX;
		sampleY = Math.abs(sampleY);
		sampleY = sampleY - (int) sampleY;
		int x = (int) (width * sampleX);
		int y = (int) (height * sampleY);
		return pixels[y * width + x];
	}

	public static Image resize(Image src, int targetWidth, int targetHeight) {
		Image res = new Image(targetWidth, targetHeight);
		float dX = 1f / (float) targetWidth;
		float dY = 1f / (float) targetHeight;
		int index = 0;
		for (int y = 0; y < targetHeight; y++)
			for (int x = 0; x < targetWidth; x++)
				res.setPixel(index++, src.sample(x * dX, y * dY));
		return res;
	}

	public static Image resizeTo(Image src, int width, int height) {
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();

		float widthScale = (float) width / (float) srcWidth;
		float heightScale = (float) height / (float) srcHeight;

		float scale = Math.min(widthScale, heightScale);

		int resWidth = (int) (srcWidth * scale);
		int resHeight = (int) (srcHeight * scale);

		Image resized = resize(src, resWidth, resHeight);

		Image frame = new Image(width, height);
		drawImage(resized, width / 2 - resWidth / 2, height / 2 - resHeight / 2, frame);

		return frame;
	}

	private static void drawImage(Image image, int offX, int offY, Image target) {
		int newX = 0;
		int newY = 0;
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();
		for (int y = newY; y < newHeight; y++)
			for (int x = newX; x < newWidth; x++) {
				int color = image.getPixel(x, y);
				target.setPixel(x + offX, y + offY, color);
			}
	}

}
