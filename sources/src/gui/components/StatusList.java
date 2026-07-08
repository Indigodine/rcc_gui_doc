package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.BorderFactory;
import core.Common;
import core.SMState;
import core.equipement.*;

public class StatusList extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton boutonClose = new JButton(Common.getString("Fermer"));

	public StatusList() {

//		System.out.println("Etat des equipements");
		setTitle(Common.getString("Show_Status"));

		ArrayList<Equipement> eqtList = Common.myClientSOAP.getState().getListEqt();

		JPanel panel = new JPanel(new GridLayout(0,1));

		if (eqtList != null) {
			for (Equipement eqt : eqtList) {
				if (eqt != null) {
					if ((eqt.getType() == EquipementType.SUBSYSTEM_NARVAL)
							 || (eqt.getType() == EquipementType.MIDAS)
							 || (eqt.getType() == EquipementType.VMECOM)
							 || (eqt.getType() == EquipementType.ECC)) {
						System.out.println(eqt.getName());
						JPanel ligne = new JPanel(new GridLayout(1,2));
						JLabel nom = new JLabel(eqt.getName());
						ligne.add(nom);
						Common.myClientSOAP.equipmentState(eqt.getName());
						SMState status = eqt.getState();
						JLabel etat = new JLabel(status.name());
						ligne.add(etat);
						panel.add(ligne);
						ligne.setBorder(BorderFactory.createLineBorder(Color.BLUE));
					}
				}
			}
		}

		JPanel sud = new JPanel();
		sud.add(boutonClose);
		boutonClose.addActionListener(this);

		JScrollPane scrollPane = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setPreferredSize(new Dimension(300,200));
		JSplitPane panelPrinc = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollPane,sud);
		panelPrinc.setDividerLocation(130);
		setContentPane(panelPrinc);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == boutonClose) {
			dispose();
		}		
	}
}