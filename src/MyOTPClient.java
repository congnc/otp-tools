import otp.network.*;
import java.io.*;
import java.rmi.*;

/**
 * Outils Client pour se connecter au serveur OTP.<br>
 * Cet outils utilise le protocole RMI pour se connecter
 * au serveur.<br>
 * Une fois la connexion etablie, un login est demande
 * sur l'entree standard (le clavier). Le client demande
 * alors au serveur un challenge pour le login specifie.
 * Le challenge est affiche sur la sortie standard (l'ecran),
 * une reponse est attendue sur l'entree standard.<br>
 * L'utilisateur doit saisir alors une chaine OTP valide
 * calcule par une calculatrice OTP non fournie ici
 * ( voir : http://www.cs.umd.edu/~harry/jotp/ ). Cette
 * reponse est envoye au serveur, puis l'attente d'une reponse
 * de reussite ou d'echec d'authentification est attendue
 * de la part du serveur.
 * @author Thomas Chemineau
 * @version 0.1
**/

public class MyOTPClient {

	/**
	 * Methode principale permettant de lancer le programme
	 * client.
	 * @param args Un tableau contenant tous les parametres
	 * entres en ligne de commande.
	**/
	public static void main( String [] args ) throws Exception {
	
		if( args.length!=2 ) {
			System.out.println( "Usage : MyOTPClient <hostname[:port]> <nameOfRMIServer>" );
			System.exit( 0 );
		}
		
		String host = args[0];
		String rminame = args[1];
		String url = "rmi://" + host + "/" + rminame ;
		
		System.out.println( "Contacting RMI Server '" + rminame + "' on " + host );
		RMIOTPServer serveur = (RMIOTPServer) Naming.lookup( url );

		if( serveur==null ) {
			System.out.println( "Connection to server failed." );
			System.exit( 0 );
		}

		System.out.print( "Login: " );
		String login = lireString();
		String challenge = serveur.askForChallenge( login );

		if( challenge==null ) {
			System.out.println( "Connection refused." );
			System.exit( 0 );
		}

		System.out.print( "OTP Challenge: " + challenge + "\nResponse: " );
		String otp = lireString();
		boolean access = serveur.checkChallengeAnswer( login, otp );
		
		if( access ) {
			System.out.println( "Successful." );
		} else {
			System.out.println( "Failed or Time out." );
		}

	}

	/**
	 * Methode statique permettant de lire une chaine de
	 * caracteres sur l'entree standard (le clavier).
	 * @return String La chaine de caracteres lue.
	**/
	public static String lireString() {

		int erreur = -1;
		BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
		String s = new String();

		while( erreur == -1 ) {
			try {
				s = in.readLine();
				erreur = 0;
			} catch( IOException e ) {
				System.out.println("Error : Wrong input.");
			}
		}

		return s;

	}
    
}