import java.io.*;
import java.util.*;
import otp.*;
import otp.hash.*;

/**
 * Outils Serveur qui permet de generer des passwords
 * a usage unique.<br>
 * Le programme charge tout d'abord le contenu du fichier
 * 'users.db' en memoire. Il regarde alors si le nom
 * d'utilisateur passe en ligne de commande est contenu
 * dans la base de donnees chargee precedement. Si ca
 * n'est pas le cas, le programme se termine.<br>
 * Puis, le programme se met en attente de lecture d'une
 * pass-phrase valide sur l'entree standard (clavier).
 * Cette pass-phrase doit contenir au minimum
 * OTPFactory.MIN_LENGTH_PASS caracteres. Si ca n'est pas
 * le cas, le programme redemande la saisie.<br>
 * Une fois que la pass-phrase est validee, un utilisateur
 * est cree, ainsi que le hash qui lui est associe, grace
 * a la fonction generate().<br>
 * Enfin, le programme demande a l'utilisateur de resoudre
 * le premier challenge afin de confirmer son entree dans
 * la base de donnees. Si le challenge est resolu, il
 * est insere dans le fichier, et le programme se termine
 * avec succes.
 * @author Thomas Chemineau
 * @version 0.1
**/

public class MyOTPPasswd {

	/**
	 * Methode principale permettant de lancer le programme
	 * client.
	 * @param args Un tableau contenant tous les parametres
	 * entres en ligne de commande.
	**/
	public static void main( String args[] ) {

		String algo = OTPFactory.HASHID_MD5;

		if( args.length!=1 ) {
			if( args.length==2 ) {
				if( args[1].compareTo( "md5" )==0 ) {
					algo = OTPFactory.HASHID_MD5;
				} else if( args[1].compareTo( "sha1" )==0 ) {
					algo = OTPFactory.HASHID_SHA1;
				} else {
					System.out.println( "No support for " + algo );
					System.exit( 0 );
				}
			} else {
				System.out.println( "Usage : MyOTPPasswd <login> [md5|sha1]" );
				System.exit( 0 );
			}
		}

		String username = args[0];
		OTPDatabase db = new OTPDatabase( "users.db" );
		OTPUser user = new OTPUser( args[0],
			algo,
			OTPFactory.INITIAL_SEQUENCE,
			OTPFactory.createSeed(),
			null );

		// On verifie le nom d'utilisateur.

		db.loadData();
		if( db.contains( username ) ) {
			System.out.println( "User " + username + " exists." );
			System.exit( 0 );
		}

		// On lit la pass-phrase.	

		System.out.print( "Adding " + username + " :\n" +
			"You need the response from an OTP generator.\n" +
			"New secret pass phrase: " );

		String password = "";
		int password_minlen = OTPFactory.MIN_LENGTH_PASS ;
		while( password.length()<password_minlen ) {
			password = lireString();
			if( password.length()<password_minlen ) {
				System.out.println( "\tToo short "
					+ "(must be greater than " + password_minlen + " characters)." );
				System.out.print( "New secret pass phrase: " );
			}
		}

		// On met la meme semence pour tous les users.
		// Ca sert a authentifier un serveur. Puis on genere le hash.

		// if( !db.isEmpty() )
		//	user.getHash().setSeed( db.getFirst().getHash().getSeed() );
		user.getHash().generate( password );

		// On verifie par une calculatrice OTP.
		OTPAuth auth = new OTPAuth();

		System.out.print( "\t" + auth.getOTPChallenge( user ) + "\n\tResponse: " );
		String otp = lireString();

		try {
			
			if( auth.checkOTPAnswer( user, otp, false ) ) {
				db.add( user );
				db.saveData();
				System.out.println( "Successful" );
			} else {
				System.out.println( "Failed" );
			}

		} catch( Exception e ) {
			System.out.println( "Error: " + e );
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
