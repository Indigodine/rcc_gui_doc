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
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.Common;
import core.SMState;
import core.reseau.ReponseStateMachine;
import gui.containers.Window;
import gui.containers.ContentPanel;
import gui.containers.Grid;
import gui.components.EquipementUI;

public class Actor extends Equipement
{
	protected String fileName;
	protected String log_level;
	protected String container; 
	public boolean template = false;
	private long tempsAcqDebitIn = 0;
	private long tempsAcqDebitOut = 0;
	private long debitPrecedentIn = 0;
	private long debitPrecedentOut = 0;
	private double debitEnCours = 0;
	protected long tempsAcqDebitEvtOut = 0;
	protected long debitPrecedentEvtOut = 0;
	protected double debitEvtEnCours = 0;

	public Actor(Window frame, String nomEquipement, String fileName, String nomHost, String log_level, String container) {
		super(frame, nomEquipement, nomHost, EquipementType.ACTOR);
		this.fileName = fileName;
		this.log_level = log_level;
		this.container = container;
	}

	public SMState getState()
	{
		if (frame == null) return null;

		ContentPanel content = frame.getContentPanel();
		
		if (content == null) return null;

		Grid grid = content.getGrid();

		if (grid == null) return null;

		EquipementUI eui = grid.getEqt(container);

		if (eui == null) return null;

		Equipement e = eui.getEqt();

		if (e == null) return null;

		statut = e.statut;

		return statut;
	}

	@Override
	public double getEventDebitOut() {
		if (fileName.equals("gnarval_ebyedat_catcher")) {
			String retour = Common.myClientSOAP.readParameter2(name, "event_counter");
			if (retour.equals("")) return 0.0;
			return calculEventDebit(retour);
		}
		if (fileName.equals("gnarval_ebyedat_merger")) {
			String retour = Common.myClientSOAP.readParameter2(name, "event_counter_o01");
			if (retour.equals("")) return 0.0;
			return calculEventDebit(retour);
		}
		if (fileName.equals("gnarval_mfm_merger")) {
			String retour = Common.myClientSOAP.readParameter2(name, "event_counter_o01");
			if (retour.equals("")) return 0.0;
			return calculEventDebit(retour);
		}
		if (fileName.equals("gnarval_ebyedat_watcher")) {
			String retour = Common.myClientSOAP.readParameter2(name, "event_counter_o01");
			if (retour.equals("")) return 0.0;
			return calculEventDebit(retour);
		}
		return 0.0;
	}

	public boolean isWatcherOn() {
		boolean watcherOn = false;
		if (Common.stopMonitor == true) watcherOn = Common.myClientSOAP.getWatcher(name);
		else {
			SMState state = Common.myClientSOAP.getEquipmentStateMachine(name).getSMState();
			if ((state == SMState.READY) || (state == SMState.RUNNING) || (state == SMState.PAUSED))
				watcherOn = Common.myClientSOAP.readWatcher2(name);
			else
				watcherOn = Common.myClientSOAP.getWatcher(name);
		}
		return watcherOn;
	}

	public void setLogLevel(String lvl) {
		log_level = lvl;
	}

	public String getLogLevel() {
		return log_level;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String f) {
		fileName = f;
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
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel("Log level :", SwingConstants.RIGHT));
		tmp.add(logField);
		tmp.add(new JLabel(Common.getString("Fichier")+" :", SwingConstants.RIGHT));
		tmp.add(fileNameField);
		contentPanel.add(tmp);

