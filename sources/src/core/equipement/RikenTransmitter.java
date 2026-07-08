package core.equipement;

import gui.containers.Window;

public class RikenTransmitter extends Actor
{
	public RikenTransmitter(Window frame, String nomEquipement, String nomHost, String log_level, String container) {
		super(frame, nomEquipement, "gnarval_riken_transmitter", nomHost, log_level, container);
		type = EquipementType.RIKENTRANSMITTER;
	}
}
