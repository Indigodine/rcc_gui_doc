package gui.containers;

import gui.Factory;
import gui.components.Displayable;
import gui.components.EquipementBar;
import gui.components.EquipementUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
//import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.JRadioButtonMenuItem;

import core.Case;
import core.Common;
import core.equipement.Actor;
import core.equipement.Equipement;
import core.equipement.EquipementType;
import core.equipement.LienEqt;
import core.reseau.Communication;

/**
 * Affiche une grille des equipements
 * Gestion du Drag'&'Drop
 * Possibilité d'ajout par plusieurs (en faisant glisser la souris)
 * ...
 * @author malassigne
 *
 */
public class Grid extends Displayable implements MouseMotionListener, MouseListener{

	private static final long serialVersionUID = 1L;
	private int focusX = 0; // position X de la case sous la souris
	private int focusY = 0; // position Y de la case sous la souris
	private boolean paintGrid = true; // la grille doit-elle est dessinée ?

	private ConcurrentHashMap<EquipementUI, Case> equipements = new ConcurrentHashMap<EquipementUI, Case>(); // liste des équipements avec leurs positions
	private HashMap<String, EquipementUI> equipsByNames = new HashMap<String, EquipementUI>(); // liste des équips avec leur nom en clé

	private ArrayList<LienEqt> liens = new ArrayList<LienEqt>();
	private EquipementUI focus = null; // l'équipementUI qui a le focus souris
	private int caseSize = 62; // taille des cases
	private Window frame;
	private EquipementBar eqtBar; // la barre d'outils qui permet de choisir un type d'équipement
	private int nbCasesX = 100; // nombre de cases en X
	private int nbCasesY = 100; // nombre de cases en Y
	private Dimension gridSize = new Dimension(nbCasesX*caseSize, nbCasesY*caseSize); // Dimension en px de la grille
	private String strSize = "NORMAL"; // <BIG/NORMAL/*> 
	private boolean hasChanged = false; // la grille a-t-elle subit une modification qui nécessite un repaint?
	private TreeEquipement tree; // l'arbre d'équipements
	private ArrayList<Case> casesAjout = new ArrayList<Case>(); // liste des cases où seront ajoutés les équipements
	private JButton ajouterEqt = new JButton("OK"); // pour ajouter plusieurs occurences de l'équipement sélectionné dans les cases
	private JButton cancel = new JButton("Cancel"); // annule une action d'ajout

	private static final Color ADD_COLOR = new Color(0, 175, 195, 128); // Couleur des cases où seront ajouté les équipements
	private static final Color FOCUS_COLOR = new Color(35, 35, 35, 160); // Couleur de la case qui a le focus souris
	private static final Color GRADIENT_1 = new Color(0, 0, 0, 255); // Couleur du début de dégradé de la case en mode Ajout
	private static final Color GRADIENT_2 = new Color(255, 255, 255, 0); // Couleur de fin de dégradé de la case en mode Ajout

	private boolean waiting = false; // Définit si la grille attend quelque chose (instructions serveur lentes par ex.)

	private JScrollPane parent = null;
	private boolean showHelp = false;

	private String eqtSource = null;
	private String eqtTarget = null;

	private boolean isLinkValid = false; // définit si le lien en cours de création est conforme

	private Color[] colors = {
//			new Color(255, 0, 0, 125),
			new Color(255, 125, 0, 125),
			new Color(255, 255, 0, 125),
			new Color(0, 255, 0, 125),
			new Color(0, 255, 255, 125),
			new Color(0, 0, 255, 125),
			new Color(125, 0, 255, 125),
			new Color(255, 0, 255, 125),
			new Color(125, 125, 125, 125)
	};

	class ZoneNarval {
		private int startX = Integer.MAX_VALUE;
		private int startY = Integer.MAX_VALUE;
		private int endX = -1;
		private int endY = -1;
		public ZoneNarval() {
		}
		public void addPos(int x, int y) {
			startX = Math.min(startX, x);
			endX = Math.max(endX, x + caseSize);
			startY = Math.min(startY, y);
			endY = Math.max(endY, y + caseSize);
		}
		public int getX() {
			return startX;
		}
		public int getY() {
			return startY;
		}
		public int sizeX() {
			return endX - startX;
		}
		public int sizeY() {
			return endY - startY;
		}
	}