		JPanel watcherPanel = new JPanel();
		watcherPanel.setLayout(new BoxLayout(watcherPanel, BoxLayout.Y_AXIS));
//		watcherPanel.setBorder( BorderFactory.createLineBorder(Color.WHITE) );
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
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}

	public String getContainerNARVAL() {
		return container;
	}

	public void start() {
		Common.myClientSOAP.equipmentStart(container);
	}

	public void stop() {
		Common.myClientSOAP.equipmentStop(container);
	}

	public void init() {
		Common.myClientSOAP.equipmentInit(container);
	}

	public void resume() {
		Common.myClientSOAP.equipmentResume(container);
	}

	public void pause() {
		Common.myClientSOAP.equipmentPause(container);
	}

	public void exit() {
		Common.myClientSOAP.equipmentExit(container);
	}

	public double getDebitIn() {
		String retour = Common.myClientSOAP.readParameter2(name, "bytes_in");
		if (retour.equals("")) return 0;
		return calculDebit(retour, true);
	}

	public double getDebitOut() {
		String retour = Common.myClientSOAP.readParameter2(name, "bytes_out");
		if (retour.equals("")) return 0;
		return calculDebit(retour, false);
	}

	public double calculDebit(String retour, boolean inOut)
	{
		if (retour.equals("")) return 0;

		long ret = 0;

		try
		{
			ret = Long.parseLong(retour.trim());
		} catch (Exception e)
		{
			ret = 0;
		}
		long tempsAcqDebit, debitPrecedent;
		if (inOut)
		{
			tempsAcqDebit = tempsAcqDebitIn;
			debitPrecedent = debitPrecedentIn;
		}
		else
		{
			tempsAcqDebit = tempsAcqDebitOut;
			debitPrecedent = debitPrecedentOut;
		}
		if (tempsAcqDebit == 0)
		{
			if (inOut)
				debitPrecedentIn = ret;
			else
				debitPrecedentOut = ret;
			// tempsAcqDebit = System.nanoTime();
			if (inOut)
				tempsAcqDebitIn = System.currentTimeMillis();
			else
				tempsAcqDebitOut = System.currentTimeMillis();
			setDebitEnCours(0);

			return 0;
		}
		else
		{
			long byteIn = ret;
			// long tempsPresent = System.nanoTime();
			long tempsPresent = System.currentTimeMillis();
			double delaiEntre = (tempsPresent - tempsAcqDebit);
			if (inOut)
				tempsAcqDebitIn = tempsPresent;
			else
				tempsAcqDebitOut = tempsPresent;
			long tmp = debitPrecedent;
			if (inOut)
				debitPrecedentIn = byteIn;
			else
				debitPrecedentOut = byteIn;

			setDebitEnCours(((byteIn - tmp) / delaiEntre));

			return debitEnCours; // valeur en KO
		}
	}

	public void razTempsAcqDebit()
	{
		tempsAcqDebitIn = 0;
		tempsAcqDebitOut = 0;

		setDebitEnCours(0);
	}

	public void setDebitEnCours(double debitEnCours)
	{
		this.debitEnCours = (double) Math.round(debitEnCours * 100) / 100;
	}

	public double getDebitEnCours()
	{
		return debitEnCours;
	}

	public double calculEventDebit(String retour)
	{
		if (retour.equals("")) return 0;

		long ret = 0;

		try
		{
			ret = Long.parseLong(retour.trim());
		} catch (Exception e)
		{
			ret = 0;
		}
		long tempsAcqDebit, debitPrecedent;

		tempsAcqDebit = tempsAcqDebitEvtOut;
		debitPrecedent = debitPrecedentEvtOut;

		if (tempsAcqDebit == 0)
		{
			debitPrecedentEvtOut = ret;
			tempsAcqDebitEvtOut = System.currentTimeMillis();
			setDebitEnCours(0);
			return 0;
		}
		else
		{
			long byteIn = ret;
			long tempsPresent = System.currentTimeMillis();
			double delaiEntre = (tempsPresent - tempsAcqDebit);

			tempsAcqDebitEvtOut = tempsPresent;
			long tmp = debitPrecedent;
			debitPrecedentEvtOut = byteIn;

			setDebitEvtEnCours(((byteIn - tmp) / delaiEntre) * 1000);

			return debitEvtEnCours;
		}
	}

	public void setDebitEvtEnCours(double debitEnCours)
	{
		debitEvtEnCours = (double) Math.round(debitEnCours * 100) / 100;
	}

	public double getDebitEvtEnCours()
	{
		return debitEvtEnCours;
	}
}
