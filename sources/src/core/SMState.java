package core;

import java.awt.Color;

public enum SMState {
	OFFLINE			(255, 0, 0),
	IDLE			(128, 128, 128),
	READY			(0, 255, 255),
	RUNNING			(0, 255, 0),
	PAUSED			(0, 128, 0),
	ERROR			(128, 0, 0),
	NOMONITORING	(220, 30, 255),
	WARNING			(255, 128, 0);

	private Color color;

	SMState(int r, int g, int b) {
		color = new Color(r, g, b);
	}

	public Color getColor() {
		return color;
	}

	public static SMState decode(int val) {
		for (SMState gs : values()) {
			if (gs.ordinal() == val) return gs;
		}
		return null;
	}

	public static SMState decode(String val) {
		for (SMState gs : values()) {
			if (gs.name().equals(val)) return gs;
		}
		return null;
	}
}
