package gui.containers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import core.Common;

@SuppressWarnings("serial")
public class LogPanel extends JPanel implements MouseListener {

	private final static int WIDTH = 1400;
	private final static int HEIGHT = 250;
	private final static int MSG_LENGTH = 75;
	private final static int LOGGER_LENGTH = 40;
	private final static Color TRACE = new Color(230, 230, 230);
	private final static Color DEBUG = new Color(220, 220, 130);
	private final static Color INFO = new Color(150, 200, 220);
	private final static Color WARN = new Color(220, 130, 25);
	private final static Color ERROR = new Color(255, 20, 55);
	private final static Color FATAL = new Color(100, 0, 0);
	private JPanel tableau = new JPanel();
	private GridBagConstraints gbc = new GridBagConstraints();
	private JTextField messageFilter = new JTextField("");
	private JComboBox loggerFilter = new JComboBox();
	private ArrayList<Message> messages = new ArrayList<Message>();
	private JComboBox levelFilter = new JComboBox();
	private JButton filtrer = new JButton(Common.getString("Filtrer"));
	private JButton clear = new JButton(Common.getString("Clear"));
	private JScrollPane scroll;
	/**
	 * Classe interne permettant d'afficher une ligne de message
	 * @author malassigne
	 *
	 */
	class TextLine extends JComponent {

		private Color color = Color.white;
		private String date;
		private String level;
		private String logger;
		private String message;
		private String remoteInfo;
		private String displayedDate;
		private String displayedLevel;
		private String displayedLogger;
		private String displayedMessage;
		private String displayedRemoteInfo;
		private final Dimension SIZE = new Dimension(1400, 25);

		/**
		 * Constructeur
		 * @param color
		 * @param s
		 * châines à utiliser
		 */
		public TextLine(Color c, String d, String l, String log, String m, String r) {

			color = c;
			date = d;
			level = l;
			logger = log;
			message = m;
			remoteInfo = r;

			displayedDate = date;
			displayedLevel = level;
			displayedLogger = logger;
			displayedMessage = message;
			displayedRemoteInfo = remoteInfo;

			setPreferredSize(SIZE);
			setMinimumSize(SIZE);
/*
			if (displayedMessage.length() > MSG_LENGTH) {
				displayedMessage = displayedMessage.substring(0, MSG_LENGTH - 3) + "...";
			}

			if (displayedLogger.length() > LOGGER_LENGTH) {
				displayedLogger = displayedLogger.substring(0, LOGGER_LENGTH - 3) + "...";
			}
*/
		}

		public String getDate() {
			return date;
		}

		public String getLevel() {
			return level;
		}

		public String getLogger() {
			return logger;
		}

		public String getMessage() {
			return message;
		}

		public String getRemoteInfo() {
			return remoteInfo;
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.fillRect(0, 0, (int)SIZE.getWidth(), (int)SIZE.getHeight());

			boolean black = ( color.getRed() + color.getGreen() + color.getBlue() > 384);

			g.setColor( black ? Color.BLACK : Color.WHITE );

			g.drawString( displayedDate, 10, 18 );
			g.drawString( displayedLevel, 200, 18 );
			g.drawString( displayedLogger, 300, 18 );
			g.drawString( displayedMessage, 600, 18 );

			g.setColor(Color.BLACK);
			g.fillRect(0, (int)SIZE.getHeight()-1, (int)SIZE.getWidth(), 1);
		}
	}

	class Message {

		private String date;
		private Level level;
		private String logger;
		private String message;
		private String remoteInfo;

		public Message(String d, Level l, String log, String m, String r) {
			date = d;
			level = l;
			logger = log;
			message = m;
			remoteInfo = r;
		}

		public String getDate() {
			return date;
		}

		public Level getLevel() {
			return level;
		}

		public String getLogger() {
			return logger;
		}

		public String getMessage() {
			return message;
		}

		public String getRemoteInfo() {
			return remoteInfo;
		}
	}

	public LogPanel() {
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		tableau.setLayout(new GridBagLayout());
		add(new TextLine(Color.BLACK, "Date", "Level", "Logger", "Message", ""));
		JPanel filters = new JPanel(new GridBagLayout());
		JPanel tmp1 = new JPanel();
		JPanel tmp2 = new JPanel();
		tmp1.setPreferredSize(new Dimension(100, 20));
		levelFilter.setPreferredSize(new Dimension(100, 20));
		loggerFilter.setPreferredSize(new Dimension(300, 20));
		messageFilter.setPreferredSize(new Dimension(300, 20));
//		filtrer.setPreferredSize(new Dimension(100, 20));
//		clear.setPreferredSize(new Dimension(100, 20));
//		tmp2.setPreferredSize(new Dimension(190, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		filters.add(tmp1,gbc);
		gbc.gridx++;
		filters.add(levelFilter,gbc);
		gbc.gridx++;
		filters.add(loggerFilter,gbc);
		gbc.gridx++;
		filters.add(messageFilter,gbc);
		gbc.gridx++;
		filters.add(filtrer,gbc);
		gbc.gridx++;
		filters.add(clear,gbc);
		add(filters);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		scroll = new JScrollPane(tableau);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scroll);
		initPanels();
		setBorder(border("Messages", Color.BLACK));
//		setOpaque(true);
//		setBackground(Color.WHITE);

		loggerFilter.addItem("ALL");

		loggerFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				filter();
			}
		});

		levelFilter.addItem(Level.ALL);
		levelFilter.addItem(Level.TRACE);
		levelFilter.addItem(Level.DEBUG);
		levelFilter.addItem(Level.INFO);
		levelFilter.addItem(Level.WARN);
		levelFilter.addItem(Level.ERROR);
		levelFilter.addItem(Level.FATAL);

		levelFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				filter();
			}
		});

		filtrer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filter();
		    }
		});

		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearMessages();
		    }
		});
	}

	public void clearMessages() {
		messages.clear();
		initPanels();
		updateUI();
	}

	public void filter() {

		initPanels();

		int last = messages.size() - 1;

		for (int i = last; i >= 0; i--) {
			Message msg = messages.get(i);
			boolean msgMatch = messageFilter.getText().length()==0 || msg.getMessage().toLowerCase().startsWith(messageFilter.getText().toLowerCase());
			boolean levelMatch = msg.getLevel().isGreaterOrEqual((Level)levelFilter.getSelectedItem()) || ((Level)levelFilter.getSelectedItem()).equals(Level.ALL);
			boolean loggerMatch = loggerFilter.getItemCount()==0 || loggerFilter.getSelectedItem().equals(msg.getLogger())
								|| loggerFilter.getSelectedItem().equals("ALL");

			if (levelMatch && msgMatch && loggerMatch)
				addLine(msg.getDate(),msg.getLevel(),msg.getLogger(),msg.getMessage(),msg.getRemoteInfo());
		}

		updateUI();
	}

	/**
	 * Si le socket lève une exception (impossible de se connecter)
	 * alors on l'affiche dans le panneau
	 */
	public void raiseException(Exception e) {
		setBorder(border("Could not connect to server : " + e.getMessage(), Color.RED));
		tableau.removeAll();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		JLabel error = new JLabel("(!) No message will be received (!)", SwingConstants.CENTER);
		error.setOpaque(true);
		error.setBackground(Color.RED.darker());
		error.setForeground(Color.YELLOW);
		tableau.add(error, gbc);
		updateUI();
	}

	/**
	 * Construit des bordures
	 * @return
	 */
	private Border border(String title, Color fore) {
		Border b = BorderFactory.createEmptyBorder();
		return BorderFactory.createTitledBorder(b, title, TitledBorder.CENTER, TitledBorder.ABOVE_TOP, Font.decode("Courrier 18"), fore);
	}

	/**
	 * Initialise le tableau
	 */
	public void initPanels() {
		tableau.removeAll();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;	
		gbc.gridwidth = 6;
	}

	private void addLine(String date, Level sev, String log, String msg, String rem) {

		Color color = TRACE;

		if      (sev.equals(Level.DEBUG)) color = DEBUG;
		else if (sev.equals(Level.INFO))  color = INFO; 
		else if (sev.equals(Level.WARN))  color = WARN;
		else if (sev.equals(Level.ERROR)) color = ERROR;
		else if (sev.equals(Level.FATAL)) color = FATAL;

		TextLine line = new TextLine(color, date, sev.toString(), log, msg, rem);

		line.addMouseListener(this);

		tableau.add(line, gbc);
		gbc.gridy++;
		tableau.add(new JSeparator(JSeparator.HORIZONTAL), gbc);
		gbc.gridy++;
		scroll.getVerticalScrollBar().setUnitIncrement(10);
		scroll.getVerticalScrollBar().setValue(0);
	}

	/**
	 * Vérifie si le logger est déjà dans la liste
	 * et l'ajoute si ce n'est pas le cas
	 * @param logger
	 */
	public void check(String logger) {
		for (int i = 0; i < loggerFilter.getItemCount(); i++) {
			if (loggerFilter.getItemAt(i).equals(logger)) return;
		}
		loggerFilter.addItem(logger);
	}

	/**
	 * Décode le message contenu dans un socket réseau
	 * @param socket le socket à décoder
	 */
	public void decodeMessage(Socket socket) {

		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (Exception e) {
			ois =  null;
			e.printStackTrace();
		}

		if (ois != null) {
			String hostName = socket.getInetAddress().getHostName();
			String remoteInfo = hostName + ":" + socket.getPort();
			System.out.println(remoteInfo + " connected");
			try {
				while (true) {
					LoggingEvent event = (LoggingEvent)ois.readObject();
					Level level = event.getLevel();
					String logger = event.getLoggerName();
					String message = (String)event.getMessage();
					SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String date = ft.format(new Date());
					check(logger);
					messages.add(new Message(date, level, logger, message, remoteInfo));
					filter();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * MouseListeners
	 */
	@Override
	public void mouseClicked(MouseEvent evt)
	{
		if (evt.getSource() instanceof TextLine) {

			TextLine text = (TextLine)evt.getSource();

			String dialog = "Level : " + text.getLevel() + "\nDate : " + text.getDate() + "\nLogger : " + text.getLogger()
							+ "\nRemote Info : " + text.getRemoteInfo() + "\n- - - - - - - - - - \nMessage : \n "+ text.getMessage();
/*
			int len = 0;

			for(char c : text.message().toCharArray()) {
				if(len>60 && (c == ' ' || c == '_' || c == '-') ) { // si on trouve un séparateur de mot on passe à la ligne
					dialog += "\n" + c;
					len=0;
				} else {
					dialog += c;
					if(c == '\n')
						len=0;
					else len++;
				}
			}
*/
			JTextPane textPane = new JTextPane();
			textPane.setText(dialog);
			textPane.setEditable(false);
			JScrollPane scroll = new JScrollPane(textPane);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setMaximumSize(new Dimension(300, 550));
			
			JPanel pan = new JPanel(new BorderLayout());
			pan.add(scroll, BorderLayout.CENTER);
			pan.setMaximumSize(new Dimension(400, 600));
			pan.setPreferredSize(new Dimension(400, 600));
			JOptionPane.showMessageDialog(this, pan, "Message", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0){}

	@Override
	public void mouseExited(MouseEvent arg0){}

	@Override
	public void mousePressed(MouseEvent arg0){}

	@Override
	public void mouseReleased(MouseEvent arg0){}
}
