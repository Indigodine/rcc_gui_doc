package core.equipement;

public enum EquipementType {
	
	SUBSYSTEM_NARVAL	("Narval sub-system"),
	ACTOR				("Actor"),
	SBUFPRODUCER		("SbufProducer"),
	EVENTBUILDER		("EventBuilder"),
	STORAGE				("Storage"),
	RIKENTRANSMITTER	("RikenTransmitter"),
	VMECOM				("VME"),
	MIDAS				("MIDAS"),
	ECC					("ECC");

	String name;

	EquipementType(String s) {
		name = s;
	}

	public static EquipementType decode(int val) {
		for(EquipementType typ : values()) {
			if(typ.ordinal() == val)
				return typ;
		}
		return null;
	}

	public String getName() {
		return name;
	}
}
