package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
//import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JSplitPane;

import core.Common;
import core.SMState;
import core.equipement.*;
import core.reseau.Communication;
import core.reseau.ReponseParam;
import ecc.ECCConfigPanel;
import gui.containers.TranslucentPopupMenu;
import gui.containers.Window;

@SuppressWarnings("serial")
/**
 * Affiche un équipement sous cette forme :
 * -------------------
 *  TYPE D'EQUIPEMENT
 *        NOM        
 *        ETAT
 * -------------------
 * Cette classe gère plusieurs tailles d'affichage :
 * - BIG
 * - NORMAL
 * - Le reste sera considéré comme SMALL
 * Ceci affecte les deux tailles de
 * la police d'écriture (normal et little)
 * et la taille globale du composant
 * @see Displayable
 */
public class EquipementUI extends Displayable {
	private boolean alive = true;
	private Window frame;
	private Dimension componentSize = new Dimension(70, 70);
	private String name = "";
	private String type = "";
	private boolean watcherOn = false;
	private boolean storageOn = false;
	private static final Color BACK_TYPE = Color.LIGHT_GRAY;
	private SMState status = SMState.NOMONITORING;
	private int normal = 12;
	private int little = 10;
	private Equipement eqt;
	private EquipementType eqtType;
	private boolean hasChanged = false;
	private BufferedImage render;
	private boolean stateChanged = false;
	private TranslucentPopupMenu popupSurv;
	private static final ImageIcon IMG_INIT = Img.icon("buttons/init");
	private static final ImageIcon IMG_START = Img.icon("buttons/start");
	private static final ImageIcon IMG_STOP = Img.icon("buttons/stop");
	private static final ImageIcon IMG_EXIT = Img.icon("buttons/exit");
	private static final Image IMG_EYE = Img.get("oeilmini");
	private static final Image IMG_STORAGE = Img.get("storage");
	private static final Image IMG_NOSTORAGE = Img.get("nostorage");
	private JMenuItem init = new JMenuItem("Init", IMG_INIT);
	private JMenuItem start = new JMenuItem("Start", IMG_START);
	private JMenuItem stop = new JMenuItem("Stop", IMG_STOP);
	private JMenuItem exit = new JMenuItem("Breakup", IMG_EXIT);
	private JMenuItem modifier = new JMenuItem(Common.getString("Modifier"));
	private JMenuItem delete = new JMenuItem(Common.getString("delete"));
	private JMenuItem specificCmdPanel = new JMenuItem(Common.getString("SpecificCmdPanel"));
	private JMenuItem params = new JMenuItem(Common.getString("ParametersList"));
	/**
	 * Constructeur
	 * @param eqt
	 * l'équipement lié à cet affichage
	 * @param taille
	 * la taille désirée :
	 * BIG
	 * NORMAL
	 * Autre : SMALL
	 */
	public EquipementUI(Window frame, Equipement eqt, String taille) {
		this.frame = frame;
		this.eqt = eqt;
		if (eqt != null) {
			this.type = this.eqt.getType().getName();
			this.name = this.eqt.getName();
			this.eqtType = this.eqt.getType();
		}
		refresh();
		setSize(taille);
		setPreferredSize(componentSize);
		initPopup();

		new Thread() {
			public void run() {
				while (alive) {
					refresh();
					try {sleep(freq * 100);} catch(InterruptedException e) {}
				}
			}
		}.start();
	}

	public void kill() {
		alive = false;
	}

