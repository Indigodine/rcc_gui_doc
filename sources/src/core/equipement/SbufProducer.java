package core.equipement;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.Common;
import gui.containers.Window;

public class SbufProducer extends Actor
{
	public SbufProducer(Window frame, String nomEquipement, String nomHost, String log_level, String container) {
		super(frame, nomEquipement, "gnarval_ebyedat_catcher", nomHost, log_level, container);
		type = EquipementType.SBUFPRODUCER;
	}

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
		final JTextField blockSizeField = new JTextField(Integer.toString(Common.myClientSOAP.readSbufProducerBlockSize(name)));
		final JTextField cpuVmeField = new JTextField(Common.myClientSOAP.readSbufProducerCrateAddr(name));
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel("Log level :", SwingConstants.RIGHT));
		tmp.add(logField);
		tmp.add(new JLabel(Common.getString("Fichier")+" :", SwingConstants.RIGHT));
		tmp.add(fileNameField);
		tmp.add(new JLabel(Common.getString("BlockSize")+" :", SwingConstants.RIGHT));
		tmp.add(blockSizeField);
		tmp.add(new JLabel("Cpu VME :", SwingConstants.RIGHT));
		tmp.add(cpuVmeField);
		contentPanel.add(tmp);

		JPanel watcherPanel = new JPanel();
		watcherPanel.setLayout(new BoxLayout(watcherPanel, BoxLayout.Y_AXIS));
		watcherPanel.setBorder( BorderFactory.createLineBorder(Color.green) );
		final JCheckBox jcbWatcher = new JCheckBox("Watcher");
		jcbWatcher.setHorizontalTextPosition(SwingConstants.LEFT);
		jcbWatcher.setSelected(Common.myClientSOAP.readWatcher(name));
		JPanel tmp0 = new JPanel();
		tmp0.add(jcbWatcher);
		watcherPanel.add(tmp0);
		JPanel optionWatcher = new JPanel(new GridLayout(1, 2, 0, 5));
		final JTextField watcherPortField = new JTextField(Integer.toString(Common.myClientSOAP.getPortWatcher(name)));
		optionWatcher.add(new JLabel(Common.getString("watcherPort")+" :", SwingConstants.RIGHT));
		optionWatcher.add(watcherPortField);
		watcherPanel.add(optionWatcher);
		contentPanel.add(watcherPanel);

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

		jcbWatcher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean newWatcher = jcbWatcher.isSelected();
				if (newWatcher) Common.myClientSOAP.writePortWatcher(name, Integer.parseInt(watcherPortField.getText()));
				if (Common.myClientSOAP.writeWatcher(name, newWatcher)) {
					if (eqtUI != null) eqtUI.setWatcherOn(newWatcher);
				}
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

		blockSizeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int newBlockSize = Integer.parseInt(blockSizeField.getText());
				Common.myClientSOAP.writeSbufProducerBlockSize(name, newBlockSize);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		cpuVmeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newCpuVme = cpuVmeField.getText();
				Common.myClientSOAP.writeSbufProducerCrateAddr(name, newCpuVme);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		watcherPortField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int newWatcherPort = Integer.parseInt(watcherPortField.getText());
				Common.myClientSOAP.writePortWatcher(name, newWatcherPort);
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
				Common.myClientSOAP.writeSbufProducerBlockSize(name, newBlockSize);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		cpuVmeField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newCpuVme = cpuVmeField.getText();
				Common.myClientSOAP.writeSbufProducerCrateAddr(name, newCpuVme);
				frame.getContentPanel().getGrid().repaint();
			}
		});

		watcherPortField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				int newWatcherPort = Integer.parseInt(watcherPortField.getText());
				Common.myClientSOAP.writePortWatcher(name, newWatcherPort);
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
				blockSizeField.setText( Integer.toString( Common.myClientSOAP.readSbufProducerBlockSize(name) ) );
				cpuVmeField.setText( Common.myClientSOAP.readSbufProducerCrateAddr(name) );
				jcbWatcher.setSelected( Common.myClientSOAP.readWatcher(name) );
				watcherPortField.setText( Integer.toString( Common.myClientSOAP.getPortWatcher(name) ) );
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
				boolean newWatcher = jcbWatcher.isSelected();
				int newWatcherPort = Integer.parseInt(watcherPortField.getText());
				int watcherPort = Common.myClientSOAP.getPortWatcher(name);
				if (watcherPort != newWatcherPort) {
					Common.myClientSOAP.writePortWatcher(name, Integer.parseInt(watcherPortField.getText()));
				}
				if (Common.myClientSOAP.writeWatcher(name, newWatcher)) {
					if (eqtUI != null) eqtUI.setWatcherOn(newWatcher);
				}
				int newBlockSize = Integer.parseInt(blockSizeField.getText());
				int blockSize = Common.myClientSOAP.readSbufProducerBlockSize(name);
				if (blockSize != newBlockSize) Common.myClientSOAP.writeSbufProducerBlockSize(name, newBlockSize);
				String newCpuVme = cpuVmeField.getText();
				String cpuVme = Common.myClientSOAP.readSbufProducerCrateAddr(name);
				if (!cpuVme.equals(newCpuVme)) Common.myClientSOAP.writeSbufProducerCrateAddr(name, newCpuVme);
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}

	@Override
	public double getDebitIn()
	{
		String retour = Common.myClientSOAP.readParameter2(name, "bytes_in");

		if (retour.equals("")) return 0;

		return calculDebit(retour, true);
	}

	@Override
	public double getEventDebitOut()
	{
		String retour = Common.myClientSOAP.readParameter2(name, "event_counter");
		if (retour.equals("")) return 0.0;
		return calculEventDebit(retour);
	}
}
