package core.equipement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.Common;
import core.SMState;
import gui.containers.Window;

public class Storage extends Actor
{
	private class DirectoryPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		JTextField jtf = new JTextField();
		JCheckBox jcb = new JCheckBox();

		public DirectoryPanel(String dir)
		{
			setLayout(new FlowLayout());
			add(jtf);
			jtf.setText(dir);
			add(jcb);
		}

		public String getText()
		{
			return (jtf.getText());
		}

		public boolean isSelected()
		{
			return (jcb.isSelected());
		}
	}

	public Storage(Window frame, String nomEquipement, String nomHost, String log_level, String container) {
		super(frame, nomEquipement, "gnarval_ebyedat_storer", nomHost, log_level, container);
		type = EquipementType.STORAGE;
	}

	public boolean isStorageOn() {
		boolean storageOn = false;
		if (Common.stopMonitor == true) storageOn = Common.myClientSOAP.getStorageStatus(name);
		else {
			SMState state = Common.myClientSOAP.getEquipmentStateMachine(name).getSMState();
			if ( (state == SMState.READY) || (state == SMState.RUNNING) || (state == SMState.PAUSED))
				storageOn = Common.myClientSOAP.readStorageStatus(name);
			else
				storageOn = Common.myClientSOAP.getStorageStatus(name);
		}
		return storageOn;
	}

	private JTextField blockSizeField = null;
	private JTextField scalerServerField = null;
	private JTextField scalerDelayField = null;
	private boolean ebyedat_storer = false;

	@Override
	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField nameField = new JTextField(name);
		hostName = Common.myClientSOAP.getEqtHost(name);
		final JTextField hostNameField = new JTextField(hostName);
		log_level = Common.myClientSOAP.getActorLogLevel(name);
		final JComboBox logField = new JComboBox();
		logField.addItem("off");
		logField.addItem("debug");
		logField.addItem("info");
		logField.addItem("warn");
		logField.addItem("error");
		logField.addItem("fatal");
		logField.setSelectedItem( log_level );
		fileName = Common.myClientSOAP.getActorExecFile(name);
		final JTextField fileNameField = new JTextField(fileName);

		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel("Log level :", SwingConstants.RIGHT));
		tmp.add(logField);
		tmp.add(new JLabel(Common.getString("Fichier")+" :", SwingConstants.RIGHT));
		tmp.add(fileNameField);

		if (fileName.equals("gnarval_ebyedat_storer")) {
			ebyedat_storer = true;
			blockSizeField = new JTextField(Integer.toString(Common.myClientSOAP.readStorageBlockSize(name)));
			scalerServerField = new JTextField(Common.myClientSOAP.readStorageScalerServer(name));
			scalerDelayField = new JTextField(Integer.toString(Common.myClientSOAP.readStorageScalerDelay(name)));
			tmp.add(new JLabel(Common.getString("BlockSize")+" :", SwingConstants.RIGHT));
			tmp.add(blockSizeField);
			tmp.add(new JLabel(Common.getString("scalerServer")+" :", SwingConstants.RIGHT));
			tmp.add(scalerServerField);
			tmp.add(new JLabel(Common.getString("scalerDelay")+" :", SwingConstants.RIGHT));
			tmp.add(scalerDelayField);
		} else {
			ebyedat_storer = false;
		}

		contentPanel.add(tmp);

		final JCheckBox onOff = new JCheckBox(Common.getString("storageCheckBox"));
		onOff.setHorizontalAlignment(SwingConstants.CENTER);
		onOff.setHorizontalTextPosition(SwingConstants.LEFT);
		onOff.setSelected(Common.myClientSOAP.readStorageStatus(name));
		final JTextField runNumberField = new JTextField(Integer.toString(Common.myClientSOAP.getStorageRunNumber(name)));
		JPanel panelRunNumber = new JPanel();
		panelRunNumber.add(new JLabel(Common.getString("RunNumber")));
		panelRunNumber.add(runNumberField);
		String path = Common.myClientSOAP.readStoragePath(name);
		StringTokenizer st = new StringTokenizer(path, ";");
		final Vector<DirectoryPanel> vector = new Vector<DirectoryPanel>();
		final JPanel dirsPanel = new JPanel(new GridLayout(0, 1));
		while ( st.hasMoreTokens() ) {
			DirectoryPanel d = new DirectoryPanel( st.nextToken() );
			vector.add( d );
			dirsPanel.add( d );
		}
		JButton addDir = new JButton(Common.getString("addDir"));
		JButton delDir = new JButton(Common.getString("delDir"));
		JPanel tmp0 = new JPanel();
		tmp0.add(addDir);
		tmp0.add(delDir);
		JPanel panelcenter = new JPanel(new BorderLayout());
		panelcenter.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.gray,Color.white),Common.getString("Path")));
		panelcenter.add(dirsPanel, BorderLayout.NORTH);
		panelcenter.add(tmp0, BorderLayout.CENTER);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(onOff, BorderLayout.CENTER);
		panel.add(panelRunNumber, BorderLayout.NORTH);
		panel.add(panelcenter, BorderLayout.SOUTH);
		contentPanel.add(panel);

		JPanel tmp1 = new JPanel(new GridLayout(0, 2));
		JButton boutonUpdate = new JButton(Common.getString("Update"));
		JButton boutonClose = new JButton(Common.getString("Fermer"));
		tmp1.add( boutonUpdate );
		tmp1.add( boutonClose );
		contentPanel.add(tmp1);

		final JDialog dialog = new JDialog(frame);
		dialog.setTitle(Common.getString("Modifier") + " " + name);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(frame.getWidth()/4,frame.getHeight()/4);
		dialog.setVisible(true);

		onOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean newStorageStatus = onOff.isSelected();
				boolean success = true;
				String state = Common.myClientSOAP.equipmentState(name);
				if (!state.equals("STOPPED")) {
					Common.myLogger.error("Operation forbidden - Storage mode can be changed only in READY state");
					JOptionPane.showMessageDialog(frame,"Operation forbidden - Storage mode can be changed only in READY state","ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				if ((newStorageStatus == true)&&(Common.myClientSOAP.readStorageStatus(name) == false)) {
					int newRunNumber = Integer.parseInt(runNumberField.getText());
					int runNumber = Common.myClientSOAP.getStorageRunNumber(name);
					if (runNumber != newRunNumber) Common.myClientSOAP.setStorageRunNumber(name, newRunNumber);
					success = Common.myClientSOAP.storageOn(name);
				} else if ((newStorageStatus == false)&&(Common.myClientSOAP.getStorageStatus(name) == true)) {
					success = Common.myClientSOAP.storageOff(name);
				}
				if (success == true) {
					if (eqtUI != null) eqtUI.setStorageOn(newStorageStatus);
				} else {
					onOff.setSelected( !newStorageStatus );
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		addDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean storageStatus = Common.myClientSOAP.readStorageStatus(name);
				String state = Common.myClientSOAP.equipmentState(name,false);
				if ((storageStatus == true) && (state.equals("RUNNING") || state.equals("PAUSED"))) {
					Common.myLogger.error(name + " : cannot modify storage path while storing");
					JOptionPane.showMessageDialog(frame,"Operation forbidden - cannot modify storage path while storing","ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (fc.showDialog(frame, "OK") == JFileChooser.APPROVE_OPTION) {
					DirectoryPanel d = new DirectoryPanel( fc.getSelectedFile().getPath() );
					vector.add( d );
					dirsPanel.add( d );
					dialog.pack();
				}
				String newPath = "";
				for (DirectoryPanel d : vector) {
					newPath = newPath + d.getText() + ";";
				}
				try {newPath = newPath.substring(0, newPath.lastIndexOf(";"));} catch(StringIndexOutOfBoundsException e) {newPath = "";}
				Common.myClientSOAP.writeStoragePath(name, newPath);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		delDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean storageStatus = Common.myClientSOAP.readStorageStatus(name);
				String state = Common.myClientSOAP.equipmentState(name,false);
				if ((storageStatus == true) && (state.equals("RUNNING") || state.equals("PAUSED"))) {
					Common.myLogger.error(name + " : cannot modify storage path while storing");
					JOptionPane.showMessageDialog(frame,"Operation forbidden - cannot modify storage path while storing","ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				Iterator<DirectoryPanel> enumeration = vector.iterator();
				while ( enumeration.hasNext() ) {
					DirectoryPanel d = (DirectoryPanel)enumeration.next();
					if ( d.isSelected() ) {
						enumeration.remove();
					}
				}
				dirsPanel.removeAll();
				for (DirectoryPanel d : vector) dirsPanel.add( d );
				dialog.pack();
				String newPath = "";
				for (DirectoryPanel d : vector) {
					newPath = newPath + d.getText() + ";";
				}
				try {newPath = newPath.substring(0, newPath.lastIndexOf(";"));} catch(StringIndexOutOfBoundsException e) {newPath = "";}
				Common.myClientSOAP.writeStoragePath(name, newPath);
				frame.getContentPanel().getGrid().repaint();
			}
		});

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

		logField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newLog = logField.getSelectedItem().toString();
				if (Common.myClientSOAP.setActorLogLevel(name, newLog)) {
					log_level = newLog;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		fileNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newFileName = fileNameField.getText();
				if (Common.myClientSOAP.setActorExecFile(name, newFileName)) {
					fileName = newFileName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		if (ebyedat_storer) {

			blockSizeField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int newBlockSize = Integer.parseInt(blockSizeField.getText());
					Common.myClientSOAP.writeStorageBlockSize(name, newBlockSize);
					frame.getContentPanel().getGrid().repaint();
				}
			});

			scalerServerField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String newScalerServer = scalerServerField.getText();
					Common.myClientSOAP.writeStorageScalerServer(name, newScalerServer);
					frame.getContentPanel().getGrid().repaint();
				}
			});

			scalerDelayField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int newScalerDelay = Integer.parseInt(scalerDelayField.getText());
					Common.myClientSOAP.writeStorageScalerDelay(name, newScalerDelay);
					frame.getContentPanel().getGrid().repaint();
				}
			});
		}

		runNumberField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Common.myClientSOAP.getStorageStatus(name) == true) {
					String state = Common.myClientSOAP.equipmentState(name,false);
					if (state.equals("RUNNING") || state.equals("PAUSED")) {
						int runNumber = Common.myClientSOAP.getStorageRunNumber(name);
						Common.myLogger.info(name + " : cannot modify run number while storing run #" + Integer.toString(runNumber));
						runNumberField.setText(Integer.toString(runNumber));
						return;
					}
				}
				int newRunNumber = Integer.parseInt(runNumberField.getText());
				Common.myClientSOAP.setStorageRunNumber(name, newRunNumber);
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

		logField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newLog = logField.getSelectedItem().toString();
				if (Common.myClientSOAP.setActorLogLevel(name, newLog)) {
					log_level = newLog;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		fileNameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newFileName = fileNameField.getText();
				if (Common.myClientSOAP.setActorExecFile(name, newFileName)) {
					fileName = newFileName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		blockSizeField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				int newBlockSize = Integer.parseInt(blockSizeField.getText());
				Common.myClientSOAP.writeStorageBlockSize(name, newBlockSize);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		scalerServerField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newScalerServer = scalerServerField.getText();
				Common.myClientSOAP.writeStorageScalerServer(name, newScalerServer);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		scalerDelayField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				int newScalerDelay = Integer.parseInt(scalerDelayField.getText());
				Common.myClientSOAP.writeStorageScalerDelay(name, newScalerDelay);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		runNumberField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				int newRunNumber = Integer.parseInt(runNumberField.getText());
				Common.myClientSOAP.setStorageRunNumber(name, newRunNumber);
				frame.getContentPanel().getGrid().repaint();
			}
		});
