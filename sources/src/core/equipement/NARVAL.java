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
import gui.containers.Window;

/**
 * cette classe permet d'instancier un equipement de type NARVAL
 * 
 * 
 */
public class NARVAL extends Equipement
{
	private String cpu;
	/**
	 * constructeur de la classe
	 * 
	 * @param nomEquipement
	 *            nom de l'equipement
	 * @param nomHost
	 *            nom de l'hote
	 */
	public NARVAL(Window frame, String nomEquipement, String nomHost, String cpu)
	{
		super(frame, nomEquipement, nomHost, EquipementType.SUBSYSTEM_NARVAL);
		this.cpu = cpu;
	}

	public String getCpu()
	{
		return cpu;
	}

	public void setCpu(String cpu)
	{
		this.cpu = cpu;
	}

	@Override
	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField nameField = new JTextField(name);
		final JTextField hostNameField = new JTextField(Common.myClientSOAP.getEqtHost(name));
		final JTextField cpuField = new JTextField(Common.myClientSOAP.getNarvalCoordinatorCpu(name));
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel("Coordinator CPU :", SwingConstants.RIGHT));
		tmp.add(cpuField);
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

		hostNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newHostName = hostNameField.getText();
				if (Common.myClientSOAP.setEqtHost(name, newHostName)) {
					hostName = newHostName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		cpuField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newCpu = cpuField.getText();
				if (Common.myClientSOAP.setNarvalCoordinatorCpu(name, newCpu)) {
					cpu = newCpu;
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

		cpuField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newCpu = cpuField.getText();
				if (Common.myClientSOAP.setNarvalCoordinatorCpu(name, newCpu)) {
					cpu = newCpu;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
*/
		boutonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hostName = Common.myClientSOAP.getEqtHost(name);
				hostNameField.setText( hostName );
				cpu = Common.myClientSOAP.getNarvalCoordinatorCpu(name);
				cpuField.setText( cpu );
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
				String newCpu = cpuField.getText();
				cpu = Common.myClientSOAP.getNarvalCoordinatorCpu(name);
				if (!cpu.equals(newCpu)) {
					if (Common.myClientSOAP.setNarvalCoordinatorCpu(name, newCpu)) {
						cpu = newCpu;
					}
				}
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}
}
