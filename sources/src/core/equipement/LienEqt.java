package core.equipement;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.Common;
import core.SMState;

/**
 * cette classe permet la creation d'un lien entre 2 equipements
 * 
 * 
 */
public class LienEqt {

	private JFrame frame;
	private String myName, myPortSrc, myPortDest, mySrcName, myDstName, sourceOutput;
	private int myBufferSize;
	private Equipement myEqtSrc;
	private Equipement myEqtDest;
	private int myBufferDepth;
	private long tempsAcqDebit = 0;
	private long debitPrecedent = 0;
	private double debitEnCours = 0;
	private double eventEnCours = 0;
	public boolean sharedDestination = false; // d'autres liens ont la meme destination

	/**
	 * constructeur de la classe
	 * 
	 * @param nom
	 *            le nom du lien
	 * @param src
	 *            l'equipement source du lien
	 * @param dest
	 *            l'equipement destinataire du lien
	 * @param portSrc
	 *            le port source utilise
	 * @param portDest
	 *            le port destinataire utilise
	 * @param bufSize
	 *            le taille du buffer
	 */
	public LienEqt(JFrame f, String nom, Equipement src, Equipement dest, String sourceOutput, String portSrc, String portDest, int bufSize, int bufferDepth)
	{
		frame = f;
		myName = nom;
		myEqtSrc = src;
		mySrcName = src.getName();
		myPortSrc = portSrc;
		this.sourceOutput = sourceOutput;
		myEqtDest = dest;
		myDstName = dest.getName();
		myPortDest = portDest;
		myBufferSize = bufSize;
		myBufferDepth = bufferDepth;
	}

	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField fieldName = new JTextField(myName);
		sourceOutput = Common.myClientSOAP.getLinkSourceOutput(myName);
		final JTextField fieldOutput = new JTextField(sourceOutput);
		final JTextField fieldPortSource = new JTextField();
		myPortSrc = Common.myClientSOAP.getLinkSourcePort(myName).toLowerCase();
		fieldPortSource.setText(myPortSrc);
		final JTextField fieldPortDest = new JTextField();
		myPortDest = Common.myClientSOAP.getLinkDestinationPort(myName).toLowerCase();
		fieldPortDest.setText(myPortDest);
		myBufferSize = Common.myClientSOAP.getLinkBuffersize(myName);
		final JTextField fieldBufsize = new JTextField(Integer.toString(myBufferSize));
		myBufferDepth = Common.myClientSOAP.getLinkDepth(myName);
		final JTextField fieldBufdepth = new JTextField(Integer.toString(myBufferDepth));
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(fieldName);
		tmp.add(new JLabel("Source", SwingConstants.RIGHT));
		JLabel s = new JLabel(Common.myClientSOAP.getLinkSource(myName), SwingConstants.CENTER);
		s.setForeground(Color.RED);
		tmp.add(s);
		tmp.add(new JLabel("Destination", SwingConstants.RIGHT));
		JLabel d = new JLabel(Common.myClientSOAP.getLinkDestination(myName), SwingConstants.CENTER);
		d.setForeground(Color.RED);
		tmp.add(d);
		tmp.add(new JLabel("Source output :", SwingConstants.RIGHT));
		tmp.add(fieldOutput);
		tmp.add(new JLabel("Source port :", SwingConstants.RIGHT));
		tmp.add(fieldPortSource);
		tmp.add(new JLabel("Destination port :", SwingConstants.RIGHT));
		tmp.add(fieldPortDest);
		tmp.add(new JLabel("Buffer size (" + Common.getString("Bytes") + ") :", SwingConstants.RIGHT));
		tmp.add(fieldBufsize);
		tmp.add(new JLabel("Buffer depth :", SwingConstants.RIGHT));
		tmp.add(fieldBufdepth);
		JButton ok = new JButton("OK");
		JButton cancel = new JButton(Common.getString("Cancel"));
		tmp.add(ok);
		tmp.add(cancel);
		contentPanel.add(tmp);
		final JDialog dialog = new JDialog(frame);
		dialog.setTitle(Common.getString("Modifier")+" "+myName);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(frame.getWidth()/4, frame.getHeight()/4);
		dialog.setVisible(true);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newName = fieldName.getText();
				String newSourceOutput = fieldOutput.getText();
				String newPortSrc = fieldPortSource.getText();
				String newPortDest = fieldPortDest.getText();
				int newBufferSize = Integer.parseInt(fieldBufsize.getText());
				int newBufferDepth = Integer.parseInt(fieldBufdepth.getText());
				if (Common.myClientSOAP.modifNomLien(myName, newName)) {
					myName = newName;
					if (Common.myClientSOAP.setLinkSourceOutput(myName, newSourceOutput)) {
						sourceOutput = newSourceOutput;
						if (Common.myClientSOAP.setLinkSourcePort(myName, newPortSrc)) {
							myPortSrc = newPortSrc;
							if (Common.myClientSOAP.setLinkDestinationPort(myName, newPortDest)) {
								myPortDest = newPortDest;
								if (Common.myClientSOAP.setLinkBuffersize(myName, newBufferSize)) {
									myBufferSize = newBufferSize;
									if (Common.myClientSOAP.setLinkDepth(myName, newBufferDepth)) {
										myBufferDepth = newBufferDepth;
									}
								}
							}
						}
					}
				}
				dialog.dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
	}

	public JFrame getFrame() {
		return frame;
	}

	public String getNom()
	{
		return myName;
	}

	public Equipement getEqtSrc()
	{
		return myEqtSrc;
	}

	public Equipement getEqtDest()
	{
		return myEqtDest;
	}

	public String getPortSrc()
	{
		return myPortSrc;
	}

	public String getPortDest()
	{
		return myPortDest;
	}

	public int getBufferSize()
	{
		return myBufferSize;
	}

	public int getBufferDepth()
	{
		return myBufferDepth;
	}

	public String getMySrcName()
	{
		return mySrcName;
	}

	public String getMyDstName()
	{
		return myDstName;
	}

	public String getSourceOutput()
	{
		return sourceOutput;
	}

	/**
	 * setter : permet de modifier un champ prive
	 * 
	 * @param arg
	 *            la nouvelle valeur du champ prive
	 */
	public void setNom(String myName)
	{
		this.myName = myName;
	}

	public void setPortSrc(String myPortSrc)
	{
		this.myPortSrc = myPortSrc;
	}

	public void setPortDest(String myPortDest)
	{
		this.myPortDest = myPortDest;
	}

	public void setEqtSrc(Instrument myEqtSrc)
	{
		this.myEqtSrc = myEqtSrc;
	}

	public void setEqtDest(Instrument myEqtDest)
	{
		this.myEqtDest = myEqtDest;
	}

	public void setSourceOutput(String sourceOutput)
	{
		this.sourceOutput = sourceOutput;
	}

	public void setBufferSize(int myBufferSize)
	{
		this.myBufferSize = myBufferSize;
	}

	public void setBufferDepth(int myBufferDepth)
	{
		this.myBufferDepth = myBufferDepth;
	}

	public double calculDebit(String retour)
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

		if (tempsAcqDebit == 0)
		{
			debitPrecedent = ret;
			tempsAcqDebit = System.currentTimeMillis();
			setDebitEnCours(0);
			return 0;
		}

		long byteIn = ret;
		long tempsPresent = System.currentTimeMillis();
		double delaiEntre = (tempsPresent - tempsAcqDebit);

		tempsAcqDebit = tempsPresent;

		long tmp = debitPrecedent;

		debitPrecedent = byteIn;

		setDebitEnCours(((byteIn - tmp) / delaiEntre));

		return debitEnCours; // valeur en KO
	}

	public double getDebit() {
		if (isRunning()) {
			String retour = "";
			debitEnCours = 0;
			if (sharedDestination == false) {
				retour = Common.myClientSOAP.readParameter2(myEqtDest.getName(), "bytes_in");
				if (retour.equals("") == false) {
					debitEnCours = calculDebit(retour);
				}
			}
			else {
				retour = Common.myClientSOAP.readParameter2(myEqtSrc.getName(), "bytes_out");
				if (retour.equals("") == false) {
					debitEnCours = calculDebit(retour);
				}
			}

			eventEnCours = myEqtSrc.getEventDebitOut();

			return debitEnCours;
		} else {
			debitEnCours = 0;
			return 0.0;
		}
	}

	public void setDebitEnCours(double debitEnCours) {
		this.debitEnCours = (double) Math.round(debitEnCours * 100) / 100;
	}

	public double getDebitEnCours() {
		return debitEnCours;
	}

	public double getDebitEventEnCours() {
		return eventEnCours;
	}

	public boolean isRunning() {
		boolean retour = 
				(
					(
						(myEqtSrc.getState() == SMState.RUNNING)
						|| (myEqtSrc.getState() == SMState.WARNING)
					)
				&&
					(
						(myEqtDest.getState() == SMState.RUNNING)
						|| (myEqtDest.getState() == SMState.WARNING)
					)
				);	
		if(!retour) {
			debitEnCours = 0;
			myEqtDest.razTempsAcqDebit();
			myEqtSrc.razTempsAcqDebit();
		}
		return retour;
	}

	public boolean isError() {
		return ((myEqtSrc.getState() == SMState.ERROR) || (myEqtDest.getState() == SMState.ERROR));
	}

	public boolean isPause() {
		myEqtDest.razTempsAcqDebit();
		myEqtSrc.razTempsAcqDebit();
		return( (myEqtSrc.getState() == SMState.PAUSED) && (myEqtDest.getState() == SMState.PAUSED) );
	}

	public boolean isMonitoring() {
		return (!(myEqtSrc.getState() == SMState.NOMONITORING) );
	}
}
