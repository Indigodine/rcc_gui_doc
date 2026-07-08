package core.reseau;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import rcc.rcc.Rcc;
import rcc.rcc.RccPortType;

import com.sun.xml.internal.ws.client.ClientTransportException;

import core.Common;
import core.equipement.Equipement;
import core.equipement.EquipementType;
import core.equipement.Actor;
import core.equipement.SbufProducer;
import core.equipement.EventBuilder;
import core.equipement.Storage;
import core.equipement.RikenTransmitter;
import core.equipement.ECC;
import core.equipement.LienEqt;
import core.equipement.MIDAS;
import core.equipement.NARVAL;
import core.equipement.VMECOM;
import gui.containers.Window;

/**
 * cette classe permet de creer un client SOAP et de le relier au serveur dedie
 * à cet usage
 * 
 * 
 */
public class ClientSOAP {

	private RccPortType port = null;
	private Rcc service = null;
	private ArrayList<Equipement> listEquipements;
	private ArrayList<LienEqt> listLiens;

	private boolean debugSoap = false; // Booleen qui permet de debugger la
	// cummunication SOAP en affichant (ou non) tous
	// les resultats des transactions
	private Window pere;

	/**
	 * constructeur de la classe
	 */
	public ClientSOAP(Window pere) {

		this.pere = pere;

		service = new Rcc();
		port = service.getRcc();

		// Modif du host server et numero de port (ecrase ceux defini dans le wsdl)
		String endpointURL = "http://" + Common.getRCCHost() + ":" + Common.getRCCPort() + "/"; 
		BindingProvider bp = (BindingProvider) port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
	}

	/**
	 * cette methode permet d'envoyer une requete de creation d'experience au
	 * serveur
	 * 
	 * @param name
	 *            nom de l'experience
	 * @return un boolean du resultat commande
	 */
	public boolean creerExperience(String name, String path) {
		Reponse out = new Reponse();
		port.rccCreateExperiment(name, path, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "creerExperience : " + name, false);
	}

