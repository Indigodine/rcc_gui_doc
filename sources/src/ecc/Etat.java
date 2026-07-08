package ecc;

import java.awt.*;
import javax.swing.JPanel;

public class Etat extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Dimension SIZE = new Dimension(178, 48);

	public enum Status
	{
		IDLE,
		DESCRIBED,
		PREPARED,
		READY,
		RUNNING
	}

	private Status statut = Status.IDLE;

	private boolean current = false;

	public Etat(Status st)
	{
		statut = st;
		setPreferredSize(SIZE);
		setMaximumSize(SIZE);
	}

	public Etat(Status st, boolean enabled)
	{
		statut = st;
		setPreferredSize(SIZE);
		setMaximumSize(SIZE);
		current = enabled;
	}

	public String getNext()
	{
		switch(statut)
		{
			case IDLE : return "describe";
			case DESCRIBED : return "prepare";
			case PREPARED : return "configure";
			case READY : return "start";
			case RUNNING : return null;
		}

		return null;
	}

	public String getUndo()
	{
		switch(statut)
		{
			case IDLE : return "undo";
			case DESCRIBED : return "undo";
			case PREPARED : return "break";
			case READY : return "stop";
			case RUNNING : return null;
		}

		return null;
	}

	public void set(boolean enabled)
	{
		current = enabled;
		repaint();
	}

	public Status getStatus()
	{
		return statut;
	}

	public void paintComponent(Graphics g)
	{
		int posX = 4;
		int posY = 4;
		if(current)
			g.setColor(new Color(0, 60, 0));
		else g.setColor(Color.BLACK);
		g.fillRect(posX, posY, (int)SIZE.getWidth() -9, (int)SIZE.getHeight()-9);
		Graphics2D g2 = (Graphics2D)g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		if(current)
		{
			g.setFont(new Font("Courrier", Font.PLAIN, 18));
			g.setColor(Color.GREEN);
			g.drawRect(0, 0, (int)SIZE.getWidth()-1, (int)SIZE.getHeight()-1);
		}
		else
		{
			g.setColor(Color.GRAY);
			g.setFont(new Font("Courrier", Font.PLAIN, 14));
		}
		FontMetrics metrics = g.getFontMetrics();
		int x = 0;
		int y = 0;
		g.drawRect(posX, posY, (int)SIZE.getWidth()-9, (int)SIZE.getHeight()-9);
		x += ((int)SIZE.getWidth() - metrics.stringWidth(statut.name())) /2;
		y += (int)SIZE.getHeight() /2 +5;
		g.drawString(statut.name(), x, y);
	}
}
