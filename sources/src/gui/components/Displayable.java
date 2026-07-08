package gui.components;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComponent;

public abstract class Displayable extends JComponent {

	private static final long serialVersionUID = 1L;
	public static Vector<Displayable> disps = new Vector<Displayable>();
	public int freq = 20; // tout les combien de tours de boucle le composant doit être refresh ? (MAX 20)

	public Displayable() {
		disps.add( this );
	}

	public void remove() {
		disps.remove( this );
	}

	public void setFreq(int f) {
		freq = f;
	}

	public static ArrayList<Displayable> getDisps() {
		ArrayList<Displayable> dis = new ArrayList<Displayable>();
		for(Displayable d : disps) {
			dis.add(d);
		}
		return dis;
	}

	public abstract void refresh();
}
