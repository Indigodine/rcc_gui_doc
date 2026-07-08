package core.reseau;

import java.util.ArrayList;

import core.equipement.Equipement;
import core.equipement.LienEqt;

/**
 * cette classe definit une structure contenant toutes les informations
 * necessaires a la definition d'une experience type
 * 
 */
public class StateExpSOAP {
	private String pathExp, nomExp;

	private ArrayList<Equipement> equipements;
	private ArrayList<LienEqt> links;

	/**
	 * constructeur de la classe
	 * 
	 * @param nom
	 *            le nom de l'experience
	 * @param path
	 *            le chemin ou se trouve l'experience
	 * @param listEqt
	 *            la liste des equipements existant
	 * @param listLien
	 *            la liste des liens existant
	 */
	public StateExpSOAP(String nom, String path, ArrayList<Equipement> listEqt, ArrayList<LienEqt> listLien)
	{
		nomExp = nom;
		pathExp = path;
		equipements = new ArrayList<Equipement>();
		links = new ArrayList<LienEqt>();
		equipements.addAll(listEqt);
		links.addAll(listLien);
	}

	/**
	 * Getter: permet d'acceder a un champ prive
	 * 
	 * @return le champ prive
	 */
	public String getPathExp()
	{
		return pathExp;
	}

	public String getNomExp()
	{
		return nomExp;
	}

	public ArrayList<Equipement> getListEqt() {
		return equipements;
	}

	public ArrayList<LienEqt> getListLink() {
		return links;
	}
}
