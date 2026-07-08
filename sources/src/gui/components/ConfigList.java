package gui.components;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import core.Common;
import core.equipement.*;

public class ConfigList extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JList list1;
	private JList list2;
	private JButton haut = new JButton(Img.icon("haut"));
	private JButton bas = new JButton(Img.icon("bas"));
	private JButton valider = new JButton("OK");
	private JButton annuler = new JButton(Common.getString("Cancel"));
	private JButton initliste = new JButton(Common.getString("Reset"));
	private JButton emptyliste = new JButton(Common.getString("Empty"));
	private JButton addButton = new JButton("-->");
	private JButton removeButton = new JButton("<--");
	private Vector<String> vect = new Vector<String>();
	private Vector<String> equipments = new Vector<String>();
	private Vector<String> availableEquipments = new Vector<String>();
	private JPanel panelTop = new JPanel();
	private JPanel panelBottom = new JPanel();
	private JPanel panelLeft = new JPanel();
	private JPanel panelCenter = new JPanel();
	private JPanel panelCenter_1 = new JPanel();
	private JPanel panelCenter_1_1 = new JPanel();
	private JPanel panelCenter_2 = new JPanel();
	private JPanel panelRight = new JPanel();
	private JLabel label1 = new JLabel(Common.getString("Available_equipments"));
	private JLabel label2 = new JLabel();
	private boolean startOrStop;

	public ConfigList(boolean b)
	{
		panelTop.setLayout( new BorderLayout() );
		panelBottom.setLayout( new FlowLayout() );
		panelLeft.setLayout( new BorderLayout() );
		panelCenter.setLayout ( new FlowLayout() );
		panelCenter_1.setLayout( new BorderLayout() );
		panelCenter_1_1.setLayout( new BorderLayout() );
		panelCenter_2.setLayout( new BorderLayout() );
		panelRight.setLayout( new GridLayout(2,1) );

		String title = Common.getString("modifStartListe");
		String label = Common.getString("START_list");

		Border loweredbevel1 = BorderFactory.createLoweredBevelBorder();
		Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

		startOrStop = b;

		if (!startOrStop)
		{
			title = Common.getString("modifStopListe");
			label = Common.getString("STOP_list");
		}

		setTitle(title);
		setLayout( new BorderLayout() );

		ArrayList<Equipement> eqtList = Common.myClientSOAP.getState().getListEqt();

		if (eqtList != null)
		{
			for (Equipement eqt : eqtList)
			{
				if (eqt != null)
				{
					if ((eqt.getType() == EquipementType.SUBSYSTEM_NARVAL)
					 || (eqt.getType() == EquipementType.MIDAS)
					 || (eqt.getType() == EquipementType.VMECOM)
					 || (eqt.getType() == EquipementType.ECC))
					{
						equipments.add( eqt.getName() );
					}
				}
			}
		}

		for (String name : equipments)
		{
			availableEquipments.add( name );
		}

		String list = "";

		if (startOrStop) list = Common.myClientSOAP.getStartList();
		else list = Common.myClientSOAP.getStopList();

		StringTokenizer st = new StringTokenizer(list, ";");

		while ( st.hasMoreTokens() )
		{
			vect.add( st.nextToken() );
		}

		for (String name : vect)
		{
			for (String n : availableEquipments)
			{
				if (name.equals( n ))
				{
					availableEquipments.remove( n );
					break;
				}
			}
		}

		list1 = new JList( availableEquipments );
		list1.setSelectedIndex(0);
		list1.setCellRenderer(new Rendu());
		list1.setBorder(loweredbevel1);

		list2 = new JList( vect );
		list2.setSelectedIndex(0);
		list2.setCellRenderer(new Rendu());
		list2.setBorder(loweredbevel2);

		label2.setText( label );

		panelLeft.add( label1, BorderLayout.NORTH );
		panelLeft.add( list1 );

		panelCenter_1_1.add( addButton, BorderLayout.NORTH );
		panelCenter_1_1.add( removeButton, BorderLayout.SOUTH );

		panelRight.add( haut );
		panelRight.add( bas );

		panelCenter_1.add( panelCenter_1_1, BorderLayout.CENTER );

		panelCenter_2.add( label2, BorderLayout.NORTH );
		panelCenter_2.add( list2 );

		panelCenter.add( panelCenter_1 );
		panelCenter.add( panelCenter_2 );

		panelTop.add( panelLeft,   BorderLayout.WEST );
		panelTop.add( panelCenter, BorderLayout.CENTER );
		panelTop.add( panelRight,  BorderLayout.EAST );

		panelBottom.add( valider );
		panelBottom.add( annuler );
		panelBottom.add( initliste );
		panelBottom.add( emptyliste );

		add( panelTop,    BorderLayout.NORTH );
		add( panelBottom, BorderLayout.SOUTH );

		valider.addActionListener(this);
		annuler.addActionListener(this);
		initliste.addActionListener(this);
		emptyliste.addActionListener(this);
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		haut.addActionListener(this);
		bas.addActionListener(this);

		setBackground(Color.white);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == haut)
		{
			int index = vect.indexOf( list2.getSelectedValue() );

			if (index > 0)
			{
				Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

				panelCenter_2.remove(list2);
				panelCenter_2.validate();

				String tmp = vect.elementAt(index);

				vect.removeElementAt(index);
				vect.add(index - 1, tmp);

				list2 = new JList( vect );
				list2.setSelectedIndex(index - 1);
				list2.setCellRenderer(new Rendu());
				list2.setBorder(loweredbevel2);

				panelCenter_2.add( list2 );
				panelCenter_2.validate();

				pack();
				repaint();
			}
		}
		else if (e.getSource() == bas)
		{
			int index = vect.indexOf( list2.getSelectedValue() );

			if (index < (vect.size() - 1))
			{
				Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

				panelCenter_2.remove( list2 );
				panelCenter_2.validate();

				String tmp = vect.elementAt(index);

				vect.removeElementAt(index);
				vect.add(index + 1, tmp);

				list2 = new JList( vect );
				list2.setSelectedIndex(index + 1);
				list2.setCellRenderer(new Rendu());
				list2.setBorder(loweredbevel2);

				panelCenter_2.add( list2 );
				panelCenter_2.validate();

				pack();
				repaint();
			}
		}
		else if (e.getSource() == addButton)
		{
			Border loweredbevel1 = BorderFactory.createLoweredBevelBorder();
			Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

			Object obj = list1.getSelectedValue();

			availableEquipments.remove( obj );

			vect.add( obj.toString() );

			panelLeft.remove( list1 );
			panelLeft.validate();

			panelCenter_2.remove( list2 );
			panelCenter_2.validate();

			list1 = new JList( availableEquipments );
			list1.setSelectedIndex(0);
			list1.setCellRenderer(new Rendu());
			list1.setBorder(loweredbevel1);

			list2 = new JList( vect );
			list2.setSelectedIndex(0);
			list2.setCellRenderer(new Rendu());
			list2.setBorder(loweredbevel2);

			panelLeft.add( list1 );
			panelLeft.validate();

			panelCenter_2.add( list2 );
			panelCenter_2.validate();

			pack();
			repaint();
		}
		else if (e.getSource() == removeButton)
		{
			Border loweredbevel1 = BorderFactory.createLoweredBevelBorder();
			Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

			Object obj = list2.getSelectedValue();

			availableEquipments.add( obj.toString() );
			vect.remove( obj );

			panelLeft.remove( list1 );
			panelLeft.validate();

			panelCenter_2.remove( list2 );
			panelCenter_2.validate();

			list1 = new JList( availableEquipments );
			list1.setSelectedIndex(0);
			list1.setCellRenderer(new Rendu());
			list1.setBorder(loweredbevel1);

			list2 = new JList( vect );
			list2.setSelectedIndex(0);
			list2.setCellRenderer(new Rendu());
			list2.setBorder(loweredbevel2);

			panelLeft.add( list1 );
			panelLeft.validate();

			panelCenter_2.add( list2 );
			panelCenter_2.validate();

			pack();
			repaint();
		}
		else if (e.getSource() == valider)
		{
			String list = "";

			for (String d : vect) {
				list = list + d + ";";
			}
			try {list = list.substring(0, list.lastIndexOf(";"));} catch (StringIndexOutOfBoundsException ex) {list = "";}

			if (startOrStop) Common.myClientSOAP.setStartList(list);
			else Common.myClientSOAP.setStopList(list);

			dispose();
		}
		else if (e.getSource() == annuler)
		{
			dispose();
		}
		else if (e.getSource() == initliste)
		{
			Border loweredbevel1 = BorderFactory.createLoweredBevelBorder();
			Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

			availableEquipments.removeAllElements();
			vect.removeAllElements();

			for (String name : equipments)
			{
				availableEquipments.add( name );
			}

			String list = "";

			if (startOrStop) list = Common.myClientSOAP.getStartList();
			else list = Common.myClientSOAP.getStopList();

			StringTokenizer st = new StringTokenizer(list, ";");
			while ( st.hasMoreTokens() ) {
				vect.add( st.nextToken() );
			}

			for (String name : vect)
			{
				for (String n : availableEquipments)
				{
					if (name.equals( n ))
					{
						availableEquipments.remove( n );
						break;
					}
				}
			}

			panelLeft.remove( list1 );
			panelLeft.validate();

			panelCenter_2.remove( list2 );
			panelCenter_2.validate();

			list1 = new JList( availableEquipments );
			list1.setSelectedIndex(0);
			list1.setCellRenderer(new Rendu());
			list1.setBorder(loweredbevel1);

			list2 = new JList( vect );
			list2.setSelectedIndex(0);
			list2.setCellRenderer(new Rendu());
			list2.setBorder(loweredbevel2);

			panelLeft.add( list1 );
			panelLeft.validate();

			panelCenter_2.add( list2 );
			panelCenter_2.validate();

			pack();
			repaint();
		}
		else if (e.getSource() == emptyliste)
		{
			Border loweredbevel1 = BorderFactory.createLoweredBevelBorder();
			Border loweredbevel2 = BorderFactory.createLoweredBevelBorder();

			availableEquipments.removeAllElements();
			vect.removeAllElements();

			for (String name : equipments)
			{
				availableEquipments.add( name );
			}

			panelLeft.remove( list1 );
			panelLeft.validate();

			panelCenter_2.remove( list2 );
			panelCenter_2.validate();

			list1 = new JList( availableEquipments );
			list1.setSelectedIndex(0);
			list1.setCellRenderer(new Rendu());
			list1.setBorder(loweredbevel1);

			list2 = new JList( vect );
			list2.setSelectedIndex(0);
			list2.setCellRenderer(new Rendu());
			list2.setBorder(loweredbevel2);

			panelLeft.add( list1 );
			panelLeft.validate();

			panelCenter_2.add( list2 );
			panelCenter_2.validate();

			pack();
			repaint();
		}
	}

	public class Rendu extends JLabel implements ListCellRenderer
	{
		private static final long serialVersionUID = 1L;
		ImageIcon icon;
		ImageIcon selectIcon;
		Color selectCouleur = Color.RED;

		public Rendu()
		{}

		public Component getListCellRendererComponent(JList list, Object value, // valeur
				// à
				// afficher
				int index, // indice d'item
				boolean isSelected, // l'item est-il sélectionné
				boolean cellHasFocus) // La liste a-t-elle le focus
		{

			String s = value.toString();
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(selectCouleur);
				setText(s);
				setIcon(selectIcon);
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
				setText(s);
			}
			setEnabled(list.isEnabled());
			setFont(new Font("Purisa", Font.BOLD, 20));
			setOpaque(true);
			return this;
		}
	}
}
