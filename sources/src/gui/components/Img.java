package gui.components;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class Img {

	private static HashMap<String, Image> images = new HashMap<String, Image>();

	/**
	 * Demande une image
	 * Si elle a déjà été demandée, renvoie l'image depuis une map, sinon l'ajoute
	 * à la map et la renvoie
	 * @param path
	 * @return
	 */
	public static Image get(String path) {
		if(images.containsKey(path)) {
			return images.get(path);
		}
		java.net.URL url = Img.class.getResource("/img/" + path + ".png");
		if (url == null)
		{
			System.out.println("Img.icon: url = null " + path + ".png");
			return null;
		}
		Image img = Toolkit.getDefaultToolkit().createImage(url);
		images.put(path, img);
		return img;
	}

	/**
	 * Demande une imageIcon
	 * Si elle a déjà été demandée, renvoie l'image depuis une map, sinon l'ajoute
	 * à la map et la renvoie
	 * @param path
	 * @return
	 */
	public static ImageIcon icon(String path) {
		if(images.containsKey(path)) {
			return new ImageIcon(images.get(path));
		}
		java.net.URL url = Img.class.getResource("/img/" + path + ".png");
		if (url == null)
		{
			System.out.println("Img.icon: url = null " + path + ".png");
			return null;
		}
		Image img = Toolkit.getDefaultToolkit().createImage(url);
		images.put(path, img);
		ImageIcon i = new ImageIcon(img);
		return i;
	}
}
