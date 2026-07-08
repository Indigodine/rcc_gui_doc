package gui.components;

import gui.Factory;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JDialog;

import core.Common;
import core.equipement.EquipementType;
import core.reseau.Communication;
import gui.containers.ContentPanel;

@SuppressWarnings("serial")
public class EquipementBar extends Displayable implements ActionListener{

	public enum Action{
		SELECT,
		EQUIPMENT,
		LINK
	}

	private ContentPanel contentPanel = null;
	private JButton narval = new JButton(Common.getString("Subsystems_Narval"));
	private ButtonGroup group = new ButtonGroup();
	private JRadioButton selectMode = new JRadioButton(Common.getString("Selection"));
	private JRadioButton eqtMode = new JRadioButton(Common.getString("Add_Eqt"));
	private JRadioButton linkMode = new JRadioButton(Common.getString("Ajouter") + " " + Common.getString("Link"));
	private JButton add = new JButton(Common.getString("new"));
	private JButton remove = new JButton(Common.getString("delete"));
	private JComponent focus = null;
	private JComboBox subsystems = new JComboBox();
	private DefaultComboBoxModel model = new DefaultComboBoxModel();
	private static final Color FORE_1 = Color.BLUE;
	private static final Color FORE_2 = Color.BLACK;

	public EquipementBar(ContentPanel panel) {
		freq = 10;
		contentPanel = panel;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		selectMode.setForeground(FORE_2);
		eqtMode.setForeground(FORE_2);
		linkMode.setForeground(FORE_2);
		selectMode.addActionListener(this);
		eqtMode.addActionListener(this);
		linkMode.addActionListener(this);
		selectMode.setFocusPainted(false);
		eqtMode.setFocusPainted(false);
		linkMode.setFocusPainted(false);
		group.add(selectMode);
		group.add(eqtMode);
		group.add(linkMode);
		add(narval);
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setSize(10,0);
		add(separator);
		add(selectMode);
		add(eqtMode);
		add(linkMode);
		eqtMode.setSelected(true);
		setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
		setBackground(new Color(230, 240, 240));
		subsystems.setModel(model);

		final JDialog dialog = new JDialog(contentPanel.getFrame());
		dialog.setTitle(Common.getString("Subsystems_Narval"));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		dialog.setContentPane(p);
		p.add(subsystems);
		p.add(add);
		p.add(remove);

		narval.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setLocation(contentPanel.getFrame().getWidth()/4,contentPanel.getFrame().getHeight()/4);
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		
		remove.addActionListener( new ActionListener() {
			synchronized public void actionPerformed(ActionEvent e) {
				Communication.deleteEqt(model.getSelectedItem().toString());
				int index = subsystems.getSelectedIndex();
				subsystems.setSelectedIndex(index-1);
				dialog.dispose();
			}
		} );

		add.addActionListener( new ActionListener() {
			synchronized public void actionPerformed(ActionEvent e) {
				Factory.createSubSystemNarval();
				dialog.dispose();
			}
		} );

		new Thread() {
			public void run() {
				while (true) {
					refresh();
					try {sleep(freq * 100);} catch(InterruptedException e) {}
				}
			}
		}.start();
	}
	
	synchronized public void actionPerformed(ActionEvent evt) {
			JComponent comp = (JComponent)evt.getSource();
			if(focus!=null) {
				focus.setForeground(FORE_2);
			}
			comp.setForeground(FORE_1);
			focus = comp;
	}

	synchronized public void refresh() {

		ArrayList<String> narvals = new ArrayList<String>();

		for (int i = 0; i < model.getSize(); i++) {
			narvals.add(model.getElementAt(i).toString());
		}

		if (contentPanel != null) {
			if (contentPanel.getGrid() != null) {
				if (contentPanel.getGrid().getEqts() != null) {
					for (EquipementUI e : contentPanel.getGrid().getEqts()) {
						if (e.getEqt() != null) {
							if (e.getEqt().getType() == EquipementType.SUBSYSTEM_NARVAL) {
								String name = e.getName();
								if (!narvals.contains(name)) model.addElement(name);
								else narvals.remove(name);
							}
						}
					}
				}
			}
		}

		for (String s : narvals) {
			model.removeElement(s);
		}
	}

	public String getNarval() {

		if (model.getSize() >= 1) return model.getSelectedItem().toString();

		return null;
	}

	public JRadioButton radio(String str) {
		return new JRadioButton(str, false);
	}

	public Action getAction() {
		if(selectMode.isSelected())
			return Action.SELECT;
		else if(linkMode.isSelected())
			return Action.LINK;
		else return Action.EQUIPMENT;
	}
}
