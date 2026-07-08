package ecc;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.xml.ws.Holder;

import core.Common;
import core.equipement.ECC;

public class MatriceEtat extends JPanel implements MouseListener{
	/**
	 * 	Classe interne qui se charge d'afficher les boutons fléchés
	 *
	 */
	class Arrow extends JComponent{

		private static final long serialVersionUID = 1L;
		private String label;
		private boolean up = false; // true si cette flèche mène à l'état suivant, false pour l'état précédent
		private boolean enabled = false;
		private final Color UP_ENABLED = Color.CYAN;
		private final Color ENABLED = Color.ORANGE;
		private final Color NORMAL = Color.GRAY;
		private boolean wait = false;

		/**
		 * Constructeur pour Arrow
		 * @param lab
		 * le label affiché sur la flèche
		 * @param up
		 * true si la flèche mène à l'état suivant (cela affecte la couleur de la flèche)
		 */
		public Arrow(String lab, boolean up)
		{
			label = lab;
			this.up = up;
			setPreferredSize(new Dimension(80, 60));
		}

		public void set(boolean bool)
		{
			if(wait)
				return;
			enabled = bool;
			repaint();
		}

		public boolean isActive()
		{
			return enabled;
		}
		/**
		 * Passe (ou non) la flèche en mode "attente"
		 * Utilisé lors d'un clic comme retour pour 
		 * l'utilisateur que son clic a été pris en charge
		 * en mode attente le label est remplacé par "..."
		 * @param bool
		 */
		public void wait(boolean bool)
		{
			wait = bool;
			repaint();
		}
		/**
		 * Le bouton est-il en mode "attente"
		 * @return true si attente
		 */
		public boolean isWaiting()
		{
			return wait;
		}

		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color color = NORMAL;
			if(enabled && up)
				color = UP_ENABLED;
			else if(enabled)
				color = ENABLED;
			int x = 18;
			int y = 30;
			int[][] res = getArrow();
			g.setColor(color.darker().darker().darker().darker());
			g.fillPolygon(res[0], res[1], 8);
			g.setColor(color);
			g.drawPolyline(res[0], res[1], 8);
			//g.drawLine(getWidth()/2, 1, getWidth()/2, getHeight());
			if(enabled)
			{
				g.setColor(Color.BLACK);
				g.fillRoundRect(0, y-10, getWidth()-1, 20, 20, 20);
				g.setColor(color);
				g.drawRoundRect(0, y-10, getWidth()-1, 20, 20, 20);
				g.setFont(new Font("Courrier", Font.PLAIN, 12));
				if(wait)
				{
					g.setFont(new Font("Courrier", Font.PLAIN, 18));
					x = (getWidth() - g.getFontMetrics().stringWidth("..."))/2;
					g.drawString("...", x, y+5);
				} else
				{
					x = (getWidth() - g.getFontMetrics().stringWidth(label))/2;
					g.drawString(label, x, y+5);
				}
			}
		}
		/**
		 * Renvoie un tableau de tableau d'entiers
		 * L'indice 0 est un tableau d'entiers de positions X
		 * L'indice 1 est un tableau d'entiers de positions Y
		 * Le tout forme un tracé qui représente une flèche simple
		 * (N'est plus utilisé)
		 * */
		public int[][] getArrays()
		{
			int posX = getWidth()/2;
			int pX[] = {posX-5, posX, posX+5};
			int y1 = (up ? getHeight()-7 : 7);
			int y2 = (up ? getHeight()-2 : 2);
			int pY[] = {y1, y2, y1};
			int ret[][] = {pX, pY};
			return ret;
		}

