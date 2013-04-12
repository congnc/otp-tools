package otp.network;

import otp.OTPAuth;
import otp.OTPDatabase;
import otp.OTPUser;

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

/**
 * Serveur OTP.
 * Gere les connexions et les authentifications.
**/

public class OTPServer extends UnicastRemoteObject implements RMIOTPServer {
	
	/** Service d'authentification. **/
	private OTPAuth auth;
	/** La base de donnees utilisateurs. **/
	private OTPDatabase db;
	/** Les utilisateurs en attente. **/
	private Hashtable<String,OTPUser> users;
	/** Nombre de connexions simultanees. **/
	private int maxConnexions;
	/** Nombre de secondes d'attente pour la réponse à un challenge. **/
	private int timeout;

// Constructeur

	/**
	 * Cree un nouveau serveur OTP.
	 * @param nomFichierDB Le nom du fichier de passwords.
	 * @param max Le nombre de connexions simultanees autorisees.
	 * @param timeout Le nombre de secondes d'attente pour la réponse à un challenge.
	**/
	public OTPServer( String nomFichierDB, int max, int timeout ) throws RemoteException {
		
		super();
		
		maxConnexions = max;
		this.timeout = timeout;
		users = new Hashtable<String,OTPUser>();
		db = new OTPDatabase( nomFichierDB );
		auth = new OTPAuth();
		
		db.loadData();
	}

// Methodes

	/**
	 * Retourne un challenge si l'utilisateur est
	 * dans la base de donnees et si il peut se connecter.
	 * L'utilisateur est mis dans une file d'attente.
	 * De plus, on gère les utilisateurs qui sont depuis trop longtemps
	 * dans la file d'attente.
	 * Bien sur, cette methode est synchronisé : aucun accès concurrent.
	 * @param login Un nom d'utilisateurs qui a deja demande une connexion.
	 * @return String Un challenge OTP.
	**/
	public String askForChallenge( String login ) throws RemoteException {

		String challenge = null;

		// On synchronise le bloc d'instruction : aucun accès concurrent.
		// Ceci est fait afin de garantir les bonnes valeurs pour la taille
		// de la file d'attente.
		synchronized( this ) {

			if( !users.contains( login ) && db.contains( login ) && users.size()<maxConnexions ) {
	
				OTPUser user = db.get( login );
				challenge = auth.getOTPChallenge( user );
	
				if( challenge!=null ) {
					user.setDate( Calendar.getInstance().getTime() );
					users.put( login, user );
				}
			}

			// On verifie ensuite si des utilisateurs sont depuis
			// trop longtemps en attente dans la file d'attente.
			// Si c'est le cas, ces utilisateurs sont supprimés.
			deleteTimeoutUsers();

		}

		if( challenge==null )
			System.out.println( "Connection refused for " + login );

		return challenge;
	}
	
	/**
	 * Teste la reponse a un challenge d'un utilisateur
	 * deja dans la file d'attente. Si la connexion est
	 * reussie, les donnees de l'utilisateur sont mis a jour
	 * et la base de donnees est sauvee.
	 * Dans tous les cas, l'utilisateur est supprimé de la file.
	 * De plus, on regarde si des utilisateurs sont encore dans la
	 * file d'attente, et n'ont pas répondu au challenge depuis
	 * un certain temps. Dans ce cas, ces utilisateurs sont eux
	 * aussi supprimés.
	 * Les accès concurrents sont gérés.
	 * @param login Un nom d'un utilisateur dans la file d'attente.
	 * @param otp Une reponse a un challenge OTP.
	 * @return boolean True si la connexion est reussie.
	**/
	public boolean checkChallengeAnswer( String login, String otp ) throws RemoteException {

		boolean result = false;
		String message = "";

		// On synchronise le bloc d'instruction suivant.
		// Cette methode peut être appelée par plusieurs autres
		// objets en même temps, il faut garantir que le code
		// suivant ne soit exécuté que par un seul objet appelant
		// à la fois. Il n'y a pas d'accès concurrent à l'authentification,
		// le fichier n'est pas ecrit plusieurs en même temps.
		synchronized( this ) {

			// On verifie si des utilisateurs sont depuis
			// trop longtemps en attente dans la file d'attente.
			// Si c'est le cas, ces utilisateurs sont supprimés.
			deleteTimeoutUsers();

			// On procède a l'authentification
			if( users.containsKey( login ) ) {
				OTPUser user = (OTPUser) users.get( login );

				// L'authentification n'est valable qu'une fois.
				// L'utilisateur est supprimé de la file d'attente.
				users.remove( login );

				if( auth.checkOTPAnswer( user, otp, false ) ) {
					result = true;

					// Si le numéro de séquence de l'utilisateur tombe
					// à zéro, on le supprime de la base de données.
					if( user.getHash().getSequence()==0 ) {
						db.delete( login );
					}

					db.saveData();
				}
	
			} else {
				message += " (Time out)" ;
			}

		}

		if( result ) {
			message = "Connection succeeded for " + login + message;
		} else {
			message = "Connection failed for " + login + message;
		}

		System.out.println( message );
		return result;
	}
	
	/**
	 * Supprime les utilisateurs qui sont depuis trop longtemps
	 * dans la file d'attente.
	**/
	protected void deleteTimeoutUsers() {

		Vector<String> deletedUsers = new Vector<String>();
		long timeInSec = Calendar.getInstance().getTimeInMillis() / 1000 ;

		synchronized( this ) {

			// On parcours et on marque les elements a supprimer.
			for( Enumeration enumeration = users.elements(); enumeration.hasMoreElements(); ) {
				OTPUser otpuser = (OTPUser) enumeration.nextElement();
	
				Calendar usertime = Calendar.getInstance();
				usertime.setTime( (Date) otpuser.getDate() );
	
				if( timeInSec > (usertime.getTimeInMillis()/1000)+timeout ) {
					deletedUsers.add( otpuser.getLogin() );
				}
			}
	
			// On supprime les elements a supprimer.
			Iterator it = deletedUsers.iterator();
			while( it.hasNext() )
				users.remove( (String) it.next() );
		
		}
	}

}