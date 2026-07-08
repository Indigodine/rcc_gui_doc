package gui;

import gui.containers.Grid;
import gui.containers.Window;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.Common;
import core.equipement.Actor;
import core.equipement.SbufProducer;
import core.equipement.EventBuilder;
import core.equipement.Storage;
import core.equipement.RikenTransmitter;
import core.equipement.ECC;
import core.equipement.Equipement;
import core.equipement.EquipementType;
import core.equipement.LienEqt;
import core.equipement.MIDAS;
import core.equipement.NARVAL;
import core.equipement.VMECOM;

public class Factory {

	private static ArrayList<Equipement> eqts = new ArrayList<Equipement>(); // liste des equipements generes
	public static Window window = null;
	public static Grid grid = null;
	private static final String index = "*";
	private static String fileName = "";

	public static void linkPanelCreation(final Equipement source, final Equipement destination) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		final ArrayList<String> forbiddenNames = grid.getLinksNames();

		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField(Common.getString("Link") + "_" + forbiddenNames.size());
		final JTextField fieldOutput = new JTextField(Common.getString("Link") + "_" + forbiddenNames.size());
		final JTextField fieldPortSource = new JTextField("fifo");
		final JTextField fieldPortDest = new JTextField("fifo");
		final JTextField fieldBufsize = new JTextField("100000");
		final JTextField fieldBufdepth = new JTextField("6");

		tmp.add(label(Common.getString("Name_link")));
		tmp.add(name);
		/**/
		tmp.add(label("Source"));
		tmp.add(labelCenter(source.getName(), Color.RED));
		/**/
		tmp.add(label("Destination"));
		tmp.add(labelCenter(destination.getName(), Color.RED));
		/**/
		tmp.add(label("Source output :"));
		tmp.add(fieldOutput);
		/**/
		tmp.add(label("Source port :"));
		tmp.add(fieldPortSource);
		/**/
		tmp.add(label("Destination port :"));
		tmp.add(fieldPortDest);
		/**/
		tmp.add(label("Buffer size (" + Common.getString("Bytes") + ") :"));
		tmp.add(fieldBufsize);
		/**/
		tmp.add(label("Buffer depth :"));
		tmp.add(fieldBufdepth);

