package core.equipement;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import gui.containers.Window;

public class ActorTemplate extends Equipement
{
	private String fileName;
	private String log_level;
	private String container; 

	private long tempsAcqDebitIn = 0;
	private long tempsAcqDebitOut = 0;
	private long debitPrecedentIn = 0;
	private long debitPrecedentOut = 0;
	private double debitEnCours = 0;
	protected long tempsAcqDebitEvtOut = 0;
	protected long debitPrecedentEvtOut = 0;
	protected double debitEvtEnCours = 0;

	public ActorTemplate(Window frame, String nomEquipement, String fileName, String nomHost, String log_level, String container) {
		super(frame, nomEquipement, nomHost, EquipementType.ACTOR);
		this.fileName = fileName;
		this.log_level = log_level;
		this.container = container;
	}

	public void setLogLevel(String lvl) {
		log_level = lvl;
	}

	public String getLogLevel()
	{
		return log_level;
	}

	public String getFileName()
	{
		return fileName;
	}

	@Override
	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField nameField = new JTextField(name);
		final JTextField hostNameField = new JTextField(Common.myClientSOAP.getEqtHost(name));
		final JComboBox logField = new JComboBox();
		logField.addItem("off");
		logField.addItem("debug");
		logField.addItem("info");
		logField.addItem("warn");
		logField.addItem("error");
		logField.addItem("fatal");
		logField.setSelectedItem( Common.myClientSOAP.getActorLogLevel(name) );
		final JTextField fileNameField = new JTextField(Common.myClientSOAP.getActorExecFile(name));
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel("Log level :", SwingConstants.RIGHT));
		tmp.add(logField);
		tmp.add(new JLabel(Common.getString("Fichier")+" :", SwingConstants.RIGHT));
		tmp.add(fileNameField);
		contentPanel.add(tmp);

		if ( Common.isOnline() ) {
			nameField.setEnabled( false );
			hostNameField.setEnabled( false );
			fileNameField.setEnabled( false );
		}

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
		watcherPortField.setEnabled( jcbWatcher.isSelected() );
		contentPanel.add(watcherPanel);

		JPanel tmp1 = new JPanel(new GridLayout(0, 2));
		JButton ok = new JButton("OK");
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp1.add(ok);
		tmp1.add(cancel);
		contentPanel.add(tmp1);

		final JDialog dialog = new JDialog(frame);
		dialog.setTitle(Common.getString("Modifier") + " " + name);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(frame.getWidth()/4,frame.getHeight()/4);
		dialog.setVisible(true);

		jcbWatcher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				watcherPortField.setEnabled( jcbWatcher.isSelected() );
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newName = nameField.getText();
				String newHostName = hostNameField.getText();
				String newLog = logField.getSelectedItem().toString();
				String newFileName = fileNameField.getText();
				boolean newWatcher = jcbWatcher.isSelected();
				int newWatcherPort = Integer.parseInt(watcherPortField.getText());
				if (Common.myClientSOAP.modifNomEqt(name, newName)) {
					name = newName;
					if (eqtUI != null) eqtUI.setName(name);
					if (Common.myClientSOAP.setEqtHost(name, newHostName)) {
						hostName = newHostName;
						if (Common.myClientSOAP.setActorLogLevel(name, newLog)) {
							log_level = newLog;
							if (Common.myClientSOAP.setActorExecFile(name, newFileName)) {
								fileName = newFileName;
								if (Common.myClientSOAP.writePortWatcher(name, newWatcherPort)) {
									if (Common.myClientSOAP.writeWatcher(name, newWatcher)) {
										if (eqtUI != null) eqtUI.setWatcherOn(newWatcher);
									}
								}
							}
						}
					}
				}
				dialog.dispose();
				frame.getContentPanel().getGrid().repaint();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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

	public double getDebitIn()
	{
		String retour = Common.myClientSOAP.readParameter2(name, "bytes_in");

		return calculDebit(retour, true);
	}

	public double getDebitOut()
	{
		String retour = Common.myClientSOAP.readParameter2(name, "bytes_out");

		return calculDebit(retour, false);
	}

	public double calculDebit(String retour, boolean inOut)
	{
		long ret = 0;

		try
		{
			ret = Long.parseLong(retour.trim());
		} catch (NumberFormatException e)
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
			return (getDebitEnCours()); // valeur en KO
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

	@Override
	public double getEventDebitOut()
	{
		String retour = Common.myClientSOAP.readParameter2(name, "event_counter");
		return calculEventDebit(retour);
	}

	public double calculEventDebit(String retour)
	{
		long ret = 0;

		try
		{
			ret = Long.parseLong(retour.trim());
		} catch (NumberFormatException e)
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
			return (getDebitEvtEnCours());
		}
	}

	public void setDebitEvtEnCours(double debitEnCours)
	{
		this.debitEvtEnCours = (double) Math.round(debitEnCours * 100) / 100;
	}

	public double getDebitEvtEnCours()
	{
		return debitEvtEnCours;
	}
}
