package core.reseau;

import core.Common;

public class Communication {

	private static Thread action = new Thread();
	public static boolean needGridUpdate = false;
	public static boolean inProgress = false;

	public static boolean deleteEqt(String name) {
		boolean status = false;
		try {status = Common.myClientSOAP.deleteEquipement(name);}
		catch (com.sun.xml.internal.ws.client.ClientTransportException e) {return false;}
		return status;
	}

	public static void init() {
		new Thread() { // on créé un Thread qui attend que 'action' se termine
			public void run() {			//  pour lui faire executer le traitement
				if(action.isAlive()) // si le thread est en execution
					try {
						action.join(); // alors on attend qu'il se termine
					} catch (InterruptedException e) {}
				action = new Thread() { // on affecte un nouveau Thread à 'action'
					public void run() {
						inProgress = true;
						Common.myClientSOAP.init(); // on effectue l'appel réseau qui demande du temps
						inProgress = false;
					}
				};
				action.start();
			}
		}.start();
	}

	public static void start() {
		new Thread() { // on créé un Thread qui attend que action se libère pour lui faire executer le traitement
			public void run() {
				if(action.isAlive())
					try {
						action.join();
					} catch (InterruptedException e) {}
				action = new Thread() {
					public void run() {
						inProgress = true;
						Common.myClientSOAP.start();
						inProgress = false;
					}
				};
				action.start();
			}
		}.start();		
	}

	public static void stop() {
		new Thread() { // on créé un Thread qui attend que action se libère pour lui faire executer le traitement
			public void run() {
				if(action.isAlive())
					try {
						action.join();
					} catch (InterruptedException e) {}
				action = new Thread() {
					public void run() {
						inProgress = true;
						Common.myClientSOAP.stop();
						inProgress = false;
					}
				};
				action.start();
			}
		}.start();
	}

	public static void exit() {
		new Thread() { // on créé un Thread qui attend que action se libère pour lui faire executer le traitement
			public void run() {
				if(action.isAlive())
					try {
						action.join();
					} catch (InterruptedException e) {}
				action = new Thread() {
					public void run() {
						inProgress = true;
						Common.myClientSOAP.exit();
						inProgress = false;
					}
				};
				action.start();
			}
		}.start();
	}
}
