package otp;

import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import otp.hash.OTPHash;

/**
 * Represente un utilisateur.
 *
 * @author Thomas CHEMINEAU
 * @version 0.5
**/

public class OTPUser {

	/** Date d'access. **/
	private Date date;
	/** Le nom d'utilisateur. **/
	private String login;
	/** Le Hash de l'utilisateur. **/
	private OTPHash hash;

// Constructeurs

	/**
	 * Construit un nouvel objet OTPUser.
	 * @param login Le nom d'utilisateur.
	 * @param algo L'HASHID de l'algorithme de hashage.
	 * @param seq Le numero de sequence du hash.
	 * @param seed La semence du hash.
	 * @param h Le hash.
	**/
	public OTPUser( String login, String algo, int seq, String seed, String h ) {

		this.login = login;
		hash = OTPFactory.createOTPHash( algo, seq, seed, h );
		date = Calendar.getInstance().getTime();
	}

	/**
	 * Construit un nouvel objet OTPUser.
	 * @param user Une chaine de caracteres retournee par toString().
	**/
	public OTPUser( String user ) {

		String tmp[] = user.split( ";" );
		login = tmp[0];
		hash = OTPFactory.createOTPHash( tmp[3], Integer.parseInt( tmp[1] ), tmp[2], tmp[4] );
		
		try {
			date = DateFormat.getDateTimeInstance().parse( tmp[5] );
		} catch( ParseException parseexception ) {
			date = Calendar.getInstance().getTime();
		}
	}

// Methodes

	/**
	 * Retourne cet objet sous forme de chaine de caracteres.
	 * @return String Cet objet sous forme de chaine de caracteres.
	**/
	public String toString() {

		return login + ";"
			+ hash.getSequence() + ";"
			+ hash.getSeed() + ";"
			+ OTPFactory.getHashID( hash ) + ";"
			+ hash.getHash() + ";"
			+ DateFormat.getDateTimeInstance().format( date );
	}

// Accesseurs et Modifieurs

	/**
	 * Retourne la date.
	 * @return Date La date.
	**/
	public Date getDate() { return date; }

	/**
	 * Retourne le nom d'utilisateur.
	 * @return String Le nom d'utilisateur.
	**/
	public String getLogin() { return login; }

	/**
	 * Retourne le hash de l'utilisateur.
	 * @return OTPHash Le hash.
	**/
	public OTPHash getHash() { return hash; }

	/**
	 * Met a jour la date.
	 * @param d Une date valide.
	**/
	public void setDate( Date d ) { date =d; }

	/**
	 * Met a jour le nom d'utilisateur.
	 * @param username Un nom d'utilisateur.
	**/
	public void setLogin( String username ) { login = username; }

	/**
	 * Met a jour le hash de l'utilisateur.
	 * @param otphash Un hash valide.
	**/
	public void setHash( OTPHash otphash ) { hash = otphash; }

}
