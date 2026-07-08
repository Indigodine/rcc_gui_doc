package ecc;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import core.Common;
import core.equipement.ECC;
import gui.components.Img;

/**
 * Cette classe regroupe toute l'interface de configuration de l'ECC :
 * liste des configIDs, matrice d'état et boutons de contrôle
 *
 */
public class ECCConfigPanel extends JDialog implements MouseListener{

	private static final long serialVersionUID = 9194224261897746331L;
	private ArrayList<ConfigID> configs = new ArrayList<ConfigID>();
	private ArrayList<String> configIdSet = new ArrayList<String>();
	private JButton selectConfig = new JButton(Common.getString("Change"));
	private JButton refresh = new JButton(Common.getString("Refresh"));
	private JCheckBox expertMode = new JCheckBox(Common.getString("Mode_expert"));
	private int selectedIndex = 0;
	private JScrollPane scroll;
	private JPanel content = new JPanel();
	private ECC equipment;
	private ConfigID currentConfigID = new ConfigID("", "", "");
	private MatriceEtat stateMatrix;
	private static final int LOOP_TIME = 4000;

	/**
	 * Constructeur de la classe
	 * @param frame
	 * la JFrame parente
	 * @param title
	 * le titre de la fenêtre
	 * @param geco
	 * l'ECC à contrôler
	 */
	public ECCConfigPanel(JFrame frame, String title, ECC eqt)
	{
		super(frame, title, false);
		this.setLocation(frame.getWidth() / 2, frame.getHeight() / 2);
		equipment = eqt;
		Dimension d = new Dimension(280, 300);
		scroll = new JScrollPane(content);
		scroll.setPreferredSize(d);
		JPanel tmp = new JPanel();
		d = new Dimension(280, 500);
		tmp.setPreferredSize(d);
		tmp.add(expertMode);
		tmp.add(scroll);
		tmp.add(refresh);
		tmp.add(new JLabel(Common.getString("ConfigID_used")));
		tmp.add(currentConfigID);
		tmp.add(selectConfig);
		JPanel global = new JPanel();
		global.add(tmp);
		stateMatrix = new MatriceEtat(equipment);
		global.add(stateMatrix);
		getContentPane().add(global);
		selectConfig.addMouseListener(this);
		selectConfig.setIcon( Img.icon("resume"));
		selectConfig.setFocusPainted(false);
		refresh.addMouseListener(this);
		refresh.setIcon( Img.icon("generate"));
		refresh.setFocusPainted(false);
		expertMode.addMouseListener(this);
		String xml = equipment.getListeConfigID();
		parse(xml);
		this.pack();
		this.setVisible(true);
		loop();
	}

	/**
	 * Récupère les noms de fichiers pour les opérations describe, prepare et configure
	 */
	public void parse(String xml)
	{
		configIdSet.clear();
		configs.clear();
		content.removeAll();
		content.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.insets = new Insets(5, 5, 0, 0);

		String[] tab = xml.split("<ConfigIdSet>");

		if (tab.length == 1)
		{
			System.out.println("<ConfigIdSet> not found");
		}
		else
		{
			String str = tab[1];

			tab = str.split("<ConfigId>");

			if (tab.length == 1)
			{
				System.out.println("<ConfigId> not found");
			}
			else
			{
				for (int i = 1; i < tab.length; i++)
				{
					str = tab[i].split("</ConfigId>")[0].trim();

					configIdSet.add(str);

					String[] table = str.split("<SubConfigId");

					if (table.length > 1)
					{
						String describe = "";
						String prepare = "";
						String configure = "";
						table = str.split("\"describe\">");
						if (table.length > 1) describe = table[1].split("</")[0].trim();
						table = str.split("\"prepare\">");
						if (table.length > 1) prepare = table[1].split("</")[0].trim();
						table = str.split("\"configure\">");
						if (table.length > 1) configure = table[1].split("</")[0].trim();
						configs.add( new ConfigID( describe, prepare, configure ) );
					}
				}

				for (ConfigID conf : configs)
				{
					content.add(conf, gbc);
					gbc.gridy++;
					conf.addMouseListener(this);
				}

				if (!configs.isEmpty()) configs.get(0).select(true);
			}
		}

		String str = equipment.getConfigID();

//		System.out.println("["+str+"]");

		tab = str.split("<ConfigId>");

		String describe = "";
		String prepare = "";
		String configure = "";

		if (tab.length == 1)
		{
			System.out.println("<ConfigId> not found");
		}
		else
		{
			str = tab[1].split("</ConfigId>")[0].trim();

			String[] table = str.split("<SubConfigId");

			if (table.length > 1)
			{
				table = str.split("\"describe\">");
				if (table.length > 1) describe = table[1].split("</")[0].trim();
				table = str.split("\"prepare\">");
				if (table.length > 1) prepare = table[1].split("</")[0].trim();
				table = str.split("\"configure\">");
				if (table.length > 1) configure = table[1].split("</")[0].trim();
			}
		}

		currentConfigID.set( describe, prepare, configure);

		content.updateUI();
	}

