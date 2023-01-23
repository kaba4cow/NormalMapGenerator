package kaba4cow.nmg;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImagePanel extends JLabel {

	private static final long serialVersionUID = 1L;

	private static final Image DEFAULT_IMAGE = new Image(1, 1);

	private Image currentImage;

	public ImagePanel() {
		currentImage = DEFAULT_IMAGE;
	}

	public void update(Image image) {
		currentImage = image;
		image = Image.resizeTo(image, getSize().width, getSize().height);
		BufferedImage bufferedImage = image.getBufferedImage();
		setIcon(new ImageIcon(bufferedImage));
	}

	public void reset() {
		update(DEFAULT_IMAGE);
	}

	public boolean isDefault() {
		return currentImage == DEFAULT_IMAGE;
	}

	public Image getCurrentImage() {
		return currentImage;
	}

}