*/
		boutonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hostName = Common.myClientSOAP.getEqtHost(name);
				hostNameField.setText( hostName );
				log_level = Common.myClientSOAP.getActorLogLevel(name);
				logField.setSelectedItem( log_level );
				fileName = Common.myClientSOAP.getActorExecFile(name);
				fileNameField.setText( fileName );
				if (ebyedat_storer) {
					blockSizeField.setText( Integer.toString( Common.myClientSOAP.readStorageBlockSize(name) ) );
					scalerServerField.setText( Common.myClientSOAP.readStorageScalerServer(name) );
					scalerDelayField.setText( Integer.toString( Common.myClientSOAP.readStorageScalerDelay(name) ) );
				}
				onOff.setSelected( Common.myClientSOAP.readStorageStatus(name) );
				runNumberField.setText( Integer.toString( Common.myClientSOAP.getStorageRunNumber(name) ) );
				vector.removeAllElements();
				dirsPanel.removeAll();
				String path = Common.myClientSOAP.readStoragePath(name);
				StringTokenizer st = new StringTokenizer(path, ";");
				while ( st.hasMoreTokens() ) {
					DirectoryPanel d = new DirectoryPanel( st.nextToken() );
					vector.add( d );
					dirsPanel.add( d );
				}
				dialog.pack();
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
				String newLog = logField.getSelectedItem().toString();
				log_level = Common.myClientSOAP.getActorLogLevel(name);
				if (!log_level.equals(newLog)) {
					if (Common.myClientSOAP.setActorLogLevel(name, newLog)) {
						log_level = newLog;
					}
				}
				String newFileName = fileNameField.getText();
				fileName = Common.myClientSOAP.getActorExecFile(name);
				if (Common.myClientSOAP.setActorExecFile(name, newFileName)) {
					fileName = newFileName;
				}
				if (ebyedat_storer) {
					int newBlockSize = Integer.parseInt(blockSizeField.getText());
					int blockSize = Common.myClientSOAP.readStorageBlockSize(name);
					if (blockSize != newBlockSize) Common.myClientSOAP.writeStorageBlockSize(name, newBlockSize);
					String newScalerServer = scalerServerField.getText();
					String scalerServer = Common.myClientSOAP.readStorageScalerServer(name);
					if (!scalerServer.equals(newScalerServer)) Common.myClientSOAP.writeStorageScalerServer(name, newScalerServer);
					int newScalerDelay = Integer.parseInt(scalerDelayField.getText());
					int scalerDelay = Common.myClientSOAP.readStorageScalerDelay(name);
					if (scalerDelay != newScalerDelay) Common.myClientSOAP.writeStorageScalerDelay(name, newScalerDelay);
				}
				boolean newStorageStatus = onOff.isSelected();
				boolean storageStatus = Common.myClientSOAP.readStorageStatus(name);
				String state = Common.myClientSOAP.equipmentState(name,false);
				if (storageStatus != newStorageStatus) {
					if (!state.equals("STOPPED")) {
						Common.myLogger.error("Operation forbidden - Storage mode can be changed only in READY state");
						JOptionPane.showMessageDialog(frame,"Operation forbidden - Storage mode can be changed only in READY state","ERROR",JOptionPane.ERROR_MESSAGE);
					} else {
						boolean success = true;
						if (storageStatus == false) {
							success = Common.myClientSOAP.storageOn(name);
						} else {
							success = Common.myClientSOAP.storageOff(name);
						}
						if (success == true) {
							if (eqtUI != null) eqtUI.setStorageOn(newStorageStatus);
						}
					}
				}
				int newRunNumber = Integer.parseInt(runNumberField.getText());
				int runNumber = Common.myClientSOAP.getStorageRunNumber(name);
				if (runNumber != newRunNumber) {
					if ((storageStatus == true) && (state.equals("RUNNING") || state.equals("PAUSED"))) {
						Common.myLogger.info(name + " : cannot modify run number while storing run #" + Integer.toString(runNumber));
					} else {
						Common.myClientSOAP.setStorageRunNumber(name, newRunNumber);
					}
				}
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}
}