	/**
	 * Renvoie une châine de caractère au format XML qui retranscrit les données du configId choisi
	 * @return
	 * une String qui représente le configId choisi
	 */
	public String generateConfig()
	{
		return "<ConfigId>" +
					"<SubConfigId type=\"describe\">" +
						configs.get(selectedIndex).getDescribe() +
					"</SubConfigId>" +
					"<SubConfigId type=\"prepare\">" +
						configs.get(selectedIndex).getPrepare() +
					"</SubConfigId>" +
					"<SubConfigId type=\"configure\">" +
						configs.get(selectedIndex).getConfigure() +
					"</SubConfigId>" +
				"</ConfigId>";
	}

	/**
	 * Sélectionne le configId passé en paramètre
	 * Les autres seront déselectionnés
	 * @param index
	 * l'index du configId à sélectionner
	 */
	public void selectConfig(int index)
	{
		for(int i=0 ; i <configs.size() ; i++)
		{
			configs.get(i).select(i==index);
		}
	}

	/**
	 * Thread qui boucle pour actualiser l'état de la matrice Etat
	 */
	public void loop()
	{
		new Thread()
		{
			public void run()
			{
				while(true)
				{
					stateMatrix.process();
					try{
						Thread.sleep(LOOP_TIME);
					}catch(InterruptedException e)
					{}
					int st = equipment.getECCState();
					stateMatrix.updateState(st);
					check(st);
					parse(equipment.getListeConfigID());
				}
			}
		}.start();
	}

	/**
	 * Cette méthode met à jour l'affichage et les autorisations.
	 * SI on est dans un état >= 2 (après IDLE)
	 * ET SI le mode expert est décoché
	 * ALORS on désactive le contrôle utilisateur sur les configId
	 * SINON le contrôle utilisateur est toujours opérationnel
	 * @param st
	 */
	public void check(int st)
	{
		boolean lock = st>=2 && !expertMode.isSelected();
		content.setEnabled(!lock);
		scroll.setEnabled(!lock);
		selectConfig.setEnabled(!lock);
		for(ConfigID config : configs)
		{
			config.setEnabled(!lock);
		}
	}

	public void mousePressed(MouseEvent evt) {}
	public void mouseReleased(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt)
	{
		if(evt.getSource() instanceof ConfigID)
		{
			ConfigID selectConfig = (ConfigID)evt.getSource();
			selectedIndex = configs.indexOf(selectConfig);
			selectConfig(selectedIndex);
		} else if(evt.getSource() == selectConfig && selectConfig.isEnabled())
		{
			selectConfig(selectedIndex);
			equipment.setConfigID(generateConfig());
			currentConfigID.setTo(configs.get(selectedIndex));
		} else if(evt.getSource() == refresh)
		{
			parse(equipment.getListeConfigID()); // refresh la liste des configId
		} else if(evt.getSource() == expertMode)
		{
			stateMatrix.setExpertMode(expertMode.isSelected());
		}
	}
}
