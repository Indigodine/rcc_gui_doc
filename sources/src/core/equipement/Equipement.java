package core.equipement;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.Common;
import core.SMState;
import core.reseau.ReponseStateMachine;
import gui.components.EquipementUI;
import gui.containers.Window;

/**
 * classe abstraite permettant de repertorier les methodes et variables communes
 * a tous les equipements
 * 
 * 
 */
public abstract class Equipement
{
	protected EquipementUI eqtUI = null;
	protected String name, hostName;
	protected EquipementType type;
	protected Window frame;
	private String messageError = null;
	protected SMState statut = SMState.OFFLINE;
	private boolean monitoring = true;
	private boolean error = false;

	/**
	 * constructeur de la classe
	 * 
	 * @param nomEquipement
	 *            nom de l'equipement
	 * @param nomHost
	 *            nom de l'hote
	 * @param type
	 *            le type de l'equipement
	 */
	public Equipement(Window frame, String nomEquipement, String nomHost, EquipementType type) {
		this.frame = frame;
		name = nomEquipement;
		hostName = nomHost;
		this.type = type;
	}

	public EquipementUI getEqtUI() {
		return eqtUI;
	}

	public void setEqtUI(EquipementUI e) {
		eqtUI = e;
	}

	public Window getFrame() {
		return frame;
	}

	public String getName() {
		return name;
	}

	public String getHostName() {
		return hostName;
	}

	public EquipementType getType() {
		return type;
	}

	public void setType(EquipementType type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField nameField = new JTextField(name);
		hostName = Common.myClientSOAP.getEqtHost(name);
		final JTextField hostNameField = new JTextField(hostName);
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		JButton boutonUpdate = new JButton(Common.getString("Update"));
		JButton boutonClose = new JButton(Common.getString("Fermer"));
		tmp.add( boutonUpdate );
		tmp.add( boutonClose );
		contentPanel.add(tmp);
		final JDialog dialog = new JDialog(frame);
		dialog.setTitle(Common.getString("Modifier") + " " + name);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(frame.getWidth()/4,frame.getHeight()/4);
		dialog.setVisible(true);

		nameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newName = nameField.getText();
				if (Common.myClientSOAP.modifNomEqt(name, newName)) {
					name = newName;
					if (eqtUI != null) eqtUI.setName(name);
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
/*
		nameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newName = nameField.getText();
				if (Common.myClientSOAP.modifNomEqt(name, newName)) {
					name = newName;
					if (eqtUI != null) eqtUI.setName(name);
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
*/
		hostNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newHostName = hostNameField.getText();
				if (Common.myClientSOAP.setEqtHost(name, newHostName)) {
					hostName = newHostName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
/*
		hostNameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newHostName = hostNameField.getText();
				if (Common.myClientSOAP.setEqtHost(name, newHostName)) {
					hostName = newHostName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
*/
		boutonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hostName = Common.myClientSOAP.getEqtHost(name);
				hostNameField.setText( hostName );
				frame.getContentPanel().getGrid().repaint();
			}
		});

		boutonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newName = nameField.getText();
				if (!name.equals(newName)) {
					if (Common.myClientSOAP.modifNomEqt(name, newName)) {
						name = newName;
						if (eqtUI != null) eqtUI.setName(name);
					}
				}
				String newHostName = hostNameField.getText();
				hostName = Common.myClientSOAP.getEqtHost(name);
				if (!hostName.equals(newHostName)) {
					if (Common.myClientSOAP.setEqtHost(name, newHostName)) {
						hostName = newHostName;
					}
				}
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}

	public double getDebitOut()
	{
		return 0.0;
	}

	public double getDebitIn()
	{
		return 0.0;
	}
	
	public double getEventDebitOut()
	{
		return 0.0;
	}

	public void razTempsAcqDebit()
	{
	}

	public void start() {
		Common.myClientSOAP.equipmentStart(name);
	}

	public void stop() {
		Common.myClientSOAP.equipmentStop(name);
	}

	public void init() {
		Common.myClientSOAP.equipmentInit(name);
	}

	public void resume() {
		Common.myClientSOAP.equipmentResume(name);
	}

	public void pause() {
		Common.myClientSOAP.equipmentPause(name);
	}

	public void exit() {
		Common.myClientSOAP.equipmentExit(name);
	}

	public void setMessageError(String message)
	{
		error = true;
		messageError = message;
	}

	public void setNoMessage()
	{
		error = false;
		messageError = null;
	}

	public String getMessageError()
	{
		return messageError;
	}

	public boolean isMessageError()
	{
		return error;
	}

	public SMState getState()
	{
		ReponseStateMachine out = Common.myClientSOAP.getEquipmentStateMachine(name);

		statut = SMState.decode( out.getIntSMState() );

		if (out.isSMWarning()) statut = SMState.WARNING;
		else if (out.isSMError()) statut = SMState.ERROR;

		return statut;
	}

	public void setMonitoring(boolean monitoring)
	{
		this.monitoring = monitoring;
	}

	public boolean isMonitoring()
	{
		return monitoring;
	}

	public boolean isWarning()
	{
		return (statut == SMState.WARNING);
	}
}
