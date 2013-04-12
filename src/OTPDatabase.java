package otp;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Gere une base de donnees OTP. Cette classe s'articule autour
 * de la classe OTPUser.
 *
 * @author Thomas CHEMINEAU
 * @version 0.5
**/

public class OTPDatabase {

	/** Le nom de fichier des donnees. **/
    private String nomFichier;
    /** Les donnees. **/
    private Hashtable<String,OTPUser> data;

// Constructeurs

	/**
	 * Construit une nouvelle instance de OTPDatabase.
	 * @param filename Le nom de fichier de donnees.
	**/
	public OTPDatabase( String filename ) {
		data = new Hashtable<String,OTPUser>();
		nomFichier = filename;
	}

// Methodes

	/**
	 * Ajoute un objet OTPUser.
	 * @param user Un objet OTPUser.
	 * @return boolean True si l'operation reussie.
	**/
	public boolean add( OTPUser user ) {

		if( data.containsKey( (String) user.getLogin() ) )
			return false;

		try {
            data.put( (String) user.getLogin(), user );
		} catch( NullPointerException nullpointerexception ) {
			return false;
		}

        return true;
    }

	/**
	 * Test si un utilisateur est dans la base.
	 * @param username Un nom d'utilisateur.
	 * @return boolean True si l'utilisateur est deja dans la base.
	**/
	public boolean contains( String username ) {
		return data.containsKey( username );
	}

	/**
	 * Supprime un objet OTPUser.
	 * @param username Le nom d'utilisateur de l'objet OTPUser.
	 * @return OTPUser L'objet supprime, null si pas trouve.
	**/
	public OTPUser delete( String username ) {
        return (OTPUser) data.remove( username );
    }

	/**
	 * Retourne un objet OTPUser.
	 * @param username Le nom d'utilisateur de l'objet OTPUser.
	 * @return OTPUser L'objet ou null si pas trouve.
	**/
	public OTPUser get( String username ) {
		return (OTPUser) data.get( username );
	}

	/**
	 * Retourne le premier objet OTPUser.
	 * @return OTPUser L'objet ou null si vide.
	**/
	public OTPUser getFirst() {

		if( data.isEmpty() )
			return null;

		Enumeration enumeration = data.elements();
		return (OTPUser) enumeration.nextElement();

	}

	/**
	 * Test si la base est vide.
	 * @return boolean True si c'est le cas.
	**/
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Charge les donnees ( du fichier de donnees ) en memoire .
	 * @return boolean True si l'operation reussie.
	**/
	public boolean loadData() {

		if( !( new File( nomFichier ) ).exists() )
			return false;

		data.clear();

		try {
			// Lecture simple
			String ligne;			
			BufferedReader bufferreader = new BufferedReader( new FileReader( nomFichier ) );
			while( ( ligne = bufferreader.readLine() )!=null ) {
				add( new OTPUser( ligne ) );
			}
			bufferreader.close();
			
			/*
			// Lecture binaire
			DataInputStream datainputstream = new DataInputStream( new FileInputStream( nomFichier ) );
			while( datainputstream.available() > 0 ) {
				add( new OTPUser( datainputstream.readUTF() ) );
			}
			datainputstream.close();
			*/

		} catch( IOException ioexception ) {
			System.out.println( "[OTPDatabase] loadData :\n" + ioexception );
			return false;
		}

		return true;
	}

	/**
	 * Sauve les donnees en memoire dans le fichier de donnees.
	 * @return boolean True si l'operation reussie.
	**/
	public boolean saveData() {

		try {
			// Ecriture simple
			OTPUser otpuser;
			PrintWriter bufferwriter = new PrintWriter(
					new BufferedWriter( new FileWriter( nomFichier) ) );
			for( Enumeration enumeration = data.elements(); enumeration.hasMoreElements(); ) {
				otpuser = (OTPUser) enumeration.nextElement();
				bufferwriter.println( otpuser.toString() );
			}
			bufferwriter.close();
			
			// Ecriture binaire
			/*
			OTPUser otpuser;
			DataOutputStream dataoutputstream = new DataOutputStream( new FileOutputStream( nomFichier ) );
            for( Enumeration enumeration = data.elements(); enumeration.hasMoreElements(); ) {
				otpuser = (OTPUser) enumeration.nextElement();
				dataoutputstream.writeUTF( otpuser.toString() );
			}
			dataoutputstream.close();
			*/

		} catch( IOException ioexception ) {
			System.out.println( "[OTPDatabase] saveData : " + ioexception );
			return false;
		}
        
		return true;
	}

// Accesseurs et Modifieurs

	/**
	 * Retourne le nom du fichier de donnees.
	 * @return String Le nom du fichier de donnees.
	**/
	public String getFileName() { return nomFichier; }

}
