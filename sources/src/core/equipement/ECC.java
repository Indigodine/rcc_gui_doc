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
import javax.xml.ws.Holder;

import core.Common;
import gui.containers.Window;

/**
 * cette classe permet d'instancier un equipement de type MIDAS
 * 
 * 
 */
public class ECC extends Equipement
{
	private int portNumber;

	/**
	 * constructeur de la classe
	 * 
	 * @param nomEquipement
	 *            nom de l'equipement
	 * @param nomHost
	 *            nom de l'hote
	 */
	public ECC(Window frame, String nomEquipement, String nomHost, int port)
	{
		super(frame, nomEquipement, nomHost, EquipementType.ECC);
		portNumber = port;
	}

	@Override
	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField nameField = new JTextField(name);
		hostName = Common.myClientSOAP.getEqtHost(name);
		final JTextField hostNameField = new JTextField(hostName);
		portNumber = Common.myClientSOAP.getECCorePort(name);
		final JTextField portNumberField = new JTextField(Integer.toString(portNumber));
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel("Port :", SwingConstants.RIGHT));
		tmp.add(portNumberField);
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
		portNumberField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int newPortNumber = Integer.parseInt(portNumberField.getText());
				if (Common.myClientSOAP.setECCorePort(name, newPortNumber)) {
					portNumber = newPortNumber;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
/*
		portNumberField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				int newPortNumber = Integer.parseInt(portNumberField.getText());
				if (Common.myClientSOAP.setECCorePort(name, newPortNumber)) {
					portNumber = newPortNumber;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
*/
		boutonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hostName = Common.myClientSOAP.getEqtHost(name);
				hostNameField.setText( hostName );
				portNumber = Common.myClientSOAP.getECCorePort(name);
				portNumberField.setText( Integer.toString( portNumber ) );
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
				int newPortNumber = Integer.parseInt(portNumberField.getText());
				portNumber = Common.myClientSOAP.getECCorePort(name);
				if (portNumber != newPortNumber) {
					if (Common.myClientSOAP.setECCorePort(name, newPortNumber)) {
						portNumber = newPortNumber;
					}
				}
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}

	public void describe(String table, Holder<String> str)
	{
		Common.myClientSOAP.eccoreDescribe(getName(), table, str);
	}
	
	public void prepare(String table, Holder<String> str)
	{
		Common.myClientSOAP.eccorePrepare(getName(), table, str);
	}
	
	public void configure(Holder<String> table)
	{
		Common.myClientSOAP.eccoreConfigure(getName(), table);
	}

	public void undo()
	{
		Common.myClientSOAP.eccoreUndo(getName());
	}

	public void breakup()
	{
		Common.myClientSOAP.eccoreBreakup(getName());
	}

	public int getECCState()
	{
		return Common.myClientSOAP.eccoreState(getName());
	}

	public String getListeConfigID()
	{
		return Common.myClientSOAP.getECCoreListeConfigID(getName());
	}

	public String getConfigID()
	{
		return Common.myClientSOAP.getECCoreConfigID(getName());
	}

	public void setConfigID(String id)
	{
		Common.myClientSOAP.setECCoreConfigID(getName(), id);
	}

	public int getPortNumber()
	{
		return portNumber;
	}

	public void setPortNumber(int port)
	{
		portNumber = port;
	}
}