	public Grid(Window f, EquipementBar bar,TreeEquipement t) {
		frame = f;
		Factory.grid = this;
		eqtBar = bar;
		tree = t;
		addMouseMotionListener(this);
		addMouseListener(this);
		setPreferredSize(gridSize);
		setLayout(null);
		add(ajouterEqt);
		ajouterEqt.setFocusPainted(false);
		add(cancel);
		cancel.setFocusPainted(false);
		ajouterEqt.setBackground(Color.GREEN.darker());
		cancel.setBackground(Color.RED.darker());
		cancel.setFont(Font.decode("Courrier PLAIN 8"));
		ajouterEqt.setFont(Font.decode("Courrier PLAIN 8"));
		cancel.setForeground(Color.WHITE);
		ajouterEqt.setForeground(Color.WHITE);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancel.setBounds(0,0,0,0);
				ajouterEqt.setBounds(0,0,0,0);
				casesAjout.clear();
				repaint();
			}
		});
		ajouterEqt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				final JComboBox box = new JComboBox();
				final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
				JButton ok = new JButton("OK");
				box.setModel(mdc);
				mdc.addElement("Electronics Control Core");
				mdc.addElement("MIDAS");
				mdc.addElement(Common.getString("VMECOM"));
				mdc.addElement(Common.getString("SbufProducer"));
				mdc.addElement(Common.getString("EventBuilder"));
				mdc.addElement(Common.getString("EbyedatWatcher"));
				mdc.addElement(Common.getString("Storage"));
				mdc.addElement(Common.getString("MfmWatcher"));
				mdc.addElement(Common.getString("MfmStorage"));
				mdc.addElement(Common.getString("TemplateActor"));
				mdc.addElement(Common.getString("Actor"));
				mdc.addElement(Common.getString("RikenTransmitter"));
				panel.add(box);
				panel.add(ok);
				final JDialog dialog = new JDialog(frame);
				dialog.setTitle(Common.getString("Choose_Type"));
				dialog.setContentPane(panel);
				dialog.setLocation(frame.getWidth()/4,frame.getHeight()/4);
				dialog.pack();
				dialog.setVisible(true);

				ok.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						int rep = box.getSelectedIndex();
						switch(rep) {
						case 0 :
							Factory.ECCPanelCreation(casesAjout.size());
							break;
						case 1 :
							Factory.MidasPanelCreation(casesAjout.size());
							break;
						case 2 :
							Factory.VMEPanelCreation(casesAjout.size());
							break;
						case 3 :
							Factory.SbufProducerPanelCreation(casesAjout.size());
							break;
						case 4 :
							Factory.EventBuilderPanelCreation(casesAjout.size());
							break;
						case 5 :
							Factory.EbyedatWatcherPanelCreation(casesAjout.size());
							break;
						case 6 :
							Factory.StoragePanelCreation(casesAjout.size());
							break;
						case 7 :
							Factory.MfmWatcherPanelCreation(casesAjout.size());
							break;
						case 8 :
							Factory.MfmStoragePanelCreation(casesAjout.size());
							break;
						case 9 :
							Factory.TemplateActorPanelCreation(casesAjout.size());
							break;
						case 10 :
							Factory.ActorPanelCreation(casesAjout.size());
							break;
						case 11 :
							Factory.RikenTransmitterPanelCreation(casesAjout.size());
							break;
						}
						dialog.dispose();
					}
				});
			}
		});

		new Thread() {
			public void run() {
				while (true) {
					refresh();
					try {sleep(freq * 100);} catch(InterruptedException e) {}
				}
			}
		}.start();
	}

	/**
	 * @param enabled
	 * TRUE pour peindre la grille
	 * FALSE pour ne pas la peindre
	 */
	public void setGridPainted(boolean enabled) {
		paintGrid = enabled;
		casesAjout.clear();
		focus = null;
		ajouterEqt.setBounds(0,0,0,0);
		cancel.setBounds(0,0,0,0);
		repaint();
	}

	/**
	 * Met (ou retire) la grille du mode 'pause'
	 * en pause la grille affiche seulement 'PLEASE WAIT...'
	 * Utile pour les traitements long réseau (nouvelle config, etc..)
	 * @param bool
	 */
	public void wait(boolean bool) {
		waiting = bool;
		repaint();
	}

	/**
	 * Redéfinition de la méthode pour afficher une grille avec les équipements
	 */
	synchronized public void paintComponent(Graphics g) {
		clear();
		int width = (int)gridSize.getWidth();
		int height = (int)gridSize.getHeight();
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, width, height);
		Graphics2D g2d = (Graphics2D)g;
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    /***	PLEASE WAIT		***/
	    if (waiting) {
	    	g.setFont(new Font("Courrier", Font.BOLD, 44));
	    	String wait = "PLEASE WAIT...";
	    	g.setColor(new Color(45, 0, 20));
	    	g.drawString(wait, 53, 53);
	    	g.setColor(new Color(200, 0, 0));
	    	g.drawString(wait, 50, 50);
	    	return;
	    }

		float dash1[] = {5.0f};
		BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
		g2d.setStroke(dashed);
		g.setColor(Color.GRAY.brighter());
		if (paintGrid) {
			for (int x=-1 ; x<width ; x+=caseSize) {
				g2d.drawLine(x, 0, x, height);
			}
			for (int y=-1 ; y<height ; y+=caseSize) {
				g2d.drawLine(0, y, width, y);
			}
		}

		boolean focusPaint = false;
		/***		On dessine tous les équipements		***/
		int minX = parent.getHorizontalScrollBar().getValue();
		int minY = parent.getVerticalScrollBar().getValue();
		Rectangle rect = parent.getVisibleRect();
		int maxX = minX + (int)rect.getSize().getWidth();
		int maxY = minY + (int)rect.getSize().getHeight();

		ArrayList<String> narvalSys = new ArrayList<String>();
		ArrayList<ZoneNarval> zones = new ArrayList<ZoneNarval>();
		HashMap<BufferedImage, Case> equipsADessiner = new HashMap<BufferedImage, Case>();

		for (EquipementUI eqt : equipements.keySet()) {

			Case c = equipements.get(eqt);
			/** Calcul zones Sous-Systeme Narval		**/
			Equipement equip = eqt.getEqt();

			if ((equip.getType() == EquipementType.ACTOR)
			|| (equip.getType() == EquipementType.SBUFPRODUCER)
			|| (equip.getType() == EquipementType.EVENTBUILDER)
			|| (equip.getType() == EquipementType.STORAGE)
			|| (equip.getType() == EquipementType.RIKENTRANSMITTER)) {

				Actor a = (Actor)equip;
				String container = a.getContainerNARVAL();
				if (!narvalSys.contains(container)) {
					narvalSys.add(container);
					zones.add(new ZoneNarval());
				}
				if (narvalSys.indexOf(container) >= 0) {
					ZoneNarval zone = zones.get( narvalSys.indexOf(container) );
					zone.addPos(c.getX()*caseSize, c.getY()*caseSize);
				}
			} else if (equip.getType() == EquipementType.SUBSYSTEM_NARVAL)
				continue;

			if ( ((c.getX() * caseSize) < (minX - caseSize)) || ((c.getX() * caseSize) > (maxX + caseSize)) ||
				 ((c.getY() * caseSize) < (minY - caseSize)) || ((c.getY() * caseSize) > (maxY + caseSize)) )
				continue;

			equipsADessiner.put(eqt.getImage(), new Case(c.getX()*caseSize, c.getY()*caseSize));
		}
		/***		Permet de dessiner les zones sous-systèmes Narval sous les équipements		***/
		int index = 0;
		FontMetrics metrics = g.getFontMetrics();
		g2d.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (ZoneNarval zone : zones) {
			String narv = narvalSys.get(zones.indexOf(zone));
			g.setColor(colors[index]);
			g.fillRect(zone.getX(), zone.getY()-20, zone.sizeX(), zone.sizeY()+20);
			g2d.drawRect(zone.getX()-5, zone.getY()-25, zone.sizeX()+10, zone.sizeY()+30);
			g.setColor(new Color(32, 32, 32, 125));
			g.fillRect(zone.getX(), zone.getY()-20, zone.sizeX(), 20);
			g.setColor(Color.WHITE);
			int xSize = metrics.stringWidth(narv);
			g.drawString(narv, (zone.sizeX() - xSize)/2 + zone.getX(), zone.getY() - 5);
			index++;
			if (index == colors.length) index = 0;
		}

		g2d.setStroke(new BasicStroke(1.5f));
		g.setColor(Color.BLACK);
		g.setFont(Font.decode("Courrier PLAIN 10"));
		metrics = g.getFontMetrics();

		for (LienEqt lien : liens) {
			lien.sharedDestination = false;
			for (LienEqt l : liens) {
				if ( (l != lien) && (lien.getEqtDest() == l.getEqtDest()) ) {
					lien.sharedDestination = true;
					break;
				}
			}

			String source = lien.getEqtSrc().getName();
			String target = lien.getEqtDest().getName();

			if (!equipsByNames.containsKey(source) || !equipsByNames.containsKey(target)) {
				continue;
			}

			Case start = equipements.get( equipsByNames.get(source) );
			Case end = equipements.get( equipsByNames.get(target) );
			g.setColor(Color.WHITE);
			int x1 = start.getX()*caseSize + caseSize/2;
			int y1 = (start.getY())*caseSize + caseSize/2;
			int x2 = end.getX()*caseSize + caseSize/2;
			int y2 = end.getY()*caseSize;
			drawArrow(g2d, x1, y1, x2, y2);

			if (!paintGrid && Common.detailedView) {
				double flow = lien.getDebit();
				double eventFlow = lien.getDebitEventEnCours();
				DecimalFormat format = new DecimalFormat("000.0");
				DecimalFormat formatEvent = new DecimalFormat("00000");
				String debit = format.format(flow) + " " + Common.getString("Ko/s");
				String debitEvent = formatEvent.format(eventFlow) + " evts/s";

				if (flow >= 1024 && flow < 1048576) { // 1048576 ko = 1Go
					debit = format.format(flow/1024) + " " + Common.getString("Mo/s");
				} else if (flow > 1048576) {
					debit = format.format(flow/1048576) + " " + Common.getString("Go/s");
				}

				g.setColor(Color.DARK_GRAY);
				g.fillRoundRect((x1+x2)/2 - metrics.stringWidth(debitEvent)/2 -5,(y1+y2)/2 -10,metrics.stringWidth(debitEvent)+10,30,10,10);
				g.setColor(Color.WHITE);
				g.drawString(debit, (x1+x2)/2 - metrics.stringWidth(debit)/2, (y1+y2)/2 +5);
				if (eventFlow != 0.0) {
					g.drawString(debitEvent, (x1+x2)/2 - metrics.stringWidth(debitEvent)/2, (y1+y2)/2 +15);
				}
				g.drawRoundRect((x1+x2)/2 - metrics.stringWidth(debitEvent)/2 -5,(y1+y2)/2 -10,metrics.stringWidth(debitEvent)+10,30,10,10);
			}

		}

		/***	========================================			***/
		/***			Dessin de la flèche en cours d'ajout		***/
		Case start = null;
		Case end = null;
		int x1 = -1;
		int y1 = -1;
		int x2 = -1;
		int y2 = -1;
		if (eqtSource != null && eqtBar.getAction() == EquipementBar.Action.LINK) {
			start = equipements.get( equipsByNames.get(eqtSource) );
			end = null;
			if (eqtTarget != null) end = equipements.get( equipsByNames.get(eqtTarget) );
			else end = new Case(focusX, focusY);
			g.setColor(isLinkValid ? Color.GREEN : Color.RED);
			x1 = start.getX()*caseSize + caseSize/2;
			y1 = (start.getY())*caseSize + caseSize/2;
			x2 = end.getX()*caseSize + caseSize/2;
			y2 = end.getY()*caseSize;
			g2d.setStroke(new BasicStroke(3.0f));
			drawArrow(g2d, x1, y1, x2, y2);
		}

		/***==============================================***/
		/***			Dessin des équipements			***/
		for(BufferedImage img : equipsADessiner.keySet()) {
			Case c = equipsADessiner.get(img);
			g.drawImage(img, c.getX(), c.getY(), null);
			if(c.getX()/caseSize == focusX && c.getY()/caseSize == focusY) {
				if(focus!=null) { // si un eqt est en drag&drop
					g.setColor(new Color(0, 255, 128, 128));
					dashed = new BasicStroke(5.0f);
					g2d.setStroke(dashed);
					g2d.drawRect(focusX*caseSize, focusY*caseSize, caseSize, caseSize);
				} else if(paintGrid) {
					g.setColor(new Color(255, 255, 255, 100));
					g.fillRect(focusX*caseSize, focusY*caseSize, caseSize, caseSize);
				}
				focusPaint = true; // on a peint la case qui a le focus
			}
		}
		/***======================================================***/
		/* 
		 * Dessin des bordures autour des équipements SOURCE et DESTINATION du
		 * Lien en cours de création
		 */
		int stroke = 5;
		g2d.setStroke(new BasicStroke(stroke));
		if(x1 != -1 && y1 != -1) {
			x1 -= caseSize/2;
			y1 -= caseSize/2;
			g.setColor(new Color(0, 125, 255, 125));
			g.drawRect(x1 - stroke, y1 -stroke, caseSize+2*stroke, caseSize+2*stroke);
		}
		if(x2 != -1 && y2 != -1) {
			x2 -= caseSize/2;
			g.setColor( isLinkValid ? new Color(0, 255, 0, 125) : new Color(255, 0, 0, 125));
			g.drawRect(x2 - stroke, y2 -stroke, caseSize+2*stroke, caseSize+2*stroke);
		}
		/***========================================================***/
		/***		Dessin des cases "SOURCE :" et "DESTINATION :" situées en haut à gauche	***/
		if(eqtSource!=null) {
			g.setFont(Font.decode("Courrier PLAIN 12"));
			int posX = parent.getHorizontalScrollBar().getValue();
			int posY = parent.getVerticalScrollBar().getValue();
			g.setColor(new Color(0, 125, 255, 125));
			g.fillRect(posX, caseSize +posY, caseSize, caseSize);
			g.setColor(Color.BLACK);
			g.drawString("Source", posX+5, caseSize +20 +posY);
			g.drawString(eqtSource==null ? "/" : eqtSource, posX+5, caseSize +35 +posY);
			if(eqtTarget!=null) {
				g.setColor( isLinkValid ? new Color(0, 255, 0, 125) : new Color(255, 0, 0, 125));
				g.fillRect(posX, 2*caseSize +posY, caseSize, caseSize);
				g.setColor(isLinkValid ? Color.BLACK : Color.YELLOW);
				g.drawString(isLinkValid ? "Target" : "INVALID !", posX+5, caseSize*2 + 20 +posY);
				g.setColor(Color.BLACK);
				g.drawString(eqtTarget, posX+5, caseSize*2 + 35 +posY);
			}
		}

		/***		Si on a pas encore dessiné le focus ET que l'on est en mode édition		***/
		/***		Alors on dessine la case qui a le focus souris							***/
		if(!focusPaint && paintGrid) {
			g.setFont(new Font("Courrier", Font.PLAIN, 10));
			g.setColor(FOCUS_COLOR);
			dashed = new BasicStroke(4.0f);
			g2d.setStroke(dashed);
			g2d.drawRect(focusX*caseSize, focusY*caseSize, caseSize, caseSize);
			/***	Si l'équipement selectionné dans la barre d'outils n'est pas "Mode sélection" (null)	***/
			/***	Alors on dessine une case avec "Ajouter" et le type d'équipement qui sera ajouté		***/
			if(eqtBar.getAction() == EquipementBar.Action.EQUIPMENT) {
				Paint gradient = new GradientPaint(0, focusY*caseSize, GRADIENT_1, 0, focusY*caseSize + caseSize, GRADIENT_2);
				g2d.setPaint(gradient);
				g.fillRect(focusX*caseSize, focusY*caseSize, caseSize, caseSize);
				g.setColor(Color.WHITE);
				int posX = (caseSize - g.getFontMetrics().stringWidth(Common.getString("Ajouter")))/2 + focusX*caseSize; // centre le texte en X
				int posY = focusY*caseSize +15; // met le texte en haut de la case
				g.drawString(Common.getString("Ajouter"), posX, posY);
			}/***	Sinon, on ecrit "Select" dans la case et on ne dessine que les contours		***/
			 else {
				int posX = (caseSize - g.getFontMetrics().stringWidth("Select"))/2 + focusX*caseSize;
				int posY = (caseSize - (g.getFontMetrics().getAscent()+10))/2 + focusY*caseSize +10;
				g.drawString("Select", posX, posY);
			}
		}

		/***=========================================***/
		if(eqtBar.getAction() == EquipementBar.Action.SELECT)
			return;
		Font num = new Font("Courrier", Font.BOLD, 16);
		/***	Dessine toutes les cases pressées où seront
		 * 		ajoutés les équipements lorsque le clic souris
		 * 		sera lâché
		 */
		int i = 1;
		g.setFont(num);
		int nbPos = (caseSize - g.getFontMetrics().stringWidth(i+""))/2;
		Color light = new Color(
				ADD_COLOR.getRed(),
				ADD_COLOR.getGreen(),
				ADD_COLOR.getBlue()
				).darker();
		Color shadow = light.brighter().brighter();
		for(Case c : casesAjout) {
			g.setColor(ADD_COLOR);
			g.fillRect(c.getX()*caseSize, c.getY()*caseSize, caseSize, caseSize);
			nbPos = (caseSize - g.getFontMetrics().stringWidth(i+""))/2;
			g.setColor(shadow);
			g.drawString(""+i, nbPos + caseSize*c.getX()+1, c.getY()*caseSize + 36);
			g.setColor(light);
			g.drawString(""+i, nbPos + caseSize*c.getX(), c.getY()*caseSize + 35);
			i++;
		}
		if(paintGrid)
			drawTips(g);
	}

	private void drawArrow(Graphics2D g, int x, int y, int xx, int yy) {
		float arrowWidth = 10.0f;
		float theta = 1.0f; //0.423f;
		int[] xPoints = new int[3], yPoints = new int[3];
		float[] vecLine = new float[2];
		float[] vecLeft = new float[2];
		float fLength;
		float th;
		float ta;
		float baseX, baseY;
		xPoints[0] = xx;
		yPoints[0] = yy; // build the line vector
		vecLine[0] = (float) xPoints[0] - x;
		vecLine[1] = (float) yPoints[0] - y;
		// build the arrow base vector - normal to the line
		vecLeft[0] = -vecLine[1];
		vecLeft[1] = vecLine[0];
		// setup length parameters
		fLength = (float) Math.sqrt(vecLine[0] * vecLine[0] + vecLine[1]
				* vecLine[1]);
		th = arrowWidth / (2.0f * fLength);
		ta = arrowWidth / (2.0f * ((float) Math.tan(theta) / 2.0f) * fLength);
		// find the base of the arrow
		baseX = ((float) xPoints[0] - ta * vecLine[0]);
		baseY = ((float) yPoints[0] - ta * vecLine[1]);
		// build the points on the sides of the arrow
		xPoints[1] = (int) (baseX + th * vecLeft[0]);
		yPoints[1] = (int) (baseY + th * vecLeft[1]);
		xPoints[2] = (int) (baseX - th * vecLeft[0]);
		yPoints[2] = (int) (baseY - th * vecLeft[1]);
		g.drawLine(x, y, (int) baseX, (int) baseY);
		g.fillPolygon(xPoints, yPoints, 3);
	}

	/**
	 * Dessine la bulle d'aide
	 * @param g
	 */
	public void drawTips(Graphics g) {
		Color back = new Color(15, 50, 95, showHelp ? 200 : 0);
		Color title = new Color(55, 245, 215, showHelp ? 200 : 0);
		Color normal = new Color(215, 240, 255, showHelp ? 200 : 0);

		Color backHelp = new Color(0, 75, 120);
		Color foreHelp = new Color(145, 215, 235);
		if(showHelp) {
			backHelp = backHelp.brighter();
			foreHelp = foreHelp.brighter();
		}
		
		Rectangle rect = parent.getVisibleRect();
		int posX = parent.getHorizontalScrollBar().getValue();
		int posY = parent.getVerticalScrollBar().getValue();
		int x = posX; //( (int)rect.getWidth() - 400 )/2 + posX;
		int y = ( (int)rect.getHeight() - 250 ) + posY;
		g.setColor(back);
		g.fillRoundRect(x, y, (int)rect.getWidth()-20, 230, 25, 25);

		/***	DESSIN DU CARRE 'HELP' EN HAUT A GAUCHE		***/
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(2.0f));
		g.setColor(backHelp);
		g.fillRect(posX, posY, caseSize, caseSize);
		g.setColor(showHelp ? Color.DARK_GRAY : Color.BLACK);
		g.drawRect(posX, posY, caseSize, caseSize);
		g.setColor(foreHelp);
		posY+=5;
		g.drawOval(posX + caseSize/4, posY, caseSize/2, caseSize/2);
		g.setFont(Font.decode("Courrier BOLD 18"));
		posX = (caseSize - g.getFontMetrics().stringWidth("?"))/2 + posX;
		posY = (caseSize/2 + g.getFontMetrics().getAscent())/2 + posY;
		g.drawString("?", posX, posY);
		posY += caseSize - g.getFontMetrics().getAscent() -15;
		posX = (caseSize - g.getFontMetrics().stringWidth(Common.getString("Help")))/2 + parent.getHorizontalScrollBar().getValue();
		g.drawString(Common.getString("Help"), posX, posY);
		/***												***/
		if(!showHelp)
			return;
		String[] tips = {
				"<TITLE>⚫ "+Common.getString("Help_HowTo"),
				Common.getString("Help_AddSeveral"),
				Common.getString("Help_Select"),
				"",
				"<TITLE>⚫ "+Common.getString("Help_Title_AddOne"),
				"1] - "+Common.getString("Help_SelectSquare"),
				"2] - "+Common.getString("Help_ClickOkOneEqt"),
				"",
				"<TITLE>⚫ "+Common.getString("Help_Title_AddMulti"),
				"1] - "+Common.getString("Help_HoldLeft"),
				"2] - "+Common.getString("Help_Release"),
				"3] - "+Common.getString("Help_CtrlAdd"),
				"4] - "+Common.getString("Help_CtrlDel"),
				"5] - "+Common.getString("Help_AddEqts"),
				"",
				"<TITLE>⚫ "+Common.getString("Help_Title_Cancel"),
				Common.getString("Help_Cancel")
			};
		x += 20;
		y += 20;
		for(String str : tips) {
			if(str.startsWith("<TITLE>")) {
				str = str.replace("<TITLE>", "");
				g.setFont(Font.decode("Courrier BOLD 16"));
				g.setColor(new Color(0, 25, 40));
				g.drawString(str, x+2, y+2);
				g.setColor(title);
			} else {
				g.setColor(normal);
				g.setFont(Font.decode("Courrier PLAIN 12"));
			}
			g.drawString(str, x, y);
			y += g.getFontMetrics().getAscent();
		}
	}

	/***		MOUSE_LISTENERS			***/
	@Override
	public void mouseDragged(MouseEvent evt) {
		if(!paintGrid)
			return;
		int onmask = InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK;
		int tmpX = focusX;
		int tmpY = focusY;
		focusX = evt.getX()/caseSize;
		focusY = evt.getY()/caseSize;
		if(tmpX != focusX || tmpY != focusY) {
			Case c = new Case(focusX, focusY);
			if(focus!=null && eqtBar.getAction() != EquipementBar.Action.LINK) {
				moveEqt(focus);
			} else if(eqtBar.getAction() == EquipementBar.Action.EQUIPMENT && (evt.getModifiers() == InputEvent.BUTTON1_MASK
					|| ((evt.getModifiersEx() & (onmask)) == onmask)) ) { // si on a CTRL enfoncé ou non
				if(!casesAjout.contains(c) && !equipements.containsValue(c))
					casesAjout.add(c);
			} else if(eqtBar.getAction() == EquipementBar.Action.LINK) {
				EquipementUI dest = null;
				if(equipements.containsValue(c)) {
					for(EquipementUI eqt : equipements.keySet()) {
						if(equipements.get(eqt).equals(c)) {
							dest = eqt;
							break;
						}
					}
				}
				if(dest!=null) {
					isLinkValid = true;
					eqtTarget = dest.getName();
					if(eqtTarget.equals(eqtSource)) {
						eqtTarget = null;
						isLinkValid = false;
					} else {
//						for(LienEqt lien : liens) {
//							if( (lien.getEqtDest().getName().equals(eqtTarget) && lien.getEqtSrc().getName().equals(eqtSource))
//									|| (lien.getEqtDest().getName().equals(eqtSource) && lien.getEqtSrc().getName().equals(eqtTarget)) ) {
//								isLinkValid = false;
//								break;
//							}
//						}
					}
				} else {
					eqtTarget = null;
					isLinkValid = false;
				}
			} else if( (evt.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK) ) {
				if(casesAjout.contains(c))
					casesAjout.remove(c);
			}
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent evt) {
		int tmpX = focusX;
		int tmpY = focusY;
		focusX = evt.getX()/caseSize;
		focusY = evt.getY()/caseSize;
		if(tmpX != focusX || tmpY != focusY) {
			repaint();
		}
		int minX = parent.getHorizontalScrollBar().getValue();
		int minY = parent.getVerticalScrollBar().getValue();
		int maxX = minX + caseSize;
		int maxY = minY + caseSize;
		if( (evt.getX() < maxX && evt.getX() > minX) && (evt.getY() < maxY && evt.getY() > minY) ) {
			showHelp = true;
			ajouterEqt.setVisible(false);
			cancel.setVisible(false);
		}
		else {
			showHelp = false;
			ajouterEqt.setVisible(true);
			cancel.setVisible(true);
		}
	}

	/**
	 * Déplace l'équipement passé en paramètre sous le pointeur souris
	 * @param eqt l'équipement à déplacer
	 */
	public void moveEqt(EquipementUI eqt) {
		if(eqt==null)
			return;
		Case c = new Case(focusX, focusY);
		if(focusX<0 || focusY<0)
			return;
		if(!equipements.containsValue(c) && !casesAjout.contains(c))
			equipements.put(eqt, c);
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		Case c = new Case(focusX, focusY);
		if(evt.getButton() == MouseEvent.BUTTON1) {
			if(equipements.containsValue(c)) {
				for(EquipementUI eqt : equipements.keySet()) {
					if(equipements.get(eqt).equals(c)) {
						focus = eqt;
						break;
					}
				}
			} else if(eqtBar.getAction() == EquipementBar.Action.EQUIPMENT) {
				if(!evt.isControlDown())
					casesAjout.clear();
				if(!casesAjout.contains(c) && paintGrid)
					casesAjout.add(c);
				//EquipementUI e = new EquipementUI("Test", eqtBar.getSelected(), strSize);
				//equipements.put(e, c);
				focus = null;
			}
		} else if(evt.getButton() == MouseEvent.BUTTON3) {
			if(casesAjout.contains(c)) {
				casesAjout.remove(c);
			}
		}
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent evt) {}
	@Override
	public void mouseExited(MouseEvent evt) {}
	@Override
	public void mousePressed(MouseEvent evt) {
//		int onmask = InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK;
		Case c = new Case(focusX, focusY);

		if(evt.getButton() == MouseEvent.BUTTON1 && eqtBar.getAction() == EquipementBar.Action.LINK) {
			if(equipements.containsValue(c)) {
				for(EquipementUI eqt : equipements.keySet()) {
					if(equipements.get(eqt).equals(c)) {
						eqtSource = eqt.getName();
						break;
					}
				}
			}
		}
		if(evt.getButton() == MouseEvent.BUTTON3) {
			if(casesAjout.contains(c)) {
				casesAjout.remove(c);
			}
		}
		if(evt.isPopupTrigger()) {
			if(equipements.containsValue(c)) {
				for(EquipementUI eqt : equipements.keySet()) {
					if(equipements.get(eqt).equals(c)) {
						eqt.getPopupSurv().show(this, evt.getX(), evt.getY());
						break;
					}
				}
			}
			return;
		}// else mouseClicked(evt);
		else {
			if(equipements.containsValue(c)) {
				for(EquipementUI eqt : equipements.keySet()) {
					if(equipements.get(eqt).equals(c)) {
						focus = eqt;
						break;
					}
				}
			} else {
				//if (!((evt.getModifiersEx() & (onmask)) == onmask))
				if(!evt.isControlDown())
					casesAjout.clear();
				ajouterEqt.setBounds(0,0,0,0);
				cancel.setBounds(0,0,0,0);
				if(eqtBar.getAction() == EquipementBar.Action.EQUIPMENT)
					if(!casesAjout.contains(c) && paintGrid)
						casesAjout.add(c);
			}
		}
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent evt) {
		focus = null;
		if(casesAjout.size()>=1) {
			Case c = casesAjout.get(casesAjout.size()-1);
			ajouterEqt.setBounds(c.getX()*caseSize, (c.getY()+1)*caseSize, caseSize, caseSize/2);
			cancel.setBounds(c.getX()*caseSize, (c.getY()+1)*caseSize+ caseSize/2, caseSize, caseSize/2);
		} else {
			ajouterEqt.setBounds(0,0,0,0);
			cancel.setBounds(0,0,0,0);
		}
		if(eqtBar.getAction() == EquipementBar.Action.LINK) {
			if(eqtSource != null && eqtTarget != null) {
				Equipement source = equipsByNames.get(eqtSource).getEqt();
				Equipement destination = equipsByNames.get(eqtTarget).getEqt();
				if(isLinkValid)
					Factory.linkPanelCreation(source, destination);
			}
			eqtSource = null;
			eqtTarget = null;
		}
		repaint();
	}

	public void addLink(LienEqt lien) {
		liens.add(lien);
		repaint();
		tree.setLiens(liens);
	}

	public void setScroll(JScrollPane scroll) {
		parent = scroll;
	}

	/**
	 * Supprime les eqtUI dont l'équipement est null
	 */
	synchronized public boolean clear() {

		boolean eqtDel = false;

		for (EquipementUI eqt : equipements.keySet()) {

			if (eqt.getEqt() == null) {
				equipements.remove(eqt);
				equipsByNames.remove(eqt.getName());
				eqt.remove();
				eqtDel = true;
			}
		}

		if (eqtDel) {
			liens.clear();
			liens.addAll(Common.myClientSOAP.getState().getListLink());
		}

		return eqtDel;
	}

	public void refresh() {

		boolean stateChange = false;

		if (!Common.stopMonitor) {
			for (EquipementUI eqt : equipements.keySet()) {
				//hasChanged = hasChanged || eqt.modified();
				stateChange = stateChange || eqt.stateHasChanged();
			}
		}

		repaint();

		if (stateChange) {
			tree.maj();
		}
		else if (hasChanged || clear()) {
			tree.setEqtUI(equipements.keySet());
			tree.setLiens(liens);
		}

		if (Communication.needGridUpdate) {
			liens.clear();
			liens.addAll(Common.myClientSOAP.getState().getListLink());
			tree.setEqtUI(equipements.keySet());
			tree.setLiens(liens);
			repaint();
			Communication.needGridUpdate = false;
		}

		hasChanged = false;
	}

	/**
	 * @return
	 * une liste des noms des équipements (pour empêcher les doublons qui crashent le serveur...)
	 */
	public ArrayList<String> getNames() {
		ArrayList<String> liste = new ArrayList<String>();
		for (EquipementUI eqtUI : equipements.keySet()) liste.add(eqtUI.getName());
		return liste;
	}

	public Set<EquipementUI> getEqts() {
		return equipements.keySet();
	}

	public EquipementUI getEqt(String name) {
		return equipsByNames.get(name);
	}

	/**
	 * @return
	 * une liste des noms des liens
	 */
	public ArrayList<String> getLinksNames() {
		ArrayList<String> liste = new ArrayList<String>();
		for (LienEqt lien : liens) liste.add(lien.getNom());
		return liste;
	}

	/**
	 * Défini la taille d'une case (en pixels)
	 * @param size
	 * Valeurs possibles :
	 * "BIG" : 75px
	 * "NORMAL" 60px
	 * AUTRE CHOSE : 50px
	 */
	public void setSize(String size) {
		strSize = size;
		JRadioButtonMenuItem bigSize = frame.getContentPanel().getBigSizeButton();
		JRadioButtonMenuItem medSize = frame.getContentPanel().getMedSizeButton();
		JRadioButtonMenuItem littleSize = frame.getContentPanel().getLittleSizeButton();
		if (size.equals("BIG")) {
			caseSize = 77;
			bigSize.setSelected(true);
			medSize.setSelected(false);
			littleSize.setSelected(false);
		} else if (size.equals("NORMAL")) {
			caseSize = 62;
			bigSize.setSelected(false);
			medSize.setSelected(true);
			littleSize.setSelected(false);
		} else {
			caseSize = 52;
			bigSize.setSelected(false);
			medSize.setSelected(false);
			littleSize.setSelected(true);
		}
		for (EquipementUI eqt : equipements.keySet()) eqt.setSize(size);
		gridSize = new Dimension(nbCasesX*caseSize, nbCasesY*caseSize);
		setPreferredSize(gridSize);
		updateUI();
	}

	public void loadLayout(File selectedFile) {

		HashMap<String, Case> tmp = new HashMap<String, Case>();

		try {
			File f = new File( selectedFile.getAbsolutePath() );

			if (!f.exists()) f.createNewFile();

			BufferedReader in = new BufferedReader(new FileReader(f));

			String line;

			if ((line = in.readLine()) != null) {
				setSize( line );
			}

			while ((line = in.readLine()) != null) {
//				System.out.println(line);
				if (line.startsWith("include")) {
//					System.out.println("include");
					String s = (line.split(" "))[1];
//					System.out.println(s);
					loadLayout( new File( s ) );
				}
				else {
					String name = line.split(" : ")[0];
					String[] _case = line.split(" : ")[1].split(";");
					int x = 0;
					int y = 0;
					try{
						x = Integer.parseInt(_case[0]);
						y = Integer.parseInt(_case[1]);
					}catch(NumberFormatException e) {}
					tmp.put(name, new Case(x, y));
				}
			}
			in.close();
		} catch (Exception e) {e.printStackTrace();}

		for (EquipementUI e : equipements.keySet()) {
			for (String name : tmp.keySet()) {
				if (name.equals(e.getName())) {
					equipements.replace(e, tmp.get(name));
					tmp.remove(name);
					break;
				}
			}
		}

		repaint();

		if (tmp.size() > 0) {
			String names = "";
			int nb = 0;
			for (String restant : tmp.keySet()) {
				names += "- " + restant +"\n";
				nb++;
			}

			JOptionPane.showMessageDialog(frame,
						Common.getString("Probleme_Sauvegarde") + "\n" + Common.getString("Eqt_manquants") + "\n"+ names,
						Common.getString("Attention") +" !",JOptionPane.WARNING_MESSAGE,null);
		}
	}

	public void loadLayout() {
		String file;

		try {file = Common.myClientSOAP.getExperimentCfgFile();}
		catch (com.sun.xml.internal.ws.client.ClientTransportException e) {return;}

		if (file.equals("")) return;

		int index = file.lastIndexOf(".xml");

		if (index != -1) file = file.substring(0, index);

		file = file + ".layout";

		final JFileChooser fc = new JFileChooser( file );

		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			loadLayout( fc.getSelectedFile() );
		}
	}

	/**
	 * Ajoute les équipements chargés à la grille
	 * @param eqts
	 */
	public void load(ArrayList<Equipement> eqts) {

		for (EquipementUI e : equipements.keySet()) {
			e.remove();
			e.kill();
		}

		equipements.clear();
		equipsByNames.clear();

		if (eqts == null) return;

		int x=2;
		int y=2;

		for (Equipement eqt : eqts) {
			EquipementUI e = new EquipementUI(frame, eqt, strSize);
			eqt.setEqtUI(e);
			Case c = new Case(x, y);
			if (eqt.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				c.setX(-1);
				c.setY(-1);
			}
			equipements.put(e, c);
			equipsByNames.put(e.getName(), e);

			x++;
			if(x>10) {
				x=0;
				y++;
			}
		}
		focus = null;
		tree.setEqtUI(equipements.keySet());
		repaint();
	}

	public void loadLinks(ArrayList<LienEqt> links) {
		liens.clear();
		liens.addAll(links);
		tree.setLiens(liens);
	}

	public void saveLayout(File selectedFile)
	{
		try {
			PrintWriter out = new PrintWriter( new FileWriter( selectedFile.getAbsolutePath(), false) );
			out.println( strSize );
			for (EquipementUI eqt : equipements.keySet()) {
				Case c = equipements.get(eqt);
				out.println(eqt.getName() + " : " + c.getX() + ";" + c.getY());
			}
			out.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * Sauvegarde dans un fichier la disposition des équipements
	 */
	public void saveLayout() {
		String file;

		try {file = Common.myClientSOAP.getExperimentCfgFile();}
		catch (com.sun.xml.internal.ws.client.ClientTransportException e) {return;}

		if (file.equals("")) return;

		int index = file.lastIndexOf(".xml");

		if (index != -1) file = file.substring(0, index);

		file = file + ".layout";

		final JFileChooser fc = new JFileChooser( file );

		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			saveLayout( fc.getSelectedFile() );
		}
	}

	/**
	 * Met tous les équipements Hors-Ligne
	 * (utilisé pour le passage en mode édition)
	 */
	public void offline() {
		for(EquipementUI eqtUI : equipements.keySet()) {
			eqtUI.offline();
		}

		tree.offline();

		repaint();
	}

	/**
	 * Met tous les équipements online
	 * (utilisé pour le passage en mode surveillance)
	 */
	public void online() {
		for(EquipementUI eqtUI : equipements.keySet()) {
			eqtUI.online();
		}

		tree.online();

		repaint();
	}

/*
	public boolean ajouterEquipement(Equipement... eqts) {
		boolean ret = true;
		int i = 0;
		for(Equipement eqt : eqts) {
			if (Common.myClientSOAP.creerEquipement(eqt)) {
				EquipementUI eqtUI = new EquipementUI(frame, eqt, strSize);
				eqt.setEqtUI(eqtUI);
				equipements.put(eqtUI, casesAjout.get(i));
				equipsByNames.put(eqtUI.getName(), eqtUI);
				ret = ret && true;
			} else ret = ret && false;
			i++;
		}
		repaint();
		return ret;
	}
*/
	/**
	 * Methode d'ajout d'equipement
	 * @param eqts
	 * les equipements a ajouter
	 * @return
	 * false si erreur
	 */
	public boolean ajouterEquipement(final ArrayList<Equipement> eqts)	{

		int i = 0;
		boolean status = true;

		for (Equipement eqt : eqts) {

			try {status = Common.myClientSOAP.creerEquipement(eqt);}
			catch (com.sun.xml.internal.ws.client.ClientTransportException e) {status = false; break;}

			if (status) {
				EquipementUI eqtUI = new EquipementUI(frame, eqt, strSize);
				eqt.setEqtUI(eqtUI);
				equipements.put(eqtUI, casesAjout.get(i));
				equipsByNames.put(eqtUI.getName(), eqtUI);
			}

			i++;
		}

		cancel.setBounds(0,0,0,0);
		ajouterEqt.setBounds(0,0,0,0);
		casesAjout.clear();
		hasChanged = true;
		repaint();
		return status;
	}

	/**
	 * Méthode ajoutant un sous-syteme Narval
	 * @param eqt
	 */
	public void addSubSystem(final Equipement eqt) {
		if (Common.myClientSOAP.creerEquipement(eqt)) {
			EquipementUI eqtUI = new EquipementUI(frame, eqt, strSize);
			eqt.setEqtUI(eqtUI);
			equipements.put(eqtUI, new Case(-10,-10));
			equipsByNames.put(eqtUI.getName(), eqtUI);
		}
		cancel.setBounds(0,0,0,0);
		ajouterEqt.setBounds(0,0,0,0);
		casesAjout.clear();
		hasChanged = true;
		repaint();
	}

	public EquipementBar getEqtBar() {
		return eqtBar;
	}

	public Window getFrame() {
		return frame;
	}
}