		JButton ok = new JButton(Common.getString("Create") + " " + Common.getString("Link"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("Link"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4, window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean exit = false;
				String linkName = name.getText();
				String portSource = fieldPortSource.getText();
				String portDest = fieldPortDest.getText();
				String output = fieldOutput.getText();
				int bufSize = 0;
				int bufDepth = 0;
				try{
					bufSize = Integer.parseInt(fieldBufsize.getText());
				} catch(NumberFormatException e) {
					error(fieldBufsize);
					exit = true;
				}
				try{
					bufDepth = Integer.parseInt(fieldBufdepth.getText());
				} catch(NumberFormatException e) {
					error(fieldBufdepth);
					exit = true;
				}
				if(linkName.length()<1) {
					error(name);
					exit = true;
				} else if(forbiddenNames.contains(linkName)) {
					name.setText("[Name already used]");
					error(name);
					exit = true;
				}
				if(output.length()<1) {
					error(fieldOutput);
					exit = true;
				}
				if ((exit) || (linkName.equals("[Name already used]")))
					return;

				dialog.dispose();

				LienEqt lien = new LienEqt(window, linkName, source, destination, output, portSource, portDest, bufSize, bufDepth);
				if (Common.myClientSOAP.creerLien(lien)) {
						Common.myClientSOAP.setLinkDepth(lien.getNom(), lien.getBufferDepth());
						grid.addLink(lien);
				}
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
	}

	/***==================================================================***/
	/***					Electronics Control Core					***/
	/***==================================================================***/
	/**
	 * @param nb
	 * @return
	 * une JDialog pour creer un ECC
	 */
	public static void ECCPanelCreation(final int nb) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JTextField port = new JTextField("8083");
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Port :"));
		tmp.add(port);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " ECC");
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle("Electronics Control Core");
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				int portNum = 0;
				try{
					portNum = Integer.parseInt(port.getText());
				} catch(NumberFormatException e) {
					error(port);
					exit = true;
				}
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					ECC ecc = new ECC(window, str, hostName.getText(), portNum);
					eqts.add(ecc);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	/**
	 * @param nb
	 * @return
	 * une JDialog pour creer un Midas
	 */
	public static void MidasPanelCreation(final int nb) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " Midas");
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle("Midas");
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					MIDAS midas = new MIDAS(window, str, hostName.getText());
					eqts.add(midas);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void ActorPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JTextField file = new JTextField();
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(label(Common.getString("Fichier")+" :"));
		tmp.add(file);

		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("Actor"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("Actor"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String fileName = file.getText();
				String logLevel = log.getSelectedItem().toString();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(fileName.length()<1) {
					error(file);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					Actor act = new Actor(window, str, fileName, hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void TemplateActorPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JButton file = new JButton();
		fileName = "";

		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(label(Common.getString("TemplateFile")+" :"));
		tmp.add(file);

		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("Actor"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("TemplateActor"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Properties env = new Properties();
				try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
				JFileChooser fc = new JFileChooser( (String)env.get("NARVAL_INSTALL") + "/templates" );
				if (env.get("NARVAL_INSTALL") == null) System.out.println("NARVAL_INSTALL is not defined");
				System.out.println("NARVAL_INSTALL = " + (String)env.get("NARVAL_INSTALL"));
				if (fc.showDialog(window, Common.getString("Choose")) == JFileChooser.APPROVE_OPTION) {
					fileName = fc.getSelectedFile().getAbsolutePath();
					file.setText(fileName);
					dialog.pack();
				}
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					Actor act = new Actor(window, str, fileName, hostName.getText(), logLevel, container);
					act.template = true;
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void EbyedatWatcherPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);

		tmp.add(label("Narval sub-system :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("EbyedatWatcher"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("EbyedatWatcher"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					Actor act = new Actor(window, str, "gnarval_ebyedat_watcher", hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void MfmWatcherPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);

		tmp.add(label("Narval sub-system :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("MfmWatcher"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("MfmWatcher"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					Actor act = new Actor(window, str, "gnarval_mfm_watcher", hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void SbufProducerPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JTextField blockSizeField = new JTextField("16384");
		final JTextField cpuVmeField = new JTextField();
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(new JLabel(Common.getString("BlockSize")+" :", SwingConstants.RIGHT));
		tmp.add(blockSizeField);
		tmp.add(new JLabel("Cpu VME :", SwingConstants.RIGHT));
		tmp.add(cpuVmeField);
		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("SbufProducer"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("SbufProducer"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				int BlockSize = Integer.parseInt(blockSizeField.getText());
				String CpuVme = cpuVmeField.getText();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					SbufProducer act = new SbufProducer(window, str, hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				for (Equipement eqt : eqts) {
					if (Common.myClientSOAP.writeSbufProducerBlockSize(eqt.getName(), BlockSize)) {
						if (Common.myClientSOAP.writeSbufProducerCrateAddr(eqt.getName(), CpuVme)) {
						}
					}
				}
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void EventBuilderPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JTextField blockSizeField = new JTextField("16384");
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(new JLabel(Common.getString("BlockSize")+" :", SwingConstants.RIGHT));
		tmp.add(blockSizeField);
		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("EventBuilder"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("EventBuilder"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				int BlockSize = Integer.parseInt(blockSizeField.getText());
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					EventBuilder act = new EventBuilder(window, str, hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				for (Equipement eqt : eqts) {
					if (Common.myClientSOAP.setEventBuilderBlockSize(eqt.getName(), BlockSize)) {
					}
				}
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void StoragePanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JTextField blockSizeField = new JTextField("16384");
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(new JLabel(Common.getString("BlockSize")+" :", SwingConstants.RIGHT));
		tmp.add(blockSizeField);
		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("Storage"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("Storage"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				int BlockSize = Integer.parseInt(blockSizeField.getText());
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					Storage act = new Storage(window, str, hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				for (Equipement eqt : eqts) {
					if (Common.myClientSOAP.writeStorageBlockSize(eqt.getName(), BlockSize)) {
					}
				}
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void MfmStoragePanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("MfmStorage"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("MfmStorage"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					Storage act = new Storage(window, str, hostName.getText(), logLevel, container);
					act.setFileName("gnarval_mfm_storer");
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	public static void RikenTransmitterPanelCreation(final int nb) {
		ArrayList<String> subsysNarval = new ArrayList<String>();
		for(Equipement e : Common.myClientSOAP.getState().getListEqt()) {
			if(e.getType() == EquipementType.SUBSYSTEM_NARVAL) {
				subsysNarval.add(e.getName());
			}
		}
		if(subsysNarval.size()==0) {
			JOptionPane.showMessageDialog(window, "No Narval sub-system found !", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JComboBox log = new JComboBox();
		log.addItem("off");
		log.addItem("debug");
		log.addItem("info");
		log.addItem("warn");
		log.addItem("error");
		log.addItem("fatal");
		log.setSelectedIndex(3);
		final JComboBox choixSys = new JComboBox();
		final DefaultComboBoxModel mdc = new DefaultComboBoxModel();
		choixSys.setModel(mdc);
		for(String s : subsysNarval) {
			mdc.addElement(s);
		}
		mdc.setSelectedItem(grid.getEqtBar().getNarval());
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Log level :"));
		tmp.add(log);
		tmp.add(label(Common.getString("Subsystem_Narval")+" :"));
		tmp.add(choixSys);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("RikenTransmitter"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("RikenTransmitter"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String logLevel = log.getSelectedItem().toString();
				String container = mdc.getSelectedItem().toString();
				if(hostName.getText().length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					RikenTransmitter act = new RikenTransmitter(window, str, hostName.getText(), logLevel, container);
					eqts.add(act);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	/***==================================================================***/
	/***					SubSystem NARVAL						***/
	/***==================================================================***/
	/**
	 * @param nb
	 * @return
	 * une JDialog pour creer un sous-systeme Narval
	 */
	public static void createSubSystemNarval() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField name = new JTextField("NARVAL");
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField hostName = new JTextField((String)env.get("HOST"));
		final JTextField cpu = new JTextField((String)env.get("HOST"));

		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("HostName :"));
		tmp.add(hostName);
		tmp.add(label("Coordinator CPU :"));
		tmp.add(cpu);
		JButton ok = new JButton(Common.getString("Create") + " " + Common.getString("Subsystem_Narval"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle("Subsystem_Narval");
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean exit = false;
				String _name = name.getText();
				String host = hostName.getText();
				String cpuStr = cpu.getText();
				if(host.length()<1) {
					error(hostName);
					exit = true;
				}
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(cpuStr.length()<1) {
					error(cpu);
					exit = true;
				}
				if(exit)
					return;
				ArrayList<String> forbiddenNames = grid.getNames();
				while(forbiddenNames.contains(_name) || _name.length()<1) {
					_name = JOptionPane.showInputDialog(window, "Name \"" + _name +
							(_name.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
					if(_name==null) { // si clic sur annuler alors on recommence
						return;
					}
				}
				NARVAL act = new NARVAL(window, _name, host, cpuStr);
				dialog.dispose();
				grid.addSubSystem(act);
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
	}


	/***==================================================================***/
	/***							VMECOM								***/
	/***==================================================================***/
	
	/**
	 * @param nb
	 * @return
	 * une JDialog pour creer un VMECOM
	 */
	public static void VMEPanelCreation(final int nb) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		Properties env = new Properties();
		try {env.load(Runtime.getRuntime().exec("env").getInputStream());} catch(java.io.IOException e){}
		final JTextField name = new JTextField( (nb>1 ? index : "") );
		final JTextField cpuVme = new JTextField();
		final JTextField user = new JTextField((String)env.get("USER"));
		final JTextField exp = new JTextField(Common.myClientSOAP.getExpName());
		final JTextField logger = new JTextField((String)env.get("HOST"));
		final JTextField size = new JTextField("16384");
		
		tmp.add(label(Common.getString("Name")+" :"));
		tmp.add(name);
		tmp.add(label("Cpu VME :"));
		tmp.add(cpuVme);
		tmp.add(label(Common.getString("UserName")+" :"));
		tmp.add(user);
		tmp.add(label(Common.getString("ExpName")+" :"));
		tmp.add(exp);
		tmp.add(label(Common.getString("LoggerName")+" :"));
		tmp.add(logger);
		tmp.add(label(Common.getString("BlockSize")+" :"));
		tmp.add(size);
		JButton ok = new JButton(Common.getString("Add") + " " + nb + " " + Common.getString("VMECOM"));
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);

		contentPanel.add(tmp);

		final JDialog dialog = new JDialog(window);
		dialog.setTitle(Common.getString("VMECOM"));
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(window.getWidth()/4,window.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				boolean exit = false;
				String _name = name.getText();
				String cpu = cpuVme.getText();
				String userName = user.getText();
				String expName = exp.getText();
				if(_name.length()<1) {
					error(name);
					exit = true;
				}
				if(cpu.length()<1) {
					error(cpuVme);
					exit = true;
				}
				if(userName.length()<1) {
					error(user);
					exit = true;
				}
				if(exit)
					return;
				if(!_name.contains(index) && nb>1)
					_name = _name+index;
				ArrayList<String> forbiddenNames = grid.getNames();
				for(int i=0; i<nb ; i++) {
					String str = _name.replace(index, ""+i);
					while(forbiddenNames.contains(str) || str.length()<1) {
						str = JOptionPane.showInputDialog(window, "Name \"" + str +
								(str.length()>=1 ? "\" already used !" : "\" not allowed !") + "\nChoose another one :", "Error", JOptionPane.ERROR_MESSAGE);
						if(str==null) { // si clic sur annuler alors on recommence
							return;
						}
					}
					VMECOM vme = new VMECOM(window, str, cpu, userName, expName, logger.getText(), Integer.parseInt(size.getText()));
					eqts.add(vme);
				}
				dialog.dispose();
				grid.ajouterEquipement(eqts);
				eqts.clear();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eqts.clear();
				dialog.dispose();
			}
		});
	}

	/**
	 * Renvoie la derniere liste d'equipements generes
	 * @return
	 */
	public static ArrayList<Equipement> getEqts() {
		return eqts;
	}

	public static void error(JTextField txt) {
		txt.setBackground(Color.RED);
		txt.setForeground(Color.WHITE);
	}

	/**
	 * @param str
	 * la châine qui sera contenue dans le JLabel
	 * @return
	 * un jlabel
	 */
	private static JLabel label(String str) {
		return new JLabel(str, SwingConstants.RIGHT);
	}

	/**
	 * @param str
	 * la châine qui sera contenue dans le JLabel
	 * @return
	 * un jlabel
	 */
	private static JLabel labelCenter(String str, Color c) {
		JLabel lab = new JLabel(str, SwingConstants.CENTER);
		lab.setForeground(c);
		return lab;
	}

	/**
	 * @param str
	 * la châine qui sera contenue dans le JLabel
	 * @return
	 * un jlabel
	 */
/*
	private static JLabel label(String str, Color c) {
		JLabel lab = new JLabel(str, SwingConstants.RIGHT);
		lab.setForeground(c);
		return lab;
	}
*/
	//retourne un JLabel prévu pour le type d'eqt
	private static JLabel title(String str) {
		JLabel lab = new JLabel(str, SwingConstants.CENTER);
		lab.setForeground(Color.BLUE);
		lab.setFont(new Font("Courrier", Font.BOLD, 24));
		return lab;
	}
}
