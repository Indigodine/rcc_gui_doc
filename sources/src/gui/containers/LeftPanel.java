package gui.containers;

import gui.components.ActivityIndicator;
import gui.components.ImagePanel;
import gui.components.Img;

import java.awt.Dimension;
//import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Common;

/**
 * Panel de gauche qui contient :
 * - Logo GANIL
 * - Mode (Surveillance / Edition)
 * - Etat complet (Avec transition et diode Erreur)
 * - Arbre des équipements
 * 
 * @author malassigne
 *
 */
public class LeftPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel activityPanel = new JPanel();
	private ActivityIndicator activity = new ActivityIndicator();
	private TreeEquipement tree = new TreeEquipement(Common.myClientSOAP.getExpName());
	private JPanel logoPanel = new JPanel();
	private ImagePanel logo = new ImagePanel(150,41);
	private JPanel modePanel = new JPanel();
	
	public LeftPanel(JLabel mode) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		java.net.URL url = LeftPanel.class.getResource("/img/ganil.png");
		logo.setImage( Img.get("logo") );
//		logo.setImage(Toolkit.getDefaultToolkit().createImage(url));
		logoPanel.add( logo );
		add( logoPanel );
		modePanel.add( mode );
		add( modePanel );
		activityPanel.add( activity );
		add( activityPanel );
		add( tree );
		setMinimumSize(new Dimension(210, 768));
	}

	public JPanel getLogoPanel() {
		return logoPanel;
	}

	public TreeEquipement getTree() {
		return tree;
	}

	public ActivityIndicator getIndicator() {
		return activity;
	}
}
