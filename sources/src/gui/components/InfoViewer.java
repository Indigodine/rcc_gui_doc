package gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("serial")
public class InfoViewer extends Displayable {

	private ArrayList<Integer> debit = new ArrayList<Integer>();
	private ArrayList<Integer> tps = new ArrayList<Integer>();
	private int time = 1;
	private int tick = 0;
	private Random rand = new Random();
	private BufferedImage img;
	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	private static final int SPACE = 5; // espace entre les points
	private static final int START_X = 100;
	private static final int START_Y = 50;
	private int decX = 0;

	private int maximum = 0;
	private ArrayList<Integer> moyennes = new ArrayList<Integer>();

	public InfoViewer() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		tps.add(0);
		debit.add(0);
		this.freq = 1;
	}

	/**
	 * Synchronisé pour éviter accès concurrents
	 */
	synchronized public void paintComponent(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		int[] y = getYArray();
		int[] x = getXArray();
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		/*
		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.setColor(Color.BLACK);
		g.drawLine(START_X, 250-max + START_Y, WIDTH, 250-max + START_Y);
		g.drawLine(START_X, 250-min + START_Y, WIDTH, 250-min + START_Y);
		*/

		g.setColor(Color.GREEN);
		g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawPolyline(x, y, debit.size());
		g.setColor(new Color(0, 255, 128, 64));
		g.fillPolygon(getXArrayFill(), getYArrayFill(), debit.size()+2);
		g.setColor(Color.BLACK);
		float dash1[] = {5.0f};
		BasicStroke dashed =
	        new BasicStroke(1.0f,
	                        BasicStroke.CAP_BUTT,
	                        BasicStroke.JOIN_MITER,
	                        5.0f, dash1, 0.0f);
	    g2d.setStroke(dashed);
		dashed = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                5.0f, dash1, 0.0f);
		g2d.setStroke(dashed);
	    g.setFont(Font.decode("Courrier PLAIN 8"));

		int a=debit.size()-1;
		float scale = 250.0f / maximum;
		int posX = tps.get(a)*SPACE + START_X - decX;
		int posY = (int)(270 - debit.get(a) * scale) + START_Y; //270 - debit.get(a)/2 + START_Y;		
		
		g.drawString(debit.get(a)+"ko/s", posX, posY);
		posY = (int)(250 - debit.get(a) * scale) + START_Y;
		g.fillOval(posX-2, posY, 5, 5);

		g.setFont(new Font("Courrier", Font.BOLD, 18));
		g.drawString(debit.get(a)+"ko/s", 250, 500);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 68, 600);
		

	    int palier = 0;
		for(int cpt=0 ; cpt<=500 ; cpt+=50) {
			g.setColor(Color.GRAY);
			g.drawLine(70, 250 - cpt/2 + START_Y, WIDTH, 250 - cpt/2 + START_Y);
			g.setColor(Color.DARK_GRAY);
			float val = maximum * (palier/10.0f);
			g.drawString( (int)val  + "", 5, 250 - cpt/2 + START_Y);
			palier++;
		}
		g.setColor(Color.GRAY);
		for(int temps=0 ; temps<500 ; temps+=SPACE*15) {
			g.drawLine(START_X + temps, 250 + START_Y, START_X + temps, START_Y);
		}
	}

	/**
	 * @return le débit maximum observé
	 */
	public int max() {
		int max = 0;
		for(int i : debit) {
			max = Math.max(max, i);
		}
		maximum = max;
		return max;
	}

	/**
	 * @return le débit minimum observé
	 */
	public int min() {
		int min = max();
		int index=0;
		for(int i : debit) {
			if(index==0) {
				index++;
				continue;
			} else min = Math.min(min, i);
		}
		return min;
	}

	/**
	 * @return Le débit moyen observé
	 */
	public int moy() {
		
		int cpt = 0;
		int index=0;
		for(int i : debit) {
			if(index==0) {
				index++;
				continue;
			}else cpt += i;
		}
		return cpt / debit.size()-1;
		

		/* Moyenne instantannée sur les 3 derniers débits
		int cpt = 0;
		int nb = debit.size()-1;
		if(nb>=4) {
			cpt += debit.get(nb);
			cpt += debit.get(nb-1);
			cpt += debit.get(nb-2);
		}
		return cpt / 3;
		*/
	}

	

	/**
	 * @return un tableau d'int représentant les positions X
	 * de l'axe du temps
	 */
	public int[] getXArray() {
		int[] x = new int[tps.size()];
		int a=0;
		for(int i : tps) {
			x[a] = i*SPACE + START_X - decX;
			a++;
		}
		return x;
	}

	/**
	 * @return un tableau d'int représentant les positions Y
	 * sur l'axe du débit
	 */
	public int[] getYArray() {
		int[] y = new int[debit.size()];
		int a=0;
		float scale = 250.0f / maximum;
		for(int i : debit) {
			//y[a] = 250 - i/2 + START_Y;
			y[a] = (int)(250 - i * scale) + START_Y;
			a++;
		}
		return y;
	}

	/**
	 * 
	 * @return Renvoie un tableau de positions X pour dessiner le fond du graphique
	 */
	public int[] getXArrayFill() {
		int[] x = new int[tps.size()+2];
		int a=0;
		for(int i : tps) {
			x[a] = i*SPACE + START_X - decX;
			a++;
		}
		x[a] = x[a-1];
		a++;
		x[a] = x[0];
		return x;
	}

	/**
	 * 
	 * @return Renvoie un tableau de positions Y pour dessiner le fond du graphique
	 */
	public int[] getYArrayFill() {
		int[] y = new int[debit.size()+2];
		int a=0;
		float scale = 250.0f / maximum;
		for(int i : debit) {
			//y[a] = 250 - i/2 + START_Y;
			y[a] = (int)(250 - i * scale) + START_Y;
			a++;
		}
		y[a] = 250 + START_Y;
		a++;
		y[a] = 250 + START_Y;
		return y;
	}

	@Override
	synchronized public void refresh() {
		time++;
		tps.add(time);
		int ko = 250 + rand.nextInt(50);
		debit.add(ko);
		maximum = Math.max(maximum, ko);
		repaint();
		if(time*SPACE+START_X>WIDTH) {
			WIDTH += 100;
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			updateUI();
		}
		if(time*SPACE > 600)
			decX+=SPACE;
	}
}
