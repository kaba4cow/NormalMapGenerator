package kaba4cow.nmg;

import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends JFrame implements DropTargetListener {

	private static final long serialVersionUID = 1L;

	private static final int IMAGE_SIZE = 300;

	private static final float MIN_HEIGHT = 0.1f;
	private static final float MAX_HEIGHT = 10f;

	private static final float MIN_BIAS = 0f;
	private static final float MAX_BIAS = 1f;

	private static final int MIN_BLUR = 0;
	private static final int MAX_BLUR = 2;

	private JButton loadButton;
	private JButton saveButton;

	private JSpinner heightSpinner;
	private JCheckBox invertHeightCheckbox;
	private JSpinner biasSpinner;
	private JCheckBox invertBiasCheckbox;
	private JSpinner blurSpinner;
	private JButton randomizeButton;

	private ImagePanel diffuseImage;
	private ImagePanel normalImage;

	private boolean listen;
	private ChangeListener changeListener;
	private ItemListener itemListener;

	private String lastLoadDirectory;
	private String lastSaveDirectory;

	public Main() {
		lastLoadDirectory = System.getProperty("user.dir");
		lastSaveDirectory = System.getProperty("user.dir");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		final int SIZE = 2 * IMAGE_SIZE;

		setTitle("Normal Map Generator");
		setSize(SIZE, SIZE);
		setLayout(null);

		final int WIDTH2 = SIZE / 2;
		final int HEIGHT2 = (SIZE - WIDTH2) / 5;

		listen = false;
		changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (listen)
					updateImage();
			}
		};
		itemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (listen)
					updateImage();
			}
		};

		loadButton = new JButton("Load");
		loadButton.setLocation(0 * WIDTH2, 0 * HEIGHT2);
		loadButton.setSize(WIDTH2, HEIGHT2);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});

		saveButton = new JButton("Save");
		saveButton.setLocation(1 * WIDTH2, 0 * HEIGHT2);
		saveButton.setSize(WIDTH2, HEIGHT2);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});

		JPanel heightPanel = new JPanel();
		heightPanel.setLocation(0 * WIDTH2, 1 * HEIGHT2);
		heightPanel.setSize(WIDTH2, HEIGHT2);
		heightPanel.setLayout(new GridLayout(1, 2));
		JLabel heightLabel = new JLabel("Height:");
		heightLabel.setHorizontalAlignment(SwingConstants.CENTER);
		heightSpinner = new JSpinner(new SpinnerNumberModel(1f, MIN_HEIGHT, MAX_HEIGHT, 0.05f));
		heightSpinner.addChangeListener(changeListener);
		heightPanel.add(heightLabel);
		heightPanel.add(heightSpinner);

		invertHeightCheckbox = new JCheckBox("Invert height");
		invertHeightCheckbox.setLocation(1 * WIDTH2, 1 * HEIGHT2);
		invertHeightCheckbox.setSize(WIDTH2, HEIGHT2);
		invertHeightCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
		invertHeightCheckbox.addItemListener(itemListener);

		JPanel biasPanel = new JPanel();
		biasPanel.setLocation(0 * WIDTH2, 2 * HEIGHT2);
		biasPanel.setSize(WIDTH2, HEIGHT2);
		biasPanel.setLayout(new GridLayout(1, 2));
		JLabel biasLabel = new JLabel("Bias:");
		biasLabel.setHorizontalAlignment(SwingConstants.CENTER);
		biasSpinner = new JSpinner(new SpinnerNumberModel(0f, MIN_BIAS, MAX_BIAS, 0.01f));
		biasSpinner.addChangeListener(changeListener);
		biasPanel.add(biasLabel);
		biasPanel.add(biasSpinner);

		invertBiasCheckbox = new JCheckBox("Invert bias");
		invertBiasCheckbox.setLocation(1 * WIDTH2, 2 * HEIGHT2);
		invertBiasCheckbox.setSize(WIDTH2, HEIGHT2);
		invertBiasCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
		invertBiasCheckbox.addItemListener(itemListener);

		JPanel blurPanel = new JPanel();
		blurPanel.setLocation(0 * WIDTH2, 3 * HEIGHT2);
		blurPanel.setSize(WIDTH2, HEIGHT2);
		blurPanel.setLayout(new GridLayout(1, 2));
		JLabel blurLabel = new JLabel("Blur:");
		blurLabel.setHorizontalAlignment(SwingConstants.CENTER);
		blurSpinner = new JSpinner(new SpinnerNumberModel(0, MIN_BLUR, MAX_BLUR, 1));
		blurSpinner.addChangeListener(changeListener);
		blurPanel.add(blurLabel);
		blurPanel.add(blurSpinner);

		randomizeButton = new JButton("Randomize");
		randomizeButton.setLocation(1 * WIDTH2, 3 * HEIGHT2);
		randomizeButton.setSize(WIDTH2, HEIGHT2);
		randomizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomize();
			}
		});

		diffuseImage = new ImagePanel();
		diffuseImage.setLocation(0 * WIDTH2, 4 * HEIGHT2);
		diffuseImage.setSize(IMAGE_SIZE, IMAGE_SIZE);
		diffuseImage.reset();

		normalImage = new ImagePanel();
		normalImage.setLocation(1 * WIDTH2, 4 * HEIGHT2);
		normalImage.setSize(IMAGE_SIZE, IMAGE_SIZE);
		normalImage.reset();

		add(loadButton);
		add(saveButton);
		add(heightPanel);
		add(invertHeightCheckbox);
		add(biasPanel);
		add(invertBiasCheckbox);
		add(blurPanel);
		add(randomizeButton);
		add(diffuseImage);
		add(normalImage);

		new DropTarget(this, this);

		listen = true;
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private void loadFile(File file) {
		if (!file.getName().toLowerCase().endsWith(".png"))
			return;
		diffuseImage.update(new Image(file));
		updateImage();
	}

	private void loadFile() {
		File directory = new File(lastLoadDirectory);
		JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG image", "png");
		fileChooser.addChoosableFileFilter(filter);
		int i = fileChooser.showOpenDialog(this);
		if (i == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				lastLoadDirectory = file.getParentFile().getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadFile(file);
		}
	}

	private void saveFile() {
		if (diffuseImage.isDefault() || normalImage.isDefault())
			return;

		File directory = new File(lastSaveDirectory);
		JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG image", "png");
		fileChooser.addChoosableFileFilter(filter);
		int i = fileChooser.showSaveDialog(this);
		if (i == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".png"))
				file = new File(file.getAbsolutePath() + ".png");
			try {
				lastSaveDirectory = file.getParentFile().getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				BufferedImage bufferedImage = normalImage.getCurrentImage().getBufferedImage();
				ImageIO.write(bufferedImage, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void randomize() {
		float height = Util.randomFloat(MIN_HEIGHT, MAX_HEIGHT);
		float bias = Util.randomFloat(MIN_BIAS, MAX_BIAS);
		int blur = Util.randomInt(MIN_BLUR, MAX_BLUR + 1);
		boolean invertHeight = Util.randomBoolean();
		boolean invertBias = Util.randomBoolean();

		listen = false;
		heightSpinner.setValue(height);
		biasSpinner.setValue(bias);
		blurSpinner.setValue(blur);
		invertHeightCheckbox.setSelected(invertHeight);
		invertBiasCheckbox.setSelected(invertBias);
		listen = true;
		updateImage();
	}

	private void updateImage() {
		if (diffuseImage.isDefault())
			return;
		Image diffuse = diffuseImage.getCurrentImage();
		Image normal = Generator.generate(diffuse, getHeightValue(), getBiasValue(), getInvertHeightValue(),
				getInvertBiasValue(), getBlurValue());
		normalImage.update(normal);

		normal = normalImage.getCurrentImage();
	}

	private float getHeightValue() {
		return ((Number) heightSpinner.getValue()).floatValue();
	}

	private float getBiasValue() {
		return ((Number) biasSpinner.getValue()).floatValue();
	}

	private int getBlurValue() {
		return ((Number) blurSpinner.getValue()).intValue();
	}

	private boolean getInvertHeightValue() {
		return invertHeightCheckbox.isSelected();
	}

	private boolean getInvertBiasValue() {
		return invertBiasCheckbox.isSelected();
	}

	public static void main(String[] args) {
		new Main();
	}

	@Override
	public void drop(DropTargetDropEvent event) {
		event.acceptDrop(DnDConstants.ACTION_COPY);

		Transferable transferable = event.getTransferable();
		DataFlavor[] flavors = transferable.getTransferDataFlavors();

		for (DataFlavor flavor : flavors) {
			try {
				if (flavor.isFlavorJavaFileListType()) {
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) transferable.getTransferData(flavor);
					loadFile(files.get(0));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent event) {

	}

	@Override
	public void dragOver(DropTargetDragEvent event) {

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent event) {

	}

	@Override
	public void dragExit(DropTargetEvent event) {

	}

}
