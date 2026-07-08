package core;

public enum SMTransition
{
	NONE	(""),
	INIT	("INIT"),
	START	("START"),
	STOP	("STOP"),
	PAUSE	("PAUSE"),
	RESUME	("RESUME"),
	EXIT	("EXIT");

	private String name;

	SMTransition(String str) {
		name = str;
	}

	public static SMTransition decode(int val) {
		for (SMTransition gs : values()) {
			if (gs.ordinal() == val)
				return gs;
		}
		return null;
	}

	public String getName() {
		return name;
	}
}