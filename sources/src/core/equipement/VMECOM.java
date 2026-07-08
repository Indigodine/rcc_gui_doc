package core.equipement;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.Common;
import gui.containers.Window;

/**
 * cette classe permet d'instancier un equipement de type VMECOM
 * 
 * 
 */
public class VMECOM extends Equipement
{
	private String userName = Common.getLoginName();
	private String expName = Common.getRCCName();
	private String loggername = "";
	private int blocksize = 16384;

	/**
	 * constructeur de la classe
	 * 
	 * @param nomEquipement
	 *            nom de l'equipement
	 * @param nomHost
	 *            nom de l'hote
	 */
	public VMECOM(Window frame, String nomEquipement, String nomHost, String userName, String expName, String loggername, int blocksize)
	{
		super(frame, nomEquipement, nomHost, EquipementType.VMECOM);
		this.userName = userName;
		this.expName = expName;
		this.loggername = loggername;
		this.blocksize = blocksize;
	}

	@Override
	public void openModificationPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JPanel tmp = new JPanel(new GridLayout(0, 2));
		final JTextField nameField = new JTextField(name);
		hostName = Common.myClientSOAP.getEqtHost(name);
		final JTextField hostNameField = new JTextField(hostName);
		userName = Common.myClientSOAP.getVmecomUsername(name);
		final JTextField userNameField = new JTextField(userName);
		expName = Common.myClientSOAP.getVmecomExpname(name);
		final JTextField expNameField = new JTextField(expName);
		loggername = Common.myClientSOAP.getVmecomLoggername(name);
		final JTextField loggerNameField = new JTextField(loggername);
		blocksize = Common.myClientSOAP.getVmecomBlocksize(name);
		final JTextField blockSizeField = new JTextField(Integer.toString(blocksize));
		tmp.add(new JLabel(Common.getString("Name")+" :", SwingConstants.RIGHT));
		tmp.add(nameField);
		tmp.add(new JLabel("HostName :", SwingConstants.RIGHT));
		tmp.add(hostNameField);
		tmp.add(new JLabel(Common.getString("UserName")+" :", SwingConstants.RIGHT));
		tmp.add(userNameField);
		tmp.add(new JLabel(Common.getString("ExpName")+" :", SwingConstants.RIGHT));
		tmp.add(expNameField);
		tmp.add(new JLabel(Common.getString("LoggerName")+" :", SwingConstants.RIGHT));
		tmp.add(loggerNameField);
		tmp.add(new JLabel(Common.getString("BlockSize")+" :", SwingConstants.RIGHT));
		tmp.add(blockSizeField);
		JButton boutonUpdate = new JButton(Common.getString("Update"));
		JButton boutonClose = new JButton(Common.getString("Fermer"));
		tmp.add( boutonUpdate );
		tmp.add( boutonClose );
		contentPanel.add(tmp);
		final JDialog dialog = new JDialog(frame);
		dialog.setTitle(Common.getString("Modifier") + " " + name);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocation(frame.getWidth()/4,frame.getHeight()/4);
		dialog.setVisible(true);

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

		userNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newUserName = userNameField.getText();
				if (Common.myClientSOAP.setVmecomUsername(name, newUserName)) {
					userName = newUserName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		expNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newExpName = expNameField.getText();
				if (Common.myClientSOAP.setVmecomExpname(name, newExpName)) {
					expName = newExpName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		loggerNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newLoggerName = loggerNameField.getText();
				if (Common.myClientSOAP.setVmecomLoggername(name, newLoggerName)) {
					loggername = newLoggerName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		blockSizeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int newBlockSize = Integer.parseInt(blockSizeField.getText());
				if (Common.myClientSOAP.setVmecomBlocksize(name, newBlockSize)) {
					blocksize = newBlockSize;
				}
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

		userNameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newUserName = userNameField.getText();
				if (Common.myClientSOAP.setVmecomUsername(name, newUserName)) {
					userName = newUserName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		expNameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newExpName = expNameField.getText();
				if (Common.myClientSOAP.setVmecomExpname(name, newExpName)) {
					expName = newExpName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		loggerNameField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				String newLoggerName = loggerNameField.getText();
				if (Common.myClientSOAP.setVmecomLoggername(name, newLoggerName)) {
					loggername = newLoggerName;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});

		blockSizeField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				int newBlockSize = Integer.parseInt(blockSizeField.getText());
				if (Common.myClientSOAP.setVmecomBlocksize(name, newBlockSize)) {
					blocksize = newBlockSize;
				}
				frame.getContentPanel().getGrid().repaint();
			}
		});
*/
		boutonUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hostName = Common.myClientSOAP.getEqtHost(name);
				hostNameField.setText( hostName );
				userName = Common.myClientSOAP.getVmecomUsername(name);
				userNameField.setText( userName );
				expName = Common.myClientSOAP.getVmecomExpname(name);
				expNameField.setText( expName );
				loggername = Common.myClientSOAP.getVmecomLoggername(name);
				loggerNameField.setText( loggername );
				blocksize = Common.myClientSOAP.getVmecomBlocksize(name);
				blockSizeField.setText( Integer.toString(blocksize) );
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
				String newUserName = userNameField.getText();
				userName = Common.myClientSOAP.getVmecomUsername(name);
				if (!userName.equals(newUserName)) {
					if (Common.myClientSOAP.setVmecomUsername(name, newUserName)) {
						userName = newUserName;
					}
				}
				String newExpName = expNameField.getText();
				expName = Common.myClientSOAP.getVmecomExpname(name);
				if (!expName.equals(newExpName)) {
					if (Common.myClientSOAP.setVmecomExpname(name, newExpName)) {
						expName = newExpName;
					}
				}
				String newLoggerName = loggerNameField.getText();
				loggername = Common.myClientSOAP.getVmecomLoggername(name);
				if (!loggername.equals(newLoggerName)) {
					if (Common.myClientSOAP.setVmecomLoggername(name, newLoggerName)) {
						loggername = newLoggerName;
					}
				}
				int newBlockSize = Integer.parseInt(blockSizeField.getText());
				blocksize = Common.myClientSOAP.getVmecomBlocksize(name);
				if (blocksize != newBlockSize) {
					if (Common.myClientSOAP.setVmecomBlocksize(name, newBlockSize)) {
						blocksize = newBlockSize;
					}
				}
				frame.getContentPanel().getGrid().repaint();
				dialog.dispose();
			}
		});
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getExpName()
	{
		return expName;
	}

	public void setExpName(String expName)
	{
		this.expName = expName;
	}

	public void setLoggername(String loggername)
	{
		this.loggername = loggername;
	}

	public String getLoggername()
	{
		return loggername;
	}

	public void setBlocksize(int blocksize)
	{
		this.blocksize = blocksize;
	}

	public int getBlocksize()
	{
		return blocksize;
	}
}
