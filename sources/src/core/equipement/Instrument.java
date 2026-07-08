package core.equipement;

import gui.containers.Window;;

/**
 * classe abstraite permettant de repertorier les methodes et variables communes
 * a tous les instruments
 * 
 * 
 */
public abstract class Instrument extends Equipement
{
	/**
	 * 
	 */

	/**
	 * constructeur de la classe
	 * 
	 * @param nomEquipement
	 *            nom de l'equipement
	 * @param nomHost
	 *            nom de l'hote
	 * @param type
	 *            type de l'equipement
	 */
	public Instrument(Window frame, String nomEquipement, String nomHost, EquipementType type)
	{
		super(frame, nomEquipement, nomHost, type);
	}
}
