package gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ImagePanel extends JComponent {

	private Image img = null;
	private int sizeX;
	private int sizeY;

	public ImagePanel(int sizeX, int sizeY) {
		setPreferredSize(new Dimension(sizeX, sizeY));
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(img, 0, 0, sizeX, sizeY, null);
	}
}