		/**
		 * Renvoie des positions qui représentent le tracé d'une flèche double
		 * @return
		 * un tableau de tableau d'entiers
		 * indice 0 : tableau de positions X
		 * indice 1 : tableau de positions Y
		 */
		public int[][] getArrow()
		{
			if(up)
			{
				int posX = getWidth()/2;
				int posY = getHeight() -1;
				int pX[] = {posX, posX-10, posX-3, posX-3, posX+3, posX+3, posX+10, posX};
				int pY[] = {posY, posY-10, posY-10, posY-60, posY-60, posY-10, posY-10, posY};
				int ret[][] = {pX, pY};
				return ret;	
			}
			int posX = getWidth()/2;
			int posY = 1;
			int pX[] = {posX, posX-10, posX-3, posX-3, posX+3, posX+3, posX+10, posX};
			int pY[] = {posY, posY+10, posY+10, posY+60, posY+60, posY+10, posY+10, posY};
			int ret[][] = {pX, pY};
			return ret;
		}
	}

	private static final long serialVersionUID = 501883081922549301L;
	private Etat[] etats = {
			new Etat(Etat.Status.IDLE, true),
			new Etat(Etat.Status.DESCRIBED),
			new Etat(Etat.Status.PREPARED),
			new Etat(Etat.Status.READY),
			new Etat(Etat.Status.RUNNING)
	};
	private ArrayList<Arrow> nextArrow = new ArrayList<Arrow>();
	private ArrayList<Arrow> undoArrow = new ArrayList<Arrow>();
	private int currentState = IDLE;
	private boolean diode = false;
	private ECC equipment;
	private static final int OFFLINE = 0;
	private static final int IDLE = 1;
	private static final int DESCRIBED = 2;
	private static final int PREPARED = 3;
	private static final int READY = 4;
	private static final int RUNNING = 5;
	private static final String[] state = {"OFFLINE", "IDLE", "DESCRIBED", "PREPARED", "READY", "RUNNING"};
	private boolean expertMode = false;

	/**
	 * Constructeur
	 * @param geco
	 * l'ECC dont on doit afficher la matrice d'état
	 */
	public MatriceEtat(ECC equipment)
	{
		this.equipment = equipment;
		Dimension d = new Dimension(300, 500);
		setPreferredSize(d);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0, 5, 0, 0);

		for (Etat etat : etats)
		{
			String next = etat.getNext();
			String undo = etat.getUndo();
			gbc.gridwidth=2;
			gbc.gridy++;
			gbc.gridx=0;
			add(etat, gbc);
			gbc.gridy++;
			gbc.gridwidth=1;

			if (undo != null)
			{
				Arrow ar2 = new Arrow(undo, false);
				undoArrow.add(ar2);
				add(ar2, gbc);
				ar2.addMouseListener(this);
			}

			gbc.gridx++;

			if (next != null)
			{
				Arrow ar = new Arrow(next, true);
				nextArrow.add(ar);
				add(ar, gbc);
				ar.addMouseListener(this);
			}
		}

		upArrows();
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
	}

	/**
	 * Met à jour les flèches boutons
	 * Seules les flèches qui mènent à l'état précédent (si il existe)
	 * et suivant (si il existe) sont activées
	 * Si le mode expert est actif, toutes les flèches sont activées
	 */
	public void upArrows()
	{
		int indexState = currentState - 1;

		for (Arrow arr : nextArrow)
		{
			if (nextArrow.indexOf(arr) == indexState) arr.set(true);
			else arr.set(expertMode);
		}

		for (Arrow arr : undoArrow)
		{
			if (undoArrow.indexOf(arr) == indexState - 1) arr.set(true);
			else arr.set(expertMode);
		}

		if (undoArrow.get(Math.max(0, indexState-1)).isWaiting()) nextArrow.get(Math.min(3, indexState)).set(expertMode);
		else if (nextArrow.get(Math.min(3, indexState)).isWaiting()) undoArrow.get(Math.max(0, indexState - 1)).set(expertMode);

		repaint();
	}

	/*	Remet les boutons flèches par défaut (plus en attente)	*/
	public void clean()
	{
		for (Arrow arr : nextArrow)
		{
			arr.wait(false);
		}

		for (Arrow arr : undoArrow)
		{
			arr.wait(false);
		}
	}

	/**
	 * Fait clignoter la diode
	 */
	public void process()
	{
		if (currentState >= PREPARED)
		{
			diode = !diode;
			repaint();
		}
	}

	/**
	 * @return le numéro d'état courrant
	 */
	public int getState()
	{
		return currentState;
	}

	/**
	 * Met à jour l'état courant et apelle les méthodes qui vont mettre à jour les boutons
	 */
	public void updateState()
	{
		int tmp = currentState;

		currentState = equipment.getECCState();

		if (tmp != currentState)
		{
			clean();
//			System.out.println("ECCore : " + equipment.getName() + " " + state[equipment.getECCState()]);
		}
		setState();
		upArrows();
	}

	public void updateState(int st)
	{
		if (st == OFFLINE) // si offline on sort (traitement possible en cours)
			return;

		int tmp = currentState;

		currentState = st;

		if (tmp != currentState)
		{
			clean();
//			System.out.println("ECCore : " + equipment.getName() + " " + state[equipment.getECCState()]);
		}
		setState();
		upArrows();
	}

	/**
	 * Active ou désactive le mode expert qui autorise à cliquer sur tous les boutons
	 * @param mode : expert ou non
	 */
	public void setExpertMode(boolean mode)
	{
		expertMode = mode;
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(30, 30, 30));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(0, 40, 20));
		g.fillRect(5, 325, 300, 170);
		if(currentState >= READY)
		{
			g.setColor(Color.GREEN);
			if(diode || currentState == RUNNING)
				g.fillOval(25, 350, 10, 10);
			else
			{
				g.setColor(new Color(0, 120, 20));
				g.fillOval(25, 350, 10, 10);
			}
			g.setColor(Color.GREEN);
		}
		else g.setColor(new Color(0, 100, 50));
		g.drawRect(5, 325, 290, 170);
		g.drawString("ACTIVE", 10, 340);
	}

	public void mousePressed(MouseEvent evt) {}

	public void mouseReleased(MouseEvent evt) {}

	public void mouseEntered(MouseEvent evt) {}

	public void mouseExited(MouseEvent evt) {}

	public void mouseClicked(MouseEvent evt)
	{
		if (evt.getSource() instanceof Arrow)
		{
			Arrow arr = (Arrow)evt.getSource();

			if (!arr.isActive() && !expertMode) return;

			arr.wait(true);

			if (nextArrow.contains(arr)) // Si on a cliqué sur une flèche next
			{
				Holder<String> holder = new Holder<String>();

				switch(nextArrow.indexOf(arr) + 1) // +1 car IDLE est à 1 et pas 0 donc décalage
				{
					case IDLE : 
								equipment.describe("<User>"+Common.getLoginName()+"</User>"+"<Experiment>"+Common.getRCCName()+"</Experiment>", holder);
								break;

					case DESCRIBED :
								equipment.prepare("test", holder);
								break;

					case PREPARED:
								equipment.configure(holder);
								break;

					case READY :
								equipment.start();
								if (currentState == 0) return;
								break;

					case RUNNING :
								break;
				}
				//System.out.println("holder : " + holder.value);
			}
			else //sinon il s'agit d'une flèche undo
			{
				switch(undoArrow.indexOf(arr) + 2) // +2 car +1 pour IDLE qui vaut 1 et +1 pour le fait que la première flèche annuler soit avec Describe
				{
					case IDLE :
								break;

					case DESCRIBED :
								equipment.undo();
								break;

					case PREPARED :
								equipment.undo();
								break;

					case READY :
								equipment.breakup();
								break;

					case RUNNING :
								equipment.stop();
								break;
				}
			}
		}
	}

	/**
	 * Définit l'état courrant en le mettant à true et les autres à false
	 */
	public void setState()
	{
		for (Etat etat : etats)
		{
			etat.set(false);
		}

		etats[currentState - 1].set(true);
	}
}
