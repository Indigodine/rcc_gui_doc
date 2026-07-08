package gui.containers;

//import gui.components.DataFlowViewer;
import gui.components.StatusList;
import gui.components.ConfigList;
import gui.components.Displayable;
import gui.components.EquipementBar;
import gui.components.EquipementUI;
import gui.components.Img;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
//import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.xml.ws.WebServiceException;

import core.Common;
import core.Filtre;
import core.SMState;
import core.equipement.Equipement;
import core.equipement.EquipementType;
import core.equipement.LienEqt;
import core.reseau.Communication;
import core.reseau.StateExpSOAP;

/**
 * Classe qui sert de contenu à la fenêtre principale
 * @author malassigne
 *
 */
public class ContentPanel extends Displayable implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private Window frame;
	private LogPanel events = new LogPanel();
	private JLabel mode = new JLabel(Common.getString("Mode_Edition"));
	private LeftPanel left = new LeftPanel(mode);

	private JMenu storageMenu = new JMenu(Common.getString("Menu_Storage"));
	private JMenu fileMenu = new JMenu(Common.getString("Fichier"));
	private JMenu layoutMenu = new JMenu(Common.getString("Layout"));
	private JMenu optionsMenu = new JMenu(Common.getString("Options"));
	private JMenu configMenu = new JMenu(Common.getString("Configuration"));
	private JMenu modeMenu = new JMenu("Mode");
	private JMenu statusMenu = new JMenu("Status");
	private JMenu aboutMenu = new JMenu("About");
	private JLabel gRNLabel = new JLabel(Common.getString("GlobalRunNumber"));
	private JLabel gRNValue = new JLabel();
	private JTextField gRNTextField = new JTextField("",4);
	private JPanel gRNPanel = new JPanel();

	private JMenuItem showStatus = new JMenuItem(Common.getString("Show_Status"));
	private JMenuItem enable = new JMenuItem(Common.getString("Enable"));
	private JMenuItem disable = new JMenuItem(Common.getString("Disable"));

	/* Menu Fichier */
	private JMenuItem newFile = new JMenuItem(Common.getString("Menu_New"), Img.icon("menu/filenew"));
	private JMenuItem openFile = new JMenuItem(Common.getString("Menu_Open"), Img.icon("menu/fileopen"));
	private JMenuItem reload = new JMenuItem(Common.getString("Menu_Reload"), Img.icon("menu/reload"));
	private JMenuItem saveFile = new JMenuItem(Common.getString("Menu_Save"), Img.icon("menu/filesave"));
	private JMenuItem saveAsFile = new JMenuItem(Common.getString("Menu_Saveas"), Img.icon("menu/filesaveas"));
	private JMenuItem quit = new JMenuItem(Common.getString("Menu_Quit"), Img.icon("menu/exit"));

	/* Menu Layout */
	private JMenuItem saveLayout = new JMenuItem(Common.getString("Save_ui"));
	private JMenuItem loadLayout = new JMenuItem(Common.getString("Load_ui"));

	/* Menu Configuration */
	private JMenuItem startConfig = new JMenuItem(Common.getString("StartConfiguration"));
	private JMenuItem stopConfig = new JMenuItem(Common.getString("StopConfiguration"));

	/* Boutons de la barre d'outils */
	private JPanel panelCommande = new JPanel();
	private JButton init = new JButton("Init", Img.icon("buttons/init"));
	private JButton start = new JButton("Start", Img.icon("buttons/start"));
	private JButton stop = new JButton("Stop", Img.icon("buttons/stop"));
	private JButton exit = new JButton("Breakup", Img.icon("buttons/exit"));
	private SMState state = null;

	private JSplitPane grid_Log;

	private JRadioButtonMenuItem expertMode = new JRadioButtonMenuItem(Common.getString("Mode_expert"));
	private JRadioButtonMenuItem normalMode = new JRadioButtonMenuItem(Common.getString("Mode_normal"));
	private JRadioButtonMenuItem basicMode = new JRadioButtonMenuItem(Common.getString("Mode_basic"));
	private JCheckBoxMenuItem afficherLog = new JCheckBoxMenuItem(Common.getString("Afficher_messages"), true);
	private JRadioButtonMenuItem bigSize = new JRadioButtonMenuItem(Common.getString("Grand"), false);
	private JRadioButtonMenuItem medSize = new JRadioButtonMenuItem(Common.getString("Normal"), true);
	private JRadioButtonMenuItem littleSize = new JRadioButtonMenuItem(Common.getString("Compact"), false);

	private JRadioButtonMenuItem editionMode = new JRadioButtonMenuItem(Common.getString("Mode_Edition"), Img.icon("menu/edition"), true);
	private JRadioButtonMenuItem survMode = new JRadioButtonMenuItem(Common.getString("Mode_Surveillance"), Img.icon("menu/surveillance"), false);
	private JRadioButtonMenuItem detailedSurvMode = new JRadioButtonMenuItem(Common.getString("Mode_Surveillance_Detail"), Img.icon("menu/surveillance"), false);
	private EquipementBar bar;
	private Grid grid;
	private JScrollPane scroll;
	private JPanel gridTool = new JPanel();

	private JLabel cfgFile;
	private JLabel narvalVersion;
	//	private JTabbedPane onglets;

	public Grid getGrid()
	{
		return grid;
	}

	public LeftPanel getLeftPanel()
	{
		return left;
	}

	public JRadioButtonMenuItem getBigSizeButton()
	{
		return bigSize;
	}

	public JRadioButtonMenuItem getMedSizeButton()
	{
		return medSize;
	}

	public JRadioButtonMenuItem getLittleSizeButton()
	{
		return littleSize;
	}

	/**
	 * Constructeur de la classe
	 */
	public ContentPanel(Window f)
	{
		cfgFile = new JLabel("");
		//		onglets = new JTabbedPane();
		//		InfoViewer viewer = new InfoViewer();
		//		scroll = new JScrollPane(viewer);
		frame = f;
		bar = new EquipementBar(this);
		grid = new Grid(frame, bar, left.getTree());
		scroll = new JScrollPane(grid);

		bar.setFreq(10);
		grid.setScroll(scroll);
		initPanelCmd();

		gridTool.setLayout(new BorderLayout());
		gridTool.add(scroll, BorderLayout.CENTER);
		gridTool.add(bar, BorderLayout.PAGE_START);
		grid_Log = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gridTool, events);
		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, grid_Log);
		grid_Log.setDividerLocation(500);
		content.setDividerLocation(200);
		setLayout(new BorderLayout());

		/***
		 * Affichage des débits
		 * Non fonctionnel pour l'instant
		 */
		//onglets.addTab("Configuration", Img.icon("config"), content);
		//DataFlowViewer view = new DataFlowViewer();
		//onglets.addTab(Common.getString("debit"), Img.icon("graphe"), new JScrollPane(view));
		//onglets.setEnabledAt(1, false);
		//add(onglets, BorderLayout.CENTER);

		add(content, BorderLayout.CENTER);

		new Thread()
		{
			public void run()
			{
				try
				{
					ServerSocket serverSocket = new ServerSocket(4000);
					while (true)
					{
						final Socket socket = serverSocket.accept();
						new Thread()
						{
							public void run()
							{
								events.decodeMessage(socket);
							}
						}.start();
					}
				} catch (Exception e)
				{
					events.raiseException(e); // on passe l'exception pour qu'il l'affiche
				}
			}
		}.start();

		new Thread() {
			public void run() {
				while (true) {
					refresh();
					try {sleep(freq * 100);} catch(InterruptedException e) {}
				}
			}
		}.start();
	}

	@Override
	public void refresh()
	{
		upButtons();
		cfgFile.setText(Common.myClientSOAP.getExperimentCfgFile());
		gRNValue.setText(Integer.toString(Common.myClientSOAP.getRunNumber()));

		if (Common.myClientSOAP.getStateMachine().getStringSMState().equals("RUNNING"))
		{
			gRNTextField.setEnabled(false);
		} else
		{
			gRNTextField.setEnabled(true);
		}
	}

	/**
	 * Créé un menu pour la JFrame
	 * Permet de conserver les références des boutons et donc de
	 * gérer les évènements depuis cette classe
	 * @return
	 */
	public JMenuBar createMenu()
	{
		JMenuBar menuBar = new JMenuBar();

		fileMenu.setMnemonic(KeyEvent.VK_F);
		optionsMenu.setMnemonic(KeyEvent.VK_O);

		/* NOUVEAU */
		newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newFile.getAccessibleContext().setAccessibleDescription(Common.getString("New_Config"));
		fileMenu.add(newFile);

		/* OUVRIR */
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openFile.getAccessibleContext().setAccessibleDescription(Common.getString("Open_Config"));
		fileMenu.add(openFile);

		/* RECHARGER */
		reload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		reload.getAccessibleContext().setAccessibleDescription(Common.getString("Reload_Config"));
		fileMenu.add(reload);

		fileMenu.addSeparator();
		/* ENREGISTRER */
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveFile.getAccessibleContext().setAccessibleDescription(Common.getString("Save_Config"));
		fileMenu.add(saveFile);

		/* ENREGISTRER SOUS */
		saveAsFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		saveAsFile.getAccessibleContext().setAccessibleDescription(Common.getString("Saveas_Config"));
		fileMenu.add(saveAsFile);

		fileMenu.addSeparator();
		/* QUITTER */
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quit.getAccessibleContext().setAccessibleDescription(Common.getString("Menu_Close"));
		fileMenu.add(quit);

		saveLayout.addActionListener(this);
		loadLayout.addActionListener(this);
		layoutMenu.add(loadLayout);
		layoutMenu.add(saveLayout);

		startConfig.addActionListener(this);
		stopConfig.addActionListener(this);
		configMenu.add(startConfig);
		configMenu.add(stopConfig);

		menuBar.add(fileMenu);
		menuBar.add(layoutMenu);
		menuBar.add(configMenu);
		menuBar.add(optionsMenu);

		init.setFocusPainted(false);
		start.setFocusPainted(false);
		stop.setFocusPainted(false);
		exit.setFocusPainted(false);

		/* MODE EDITION ou SURVEILLANCE */
		ButtonGroup gr3 = new ButtonGroup();
		gr3.add(editionMode);
		gr3.add(survMode);
		gr3.add(detailedSurvMode);
		editionMode.setSelected(true);
		modeMenu.setMnemonic(KeyEvent.VK_M);
		modeMenu.add(editionMode);
		modeMenu.add(survMode);
		modeMenu.add(detailedSurvMode);

		menuBar.add(modeMenu);

		statusMenu.add(showStatus);

		menuBar.add(statusMenu);

		storageMenu.add(enable);
		storageMenu.add(disable);

		menuBar.add(storageMenu);

		String nom = null;
		Properties env = null;

		try
		{
			env = Common.getEnvironment();
		} catch (IOException e)
		{
		}

		nom = (String)env.get("NARVAL_INSTALL");

		narvalVersion = new JLabel("NARVAL version : "+nom);
		aboutMenu.add(narvalVersion);
		menuBar.add(aboutMenu);
		gRNPanel.setLayout(new FlowLayout());
		menuBar.add(gRNPanel);
		gRNPanel.add(gRNLabel);
		gRNPanel.add(gRNValue);
		gRNPanel.add(gRNTextField);
		gRNTextField.addActionListener(this);

		menuBar.add(Box.createHorizontalGlue());

		menuBar.add(cfgFile);

		Common.modeExpert = false;
		Common.modeBasic = false;
		ButtonGroup gr4 = new ButtonGroup();
		gr4.add(basicMode);
		gr4.add(normalMode);
		gr4.add(expertMode);
		normalMode.setSelected(true);
		modeMenu.add(basicMode);
		modeMenu.add(normalMode);
		modeMenu.add(expertMode);
		optionsMenu.add(afficherLog);

		/*** 	TAILLE D'AFFICHAGE		***/
		JMenu sizeMenu = new JMenu(Common.getString("Taille_affichage"));
		optionsMenu.add(sizeMenu);
		sizeMenu.add(bigSize);
		sizeMenu.add(medSize);
		sizeMenu.add(littleSize);
		sizeMenu.addSeparator();
		ButtonGroup gr = new ButtonGroup();
		gr.add(bigSize);
		gr.add(medSize);
		gr.add(littleSize);
		bigSize.addActionListener(this);
		medSize.addActionListener(this);
		littleSize.addActionListener(this);

		/* AJOUT DES LISTENERS */
		editionMode.addActionListener(this);
		survMode.addActionListener(this);
		detailedSurvMode.addActionListener(this);

		newFile.addActionListener(this);
		openFile.addActionListener(this);
		reload.addActionListener(this);
		saveFile.addActionListener(this);
		saveAsFile.addActionListener(this);
		quit.addActionListener(this);

		showStatus.addActionListener(this);

		init.addActionListener(this);
		start.addActionListener(this);
		stop.addActionListener(this);
		exit.addActionListener(this);
		afficherLog.addActionListener(this);
		basicMode.addActionListener(this);
		normalMode.addActionListener(this);
		expertMode.addActionListener(this);
        enable.addActionListener(this);
        disable.addActionListener(this);

        return menuBar;
	}

	/**
	 * Initialise le panneau de commandes
	 * (init start stop exit)
	 */
	public void initPanelCmd()
	{
		panelCommande.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panelCommande.add(init, gbc);
		gbc.gridx++;
		panelCommande.add(start, gbc);
		gbc.gridx++;
		panelCommande.add(stop, gbc);
		gbc.gridx++;
		panelCommande.add(exit, gbc);
		panelCommande.setBorder(BorderFactory.createLineBorder(new Color(140, 185, 255), 2));
		panelCommande.setBackground(new Color(220, 255, 255));
	}

	/**
	 * Action performed pour les boutons
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == enable) {
			Set<EquipementUI> liste = grid.getEqts();
			for (EquipementUI eqt : liste)
			{
				if (eqt.getEqtType() == EquipementType.STORAGE) {
					String state = Common.myClientSOAP.equipmentState(eqt.getName());
					if (!state.equals("STOPPED")) {
						Common.myLogger.error("Operation forbidden - Storage mode can be changed only in READY state");
						JOptionPane.showMessageDialog(frame,"Operation forbidden - Storage mode can be changed only in READY state","ERROR",JOptionPane.ERROR_MESSAGE);
						return;
					}
					boolean success = true;
					success = Common.myClientSOAP.storageOn(eqt.getName());
					if (success == true) eqt.setStorageOn(true);
				}
			}
			frame.getContentPanel().getGrid().repaint();
		} else if (evt.getSource() == disable) {
			Set<EquipementUI> liste = grid.getEqts();
			for (EquipementUI eqt : liste)
			{
				if (eqt.getEqtType() == EquipementType.STORAGE) {
					String state = Common.myClientSOAP.equipmentState(eqt.getName());
					if (!state.equals("STOPPED")) {
						Common.myLogger.error("Operation forbidden - Storage mode can be changed only in READY state");
						JOptionPane.showMessageDialog(frame,"Operation forbidden - Storage mode can be changed only in READY state","ERROR",JOptionPane.ERROR_MESSAGE);
						return;
					}
					boolean success = true;
					success = Common.myClientSOAP.storageOff(eqt.getName());
					if (success == true) eqt.setStorageOn(false);
				}
			}
			frame.getContentPanel().getGrid().repaint();
		}
		else if (evt.getSource() == start) {
			Set<EquipementUI> liste = grid.getEqts();
			boolean storage = false;
			int runNumber = 0;
			for (EquipementUI eqt : liste)
			{
				if (eqt.getEqtType() == EquipementType.STORAGE)
				{
					String state = Common.myClientSOAP.equipmentState(eqt.getName());
					if (state.equals("STOPPED"))
					{
						if (Common.myClientSOAP.readStorageStatus(eqt.getName()))
						{
							storage = true;
							runNumber = Common.myClientSOAP.getStorageRunNumber(eqt.getName());
							break;
						}
					}
				}
			}

			if (storage)
			{
				final JDialog dialog = new JDialog(frame);
				JButton ok = new JButton("OK");
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				final JTextArea text = new JTextArea("Started run #" + runNumber + "\n");
				JScrollPane scroll = new JScrollPane(text);
				panel.add(scroll);
				panel.add(ok);
				if (Common.elogPresent())
					dialog.setTitle(Common.getString("RunComment") + " to LogBook " + ":" + "Run #" + runNumber);
				else
					dialog.setTitle(Common.getString("RunComment") + ":" + "Run #" + runNumber);
				dialog.setContentPane(panel);
				dialog.setPreferredSize(new Dimension(400, 200));
				dialog.pack();
				dialog.setLocation(frame.getWidth() / 4, frame.getHeight() / 4);
				dialog.setVisible(true);
				final int num = runNumber;
				ok.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						if (Common.elogPresent()) envoieElog("Start",text.getText(), num);
						Common.myLogger.info(text.getText());
						dialog.dispose();
					}
				});
			}
			Communication.start();
		} else if (evt.getSource() == stop)
		{
			Set<EquipementUI> liste = grid.getEqts();
			boolean storage = false;
			int runNumber = 0;
			for (EquipementUI eqt : liste)
			{
				if (eqt.getEqtType() == EquipementType.STORAGE)
				{
					String state = Common.myClientSOAP.equipmentState(eqt.getName());
					if (state.equals("RUNNING"))
					{
						if (Common.myClientSOAP.readStorageStatus(eqt.getName()))
						{
							storage = true;
							runNumber = Common.myClientSOAP.getStorageRunNumber(eqt.getName());
							break;
						}
					}
				}
			}
			Communication.stop();
			if (storage)
			{
				if (Common.elogPresent()) envoieElog("Stop","Stopped run #" + runNumber + "\n", runNumber);
				Common.myLogger.info("Stopped run #" + runNumber + "\n");
			}
		} else if (evt.getSource() == init)
		{
			Communication.init();
		} else if (evt.getSource() == exit)
		{
			Communication.exit();
		} else if (evt.getSource() == startConfig)
		{
			new ConfigList(true);
		} else if (evt.getSource() == stopConfig)
		{
			new ConfigList(false);
		} else if (evt.getSource() == showStatus)
		{
//			System.out.println("showStatus");
			new StatusList();
		} else if (evt.getSource() == editionMode)
		{
			Common.stopMonitor = true;
			Common.setOffline();
			Common.myClientSOAP.monitoringOff();
			grid.setGridPainted(true);
			grid.offline();
			mode.setText(Common.getString("Mode_Edition"));
			gridTool.remove(panelCommande);
			gridTool.add(bar, BorderLayout.PAGE_START);
			//			grid_Log.remove(events);
		} else if (evt.getSource() == survMode)
		{
			mode.setText(Common.getString("Mode_Surveillance"));
			Common.detailedView = false;

			if (Common.stopMonitor == true)
			{
				Common.setOnline();
				Common.myClientSOAP.monitoringOn();
				Common.stopMonitor = false;
				grid.online();
				grid.setGridPainted(false);
				gridTool.remove(bar);
				gridTool.add(panelCommande, BorderLayout.PAGE_START);
			}
		} else if (evt.getSource() == detailedSurvMode)
		{
			mode.setText(Common.getString("Mode_Surveillance_Detail"));
			Common.detailedView = true;

			if (Common.stopMonitor == true)
			{
				Common.setOnline();
				Common.myClientSOAP.monitoringOn();
				Common.stopMonitor = false;
				grid.online();
				grid.setGridPainted(false);
				gridTool.remove(bar);
				gridTool.add(panelCommande, BorderLayout.PAGE_START);
			}
		} else if (evt.getSource() == afficherLog)
		{
			if (!afficherLog.isSelected())
			{
				grid_Log.remove(events);
			} else
			{
				grid_Log.add(events);
				grid_Log.setDividerLocation(500);
			}
		} else if (evt.getSource() == bigSize)
		{
			grid.setSize("BIG");
		} else if (evt.getSource() == medSize)
		{
			grid.setSize("NORMAL");
		} else if (evt.getSource() == littleSize)
		{
			grid.setSize("SMALL");
		} else if (evt.getSource() == newFile)
		{
			newExp();
		} else if (evt.getSource() == openFile)
		{
			openConfig();
		} else if (evt.getSource() == reload)
		{
			reload();
		} else if (evt.getSource() == saveFile)
		{
			if (!saveConfig())
				JOptionPane.showMessageDialog(frame, "Could not save current configuration.", "ERROR", JOptionPane.ERROR_MESSAGE);
		} else if (evt.getSource() == saveAsFile)
		{
			saveConfigAs();
		} else if (evt.getSource() == quit)
		{
			System.exit(0);
		} else if (evt.getSource() == loadLayout)
		{
			grid.loadLayout();
		} else if (evt.getSource() == saveLayout)
		{
			grid.saveLayout();
		} else if (evt.getSource() == expertMode)
		{
			System.out.println("bouton mode expert");
			Common.modeBasic = false;
			Common.modeExpert = true;
		} else if (evt.getSource() == normalMode)
		{
			System.out.println("bouton mode normal");
			Common.modeExpert = false;
			Common.modeBasic = false;
		} else if (evt.getSource() == basicMode)
		{
			System.out.println("bouton mode basic");
			Common.modeExpert = false;
			Common.modeBasic = true;
		} else if (evt.getSource() == gRNTextField)
		{
			Common.myClientSOAP.setRunNumber(Integer.parseInt(gRNTextField.getText()));
		}
	}

	public void envoieElog(String subject, String text, int runNumber)
	{
		try
		{
			if (text.equals(null) || text.trim().equals(""))
				text = "No comment";
			new client.FileClient(Common.getRCCHost(), Common.getRCCName(), null, "Comment", subject+" RUN #" + runNumber, "Acquisition", text, "Run Control");
		} catch (IOException e)
		{
			System.err.println("Error Elog Access");
		} catch (WebServiceException e1)
		{
			JOptionPane.showMessageDialog(this, "Check the ServerElog presence !!!!\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			//e1.printStackTrace();
		}
	}

	/**
	 * Créé une nouvelle expérience
	 */
	public void newExp()
	{
		int confirm = JOptionPane.showConfirmDialog(this, Common.getString("Confirm_new"), Common.getString("Attention"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION)
		{
			grid.wait(true);

			boolean status = false;

			try
			{
				status = Common.myClientSOAP.emptyExperiment();
			} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
			{
				status = false;
			}

			if (status == true)
			{
				grid.load(null);
			}

			grid.wait(false);
		}
	}

	/**
	 * Ouvre une configuration existante
	 */
	public void openConfig()
	{
		String file;

		try
		{
			file = Common.myClientSOAP.getExperimentCfgFile();
		} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
		{
			return;
		}

		if (file.equals(""))
			return;

		final JFileChooser fc = new JFileChooser(file);

		fc.setFileFilter(new Filtre());

		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			editionMode.setSelected(true);
			survMode.setSelected(false);
			Common.stopMonitor = true;
			Common.setOffline();
			Common.myClientSOAP.monitoringOff();
			grid.setGridPainted(true);
			grid.offline();
			mode.setText(Common.getString("Mode_Edition"));
			gridTool.remove(panelCommande);
			gridTool.add(bar, BorderLayout.PAGE_START);
			repaint();

			grid.wait(true);

			boolean status = false;

			try
			{
				status = Common.myClientSOAP.loadConfig(fc.getSelectedFile().getAbsolutePath());
			} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
			{
				status = false;
			}

			if (status == true)
				reload();

			grid.wait(false);
		}
	}

	/**
	 * (Re)charge la configuration actuelle
	 */
	public void reload()
	{
		StateExpSOAP config = null;

		try
		{
			config = Common.myClientSOAP.getState();
		} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
		{
			return;
		}

		if (config == null)
			return;

		loadEquipements(config.getListEqt());
		loadLinks(config.getListLink());

		String file;

		try
		{
			file = Common.myClientSOAP.getExperimentCfgFile();
		} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
		{
			return;
		}

		if (file.equals(""))
			return;

		int index = file.lastIndexOf(".xml");

		if (index != -1)
			file = file.substring(0, index);

		file = file + ".layout";

		grid.loadLayout(new File(file));
	}

	public boolean saveConfig()
	{
		String file;

		try
		{
			file = Common.myClientSOAP.getExperimentCfgFile();
		} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
		{
			return false;
		}

		if (file.equals(""))
			return false;

		int index = file.lastIndexOf(".xml");

		if (index != -1)
			file = file.substring(0, index);

		file = file + ".layout";

		grid.saveLayout(new File(file));

		boolean status = false;

		try
		{
			status = Common.myClientSOAP.saveConfig();
		} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
		{
			status = false;
		}

		return status;
	}

	/**
	 * Sauvegarde la configuration actuelle sous un autre nom
	 * @return
	 */
	public boolean saveConfigAs()
	{
		String file;

		try
		{
			file = Common.myClientSOAP.getExperimentCfgFile();
		} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
		{
			return false;
		}

		if (file.equals(""))
			return false;

		final JFileChooser fc = new JFileChooser(file);

		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			boolean status;

			try
			{
				status = Common.myClientSOAP.saveConfig(fc.getSelectedFile().getAbsolutePath());
			} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
			{
				return false;
			}

			if (status == false)
				return false;

			try
			{
				file = Common.myClientSOAP.getExperimentCfgFile();
			} catch (com.sun.xml.internal.ws.client.ClientTransportException e)
			{
				return false;
			}

			if (file.equals(""))
				return false;

			int index = file.lastIndexOf(".xml");

			if (index != -1)
				file = file.substring(0, index);

			file = file + ".layout";

			grid.saveLayout(new File(file));
		}

		return true;
	}

	/**
	 * Met à jour l'état des boutons
	 */
	public void upButtons()
	{
		if (Common.myClientSOAP == null)
			return;

		state = Common.myClientSOAP.getStateMachine().getSMState();

		if ((Common.stopMonitor) || (Communication.inProgress == true))
		{
			init.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(false);
			exit.setEnabled(false);
			return;
		}

		if (Common.modeExpert)
		{
//			System.out.println("modeExpert");
			switch (state)
			{
			case ERROR:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case NOMONITORING:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(false);
				exit.setEnabled(false);
				break;
			case IDLE:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case OFFLINE:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case PAUSED:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case READY:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case RUNNING:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case WARNING:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			}
		} else if (Common.modeBasic)
		{
//			System.out.println("modeBasic");
			switch (state)
			{
			case ERROR:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case NOMONITORING:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(false);
				exit.setEnabled(false);
				break;
			case IDLE:
				init.setEnabled(true);
				start.setEnabled(false);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case OFFLINE:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(false);
				exit.setEnabled(false);
				break;
			case PAUSED:
				init.setEnabled(false);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(false);
				break;
			case READY:
				init.setEnabled(false);
				start.setEnabled(true);
				stop.setEnabled(false);
				exit.setEnabled(false);
				break;
			case RUNNING:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(true);
				exit.setEnabled(false);
				break;
			case WARNING:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			}
		} else
		{
//			System.out.println("modeNormal");
			switch (state)
			{
			case ERROR:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case NOMONITORING:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(false);
				exit.setEnabled(false);
				break;
			case IDLE:
				init.setEnabled(true);
				start.setEnabled(false);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case OFFLINE:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(false);
				exit.setEnabled(false);
				break;
			case PAUSED:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			case READY:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(false);
				exit.setEnabled(true);
				break;
			case RUNNING:
				init.setEnabled(false);
				start.setEnabled(false);
				stop.setEnabled(true);
				exit.setEnabled(false);
				break;
			case WARNING:
				init.setEnabled(true);
				start.setEnabled(true);
				stop.setEnabled(true);
				exit.setEnabled(true);
				break;
			}
		}
	}

	public void loadEquipements(ArrayList<Equipement> eqts)
	{
		grid.load(eqts);
	}

	public void loadLinks(ArrayList<LienEqt> liens)
	{
		grid.loadLinks(liens);
	}

	public Window getFrame()
	{
		return frame;
	}
}
