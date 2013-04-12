package otp.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface Reseau RMI du cote serveur.
**/

public interface RMIOTPServer extends Remote {
	
	/**
	 * Retourne un challenge si l'utilisateur est
	 * dans la base de donnees et si il peut se connecter.
	 * L'utilisateur est mis dans une file d'attente.
	 * @param login Un nom d'utilisateurs qui a deja demande une connexion.
	 * @return String Un challenge OTP.
	**/
	public String askForChallenge( String login ) throws RemoteException;
	
	/**
	 * Teste la reponse a un challenge d'un utilisateur
	 * deja dans la file d'attente.
	 * @param login Un nom d'un utilisateur dans la file d'attente.
	 * @param otp Une reponse a un challenge OTP.
	 * @return boolean True si la connexion est reussie.
	**/
	public boolean checkChallengeAnswer( String login, String otp ) throws RemoteException;
	
}