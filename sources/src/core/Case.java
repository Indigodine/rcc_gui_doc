package core;

/**
 * Represente une case de la grille
 * @author malassigne
 *
 */
public class Case {

	private int x;
	private int y;

	public Case(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object another) {
		if(another instanceof Case) {
			Case c = (Case)another;
			return x == c.getX() && y == c.getY();
		}
		return false;
	}
}
