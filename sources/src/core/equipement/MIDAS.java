package core.equipement;

import gui.containers.Window;

/**
 * cette classe permet d'instancier un equipement de type MIDAS
 * 
 * 
 */
public class MIDAS extends Equipement
{
	/**
	 * constructeur de la classe
	 * 
	 * @param nomEquipement
	 *            nom de l'equipement
	 * @param nomHost
	 *            nom de l'hote
	 */
	public MIDAS(Window frame, String nomEquipement, String nomHost)
	{
		super(frame, nomEquipement, nomHost, EquipementType.MIDAS);
	}
}
