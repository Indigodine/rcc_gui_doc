package gui.containers;

import gui.components.Img;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class SplashScreen extends JComponent {

	private Image noTick = Img.get("case");
	private Image tick = Img.get("caseTicked");
	private Image ganil = Img.get("logo");
	private static final Color errColor = Color.RED;
	private static final Color normalColor = Color.WHITE;
	private static final Color okColor = new Color(165, 255, 0); //Color.GREEN;
	private static final String[] progress = {
		"Connect to Run Control Core",
		"Load config",
		"Create experiment",
		"Run Control ready",
		"Preparing User Interface"
	};
	private boolean[] state = { false, false, false, false, false };
	private String error = "";
//	private Thread thread = loop();
	private int angle = 0;
	private static final int SIZE_X = 500;
	private static final int SIZE_Y = 300;
	

	public SplashScreen() {
		setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
//		thread.start();
	}

	public Thread loop() {
		return new Thread() {
			public void run() {
				while(true) {
					try{
						sleep(25);
					} catch(InterruptedException e) {}
					angle += 5;
					if(angle>=360)
						angle = 0;
					repaint();
				}
			}
		};
	}

	public void error(String error) {
		this.error = error;
		repaint();
	}

	public void step(int i, boolean val) {
		state[i] = val;
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(45, 200, 255));
		g.fillRect(0, 0, 500, 300);
		int posX = 10;
		int posY = 10;
		g.drawImage(ganil, (SIZE_X - ganil.getWidth(null)) /2, posY, null);
		posY += ganil.getHeight(null) + 20;
		Paint gradient = new GradientPaint(0, SIZE_Y, Color.BLACK, 0, 0, new Color(0, 0, 0, 0));
		g2.setPaint(gradient);
		g.fillRect(0, 0, SIZE_X, SIZE_Y);
		for(int i=0 ; i<5 ; i++) {
			g.drawImage( state[i] ? tick : noTick, posX, posY, null);
			g.setColor( (state[i] ? okColor : normalColor) );
			g.drawString(progress[i], posX+30, posY+18);
			posY+=30;
		}
		g.setColor(errColor);
		g.drawString(error, posX+30, posY+15);
		int r = 0;
		int gr = 255;
		int b = 195;
		int arcSize = 15;
		int arc = arcSize;
		if(error.length()>=1) {
			gr = b = 0;
			r = 200;
		}
/*
		for(int alpha=0 ; alpha<255 ; alpha+=10) {
			g.setColor(new Color(r, gr, b, alpha));
			g.fillArc(270, 100, 150, 150, -angle -arc, arcSize+1);
			arc+=arcSize;
		}
*/
		g.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3.0f));
		g.drawRect(0, 0, SIZE_X-2, SIZE_Y-2);
	}
}