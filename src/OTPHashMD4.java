package otp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import otp.exception.NoHashSupportException;
import otp.exception.InvalidHashException;

/**
 * Represente un Hash MD4.
 *
 * @author Thomas CHEMINEAU
 * @version 0.1
**/

public class OTPHashMD4 extends OTPHashMD5 {

// Constructeurs

	public OTPHashMD4( int sequence, String seed ) {
		super( sequence, seed );
	}

	public OTPHashMD4( int sequence, String seed, String hash ) {
		super( sequence, seed, hash );
	}

// Methodes redefinies

	/**
	 * Hashe un tableau d'octets.
	 * @return byte[] Le resultat du hashage.
	**/
    public byte[] hash( byte tab[] ) throws NoHashSupportException {

		byte result[] = null;

		/*
		 * Non supporté par Java.
		 * A implementer soit meme.

		try {
			result = MessageDigest.getInstance("MD4").digest( tab );
		} catch( NoSuchAlgorithmException nosuchalgorithmexception ) {
			throw new NoHashSupportException( "MD4" );
		}
		
		*/

		return result;
	}

}
