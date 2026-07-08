package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.util.HashMap;

import core.Common;
import core.SMState;
import core.reseau.retourStateMachine;

public class ActivityIndicator extends Displayable {

	private static final long serialVersionUID = 1L;
	private int angle = 0;
	private static final Dimension SIZE = new Dimension(30, 30); // taille du radar
	private static final Dimension REAL_SIZE = new Dimension(200, 100);
	private Color fore = SMState.NOMONITORING.getColor();
	private int x=0;
	private int y=5;
	private boolean isMonitoring = false;
	private SMState smState = SMState.NOMONITORING;
	private int brightness = 0;
	private boolean incRed = true;
	private boolean error = false;
	private String transition = "";
	private boolean transClignote = false;
	private HashMap<SMState, Image> imagesEtat = new HashMap<SMState, Image>();
	private int askState = 0;

	public ActivityIndicator() {
		freq = 2;
		setPreferredSize(REAL_SIZE);
		setMaximumSize(REAL_SIZE);
		setMinimumSize(REAL_SIZE);
		x = (int)(REAL_SIZE.getWidth() - SIZE.getWidth()) / 2;
		for (SMState st : SMState.values()) imagesEtat.put(st,Img.get("eqt/" + st.name().toLowerCase()));

		new Thread() {
			public void run() {
				while (true) {
					refresh();
					try {sleep(freq * 100);} catch(InterruptedException e) {}
				}
			}
		}.start();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor( new Color(25, 25, 25) );
		g.fillRoundRect(0, 0, 200, 100, 20, 20);
	    FontMetrics metrics = g.getFontMetrics();
	    int posX = (int)(REAL_SIZE.getWidth() - metrics.stringWidth(smState.name())) /2;
	    if (brightness % 20 == 0) transClignote = !transClignote;
	    if (isMonitoring) {
		    Paint gradient = new GradientPaint(x, y, fore, x+30, y+30, fore.darker().darker());
	    	g2.setPaint(gradient);
			g.fillArc(x, y, (int)SIZE.getWidth(), (int)SIZE.getHeight(), angle, 60);
			g.fillArc(x, y, (int)SIZE.getWidth(), (int)SIZE.getHeight(), angle +120, 60);
			g.fillArc(x, y, (int)SIZE.getWidth(), (int)SIZE.getHeight(), angle +240, 60);

			gradient = new GradientPaint(x, y, Color.WHITE, x+30, y+30, Color.LIGHT_GRAY);
	    	g2.setPaint(gradient);
			g.fillArc(x, y, (int)SIZE.getWidth(), (int)SIZE.getHeight(), angle+60, 60);
			g.fillArc(x, y, (int)SIZE.getWidth(), (int)SIZE.getHeight(), angle+180, 60);
			g.fillArc(x, y, (int)SIZE.getWidth(), (int)SIZE.getHeight(), angle+300, 60);

			g.setColor(Color.BLACK);
			g.fillOval(x+5, y+5, 20, 20);
			g.setColor(new Color(fore.getRed(), fore.getGreen(), fore.getBlue(), brightness));
	    	g.fillOval(x+10, y+10, 10, 10);

	    	g.drawImage(imagesEtat.get(smState), 10, 40, null);
	    	g.setColor(Color.WHITE);
		    g.drawString(smState.name(), 30, 52);
//		    drawLEDError(g2);
		    if (transition.length() > 0) {
		    	String text = transition+" IN PROGRESS ...";
		    	int posY = 95;
			    g.setColor(Color.ORANGE);
			    posX = ((int)REAL_SIZE.getWidth() - 10 - metrics.stringWidth("<|> " + text + " <|>"))/2;
			    g.setColor(Color.RED);
			    g.drawString( transClignote ? " " : "<", posX, posY);
			    posX += metrics.stringWidth(">");
			    g.setColor(Color.ORANGE);
			    g.drawString("|", posX, posY);
			    posX += metrics.stringWidth("|");
			    g.setColor(Color.RED);
			    g.drawString( transClignote ? ">  " : "  ", posX, posY);
			    posX += metrics.stringWidth(">  ");
			    g.setColor(Color.ORANGE);
			    g.drawString(text, posX, posY);

			    posX += metrics.stringWidth(text);
			    g.setColor(Color.RED);
			    g.drawString( transClignote ? "  <" : " ", posX, posY);
			    posX += metrics.stringWidth("  <");
			    g.setColor(Color.ORANGE);
			    g.drawString("|", posX, posY);
			    posX += metrics.stringWidth("|");
			    g.setColor(Color.RED);
			    g.drawString( transClignote ? "" : ">", posX, posY);
		    }
	    } else {
	    	g.drawImage(imagesEtat.get(smState), 10, 40, null);
	    	g.setColor(Color.WHITE);
		    g.drawString("OFFLINE", 30, 52);
	    }
	}

	/**
	 * Dessine la diode 'ERROR'
	 * Qui s'allume si SMErro vaut 1
	 * @param g
	 */
	public void drawLEDError(Graphics2D g) {
		int posX = 10;
		int posY = 60;
		g.drawImage(imagesEtat.get( error ? SMState.ERROR : SMState.OFFLINE), posX, posY, null);
		g.setColor((error ? Color.RED : Color.GRAY));
		g.drawString("ERROR", posX + 20, posY + 12);
	}

	@Override
	public void refresh() {
		if (Common.stopMonitor) {
			isMonitoring = false;
			repaint();
			return;
		}
		if (Common.myClientSOAP != null) {
			if (askState == 10) {
				SMState gs = SMState.NOMONITORING;
				retourStateMachine stateMachine = Common.myClientSOAP.getStateMachine();
				gs = stateMachine.getSMState();
				error = !(stateMachine.getError() == 0);
				transition = stateMachine.getStringSMTransition();
				fore = gs.getColor();
				isMonitoring = ((gs != SMState.NOMONITORING) && (gs != SMState.OFFLINE));
				smState = gs;
				if (gs == SMState.NOMONITORING) isMonitoring = false;
				else isMonitoring = true;
				askState = 0;
			} else askState++;
		}
		angle = (angle + 10) % 360; // pour augmenter l'angle sans dépasser 360
		brightness = (incRed ? brightness+5 : brightness-5);
		if (brightness >= 250) incRed = false;
		else if (brightness <= 64) incRed = true;
		repaint();
	}

	public void setColor(Color c) {
		fore = c;
		repaint();
	}
}