	public void initPopup() {

		popupSurv = new TranslucentPopupMenu();

		init.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				init();
			}
		});

		popupSurv.add( init );

		start.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				start();
			}
		});		

		popupSurv.add( start );

		stop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			     stop();
			}
		});		

		popupSurv.add( stop );

		exit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exit();
			}
		});

		popupSurv.add( exit );

		modifier.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (eqt != null) {
					eqt.openModificationPanel();
					name = eqt.getName();
				}
			}
		});

		popupSurv.add( modifier );

		delete.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (JOptionPane.showConfirmDialog(frame, Common.getString("voulez_Vous_Supprimer") + " : " + name,
						   Common.getString("suppression"), JOptionPane.YES_NO_OPTION)==0) {
					delete();					   
				}
			}
		});

		popupSurv.add( delete );

		if (eqtType == EquipementType.ECC) {
			specificCmdPanel.addActionListener( new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					new ECCConfigPanel( frame, name, (ECC)eqt );
				}
			});

			popupSurv.add( specificCmdPanel );
		}

		params.addActionListener( new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ParameterList lp = new ParameterList( name );
				lp.setLocationRelativeTo( frame );
				lp.setVisible(true);
			}
		});

		popupSurv.add( params );

		if ( Common.isOnline() == false ) {
			init.setEnabled( false );
			start.setEnabled( false );
			stop.setEnabled( false );
			exit.setEnabled( false );
		}
	}

	public TranslucentPopupMenu getPopupSurv() {
		return popupSurv;
	}

	/**
	 * @return
	 * L'équipement associé à cet affichage
	 */
	public Equipement getEqt() {
		return eqt;
	}

	/**
	 * @return
	 * le nom de cet équipement
	 */
	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	public void setWatcherOn(boolean b) {
		watcherOn = b;
	}

	public boolean isWatcherOn() {
		return watcherOn;
	}

	public void setStorageOn(boolean b) {
		storageOn = b;
	}

	public boolean isStorageOn() {
		return storageOn;
	}

	/**
	 * @return
	 * le type d'équipement
	 */
	public String getType() {
		return type;
	}

	public EquipementType getEqtType() {
		return eqtType;
	}

	/**
	 * Défini une nouvelle taille pour ce composant
	 * @param size
	 * BIG : Grande taille (75px * 75px)
	 * NORMAL : Taille normale  (60px * 60px)
	 * ??? : Taille petite (50px * 50px)
	 */
	public void setSize(String size) {
		if(size.equals("BIG")) {
			normal = 14;
			little = 12;
			componentSize = new Dimension(75, 75);
		} else if(size.equals("NORMAL")) {
			normal = 12;
			little = 10;
			componentSize = new Dimension(60, 60);
		} else {
			normal = 10;
			little = 8;
			componentSize = new Dimension(50, 50);
		}
		hasChanged = true;
	}

	/**
	 * Méthode héritée de Displayable, redéfinie pour
	 * rafraîchir l'état
	 * @see Displayable
	 */
	public void refresh() {

		if (Common.stopMonitor) {
			offline();
			return;
		}

		SMState old = status;

		if (eqt != null) {

			if ((eqtType == EquipementType.ACTOR) || (eqtType == EquipementType.EVENTBUILDER) || (eqtType == EquipementType.SBUFPRODUCER)) {
				if ( !(((Actor)eqt).getContainerNARVAL().equals("agata")) ) {
					watcherOn = ((Actor)eqt).isWatcherOn();
				}
			}

			if (eqtType == EquipementType.STORAGE) {
				storageOn = ((Storage)eqt).isStorageOn();
			}

			if (eqtType == EquipementType.ACTOR) {
//				if ( !(((Actor)eqt).getContainerNARVAL().equals("agata")) ) {
					status = eqt.getState();
//				}
			} else {

				status = eqt.getState();
			}

			if (old != status) {
				hasChanged = true;
				stateChanged = true;
			}
		}
	}

	/**
	 * Affiche l'équipement hors-ligne
	 * Ne doit servir qu'au moment du passage en mode EDITION
	 * @see SMState
	 */
	public void offline() {
		init.setEnabled(false);
		start.setEnabled(false);
		stop.setEnabled(false);
		exit.setEnabled(false);
		delete.setEnabled(true);
		if (status == SMState.NOMONITORING) return;
		status = SMState.NOMONITORING;
		stateChanged = true;
	}

	/**
	 * Passe l'équipement online
	 * Ne doit servir qu'au moment du passage en mode SURVEILLANCE
	 * @see SMState
	 */
	public void online() {
		init.setEnabled(true);
		start.setEnabled(true);
		stop.setEnabled(true);
		exit.setEnabled(true);
		delete.setEnabled(false);
		refresh();
	}

	/**
	 * Est-ce que l'equipementUI a subit une 
	 * modification visuelle ?
	 * @return
	 */
	public boolean modified() {
		return hasChanged;
	}

	/**
	 * @return
	 * TRUE si l'état à changé
	 * FALSE sinon
	 */
	public boolean stateHasChanged() {
		boolean ret = stateChanged;
		stateChanged = false;
		return ret;
	}

	public void paint(Graphics g) {
		int w = (int)componentSize.getWidth();
		int h = (int)componentSize.getHeight();
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, w, h);
		g.setColor(BACK_TYPE);
		g.fillRect(1, 1, w-2, 15);

		g.setFont(new Font("Courrier", Font.BOLD, little-2));
	    int posX = (w - g.getFontMetrics().stringWidth(type)) / 2;
		g.setColor(Color.BLACK);

		if (watcherOn) g.drawImage( IMG_EYE, w-20, h-20, 20, 20, null);

		if (eqtType == EquipementType.STORAGE) {
			g.drawString("run #" + Integer.toString(Common.myClientSOAP.getStorageRunNumber(name)), posX, 13);
			if (storageOn) g.drawImage( IMG_STORAGE, w-30, h-30, 30, 30, null);
			else g.drawImage( IMG_NOSTORAGE, w-30, h-30, 30, 30, null);
		} else g.drawString(type, posX, 13);

	    g.setFont(new Font("Courrier", Font.PLAIN, (name.length() > 8) ? little-2 : little));
	    posX = (w - g.getFontMetrics().stringWidth(name)) / 2;
	    int posY = (h - g.getFontMetrics().getAscent()) / 2 + g.getFontMetrics().getAscent();
		g.setColor(Color.WHITE);
		g.drawString(name, posX, posY);

		g.setFont(new Font("ARIAL", Font.BOLD, 8));
		String etat = status.name();
		if (status == SMState.NOMONITORING) etat = "";
		posX = (w - g.getFontMetrics().stringWidth(etat)) / 2;
		g.setColor(status.getColor());
		g.drawString(etat, posX, h-7);
		g.fillRect(0, h-5, w, 4);

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, w-1, h-1);
	}

	@Override
	public boolean equals(Object another) {
		if (another instanceof EquipementUI) {
			EquipementUI other = (EquipementUI)another;
			return other.getName().equals(name);
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public void init() {
		new Thread()  {
			public void run() {
				eqt.init();
			}
		}.start();
	}

	public void start() {
		new Thread()  {
			public void run() {
				eqt.start();
			}
		}.start();
	}

	public void stop() {
		new Thread()  {
			public void run() {
				eqt.stop();
			}
		}.start();
	}

	public void exit() {
		new Thread()  {
			public void run() {
				eqt.exit();
			}
		}.start();
	}

	public void delete() {
		if (Communication.deleteEqt(name) == true) {
			eqt = null;
			remove();
		}
	}

	/**
	 * @return
	 * L'image de ce composant.
	 * Cette image est récupérée par la grille qui se charge de l'afficher
	 */
	public BufferedImage getImage() {
		int w = (int)componentSize.getWidth();
		int h = (int)componentSize.getHeight();
		render = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = render.createGraphics();
		paint(g);
		hasChanged = false;
		return render;
	}

	public ImageIcon getStateImg() {
		stateChanged = false;
		return Img.icon("eqt/"+status.name().toLowerCase());
	}

	public SMState getState() {
		return status;
	}

	class ParameterList extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1L;
		JPanel panel = null;
		int nb = 0;
		String nomEqt = "";
		JButton boutonRefresh = new JButton(Common.getString("Update"));
		JButton boutonClose = new JButton(Common.getString("Fermer"));
		Vector<Parameter> vecteurParam = new Vector<Parameter>();

		public ParameterList(String nom) {
			nomEqt = nom;
			setTitle(Common.getString("ParametersList") + " : " + nomEqt);
			nb = Common.myClientSOAP.getParameterCount(nomEqt);
			panel = new JPanel(new GridLayout(nb, 1));
			JPanel sud = new JPanel();
			sud.add(boutonRefresh);
			sud.add(boutonClose);
			boutonRefresh.addActionListener(this);
			boutonClose.addActionListener(this);

			if (nb == 0) {
				JLabel label = new JLabel(Common.getString("NoParameter"));
				label.setForeground(new Color(80, 153, 252));
				label.setFont(new Font("Purisa", Font.BOLD, 20));
				panel.add(label);
			} else {
				for (int i = 0; i < nb; i++) {
					Color couleur;
					if (i % 2 == 0) couleur = Color.gray.brighter();
					else couleur = Color.white;
					ReponseParam reponse = Common.myClientSOAP.getParameterByIndex(nomEqt, i);
					Parameter p = new Parameter(reponse, nomEqt, couleur);
					vecteurParam.add( p );
					panel.add( p );
					p.refresh();
				}
			}

			JScrollPane scrollPane = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			setPreferredSize(new Dimension(500,600));
			JSplitPane panelPrinc = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollPane,sud);
			panelPrinc.setDividerLocation(525);
			setContentPane( panelPrinc );
			pack();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == boutonRefresh) {
				for (Parameter p : vecteurParam) p.refresh();
			}
			else if (e.getSource() == boutonClose) {
				for (Parameter p : vecteurParam) p.update();
				dispose();
			}
			frame.getContentPanel().getGrid().repaint();
		}
	}

	class Parameter extends JPanel implements ActionListener, FocusListener {
		private static final long serialVersionUID = 1L;
		ReponseParam reponseParam = null;
		JLabel nom = new JLabel();
		JTextField valeur = new JTextField(20);
		String nomEqt = "";
		String nomParam = "";
		String type = "";
		String permission = "";

		public Parameter(ReponseParam rp, String n, Color couleur) {
			setLayout(new GridLayout(1, 2));
			reponseParam = rp;
			nomEqt = n;
			nomParam = reponseParam.getStringName();
			nom.setForeground(new Color(80, 153, 252));
			setBackground(couleur);
			nom.setFont(new Font("Purisa", Font.BOLD, 12));
			nom.setText( nomParam );
			add(nom);
			type = reponseParam.getStringType();
			valeur.setText( reponseParam.getStringValue() );
			valeur.setBackground(couleur);
			add(valeur);
			permission = reponseParam.getStringPermission();

//			if (permission.toUpperCase().equals("READ_ONLY")) {
//				valeur.setEditable(false);
//			} else {
				valeur.addActionListener(this);
				valeur.addFocusListener(this);
//			}
		}

		public void refresh() {
			String val = Common.myClientSOAP.readParameter(nomEqt, nomParam);
			if (!val.equals("ERROR")) valeur.setText( val );
		}

		public void update() {
			String val = Common.myClientSOAP.readParameter(nomEqt, nomParam);
			if (!val.equals("ERROR")) {
				String newVal = valeur.getText();
				if (!val.equals(newVal)) Common.myClientSOAP.writeParameter(nomEqt, nomParam, newVal);
			}
		}

		public void actionPerformed(ActionEvent e) {
			Common.myClientSOAP.writeParameter(nomEqt, nomParam, valeur.getText());
			frame.getContentPanel().getGrid().repaint();
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
//			Common.myClientSOAP.writeParameter(nomEqt, nomParam, valeur.getText());
		}
	}
}
