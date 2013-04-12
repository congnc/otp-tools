package otp;

import otp.OTPDatabase;
import otp.OTPFactory;
import otp.OTPUser;
import otp.hash.OTPHash;
import java.util.Date;
import java.util.Calendar;

/**
 * Gere une authentification.
**/

public class OTPAuth {

	/** Indique si il y a authentification. **/
	private boolean isChecking;

// Constructeur

	/**
	 * Cree un nouvel objet d'authentification.
	**/
	public OTPAuth() {
		isChecking = false;
	}

// Methodes

	/**
	 * Verifie la reponse a un challenge OTP et met
	 * a jour les donnes de l'utilisateur en cas de succes.
	 * @param user Un utilisateur.
	 * @param otpStr Une chaine OTP.
	 * @param check Indique si il faut utiliser l'etat isChecking.
	 * @return boolean True si la verification reussie.
	**/
	public boolean checkOTPAnswer( OTPUser user, String otpStr, boolean check ) {

		if( check )
			isChecking = true;

		try {

			String hexStr = OTPHash.parseOTPStringToHexString( otpStr );
			OTPHash hash = (OTPHash) user.getHash().clone();
			hash.setSequence( hash.getSequence()-1 );
			hash.setHash( hexStr );
			hash.hashOneTime();

			if( !hash.equal( user.getHash() ) )
				return false;

			user.getHash().setHash( hexStr );
			user.getHash().setSequence( user.getHash().getSequence()-1 );
			user.setDate( Calendar.getInstance().getTime() );

		} catch( Exception e ) {
			return false;
		}

		if( check )
			isChecking = false;

		return true;
	}

	/**
	 * Recupere un challenge a partir d'un nom d'utilisateur.
	 * @param user Un nom d'utilisateur.
	 * @return String Un challenge OTP, null si pas trouve.
	**/
	public String getOTPChallenge( OTPUser user ) {
		return OTPFactory.createChallenge( user );
	}

	/**
	 * Test si il y a authentification en cours.
	 * Peut servir pour gerer des acces concurrents.
	 * @return boolean True si une authentification est en cours.
	**/
	public boolean isChecking() { return isChecking; }
	
	/**
	 * Met a jour la variable de checking.
	 * @param b Un boolean indiquant l'etat de cet objet.
	**/
	public void setIsChecking( boolean b ) { isChecking = b; }

}