	/**
	 * cette methode d'attribuer un nom a l'experience
	 * 
	 * @param name
	 *            nom de l'experience
	 * @return un boolean du resultat commande
	 */
	public boolean setNomExperience(String name) {
		Reponse out = new Reponse();
		port.rccSetExperimentName(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setNomExperience : " + name, false);
	}

	/**
	 * cette methode d'attribuer un chemin a l'experience
	 * 
	 * @param name
	 *            Chemin
	 * @return un boolean du resultat commande
	 */
	public boolean setPathExperience(String name) {
		Reponse out = new Reponse();
		port.rccSetExperimentPath(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setPathExperience : " + name, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de destruction d'experience au
	 * serveur
	 * 
	 * @return un boolean du resultat commande
	 */
	public boolean deleteExperience(){
		Reponse out = new Reponse();
		port.rccDeleteExperiment(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "deleteExperience", false);
	}

	public boolean emptyExperiment(){
		Reponse out = new Reponse();
		port.rccEmptyExperiment(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "emptyExperiment", false);
	}

	/**
	 * cette methode permet d'envoyer une requete de creation d'equipement au
	 * serveur
	 * 
	 * @param eqt
	 *            l'equipement a creer
	 * @return un boolean du resultat commande
	 * 
	 */
	public boolean creerEquipement(Equipement eqt) {

		Reponse out = new Reponse();

		switch(eqt.getType()) {
			case SUBSYSTEM_NARVAL :
				NARVAL narval = (NARVAL)eqt;
				port.rccCreateNarval(narval.getName(), narval.getHostName(), narval.getCpu(), out.getError(), out.getErrorMessage());
				break;
			case MIDAS :
				MIDAS midas = (MIDAS)eqt;
				port.rccCreateMidas(midas.getName(), midas.getHostName(), out.getError(), out.getErrorMessage());
				break;
			case VMECOM :
				VMECOM vmecom = (VMECOM)eqt;
				port.rccCreateVmecom(vmecom.getName(), vmecom.getHostName(), vmecom.getUserName(), vmecom.getExpName(),
						vmecom.getLoggername(), vmecom.getBlocksize(), out.getError(), out.getErrorMessage());
				break;
			case ECC :
				ECC ecc = (ECC)eqt;
				port.rccCreateECCore(ecc.getName(), ecc.getHostName(), ecc.getPortNumber(), out.getError(), out.getErrorMessage());
				break;
			case ACTOR :
				Actor a = (Actor)eqt;
				if (a.template) {
					port.rccCreateActorFromTemplate(
							a.getName(),
							a.getFileName(),
							a.getHostName(),
							a.getContainerNARVAL(),
							a.getLogLevel(),
							out.getError(),
							out.getErrorMessage());
					a.setFileName(getActorExecFile(a.getName()));
				}
				else
					port.rccCreateActor(
							a.getName(),
							a.getHostName(),
							a.getContainerNARVAL(),
							a.getFileName(),
							a.getLogLevel(),
							out.getError(),
							out.getErrorMessage());
				break;
			case SBUFPRODUCER :
				a = (Actor)eqt;
				port.rccCreateSbufProducer(
						a.getName(),
						a.getHostName(),
						a.getContainerNARVAL(),
						a.getFileName(),
						a.getLogLevel(),
						out.getError(),
						out.getErrorMessage());
				break;
			case EVENTBUILDER :
				a = (Actor)eqt;
				port.rccCreateEventBuilder(
						a.getName(),
						a.getHostName(),
						a.getContainerNARVAL(),
						a.getFileName(),
						a.getLogLevel(),
						out.getError(),
						out.getErrorMessage());
				break;
			case STORAGE :
				a = (Actor)eqt;
				port.rccCreateStorage(
						a.getName(),
						a.getHostName(),
						a.getContainerNARVAL(),
						a.getFileName(),
						a.getLogLevel(),
						out.getError(),
						out.getErrorMessage());
				break;
			case RIKENTRANSMITTER :
				a = (Actor)eqt;
				port.rccCreateRikenTransmitter(
						a.getName(),
						a.getHostName(),
						a.getContainerNARVAL(),
						a.getFileName(),
						a.getLogLevel(),
						out.getError(),
						out.getErrorMessage());
				break;
		}

		if (debugSoap) this.checkup(eqt.getName(), out);

		return traiterErreur(out, "creerEquipement : " + eqt.getName(), false);
	}

	/**
	 * cette methode permet d'envoyer une requete de creation d'un lien au
	 * serveur
	 * 
	 * @param lien
	 *            le lien a creer
	 * @return un boolean du resultat commande
	*/
	public boolean creerLien(LienEqt lien) {
		Reponse out = new Reponse();

		port.rccCreateLink(lien.getNom(), lien.getMySrcName(), lien.getMyDstName(), lien.getSourceOutput(), lien.getPortSrc(),
				lien.getPortDest(), lien.getBufferSize(), out.getError(), out.getErrorMessage());

		if (traiterErreur(out, "creerLien : " + lien.getNom(), false) == false) return false;

		return setLinkDepth(lien.getNom(), lien.getBufferDepth());
	}

	/**
	 * cette methode permet d'envoyer une requete de suppression d'un equipement
	 * au serveur
	 * 
	 * @param nom
	 *            l'equipement a supprimer
	 * @return un boolean du resultat commande
	 */
	public boolean deleteEquipement(String nom) {
		Reponse out = new Reponse();
		port.rccDeleteEquipment(nom, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "deleteEquipement : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de suppression d'un lien au
	 * serveur
	 * 
	 * @param nom
	 *            le lien a supprimer
	 * @return un boolean du resultat commande
	 */
	public boolean deleteLien(String nom)
	{
		Reponse out = new Reponse();

		port.rccDeleteLink(nom, out.getError(), out.getErrorMessage());

		if (debugSoap)
			this.checkup(nom, out);
		return traiterErreur(out, "deleteLien : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du nom d'un
	 * equipement au serveur
	 * 
	 * @param oldName
	 *            l'ancien nom de l'equipement
	 * @param newName
	 *            le nouveau nom de l'equipement
	 * @return un boolean du resultat commande
	 */
	public boolean modifNomEqt(String oldName, String newName)
	{
		Reponse out = new Reponse();

		port.rccSetEquipmentName(oldName, newName, out.getError(), out.getErrorMessage());

		if (debugSoap)
			this.checkup(newName, out);
		return traiterErreur(out, "modifNomEqt : " + oldName + " -> " + newName, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du niveau de
	 * Debug d'un acteur NARVAL au serveur
	 * 
	 * @param nomActeur
	 *            le nom de l'acteur concerne
	 * @param debugLevel
	 *            le nouveau niveau de debug
	 * @return un boolean du resultat commande
	 */
	public boolean setActorLogLevel(String nomActeur, String debugLevel)
	{
		Reponse out = new Reponse();

		port.rccSetActorLogLevel(nomActeur, debugLevel, out.getError(), out.getErrorMessage());

		if (debugSoap)
			this.checkup(nomActeur, out);
		return traiterErreur(out, "setLogLevel : " + nomActeur, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du nom d'un
	 * lien au serveur
	 * 
	 * @param oldName
	 *            l'ancien nom du lien
	 * @param newName
	 *            le nouveau nom du lien
	 * @return un boolean du resultat commande
	 */
	public boolean modifNomLien(String oldName, String newName)
	{
		Reponse out = new Reponse();

		port.rccSetLinkName(oldName, newName, out.getError(), out.getErrorMessage());

		if (debugSoap)
			this.checkup(newName, out);
		return traiterErreur(out, "modifNomLien : " + oldName + "  -> " + newName, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement du nom de
	 * l'experience en cours sur le serveur
	 * 
	 * @return une structure de type ReponseString possedant la reponse du
	 *         serveur a cette requete
	 * 
	 */
	public String getExpName()
	{
		ReponseString out = new ReponseString();

		port.rccGetExperimentName(out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getExpName", false);
	}

	/**
	 * cette methode permet de savoir si une experience existe
	 * 
	 * @return boolean en reponse
	 * 
	 */
	public boolean isExpExist() throws ClientTransportException
	{
		ReponseString out = new ReponseString();

		port.rccGetExperimentName(out.getError(), out.getErrorMessage(), out.getValue());
		if (out.getError().value == 0)
			return true;
		else
			return false;
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement du chemin de
	 * l'experience en cours sur le serveur
	 * 
	 * @return une structure de type Reponse possedant la reponse du serveur a
	 *         cette requete
	 */
	public String getExpPath()
	{
		ReponseString out = new ReponseString();

		port.rccGetExperimentPath(out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getExpPath", false);
	}
	/**
	 * cette methode permet d'envoyer une requete de renseignement du chemin de
	 * l'experience en cours sur le serveur
	 * 
	 * @return une structure de type Reponse possedant la reponse du serveur a
	 *         cette requete
	 */
	public String getExperimentCfgFile()
	{
		ReponseString out = new ReponseString();

		port.rccGetExperimentCfgFile(out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getExperimentCfgFile", false);
	}
	/**
	 * cette methode permet d'envoyer une requete de renseignement du nombre
	 * d'equipements de l'experience en cours sur le serveur
	 * 
	 * @return une structure de type Reponse possèdant la reponse du serveur a
	 *         cette requete
	 */
	public int getNbEqt()
	{
		ReponseInt out = new ReponseInt();

		port.rccGetEquipmentCount(out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getNbEqt", false);
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement de
	 * l'equipement present a l'index donne dans l'experience en cours sur le
	 * serveur
	 * 
	 * @param index
	 *            index de l'equipement a recuperer
	 * 
	 * @return une structure de type Reponse possedant la reponse du serveur a
	 *         cette requete
	 */
	public ReponseEquip getEqtAt(int index) {
		ReponseEquip out = new ReponseEquip();
		port.rccGetEquipmentByIndex(index, out.getError(), out.getErrorMessage(), out.getName(), out.getType(), out.getTypestr(),
				out.getHost(), out.getCpu(), out.getNarval(), out.getExe(), out.getLog(), out.getUsername(), out.getExpname(),
				out.getLoggername(), out.getBlocksize(), out.getPort(), out.getId());
		return out;
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement du type d'un
	 * equipement donne dans l'experience en cours sur le serveur
	 * 
	 * @param name
	 *            nom de l'equipement dont on veut connaître le type
	 * 
	 * @return une structure de type Reponse possèdant la reponse du serveur a
	 *         cette requete
	 */
	public int getTypeEqt(String name)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetEquipmentType(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getTypeEqt : " + name, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement du nom de
	 * l'hote d'un equipement donne dans l'experience en cours sur le serveur
	 * 
	 * @param name
	 *            nom de l'equipement dont veut connaitre l'hote
	 * 
	 * @return une structure de type Reponse possedant la reponse du serveur a�
	 *         cette requete
	 */
	public String getEqtHost(String name)
	{
		ReponseString out = new ReponseString();
		port.rccGetEquipmentHost(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getEqtHost : " + name, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modif du nom de l'hote
	 * 
	 * @param name
	 *            nom de l'equipement dont veut connaitre l'hote, nouveau hote
	 * 
	 * @return true si pas d'erreur
	 */
	public boolean setEqtHost(String name, String host)
	{
		Reponse out = new Reponse();
		port.rccSetEquipmentHost(name, host, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setEqtHost : " + name, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement du nombre de
	 * lien existent dans l'experience en cours sur le serveur
	 * 
	 * @return une structure de type Reponse possedant la reponse du serveur a
	 *         cette requete
	 */
	public int getNbLien()
	{
		ReponseInt out = new ReponseInt();

		port.rccGetLinkCount(out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getNbLien", false);
	}

	/**
	 * cette methode permet d'envoyer une requete de renseignement du lien
	 * present a l'index donne dans l'experience en cours sur le serveur
	 * 
	 * @param index
	 *            index du lien a recuperer
	 * 
	 * @return une structure de type Reponse possedant la reponse du serveur a
	 *         cette requete
	 */
	public ReponseLink getLienAt(int index)
	{
		ReponseLink out = new ReponseLink();

		port.rccGetLinkByIndex(index, out.getError(), out.getErrorMessage(), out.getName(), out.getSourceName(), out.getDestName(),
				out.getSourceOutput(), out.getSourcePort(), out.getDestPort(), out.getBufferSize());

		return out;
	}

	/**
	 * cette methode permet d'envoyer une requete de recuperation du nom du
	 * fichier executable d'un acteur au serveur 
	 * @param nom
	 *            nom de l'acteur dont on veut recuperer le nom du fichier
	 *            executable
	 * 
	 * @return le nom du fichier executable
	 */
	public String getActorExecFile(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetActorExecutableFile(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getExecFile : " + nom, false);
	}

	/**
	 * cette methode permet de modifier le nom du fichier executable d'un acteur
	 * au serveur
	 * 
	 * @param nom
	 *            nom de l'acteur dont on veut recuperer le nom du fichier
	 *            executable
	 * @param file
	 *            nouveau fichier
	 * @return booleen
	 */
	public boolean setActorExecFile(String nom, String file)
	{
		Reponse out = new Reponse();

		port.rccSetActorExecutableFile(nom, file, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setActorExecFile : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de connect
	 * 
	 * @param nom
	 *            nom de l'acteur concerne
	 * @return un boolean du resultat commande
	 */
	public boolean sbufProducerConnect(String nom)
	{
		Reponse out = new Reponse();

		port.rccSbufProducerConnect(nom, out.getError(), out.getErrorMessage());

		if (debugSoap) this.checkup(nom, out);

		return traiterErreur(out, "sbufProducerConnect : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de disconnect
	 * 
	 * @param nom
	 *            nom de l'acteur concerne
	 * @return un boolean du resultat commande
	 */
	public boolean sbufProducerDisconnect(String nom)
	{
		Reponse out = new Reponse();

		port.rccSbufProducerDisconnect(nom, out.getError(), out.getErrorMessage());

		if (debugSoap) this.checkup(nom, out);

		return traiterErreur(out, "sbufProducerDisconnect : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de l'etat de la connection
	 * d'un sbufproducer � son chassis
	 * 
	 * @param nom
	 *            nom de l'acteur dont on veut recuperer l'etat de la connection
	 * 
	 * @return etat
	 */
	public boolean getConnectState(String nom)
	{
		ReponseBool out = new ReponseBool();

		port.rccGetSbufProducerConnectState(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getConnectState : " + nom, false);
	}

	public boolean readConnectState(String nom)
	{
		ReponseBool out = new ReponseBool();

		port.rccReadSbufProducerConnectState(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readConnectState : " + nom, false);
	}

	/**
	 * cette methode permet de connaitre le nom du chassis du sbufproducer
	 * @param nom
	 *            nom de l'acteur sbufproducer
	 * 
	 * @return etat
	 */
	public String getSbufProducerCrateAddr(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetSbufProducerCrateAddr(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerCrateAddr : " + nom, false);
	}

	public String readSbufProducerCrateAddr(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccReadSbufProducerCrateAddr(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerCrateAddr : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification dela cpu
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param nomCpu
	 *            le nouveau nom de la cpu
	 * @return un boolean du resultat commande
	 */
	public boolean setSbufProducerCrateAddr(String nomActeur, String nomCpu)
	{
		Reponse out = new Reponse();

		port.rccSetSbufProducerCrateAddr(nomActeur, nomCpu, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerCrateAddr : " + nomActeur + " " + nomCpu, false);
	}

	public boolean writeSbufProducerCrateAddr(String nomActeur, String nomCpu)
	{
		Reponse out = new Reponse();

		port.rccWriteSbufProducerCrateAddr(nomActeur, nomCpu, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerCrateAddr : " + nomActeur + " " + nomCpu, false);
	}

	/**
	 * cette methode permet de connaitre le blocksize du sbuf producer
	 * 
	 * @param nom
	 *            nom de l'acteur sbuf producer
	 * 
	 * @return blocksize (-1 si erreur)
	 */
	public int getSbufProducerBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetSbufProducerBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerBlockSize : " + nom, false);
	}

	public int readSbufProducerBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadSbufProducerBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerBlockSize : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du blocksize
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param blockSize
	 *            le nouveau blocksize
	 * @return resultat commande (true OK)
	 */
	public boolean setSbufProducerBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccSetSbufProducerBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerBlockSize : " + nomActeur, false);
	}

	public boolean writeSbufProducerBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccWriteSbufProducerBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerBlockSize : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le flushtime du sbuf producer
	 * 
	 * @param nom
	 *            nom de l'acteur sbuf producer
	 * 
	 * @return flushtime (-1 si erreur)
	 */
	public float getSbufProducerFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccGetSbufProducerFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerFlushTime : " + nom, false);
	}

	public float readSbufProducerFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccReadSbufProducerFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerFlushTime : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du flushtime
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param flushtime
	 *            le nouveau flushtime
	 * @return resultat commande (true OK)
	 */
	public boolean setSbufProducerFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccSetSbufProducerFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerFlushTime : " + nomActeur, false);
	}

	public boolean writeSbufProducerFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccWriteSbufProducerFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerFlushTime : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si endianChecking du sbuf producer
	 * 
	 * @param nom
	 *            nom de l'acteur sbuf producer
	 * 
	 * @return endianChecking
	 */
	public boolean getSbufProducerEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetSbufProducerEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerEndianChecking : " + nom, false);
	}

	public boolean readSbufProducerEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadSbufProducerEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerEndianChecking : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le endianChecking
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param endianChecking
	 * @return resultat commande (true OK)
	 */
	public boolean setSbufProducerEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccSetSbufProducerEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerEndianChecking : " + nomActeur, false);
	}

	public boolean writeSbufProducerEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccWriteSbufProducerEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerEndianChecking : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si timeStamp du sbuf producer
	 * 
	 * @param nom
	 *            nom de l'acteur sbuf producer
	 * 
	 * @return timeStamp
	 */
	public boolean getSbufProducerTimeStamped(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetSbufProducerTimeStamped(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerTimeStamp : " + nom, false);
	}

	public boolean readSbufProducerTimeStamped(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadSbufProducerTimeStamped(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerTimeStamp : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le timeStamp
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param timeStamp
	 * @return resultat commande (true OK)
	 */
	public boolean setSbufProducerTimeStamped(String nomActeur, boolean timeStamp)
	{
		ReponseBool out = new ReponseBool();

		port.rccSetSbufProducerTimeStamped(nomActeur, timeStamp, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerTimeStamp : " + nomActeur, false);
	}

	public boolean writeSbufProducerTimeStamped(String nomActeur, boolean timeStamp)
	{
		ReponseBool out = new ReponseBool();

		port.rccWriteSbufProducerTimeStamped(nomActeur, timeStamp, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerTimeStamp : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si source modifiee du sbuf producer
	 * 
	 * @param nom
	 *            nom de l'acteur sbuf producer
	 * 
	 * @return sourceModif
	 */
	public boolean getSbufProducerSourceModif(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetSbufProducerSourceModif(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerSourceModif : " + nom, false);
	}

	public boolean readSbufProducerSourceModif(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadSbufProducerSourceModif(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerSourceModif : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier la source
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param sourceModif
	 * @return resultat commande (true OK)
	 */
	public boolean setSbufProducerSourceModif(String nomActeur, boolean sourceModif)
	{
		ReponseBool out = new ReponseBool();

		port.rccSetSbufProducerSourceModif(nomActeur, sourceModif, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerSourceModif : " + nomActeur, false);
	}

	public boolean writeSbufProducerSourceModif(String nomActeur, boolean sourceModif)
	{
		ReponseBool out = new ReponseBool();

		port.rccWriteSbufProducerSourceModif(nomActeur, sourceModif, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerSourceModif : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre l'ident du source du sbuf producer
	 * 
	 * @param nom
	 *            nom de l'acteur sbuf producer
	 * 
	 * @return sourceIdent (-1 si erreur)
	 */
	public int getSbufProducerSourceIdent(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetSbufProducerSourceIdent(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getSbufProducerSourceIdent : " + nom, false);
	}

	public int readSbufProducerSourceIdent(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadSbufProducerSourceIdent(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readSbufProducerSourceIdent : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de l'ident du
	 * source
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param sourceIdent
	 *            le nouveau sourceIdent
	 * @return resultat commande (true OK)
	 */
	public boolean setSbufProducerSourceIdent(String nomActeur, int sourceIdent)
	{
		Reponse out = new Reponse();

		port.rccSetSbufProducerSourceIdent(nomActeur, sourceIdent, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setSbufProducerSourceIdent : " + nomActeur, false);
	}

	public boolean writeSbufProducerSourceIdent(String nomActeur, int sourceIdent)
	{
		Reponse out = new Reponse();

		port.rccWriteSbufProducerSourceIdent(nomActeur, sourceIdent, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeSbufProducerSourceIdent : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le blocksize du Transmitter
	 * 
	 * @param nom
	 *            nom de l'acteur Transmitter
	 * 
	 * @return blocksize (-1 si erreur)
	 */
	public int getTransmitterBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetTransmitterBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getTransmitterBlockSize : " + nom, false);
	}

	public int readTransmitterBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadTransmitterBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readTransmitterBlockSize : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du blocksize
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param blockSize
	 *            le nouveau blocksize
	 * @return resultat commande (true OK)
	 */
	public boolean setTransmitterBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccSetTransmitterBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setTransmitterBlockSize : " + nomActeur, false);
	}

	public boolean writeTransmitterBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccWriteTransmitterBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeTransmitterBlockSize : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le blocksize du RikenTransmitter
	 * 
	 * @param nom
	 *            nom de l'acteur RikenTransmitter
	 * 
	 * @return blocksize (-1 si erreur)
	 */
	public int getRikenTransmitterBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetRikenTransmitterBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getRikenTransmitterBlockSize : " + nom, false);
	}

	public int readRikenTransmitterBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadRikenTransmitterBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readRikenTransmitterBlockSize : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du blocksize
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param blockSize
	 *            le nouveau blocksize
	 * @return resultat commande (true OK)
	 */
	public boolean setRikenTransmitterBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccSetRikenTransmitterBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setRikenTransmitterBlockSize : " + nomActeur, false);
	}

	public boolean writeRikenTransmitterBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccWriteRikenTransmitterBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeRikenTransmitterBlockSize : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le blocksize du EventBuilder
	 * @param nom
	 *            nom de l'acteur Transmitter
	 * 
	 * @return blocksize (-1 si erreur)
	 */
	public int getEventBuilderBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetEventBuilderBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getEventBuilderBlockSize : " + nom, false);
	}

	public int readEventBuilderBlockSize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadEventBuilderBlockSize(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readEventBuilderBlockSize : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du blocksize
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param blockSize
	 *            le nouveau blocksize
	 * @return resultat commande (true OK)
	 */
	public boolean setEventBuilderBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccSetEventBuilderBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setEventBuilderBlockSize : " + nomActeur, false);
	}

	public boolean writeEventBuilderBlockSize(String nomActeur, int blockSize)
	{
		Reponse out = new Reponse();

		port.rccWriteEventBuilderBlockSize(nomActeur, blockSize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeEventBuilderBlockSize : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le flushtime du Tranmitter
	 *  
	 * @param nom
	 *            nom de l'acteur Tranmitter
	 * 
	 * @return flushtime (-1 si erreur)
	 */
	public float getTransmitterFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccGetTransmitterFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getTransmitterFlushTime : " + nom, false);
	}

	public float readTransmitterFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccReadTransmitterFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readTransmitterFlushTime : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du flushtime
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param flushtime
	 *            le nouveau flushtime
	 * @return resultat commande (true OK)
	 */
	public boolean setTransmitterFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccSetTransmitterFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setTransmitterFlushTime : " + nomActeur, false);
	}

	public boolean writeTransmitterFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccWriteTransmitterFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeTransmitterFlushTime : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le flushtime du RikenTranmitter
	 * 
	 * @param nom
	 *            nom de l'acteur RikenTranmitter
	 * 
	 * @return flushtime (-1 si erreur)
	 */
	public float getRikenTransmitterFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccGetRikenTransmitterFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getRikenTransmitterFlushTime : " + nom, false);
	}

	public float readRikenTransmitterFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccReadRikenTransmitterFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readRikenTransmitterFlushTime : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du flushtime
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param flushtime
	 *            le nouveau flushtime
	 * @return resultat commande (true OK)
	 */
	public boolean setRikenTransmitterFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccSetRikenTransmitterFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setRikenTransmitterFlushTime : " + nomActeur, false);
	}

	public boolean writeRikenTransmitterFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccWriteRikenTransmitterFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeRikenTransmitterFlushTime : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le flushtime du EventBuilder
	 * 
	 * @param nom
	 *            nom de l'acteur EventBuilder
	 * 
	 * @return flushtime (-1 si erreur)
	 */
	public float getEventBuilderFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccGetEventBuilderFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getEventBuilderFlushTime : " + nom, false);
	}

	public float readEventBuilderFlushTime(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccReadEventBuilderFlushTime(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readEventBuilderFlushTime : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du flushtime
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param flushtime
	 *            le nouveau flushtime
	 * @return resultat commande (true OK)
	 */
	public boolean setEventBuilderFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccSetEventBuilderFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setEventBuilderFlushTime : " + nomActeur, false);
	}

	public boolean writeEventBuilderFlushTime(String nomActeur, float flushtime)
	{
		Reponse out = new Reponse();

		port.rccWriteEventBuilderFlushTime(nomActeur, flushtime, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeEventBuilderFlushTime : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si endianChecking du Transmitter
	 * 
	 * @param nom
	 *            nom de l'acteur Transmitter
	 * 
	 * @return endianChecking
	 */
	public boolean getTransmitterEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);
		
		port.rccGetTransmitterEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getTransmitterEndianChecking : " + nom, false);
	}

	public boolean readTransmitterEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadTransmitterEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readTransmitterEndianChecking : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le endianChecking
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param endianChecking
	 * @return resultat commande (true OK)
	 */
	public boolean setTransmitterEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccSetTransmitterEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setTransmitterEndianChecking : " + nomActeur, false);
	}

	public boolean writeTransmitterEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccWriteTransmitterEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeTransmitterEndianChecking : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si bypass du RikenTransmitter
	 * 
	 * @param nom
	 *            nom de l'acteur RikenTransmitter
	 * 
	 * @return erruer
	 */
	public boolean getRikenTransmitterByPass(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetRikenTransmitterBypass(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getRikenTransmitterByPass : " + nom, false);
	}

	public boolean readRikenTransmitterByPass(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadRikenTransmitterBypass(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readRikenTransmitterByPass : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le bypass
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param bypass
	 * @return resultat commande (true OK)
	 */
	public boolean setRikenTransmitterByPass(String nomActeur, boolean bypass)
	{
		Reponse out = new Reponse();

		port.rccSetRikenTransmitterBypass(nomActeur, bypass, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setRikenTransmitterByPass : " + nomActeur, false);
	}

	public boolean writeRikenTransmitterByPass(String nomActeur, boolean bypass)
	{
		Reponse out = new Reponse();

		port.rccWriteRikenTransmitterBypass(nomActeur, bypass, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeRikenTransmitterByPass : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si bypass du Transmitter
	 * 
	 * @param nom
	 *            nom de l'acteur Transmitter
	 * 
	 * @return erreur
	 */
	public boolean getTransmitterByPass(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetTransmitterBypass(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getTransmitterByPass : " + nom, false);
	}

	public boolean readTransmitterByPass(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadTransmitterBypass(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readTransmitterByPass : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le bypass
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param bypass
	 * @return resultat commande (true OK)
	 */
	public boolean setTransmitterByPass(String nomActeur, boolean bypass)
	{
		Reponse out = new Reponse();

		port.rccSetTransmitterBypass(nomActeur, bypass, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setTransmitterByPass : " + nomActeur, false);
	}

	public boolean writeTransmitterByPass(String nomActeur, boolean bypass)
	{
		Reponse out = new Reponse();

		port.rccWriteTransmitterBypass(nomActeur, bypass, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeTransmitterByPass : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si endianChecking du RikenTransmitter
	 * 
	 * @param nom
	 *            nom de l'acteur RikenTransmitter
	 * 
	 * @return endianChecking
	 */
	public boolean getRikenTransmitterEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetRikenTransmitterEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getRikenTransmitterEndianChecking : " + nom, false);
	}

	public boolean readRikenTransmitterEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadRikenTransmitterEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readRikenTransmitterEndianChecking : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le endianChecking
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param endianChecking
	 * @return resultat commande (true OK)
	 */
	public boolean setRikenTransmitterEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccSetRikenTransmitterEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setRikenTransmitterEndianChecking : " + nomActeur, false);
	}

	public boolean writeRikenTransmitterEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccWriteRikenTransmitterEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeRikenTransmitterEndianChecking : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si endianChecking du EventBuilder
	 * 
	 * @param nom
	 *            nom de l'acteur EventBuilder
	 * 
	 * @return endianChecking
	 */
	public boolean getEventBuilderEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetEventBuilderEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getEventBuilderEndianChecking : " + nom, false);
	}

	public boolean readEventBuilderEndianChecking(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadEventBuilderEndianChecking(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readEventBuilderEndianChecking : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier le endianChecking
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param endianChecking
	 * @return resultat commande (true OK)
	 */
	public boolean setEventBuilderEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccSetEventBuilderEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setEventBuilderEndianChecking : " + nomActeur, false);
	}

	public boolean writeEventBuilderEndianChecking(String nomActeur, boolean endianChecking)
	{
		Reponse out = new Reponse();

		port.rccWriteEventBuilderEndianChecking(nomActeur, endianChecking, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeEventBuilderEndianChecking : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si source modifiee du Transmitter
	 * @param nom
	 *            nom de l'acteur Transmitter
	 * 
	 * @return sourceModif
	 */
	public boolean getTransmitterSourceModif(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetTransmitterSourceModif(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getTransmitterSourceModif : " + nom, false);
	}

	public boolean readTransmitterSourceModif(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadTransmitterSourceModif(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readTransmitterSourceModif : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier la source
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param sourceModif
	 * @return resultat commande (true OK)
	 */
	public boolean setTransmitterSourceModif(String nomActeur, boolean sourceModif)
	{
		ReponseBool out = new ReponseBool();

		port.rccSetTransmitterSourceModif(nomActeur, sourceModif, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setTransmitterSourceModif : " + nomActeur, false);
	}

	public boolean writeTransmitterSourceModif(String nomActeur, boolean sourceModif)
	{
		ReponseBool out = new ReponseBool();

		port.rccWriteTransmitterSourceModif(nomActeur, sourceModif, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeTransmitterSourceModif : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre si source modifiee du EventBuilder
	 * 
	 * @param nom
	 *            nom de l'acteur EventBuilder
	 * 
	 * @return sourceModif
	 */
	public boolean getEventBuilderSourceModif(String nom) throws AccesException
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetEventBuilderSourceModif(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getEventBuilderSourceModif : " + nom, false);
	}

	public boolean readEventBuilderSourceModif(String nom) throws AccesException
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadEventBuilderSourceModif(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readEventBuilderSourceModif : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une modifier la source
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param sourceModif
	 * @return resultat commande (true OK)
	 */
	public boolean setEventBuilderSourceModif(String nomActeur, boolean sourceModif)
	{
		ReponseBool out = new ReponseBool();

		port.rccSetEventBuilderSourceModif(nomActeur, sourceModif, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setEventBuilderSourceModif : " + nomActeur, false);
	}

	public boolean writeEventBuilderSourceModif(String nomActeur, boolean sourceModif)
	{
		ReponseBool out = new ReponseBool();

		port.rccWriteEventBuilderSourceModif(nomActeur, sourceModif, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeEventBuilderSourceModif : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre l'ident du source du Transmitter
	 * 
	 * @param nom
	 *            nom de l'acteur Transmitter
	 * 
	 * @return sourceIdent (-1 si erreur)
	 */
	public int getTransmitterSourceIdent(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetTransmitterSourceIdent(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getTransmitterSourceIdent : " + nom, false);
	}

	public int readTransmitterSourceIdent(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadTransmitterSourceIdent(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readTransmitterSourceIdent : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de l'ident
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param sourceIdent
	 *            le nouveau sourceIdent
	 * @return resultat commande (true OK)
	 */
	public boolean setTransmitterSourceIdent(String nomActeur, int sourceIdent)
	{
		Reponse out = new Reponse();

		port.rccSetTransmitterSourceIdent(nomActeur, sourceIdent, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setTransmitterSourceIdent : " + nomActeur, false);
	}

	public boolean writeTransmitterSourceIdent(String nomActeur, int sourceIdent)
	{
		Reponse out = new Reponse();

		port.rccWriteTransmitterSourceIdent(nomActeur, sourceIdent, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeTransmitterSourceIdent : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre l'ident du source du EventBuilder
	 * @param nom
	 *            nom de l'acteur EventBuilder
	 * 
	 * @return sourceIdent (-1 si erreur)
	 */
	public int getEventBuilderSourceIdent(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetEventBuilderSourceIdent(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getEventBuilderSourceIdent : " + nom, false);
	}

	public int readEventBuilderSourceIdent(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadEventBuilderSourceIdent(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readEventBuilderSourceIdent : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de l'ident
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param sourceIdent
	 *            le nouveau sourceIdent
	 * @return resultat commande (true OK)
	 */
	public boolean setEventBuilderSourceIdent(String nomActeur, int sourceIdent)
	{
		Reponse out = new Reponse();

		port.rccSetEventBuilderSourceIdent(nomActeur, sourceIdent, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setEventBuilderSourceIdent : " + nomActeur, false);
	}

	public boolean writeEventBuilderSourceIdent(String nomActeur, int sourceIdent)
	{
		Reponse out = new Reponse();

		port.rccWriteEventBuilderSourceIdent(nomActeur, sourceIdent, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeEventBuilderSourceIdent : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le FragmentNum du RikenTransmitter
	 *  
	 * @param nom
	 *            nom de l'acteur RikenTransmitter
	 * 
	 * @return FragmentNum (-1 si erreur)
	 */
	public int getRikenTransmitterFragmentNum(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetRikenTransmitterFragmentNum(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getRikenTransmitterFragmentNum : " + nom, false);
	}

	public int readRikenTransmitterFragmentNum(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadRikenTransmitterFragmentNum(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readRikenTransmitterFragmentNum : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du FragmentNum
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param FragmentNum
	 *            le nouveau FragmentNum
	 * @return resultat commande (true OK)
	 */
	public boolean setRikenTransmitterFragmentNum(String nomActeur, int FragmentNum)
	{
		Reponse out = new Reponse();

		port.rccSetRikenTransmitterFragmentNum(nomActeur, FragmentNum, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setRikenTransmitterFragmentNum : " + nomActeur, false);
	}

	public boolean writeRikenTransmitterFragmentNum(String nomActeur, int FragmentNum)
	{
		Reponse out = new Reponse();

		port.rccWriteRikenTransmitterFragmentNum(nomActeur, FragmentNum, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeRikenTransmitterFragmentNum : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre fonction watcher activee
	 * 
	 * @param nom
	 *            nom de l'acteur
	 * 
	 * @return watcher
	 */
	public boolean getWatcher(String nom)
	{
		ReponseBool out = new ReponseBool();

		port.rccGetActorWatcherRunning(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getWatcher : " + nom, false);
	}

	public boolean readWatcher(String nom)
	{
		ReponseBool out = new ReponseBool();

		port.rccReadActorWatcherRunning(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readWatcher : " + nom, false);
	}

	public boolean readWatcher2(String nom)
	{
		ReponseBool out = new ReponseBool();

		port.rccReadActorWatcherRunning2(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readWatcher2 : " + nom, false);
	}

	/**
	 * cette methode permet de valider ou non la fonction watcher
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param watcher
	 * @return resultat commande (true OK)
	 */
	public boolean setWatcher(String nomActeur, boolean watcher)
	{
		Reponse out = new Reponse();

		port.rccSetActorWatcherRunning(nomActeur, watcher, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setWatcher : " + nomActeur, false);
	}

	public boolean writeWatcher(String nomActeur, boolean watcher)
	{
		Reponse out = new Reponse();

		port.rccWriteActorWatcherRunning(nomActeur, watcher, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeWatcher : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le port du watcher
	 * 
	 * @param nom
	 *            nom de l'acteur
	 * 
	 * @return port (-1 si erreur)
	 */
	public int getPortWatcher(String nom)
	{
		ReponseInt out = new ReponseInt();
		port.rccGetActorWatcherPort(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getPortWatcher : " + nom, false);
	}

	public int readPortWatcher(String nom)
	{
		ReponseInt out = new ReponseInt();
		port.rccReadActorWatcherPort(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "readPortWatcher : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du port du
	 * Watcher
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param portWatcher
	 *            le nouveau port du Watcher
	 * @return resultat commande (true OK)
	 */
	public boolean setPortWatcher(String nomActeur, int portWatcher)
	{
		Reponse out = new Reponse();

		port.rccSetActorWatcherPort(nomActeur, portWatcher, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setPortWatcher : " + nomActeur, false);
	}

	public boolean writePortWatcher(String nomActeur, int portWatcher)
	{
		Reponse out = new Reponse();

		port.rccWriteActorWatcherPort(nomActeur, portWatcher, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writePortWatcher : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le outputNumber du watcher
	 * 
	 * @param nom
	 *            nom de l'acteur
	 * 
	 * @return outputNumber (-1 si erreur)
	 */
	public int getOutputNumberWatcher(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetActorWatcherOutputNumber(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getOutputNumberWatcher : " + nom, false);
	}

	public int readOutputNumberWatcher(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadActorWatcherOutputNumber(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readOutputNumberWatcher : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du
	 * outputNumber du Watcher
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param outputNumber
	 *            le nouveau outputNumber du Watcher
	 * @return resultat commande (true OK)
	 */
	public boolean setOutputNumberWatcher(String nomActeur, int outputNumber)
	{
		Reponse out = new Reponse();

		port.rccSetActorWatcherOutputNumber(nomActeur, outputNumber, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setOutputNumberWatcher : " + nomActeur, false);
	}

	public boolean writeOutputNumberWatcher(String nomActeur, int outputNumber)
	{
		Reponse out = new Reponse();

		port.rccWriteActorWatcherOutputNumber(nomActeur, outputNumber, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeOutputNumberWatcher : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre le watcher delay d'un acteur
	 * 
	 * @param nom
	 *            nom de l'acteur
	 * 
	 * @return watcherDelay (-1 si erreur)
	 */
	public float getWatcherDelay(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccGetActorWatcherDelay(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getWatcherDelay : " + nom, false);
	}

	public float readWatcherDelay(String nom)
	{
		ReponseFloat out = new ReponseFloat();

		port.rccReadActorWatcherDelay(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readWatcherDelay : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du
	 * watcherDelay
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param watcherDelay
	 *            le nouveau watcherDelay
	 * @return resultat commande (true OK)
	 */
	public boolean setWatcherDelay(String nomActeur, float watcherDelay)
	{
		Reponse out = new Reponse();

		port.rccSetActorWatcherDelay(nomActeur, watcherDelay, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setWatcherDelay : " + nomActeur, false);
	}

	public boolean writeWatcherDelay(String nomActeur, float watcherDelay)
	{
		Reponse out = new Reponse();

		port.rccWriteActorWatcherDelay(nomActeur, watcherDelay, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeWatcherDelay : " + nomActeur, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du user d'un
	 * VMECOM
	 * 
	 * @param nom
	 *            nom du vmecom
	 * @param nomUser
	 *            le nouveau user
	 * @return un boolean du resultat commande
	 */
	public boolean setVmecomUsername(String nom, String nomUser)
	{
		Reponse out = new Reponse();

		port.rccSetVmecomUsername(nom, nomUser, out.getError(), out.getErrorMessage());

		if (debugSoap) this.checkup(nom, out);

		return traiterErreur(out, "setVmecomUsername : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du user
	 * d'un vmecom
	 * @param nom
	 *            nom du VMECOM
	 * 
	 * @return le user du vmecom
	 */
	public String getVmecomUsername(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetVmecomUsername(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getVmecomUsername : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du nom exp
	 * d'un VMECOM
	 * 
	 * @param nom
	 *            nom du vmecom
	 * @param exp
	 *            la nouvelle exp
	 * @return un boolean du resultat commande
	 */
	public boolean setVmecomExpname(String nom, String exp)
	{
		Reponse out = new Reponse();

		port.rccSetVmecomExpname(nom, exp, out.getError(), out.getErrorMessage());

		if (debugSoap) this.checkup(nom, out);

		return traiterErreur(out, "setVmecomExpname : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du nom exp
	 * d'un vmecom
	 * 
	 * @param nom
	 *            nom du VMECOM
	 * 
	 * @return le nom exp du vmecom
	 */
	public String getVmecomExpname(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetVmecomExpname(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getVmecomExpname : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du nom logger
	 * d'un VMECOM
	 * 
	 * @param nom
	 *            nom du vmecom
	 * @param loggername
	 *            nouveau loggername
	 * @return un boolean du resultat commande
	 */
	public boolean setVmecomLoggername(String nom, String loggername)
	{
		Reponse out = new Reponse();

		port.rccSetVmecomLoggername(nom, loggername, out.getError(), out.getErrorMessage());

		if (debugSoap) this.checkup(nom, out);

		return traiterErreur(out, "setVmecomLoggername : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du
	 * blocksize d'un vmecom
	 * 
	 * @param nom
	 *            nom du VMECOM
	 * 
	 * @return le blocksize
	 */
	public int getVmecomBlocksize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetVmecomBlocksize(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getVmecomBlocksize : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du blocksize
	 * d'un VMECOM
	 * 
	 * @param nom
	 *            nom du vmecom
	 * @param blocksize
	 *            nouveau blocksize
	 * @return un boolean du resultat commande
	 */
	public boolean setVmecomBlocksize(String nom, int blocksize)
	{
		Reponse out = new Reponse();

		port.rccSetVmecomBlocksize(nom, blocksize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setVmecomBlocksize : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du nom du
	 * logger d'un vmecom
	 * 
	 * @param nom
	 *            nom du VMECOM
	 * 
	 * @return le nom du logger
	 */
	public String getVmecomLoggername(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetVmecomLoggername(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getVmecomLoggername : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du
	 * buffersize d'un lien
	 * 
	 * @see LienEqt
	 * 
	 * @param nom
	 *            nom du Lien
	 * 
	 * @return le buffersise du lien
	 */
	public int getLinkBuffersize(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetLinkBufferSize(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkBuffersize : " + nom, false);
	}

	/**
	 * envoie une requete de demande de profondeur de buffer pour un lien donné
	 * @param nom nom du lien
	 * @return la profondeur du lien
	 */
	public int getLinkDepth(String nom)
	{
		ReponseInt out = new ReponseInt();
		port.rccGetLinkDepth(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkBuffersize : " + nom, false);
	}

	public boolean setLinkDepth(String nom, int bufferDepth)
	{
		Reponse out = new Reponse();

		port.rccSetLinkDepth(nom, bufferDepth, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkDepth : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du buffersize
	 * d'un lien
	 * 
	 * @param nom
	 *            nom du Lien
	 * @param buffersize
	 *            nouveau buffer
	 * @return un boolean du resultat commande
	 */
	public boolean setLinkBuffersize(String nom, int buffersize)
	{
		Reponse out = new Reponse();

		port.rccSetLinkBufferSize(nom, buffersize, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkBuffersize : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du
	 * sourceoutput d'un lien
	 * 
	 * @see LienEqt
	 * 
	 * @param nom
	 *            nom du Lien
	 * 
	 * @return le niveau de debug
	 */
	public String getLinkSourceOutput(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetLinkSourceOutput(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkSourceOutput : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du
	 * Sourceoutput d'un lien
	 * 
	 * @param nom
	 *            nom du Lien
	 * @param Sourceoutput
	 *            nouveau Sourceoutput
	 * @return un boolean du resultat commande
	 */
	public boolean setLinkSourceOutput(String nom, String Sourceoutput)
	{
		Reponse out = new Reponse();
		port.rccSetLinkSourceOutput(nom, Sourceoutput, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkBuffersize : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation de la
	 * source d'un lien
	 * 
	 * @see LienEqt
	 * 
	 * @param nom
	 *            nom du Lien
	 * 
	 * @return le niveau de debug
	 */
	public String getLinkSource(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetLinkSource(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkSource : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de la source
	 * d'un lien
	 * 
	 * @param nom
	 *            nom du lien
	 * @param source
	 *            nouvelle source
	 * @return un boolean du resultat commande
	 */
	public boolean setLinkSource(String nom, String source)
	{
		Reponse out = new Reponse();

		port.rccSetLinkSource(nom, source, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkSource : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation de la
	 * destination d'un lien
	 * 
	 * @see LienEqt
	 * 
	 * @param nom
	 *            nom du Lien
	 * 
	 * @return le niveau de debug
	 */
	public String getLinkDestination(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetLinkDestination(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkDestination : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification de la
	 * destination d'un lien
	 * 
	 * @param nom
	 *            nom du lien
	 * @param destination
	 *            nouvelle source
	 * @return un boolean du resultat commande
	 */
	public boolean setLinkDestination(String nom, String destination)
	{
		Reponse out = new Reponse();

		port.rccSetLinkDestination(nom, destination, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkDestination : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation Port de la
	 * source d'un lien
	 * 
	 * @see LienEqt
	 * 
	 * @param nom
	 *            nom du Lien
	 * 
	 * @return le niveau de debug
	 */
	public String getLinkSourcePort(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetLinkSourcePort(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkSourcePort : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification Port de la
	 * source d'un lien
	 * 
	 * @param nom
	 *            nom du lien
	 * @param sourcePort
	 *            nouvelle sourcePort
	 * @return un boolean du resultat commande
	 */
	public boolean setLinkSourcePort(String nom, String sourcePort)
	{
		Reponse out = new Reponse();

		port.rccSetLinkSourcePort(nom, sourcePort, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkSourcePort : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation Port de la
	 * destination d'un lien
	 * 
	 * @see LienEqt
	 * 
	 * @param nom
	 *            nom du Lien
	 * 
	 * @return le niveau de debug
	 */
	public String getLinkDestinationPort(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetLinkDestinationPort(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getLinkDestinationPort : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification port de la
	 * destination d'un lien
	 * 
	 * @param nom
	 *            nom du lien
	 * @param destinationPort
	 *            nouvelle destinationPort
	 * @return un boolean du resultat commande
	 */
	public boolean setLinkDestinationPort(String nom, String destinationPort)
	{
		Reponse out = new Reponse();

		port.rccSetLinkDestinationPort(nom, destinationPort, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setLinkDestinationPort : " + nom, false);
	}

	/**
	 * /** cette methode permet d'envoyer une requete de recuperation du niveau
	 * de debug d'un acteur au serveur
	 * 
	 * @param nom
	 *            nom de l'acteur dont on veut recupere le niveau de debug
	 * 
	 * @return le niveau de debug
	 */
	public String getActorLogLevel(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetActorLogLevel(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getDebugLevel : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de recuperation du nom du
	 * coordinateur d'un acteur au serveur
	 * 
	 * @param nom
	 *            nom de l'acteur dont on veut recupere le nom du coordinateur
	 * 
	 * @return le nom du coordinateur
	 */
	public String getCoordinator(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetActorCoordinator(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getCoordinator : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de recuperation du nom dela
	 * cpu d'un coordinator Narval
	 * 
	 * 
	 * @see NARVAL
	 * 
	 * @param nom
	 *            nom de l'acteur dont on veut recupere le nom du coordinateur
	 * 
	 * @return le nom de la cpu
	 */
	public String getNarvalCoordinatorCpu(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetNarvalCoordinatorCpu(nom, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getNarvalCoordinatorCpu : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de configuration file
	 * coordinator d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * 
	 * @return un boolean du resultat commande
	 */
	public boolean narvalDefineCfgFile(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalDefineCfgFile(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "narvalDefineCfgFile : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du cpu
	 * coordinator d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @param cpu
	 *            nouvelle cpu
	 * @return un boolean du resultat commande
	 */
	public boolean setNarvalCoordinatorCpu(String nom, String cpu)
	{
		Reponse out = new Reponse();

		port.rccSetNarvalCoordinatorCpu(nom, cpu, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setNarvalCoordinatorCpu : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de launch d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean launchNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalLaunch(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "launchNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de initialise d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean initialiseNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalInitialise(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "initialiseNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de RESETcom d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean resetComNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalResetCom(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "resetComNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de finish d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean finishNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalFinish(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "finishNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de Partial Reset d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean partialResetNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalPartialReset(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "partialResetNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de Full Reset d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean fullResetNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalFullReset(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "fullResetNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer configure d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean configureNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalConfigure(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "configureNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer un load d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean loadNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalLoad(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "loadNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer unconfigure d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean unconfigureNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalUnconfigure(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "unconfigureNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer un unload d'un Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean unloadNarval(String nom)
	{
		Reponse out = new Reponse();

		port.rccNarvalUnload(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "unloadNarval : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de Generate Cfg file d'un
	 * Narval
	 * 
	 * 
	 * @param nom
	 *            nom du NARVAL
	 * @return un boolean du resultat commande
	 */
	public boolean generateNarvalCfgFile(String nom)
	{
		Reponse out = new Reponse();

		port.rccGenerateNarvalCfgFile(nom, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "generateNarvalCfgFile : " + nom, false);
	}

	/**
	 * Creation d'un parametre pour un equipement(static ou dynamic) Utile
	 * uniquement pour les acteurs Narval
	 * 
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @param nom
	 *            nom du parametre
	 * @param type
	 *            type de parametre
	 * @param perm
	 *            permission du parametre
	 * @param dyn
	 *            0 si parametre statique, 1 si parametre dynamique
	 * @return boolean pour indiquer si la commande c'est bien passee
	 */
	public boolean createParameter(String equip, String nom, String type, String perm, String val, boolean in, boolean dyn, boolean script)
	{
		Reponse out = new Reponse();

		port.rccCreateParameter(equip, nom, type, perm, val, in, dyn, script, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "createParameter : " + equip, false);
	}

	/**
	 * Supprimer un parametre d'un equipement
	 *  
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @param nom
	 *            nom du parametre
	 * @return boolean pour indiquer si la commande c'est bien passee
	 */
	public boolean deleteParameter(String equip, String nom)
	{
		Reponse out = new Reponse();

		port.rccDeleteParameter(equip, nom, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "deleteParameter : " + equip, false);
	}

	/**
	 * Mettre un parametre en Static ou Dynamic
	 * 
	 * 
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @param nom
	 *            nom du parametre
	 * @param dyn
	 *            parametre static ou dynamique
	 * @return boolean pour indiquer si la commande c'est bien passee
	 */
	public boolean setParameterDynamic(String equip, String nom, boolean dyn)
	{
		Reponse out = new Reponse();

		port.rccSetParameterDynamic(equip, nom, dyn, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setParameterDynamic : " + equip, false);
	}

	/**
	 * Modifier le type du parametre
	 *  
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @param nom
	 *            nom du parametre
	 * @param type
	 *            type du parametre
	 * @return boolean pour indiquer si la commande c'est bien passee
	 */
	public boolean setParameterType(String equip, String nom, String type)
	{
		Reponse out = new Reponse();

		port.rccSetParameterType(equip, nom, type, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setParameterType : " + equip, false);
	}

	/**
	 * Modifier les permissions du paramètre
	 *  
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @param nom
	 *            nom du parametre
	 * @param perm
	 *            permissions du parametre
	 * @return boolean pour indiquer si la commande c'est bien passee
	 */
	public boolean setParameterPermission(String equip, String nom, String perm)
	{
		Reponse out = new Reponse();

		port.rccSetParameterPermission(equip, nom, perm, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setParameterPermission : " + equip, false);
	}

	/**
	 * Modifier la valeur du parametre
	 *  
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @param nom
	 *            nom du parametre
	 * @param val
	 *            valeur du parametre
	 * @return boolean pour indiquer si la commande c'est bien passee
	 */
	public boolean writeParameter(String equip, String nom, String val)
	{
		Reponse out = new Reponse();

		port.rccWriteParameter(equip, nom, val, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "writeParameter : " + equip, false);
	}

	public boolean setParameterInNarvalCfgFile(String equip, String nom, boolean val)
	{
		Reponse out = new Reponse();

		port.rccSetParameterInNarvalCfgFile(equip, nom, val, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setParamterInNarvalCfgFile : " + equip, false);
	}

	public boolean setParameterInNarvalScript(String equip, String nom, boolean val)
	{
		Reponse out = new Reponse();

		port.rccSetParameterInNarvalScript(equip, nom, val, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setParamterInNarvalScript : " + equip, false);
	}

	/**
	 * Recuperer le nombre de parametre de l'equipement
	 * 
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @return le nombre de parametre (-1 si erreur)
	 */
	public int getParameterCount(String equip)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetParameterCount(equip, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterCount : " + equip, false);
	}

	/**
	 * Recuperer le type de l'equipement
	 * 
	 * @param equip
	 *            nom de l'equipement
	 * @return le type de l'equipement
	 */
	public int getEquipementType(String equip)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetEquipmentType(equip, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getEquipementType : " + equip, false);
	}

	/**
	 * Recuperer la propriete dynamique/statique d'un parametre
	 * @param equip
	 *            nom equipement
	 * @param parametre
	 *            nom parametre
	 * @return propriete du parametre (0 static 1 dynamic -1 si erreur)
	 */
	public boolean getParameterDynamic(String equip, String parametre)
	{
		ReponseBool out = new ReponseBool();

		port.rccGetParameterDynamic(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterDynamic : " + equip, false);
	}

	/**
	 * Recuperer le type d'un parametre
	 * 
	 * 
	 * @param equip
	 *            nom equipement
	 * @param parametre
	 *            nom parametre
	 * 
	 * @return Type de parametre
	 */
	public String getParameterType(String equip, String parametre)
	{
		ReponseString out = new ReponseString();

		port.rccGetParameterType(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterType : " + equip, false);
	}

	/**
	 * Recuperer la permission d'un parametre d'un equipement
	 * 
	 * 
	 * @param equip
	 *            nom equipement
	 * @param parametre
	 *            nom parametre
	 * 
	 * @return Permission de parametre
	 */
	public String getParameterPermission(String equip, String parametre)
	{
		ReponseString out = new ReponseString();

		port.rccGetParameterPermission(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterPermission : " + equip, false);
	}

	/**
	 * Recuperer la valeur d'un parametre d'un equipement
	 * 
	 * 
	 * @param equip
	 *            nom equipement
	 * @param parametre
	 *            nom parametre
	 * 
	 * @return Valeur du parametre (-1 si erreur)
	 */
	public String getParameterValue(String equip, String parametre)
	{
		ReponseString out = new ReponseString();

		port.rccGetParameterValue(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterValue : " + equip, false);
	}

	public boolean getParameterInNarvalCfgFile(String equip, String parametre)
	{
		ReponseBool out = new ReponseBool();

		port.rccGetParameterInNarvalCfgFile(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterInNarvalCfgFile : " + equip, false);
	}

	public boolean getParameterInNarvalScript(String equip, String parametre)
	{
		ReponseBool out = new ReponseBool();

		port.rccGetParameterInNarvalScript(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getParameterInNarvalScript : " + equip, false);
	}

	/**
	 * Recuperer la valeur d'un parametre d'un equipement
	 * 
	 * 
	 * @param equip
	 *            nom equipement
	 * @param parametre
	 *            nom parametre
	 * 
	 * @return Valeur du parametre (-1 si erreur)
	 */
	public String readParameter(String equip, String parametre)
	{
		ReponseString out = new ReponseString();

		port.rccReadParameter(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "readParameter : " + equip, false);
	}

	public String readParameter2(String equip, String parametre)
	{
		ReponseString out = new ReponseString();

		port.rccReadParameter2(equip, parametre, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "readParameter2 : " + equip, false);
	}

	/**
	 * Recuperer un parametre d'un equipement par son index
	 * 
	 * 
	 * @param equip
	 *            nom equipement
	 * @param index
	 *            index du parametre
	 * 
	 * @return OutputParamSOAP
	 */
	public ReponseParam getParameterByIndex(String equip, int index)
	{
		ReponseParam out = new ReponseParam();

		port.rccGetParameterByIndex(equip, index, out.getError(), out.getErrorMessage(), out.getName(), out.getType(),
				out.getPermission(), out.getValue(), out.getNarvalcfgfile(), out.getDynamic());
		traiterErreur(out, "getParameterByIndex " + equip, false);
		return out;
	}

	/**
	 * Sauvegarde de la configuration
	 * 
	 * @param path
	 *            chemin et nom sauvegarde
	 * @return un boolean du resultat commande
	 */
	public boolean saveConfig(String path)
	{
		Reponse out = new Reponse();
		port.rccSave(path, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "saveConfig", true);
	}

	/**
	 * Sauvegarde de la configuration
	 * 
	 * @return un boolean du resultat commande
	 */
	public boolean saveConfig()
	{
		Reponse out = new Reponse();
		port.rccSave("", out.getError(), out.getErrorMessage());
		return traiterErreur(out, "saveConfig", true);
	}

	/**
	 * Charger une configuration
	 * 
	 * @param path
	 *            chemin et nom sauvegarde
	 * @return un boolean du resultat commande
	 * 
	 */
	public boolean loadConfig(String path)
	{
		Reponse out = new Reponse();

		port.rccLoad(path, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "loadConfig", true);
	}

	/**
	 * cette methode permet l'impression dans la console des differents messages
	 * reponse du serveur suivant la structure Reponse donnee
	 * 
	 * @param name
	 *            nom de l'element reference
	 * @param out
	 *            structure a interpreter
	 */
	public void checkup(String name, Reponse out)
	{
		System.out.println("*********************************");
		System.out.println("controle de " + name);
		System.out.println("");
		System.out.println("error        : " + out.getError().value);
		System.out.println("errorMessage : " + out.getErrorMessage().value);
		// System.out.println("value        : " + out.value.value);
		// System.out.println("text         : " + out.text.value);
		System.out.println("*********************************");
	}

	/**
	 * cette methode permet de recuperer les informations indispensable au
	 * renseignement sur l'experience en cours sur le serveur
	 * 
	 * @return une structure de type StateExpSOAP possedant les caracteristiques
	 *         globales de l'experience en cours du serveur
	 */
	public StateExpSOAP getState() {
		String out = null;

		listEquipements = new ArrayList<Equipement>();
		listLiens = new ArrayList<LienEqt>();

		String nameExp = "", pathExp = "";

		out = this.getExpName();

		if (!out.equals(""))
		{
			nameExp = out;
			out = this.getExpPath();
			if (!out.equals(""))
			{
				pathExp = out;
				int outInt = this.getNbEqt();
				if (outInt != -1)
				{
					int nbEqt = outInt;

					/**
					 * recuperation des equipements presents dans l'experience
					 * en cours du serveur
					 **/
					for (int i = 0; (i < nbEqt); i++)
					{
						ReponseEquip outEquip = this.getEqtAt(i);
						if (outEquip.getError().value == 0)
						{
							Equipement eqt = creerEqt(outEquip);
							// System.out.println(i +
							// "  -StateExpSOAP getState()----------------" +
							// eqt.getName() + " "
							// + eqt.getType());
							
							if (eqt != null)
								listEquipements.add(eqt);
						}
					}

					outInt = this.getNbLien();

					if (outInt != -1)
					{
						int nbLien = outInt;
						for (int i = 0; (i < nbLien); i++)
						{
							ReponseLink outLink = this.getLienAt(i);

							if (outLink.getError().value == 0) {
								LienEqt lien = this.recupLien(outLink);
								if (lien != null)
									listLiens.add(lien);
							}
						}
					}
				}
			}
		}
		else
		{
			nameExp = "ERROR";
		}
		return new StateExpSOAP(nameExp, pathExp, listEquipements, listLiens);
	}

	/**
	 * cette methode permet de recupere un lien suivant la structure
	 * OutputLinkSOAP donne
	 * 
	 * @see LienEqt
	 * 
	 * @param reponseLink
	 *            la structure OutputLinkSOAP dont veux extraire les
	 *            informations
	 * @return le lien issu des informations de la structure
	 */
	private LienEqt recupLien(ReponseLink reponseLink) {

		ReponseLink out = reponseLink;

		String nomLien = out.getName().value;
		String srcName = out.getSourceName().value;
		String dstName = out.getDestName().value;
		String sourceOutput = out.getSourceOutput().value;
		String srcPort = out.getSourcePort().value;
		String dstPort = out.getDestPort().value;

		int bufSize = out.getBufferSize().value;

		Equipement eqtSrc = recupMyEquipement(srcName);
		Equipement eqtDst = recupMyEquipement(dstName);

		if (eqtSrc == null || eqtDst == null)
		{
			System.err.println("Probleme de recuperation des instruments " + srcName + " ou " + dstName + " !!!");
			return null;
		}
		else
		{
			int depth = getLinkDepth(nomLien);
			return new LienEqt(pere, nomLien, eqtSrc, eqtDst, sourceOutput, srcPort, dstPort, bufSize, depth);
		}
	}

	public Equipement recupMyEquipement(String nom) {
		for(Equipement eqt : listEquipements) {
			if(eqt.getName().equals(nom))
				return eqt;
		}
		return null;
	}

	/**
	 * cette methode est utilisee pour creer un equipement a partir des
	 * informations donnes en argument. Concernant le type, il existe une
	 * interpretation pour les entiers suivants:
	 * 
	 * 1: definit un equipement 2: definit un equipement NARVAL 4: definit un
	 * instrument 8: definit un instrument VMECOM 16: definit un instrument
	 * MIDAS 32: definit un instrument Acteur NARVAL
	 * 
	 * @param outEquip
	 *            l'equipement
	 * 
	 * @return l'equipement cree
	 */
	private Equipement creerEqt(ReponseEquip outEquip)
	{
		String nom = outEquip.getName().value;
		String hostName = outEquip.getHost().value;
		String level = outEquip.getLog().value;
		String cpu = outEquip.getCpu().value;
		String narval = outEquip.getNarval().value;
		String exe = outEquip.getExe().value;
		String username = outEquip.getUsername().value;
		String expname = outEquip.getExpname().value;
		String loggername = outEquip.getLoggername().value;
		int blocksize = outEquip.getBlocksize().value;
		int port = outEquip.getPort().value;
		String id = outEquip.getId().value;

		EquipementType type = EquipementType.ACTOR;

		switch (outEquip.getType().value) {
			/** cas NARVAL **/
			case 2: type = EquipementType.SUBSYSTEM_NARVAL;		break;
			/** cas VMECOM **/
			case 8: type = EquipementType.VMECOM;				break;
			/** cas MIDAS **/
			case 16: type = EquipementType.MIDAS;				break;
			/** cas Acteur NARVAL **/
			case 32: type = EquipementType.ACTOR;				break;
			/** cas Acteur NARVAL SBUFPRODUCER **/
			case 64: type = EquipementType.SBUFPRODUCER;		break;
			/** cas Acteur NARVAL EVENTBUILDER **/
			case 128: type = EquipementType.EVENTBUILDER;		break;
			/** cas Acteur NARVAL STORAGE **/
			case 256: type = EquipementType.STORAGE;			break;
			/** cas Acteur NARVAL RIKENTRANSMITTER **/
			case 1024: type = EquipementType.RIKENTRANSMITTER;	break;
			/** cas ECC **/
			case 8192: type = EquipementType.ECC;				break;
			default:
				System.err.println("Type (" + type + ")d'equipement inconnu (" + nom + ")!!!");
		}

		Equipement eqt = null;

		switch (type) {
			case SUBSYSTEM_NARVAL:
				eqt = new NARVAL(pere, nom, hostName, cpu);
				break;

			case VMECOM:
				eqt = new VMECOM(pere, nom, hostName, username, expname, loggername, blocksize);
				break;

			case MIDAS:
				eqt = new MIDAS(pere, nom, hostName);
				break;

			case ACTOR:
				eqt = new Actor(pere, nom, exe, hostName, level, narval);
				break;

			case SBUFPRODUCER:
				eqt = new SbufProducer(pere, nom, hostName, level, narval);
				break;

			case EVENTBUILDER:
				eqt = new EventBuilder(pere, nom, hostName, level, narval);
				break;

			case STORAGE:
				eqt = new Storage(pere, nom, hostName, level, narval);
				break;

			case RIKENTRANSMITTER:
				eqt = new RikenTransmitter(pere, nom, hostName, level, narval);
				break;

			case ECC:
				eqt = new ECC(pere, nom, hostName, port);
				break;

			default:
				System.err.println("Type (" + type + ")d'equipement inconnu (" + nom + ")!!!");

		}

		if (eqt == null)
		{
			System.err.println("la creation de l'equipement '"+nom+"' a echoue au niveau de la methode ClientSOAP.creereqt(..)!!!");
		}

		return eqt;
	}

	/**
	 * Storage On
	 * 
	 * @param eqt
	 * @return un boolean du resultat commande
	 */
	public boolean storageOn(String name)
	{
		Reponse out = new Reponse();

		port.rccStorageOn(name, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "storageOn", false);
	}

	/**
	 * Storage off
	 * 
	 * @param eqt
	 * @return un boolean du resultat commande
	 */
	public boolean storageOff(String name)
	{
		Reponse out = new Reponse();

		port.rccStorageOff(name, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "storageOff", false);
	}

	/**
	 * Storage on ou off
	 * 
	 * @param name
	 *            eqt
	 * 
	 * @return un boolean du resultat commande
	 */
	public boolean getStorageStatus(String name)
	{
		ReponseString out = new ReponseString();

		port.rccGetStorageFileName(name, out.getError(), out.getErrorMessage(), out.getValue());

		if (out.getValue().value.equals("X"))
			return false;
		else
			return true;
	}

	public boolean readStorageStatus(String name)
	{
		ReponseString out = new ReponseString();

		port.rccReadStorageFileName(name, out.getError(), out.getErrorMessage(), out.getValue());

		if (out.getValue().value.equals("X"))
			return false;
		else
			return true;
	}

	/**
	 * Storage path
	 * 
	 * @param nom
	 * 
	 * @return path
	 */
	public String getStoragePath(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetStoragePath(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStoragePath : " + nom, false);
	}

	public String readStoragePath(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccReadStoragePath(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readStoragePath : " + nom, false);
	}

	/**
	 * get Storage run number
	 * 
	 * @param nom
	 * 
	 * @return run number
	 */
	public int getStorageRunNumber(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetStorageRunNumber(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStorageRunNumber : " + nom, false);
	}

	/**
	 * Storage run number
	 * 
	 * @param nom
	 *            eqt
	 * 
	 */
	public boolean setStorageRunNumber(String nom, int val)
	{
		Reponse out = new Reponse();

		port.rccSetStorageRunNumber(nom, val, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStorageRunNumber : " + nom, false);
	}

	/**
	 * Storage path
	 * 
	 * @param nom
	 * 
	 */
	public boolean setStoragePath(String nom, String path)
	{
		Reponse out = new Reponse();

		port.rccSetStoragePath(nom, path, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStoragePath : " + nom, false);
	}

	public boolean writeStoragePath(String nom, String path)
	{
		Reponse out = new Reponse();

		port.rccWriteStoragePath(nom, path, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeStoragePath : " + nom, false);
	}

	/**
	 * Storage Scaler Server
	 * 
	 * @param nom
	 * 
	 */
	public boolean setStorageScalerServer(String nom, String server)
	{
		Reponse out = new Reponse();

		port.rccSetStorageScalerServer(nom, server, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStorageScalerServer : " + nom, false);
	}

	public boolean writeStorageScalerServer(String nom, String server)
	{
		Reponse out = new Reponse();

		port.rccWriteStorageScalerServer(nom, server, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeStorageScalerServer : " + nom, false);
	}

	/**
	 * Storage scaler server
	 * 
	 * @param nom
	 * 
	 * @return path
	 */
	public String getStorageScalerServer(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccGetStorageScalerServer(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStorageScalerServer : " + nom, false);
	}

	public String readStorageScalerServer(String nom)
	{
		ReponseString out = new ReponseString();

		port.rccReadStorageScalerServer(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readStorageScalerServer : " + nom, false);
	}

	/**
	 * get Synchro with narval
	 * 
	 * @param nom
	 * @return bool synchro
	 */
	public boolean getStorageSynchroWithNarval(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccGetStorageSynchroWithNarval(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStorageSynchroWithNarval : " + nom, false);
	}

	public boolean readStorageSynchroWithNarval(String nom)
	{
		ReponseBool out = new ReponseBool();
		Common.setErrorSoap(false);

		port.rccReadStorageSynchroWithNarval(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readStorageSynchroWithNarval : " + nom, false);
	}

	/**
	 * set Synchro with narval
	 * 
	 * @param nom
	 * 
	 */
	public boolean setStorageSynchroWithNarval(String nom, boolean synchro)
	{
		Reponse out = new Reponse();
		Common.setErrorSoap(false);

		port.rccSetStorageSynchroWithNarval(nom, synchro, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStorageSynchroWithNarval : " + nom, false);
	}

	public boolean writeStorageSynchroWithNarval(String nom, boolean synchro)
	{
		Reponse out = new Reponse();
		Common.setErrorSoap(false);

		port.rccWriteStorageSynchroWithNarval(nom, synchro, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeStorageSynchroWithNarval : " + nom, false);
	}

	/**
	 * cette methode permet de connaitre "split_size_mega_byte" parameter
	 * 
	 * @param nom
	 *            nom de l'acteur Storage
	 * 
	 * @return split_size_mega_byte (-1 si erreur)
	 */
	public int getStorageSplitSizeMegaByte(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetStorageSplitSizeMegaByte(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStorageSplitSizeMegaByte : " + nom, false);
	}

	public int readStorageSplitSizeMegaByte(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadStorageSplitSizeMegaByte(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readStorageSplitSizeMegaByte : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du parametre
	 * split_size_mega_byte
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param split_size_mega_byte
	 *            le nouveau split_size_mega_byte
	 * @return resultat commande (true OK)
	 */
	public boolean setStorageSplitSizeMegaByte(String nomActeur, int split_size_mega_byte)
	{
		Reponse out = new Reponse();

		port.rccSetStorageSplitSizeMegaByte(nomActeur, split_size_mega_byte, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStorageSplitSizeMegaByte : " + nomActeur, false);
	}

	public boolean writeStorageSplitSizeMegaByte(String nomActeur, int split_size_mega_byte)
	{
		Reponse out = new Reponse();

		port.rccWriteStorageSplitSizeMegaByte(nomActeur, split_size_mega_byte, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeStorageSplitSizeMegaByte : " + nomActeur, false);
	}

	/**
	 * cette methode permet de connaitre "scaler delay" parameter
	 * 
	 * @param nom
	 *            nom de l'acteur Storage
	 * 
	 * @return delay (-1 si erreur)
	 */
	public int getStorageScalerDelay(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetStorageScalerDelay(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStorageScalerDelay : " + nom, false);
	}

	public int readStorageScalerDelay(String nom)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadStorageScalerDelay(nom, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readStorageScalerDelay : " + nom, false);
	}

	/**
	 * cette methode permet d'envoyer une requete de modification du parametre
	 * scaler delay
	 * 
	 * @param nomActeur
	 *            nom de l'acteur concerne
	 * @param delay
	 *            le nouveau delay
	 * @return resultat commande (true OK)
	 */
	public boolean setStorageScalerDelay(String nomActeur, int delay)
	{
		Reponse out = new Reponse();

		port.rccSetStorageScalerDelay(nomActeur, delay, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStorageScalerDelay : " + nomActeur, false);
	}

	public boolean writeStorageScalerDelay(String nomActeur, int delay)
	{
		Reponse out = new Reponse();

		port.rccWriteStorageScalerDelay(nomActeur, delay, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeStorageScalerDelay : " + nomActeur, false);
	}

	public int getStorageBlockSize(String name)
	{
		ReponseInt out = new ReponseInt();

		port.rccGetStorageBlockSize(name, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "getStorageBlockSize : " + name, false);
	}

	public int readStorageBlockSize(String name)
	{
		ReponseInt out = new ReponseInt();

		port.rccReadStorageBlockSize(name, out.getError(), out.getErrorMessage(), out.getValue());

		return traiterErreur(out, "readStorageBlockSize : " + name, false);
	}

	public boolean setStorageBlockSize(String name, int size)
	{
		Reponse out = new Reponse();

		port.rccSetStorageBlockSize(name, size, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "setStorageBlockSize : " + name, false);
	}

	public boolean writeStorageBlockSize(String name, int size)
	{
		Reponse out = new Reponse();

		port.rccWriteStorageBlockSize(name, size, out.getError(), out.getErrorMessage());

		return traiterErreur(out, "writeStorageBlockSize : " + name, false);
	}

	public int getECCorePort(String name)
	{
		ReponseInt out = new ReponseInt();
		port.rccGetECCorePort(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getECCorePort : " + name, false);
	}

	public boolean setECCorePort(String name, int num)
	{
		Reponse out = new Reponse();
		port.rccSetECCorePort(name, num, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setECCorePort : " + name, false);
	}

	public String getECCoreConfigID(String name)
	{
		ReponseString out = new ReponseString();
		port.rccGetECCoreConfigID(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getECCoreConfigID : " + name, false);
	}

	public boolean setECCoreConfigID(String name, String id)
	{
		Reponse out = new Reponse();
		port.rccSetECCoreConfigID(name, id, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setECCoreConfigID : " + name, false);
	}

	public String getECCoreListeConfigID(String name)
	{
		ReponseString out = new ReponseString();
		port.rccECCoreGetConfigIDs(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getECCoreListeConfigID : " + name, false);
	}

	public boolean eccoreDescribe(String name, String table, Holder<String> str)
	{
		Reponse out = new Reponse();
		port.rccECCoreDescribe(name, table, out.getError(), out.getErrorMessage(), str);
		return traiterErreur(out, "eccoreDescribe : " + name, false);
	}

	public boolean eccorePrepare(String name, String table, Holder<String> str)
	{
		Reponse out = new Reponse();
		port.rccECCorePrepare(name, table, out.getError(), out.getErrorMessage(), str);
		return traiterErreur(out, "eccorePrepare : " + name, false);
	}

	public boolean eccoreConfigure(String name, Holder<String> str)
	{
		Reponse out = new Reponse();
		port.rccECCoreConfigure(name, out.getError(), out.getErrorMessage(), str);
		return traiterErreur(out, "eccoreConfigure : " + name, false);
	}

	public boolean eccoreBreakup(String name)
	{
		Reponse out = new Reponse();
		port.rccECCoreBreakup(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "eccoreBreakup : " + name, false);
	}

	public boolean eccoreUndo(String name)
	{
		Reponse out = new Reponse();
		port.rccECCoreUndo(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "eccoreUndo : " + name, false);
	}

	public int eccoreState(String name)
	{
		ReponseInt out = new ReponseInt();
		port.rccGetECCoreEccState(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "eccoreState : " + name, false);
	}

	/**
	 * init Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public boolean equipmentInit(String name)
	{
		Reponse out = new Reponse();

		port.rccEquipmentInit(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "equipmentInit : " + name, false);
	}

	/**
	 * exit Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public boolean equipmentExit(String name)
	{
		Reponse out = new Reponse();

		port.rccEquipmentExit(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "equipmentExit : " + name, false);
	}

	/**
	 * start Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public boolean equipmentStart(String name)
	{
		Reponse out = new Reponse();

		port.rccEquipmentStart(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "equipmentStart : " + name, false);
	}

	/**
	 * stop Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public boolean equipmentStop(String name)
	{
		Reponse out = new Reponse();

		port.rccEquipmentStop(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "equipmentStop : " + name, false);
	}

	/**
	 * pause Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public boolean equipmentPause(String name)
	{
		Reponse out = new Reponse();

		port.rccEquipmentPause(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "equipmentPause : " + name, false);
	}

	/**
	 * resume Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public boolean equipmentResume(String name)
	{
		Reponse out = new Reponse();

		port.rccEquipmentResume(name, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "equipmentResume : " + name, false);
	}

	/**
	 * etat Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public String equipmentState(String name, boolean affiche)
	{
		ReponseString out = new ReponseString();

		port.rccReadEquipmentState(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "equipmentState : " + name, affiche);
	}

	/**
	 * etat Equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public String equipmentState(String name)
	{
		ReponseString out = new ReponseString();

		port.rccReadEquipmentState(name, out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "equipmentState : " + name, false);
	}

	/**
	 * start
	 * 
	 */
	public boolean start()
	{
		Reponse out = new Reponse();

		port.rccStart(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "start", true);
	}

	/**
	 * stop
	 * 
	 */
	public boolean stop()
	{
		Reponse out = new Reponse();

		port.rccStop(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "stop", true);
	}

	/**
	 * init global
	 * 
	 */
	public boolean init()
	{
		Reponse out = new Reponse();

		port.rccInit(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "init", true);
	}

	/**
	 * pause global
	 * 
	 */
	public boolean pause()
	{
		Reponse out = new Reponse();

		port.rccPause(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "pause", true);
	}

	/**
	 * resume global
	 * 
	 */
	public boolean resume()
	{
		Reponse out = new Reponse();

		port.rccResume(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "resume", true);
	}

	/**
	 * exit global
	 * 
	 */
	public boolean exit()
	{
		Reponse out = new Reponse();

		port.rccExit(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "exit", true);
	}

	/**
	 * State Machine globale
	 * 
	 */
	public retourStateMachine getStateMachine()
	{
		ReponseStateMachine out = new ReponseStateMachine();

		port.rccStateMachine(out.getError(), out.getErrorMessage(), out.getSMEtat(), out.getSMTransit(), out.getSMError());

		return (new retourStateMachine(out));
	}

	/**
	 * State Machine d'un equipement
	 * 
	 * @param name
	 *            eqt
	 */
	public ReponseStateMachine getEquipmentStateMachine(String name)
	{
		ReponseStateMachine out = new ReponseStateMachine();

		port.rccEquipmentStateMachine(name, out.getError(), out.getErrorMessage(), out.getSMEtat(), out.getSMTransit(),
				out.getSMError());

		return out;
	}

	/**
	 * Set start liste
	 * 
	 * @param liste
	 *            liste
	 * 
	 */
	public boolean setStartList(String liste)
	{
		Reponse out = new Reponse();

		port.rccSetStartList(liste, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setStartList", false);
	}

	/**
	 * Set stop liste
	 * 
	 * @param liste
	 *            liste
	 * 
	 */
	public boolean setStopList(String liste)
	{
		Reponse out = new Reponse();

		port.rccSetStopList(liste, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setStopList", false);
	}

	public String getStartList()
	{
		ReponseString out = new ReponseString();

		port.rccGetStartList(out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getStartList", false);
	}

	public String getStopList()
	{
		ReponseString out = new ReponseString();

		port.rccGetStopList(out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getStopList", false);
	}

	/**
	 * monitoring on
	 * 
	 */
	public boolean monitoringOn()
	{
		Reponse out = new Reponse();

		port.rccMonitoringOn(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "monitoring On", false);
	}

	/**
	 * monitoring off
	 * 
	 */
	public boolean monitoringOff()
	{
		Reponse out = new Reponse();

		port.rccMonitoringOff(out.getError(), out.getErrorMessage());
		return traiterErreur(out, "monitoring Off", false);
	}

	/**
	 * get monitoring state
	 * 
	 */
	public boolean getMonitoringState()
	{
		ReponseBool out = new ReponseBool();

		port.rccGetMonitoringState(out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getMonitoringState", false);
	}

	public int getRunNumber()
	{
		ReponseInt out = new ReponseInt();

		port.rccGetRunNumber(out.getError(), out.getErrorMessage(), out.getValue());
		return traiterErreur(out, "getRunNumber", false);
	}

	public boolean setRunNumber(int val)
	{
		Reponse out = new Reponse();
		port.rccSetRunNumber(val, out.getError(), out.getErrorMessage());
		return traiterErreur(out, "setRunNumber", false);
	}

	private String traiterErreur(ReponseString out, String origine, boolean affiche)
	{
		if (out.getError().value != 0)
		{
			if (affiche)
				JOptionPane.showMessageDialog(pere, "ERROR : " + origine + " " + out.getErrorMessage().value, "ERROR",
						JOptionPane.ERROR_MESSAGE);
		}

		return out.getStringValue();
	}

	private boolean traiterErreur(ReponseBool out, String origine, boolean affiche)
	{
		Common.setErrorSoap(false);

		if (out.getError().value != 0)
		{
			if (affiche)
				JOptionPane.showMessageDialog(pere, "ERROR : " + origine + " \n" + out.getErrorMessage().value, "ERROR",
					JOptionPane.ERROR_MESSAGE);

			Common.setErrorSoap(true);
		}

		return out.getBooleanValue();
	}

	private boolean traiterErreur(Reponse out, String origine, boolean affiche)
	{
		if (out.getError().value != 0)
		{
			if (affiche)
				JOptionPane.showMessageDialog(pere, "ERROR : " + origine + " \n" + out.getErrorMessage().value, "ERROR",
					JOptionPane.ERROR_MESSAGE);

			return false;
		}

		return true;
	}

	private int traiterErreur(ReponseInt out, String origine, boolean affiche)
	{
		if (out.getError().value != 0)
		{
			if (affiche)
				JOptionPane.showMessageDialog(pere, "ERROR : " + origine + " \n" + out.getErrorMessage().value, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

		return out.getIntValue();
	}

	private float traiterErreur(ReponseFloat out, String origine, boolean affiche)
	{
		if (out.getError().value != 0)
		{
			if (affiche)
				JOptionPane.showMessageDialog(pere, "ERROR : " + origine + " \n" + out.getErrorMessage().value, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

		return out.getFloatValue();
	}
}