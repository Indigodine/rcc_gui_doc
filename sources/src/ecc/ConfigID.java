package ecc;

import java.awt.*;

import javax.swing.JComponent;

/**
 * Cette classe affiche les fichiers de configuration pour une configID donnée
 *
 */
public class ConfigID extends JComponent {

	private static final long serialVersionUID = -6410079701152970928L;
	private String describe;
	private String prepare;
	private String configure;
	private static final int SIZE_X = 250;
	private static final int SIZE_Y = 50;
	private boolean selected = false;
	private static final Color[] color = {
			new Color(0, 30, 90),
			new Color(200, 200, 200),
			Color.CYAN,
			Color.BLACK,
			Color.WHITE,
			Color.RED,
			Color.DARK_GRAY,
			Color.GRAY
	};
	private static final int BACK_SELECT = 0;
	private static final int BACK_NORMAL = 1;
	private static final int LABEL_SELECT = 2;
	private static final int LABEL_NORMAL = 3;
	private static final int VALUE_SELECT = 4;
	private static final int VALUE_NORMAL = 5;
	private static final int BACK_DISABLED = 6;
	private static final int LABEL_DISABLED = 7;
	private static final String DESC = "DESCRIBE : ";
	private static final String PREP = "PREPARE : ";
	private static final String CONF = "CONFIGURE : ";
	private static final int MARGE_X = 5;
	private boolean enabled = true;

	/**
	 * Constructeur de la classe, on lui passe directement les noms de fichiers
	 * @param desc fichier pour 'describe'
	 * @param prep fichier pour 'prepare'
	 * @param conf fichier pour 'configure'
	 */
	public ConfigID(String desc, String prep, String conf)
	{
		describe = desc;
		prepare = prep;
		configure = conf;
		setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
		setMinimumSize(new Dimension(SIZE_X, SIZE_Y));
	}

	/**
	 * Définit si le configId est sélectionnable ou non
	 */
	public void setEnabled(boolean bool)
	{
		enabled = bool;
		repaint();
	}

	/**
	 * [Dé]sélectionne le configID
	 * @param bool
	 */
	public void select(boolean bool)
	{
		selected = bool;
		repaint();
	}

	/**
	 * 
	 * @return
	 * le nom du fichier pour 'describe'
	 */
	public String getDescribe()
	{
		return describe;
	}

	/**
	 * 
	 * @return
	 * le nom du fichier pour 'prepare'
	 */
	public String getPrepare()
	{
		return prepare;
	}

	/**
	 * 
	 * @return
	 * le nom du fichier pour 'configure'
	 */
	public String getConfigure()
	{
		return configure;		
	}

	/**
	 * Cette méthode sert UNIQUEMENT au configId qui montre le configId actuellement utilisé
	 * Ce configID n'affiche donc que des 'copies' d'autres configId
	 * @param another
	 * un configId à copier
	 */
	public void setTo(ConfigID another)
	{
		describe = another.getDescribe();
		prepare = another.getPrepare();
		configure = another.getConfigure();
		repaint();
	}

	public void set(String desc, String prep, String conf)
	{
		describe = desc;
		prepare = prep;
		configure = conf;
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		Color intern = color[BACK_NORMAL];
		if(!enabled)
		{
			intern = color[BACK_DISABLED];
		}
		else if(selected) {
			intern = color[BACK_SELECT];
		}
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, SIZE_X, SIZE_Y, 15, 15);
		g.setColor(intern);
		g.fillRoundRect(2, 2, SIZE_X-4, SIZE_Y-4, 15, 15);
		int y = 15;
		FontMetrics metrics = g.getFontMetrics();
		drawStrings(g, DESC, describe, MARGE_X, y);
		y+=metrics.getAscent() +3;
		drawStrings(g, PREP, prepare, MARGE_X, y);
		y+=metrics.getAscent() +3;
		drawStrings(g, CONF, configure, MARGE_X, y);
	}

	/**
	 * Dessine deux chaînes de caractères passées en paramètre à une certaine position et des couleurs en fonction du configId
	 * @param g graphics de la méthode paint
	 * @param str1 le label à afficher
	 * @param str2 la valeur correspondante au label
	 * @param x position en X du texte
	 * @param y position en Y du texte
	 */
	public void drawStrings(Graphics g, String str1, String str2, int x, int y) {
		FontMetrics metrics = g.getFontMetrics();
		if(!enabled)
			g.setColor(color[LABEL_DISABLED]);
		else if(selected)
			g.setColor(color[LABEL_SELECT]);
		else
			g.setColor(color[LABEL_NORMAL]);
		g.drawString(str1, x, y);
		if(!enabled)
			g.setColor(color[LABEL_DISABLED]);
		else if(selected)
			g.setColor(color[VALUE_SELECT]);
		else g.setColor(color[VALUE_NORMAL]);
		g.drawString(str2, MARGE_X + metrics.stringWidth(str1), y);
	}
}
