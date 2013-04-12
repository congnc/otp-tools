package otp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import otp.exception.NoHashSupportException;
import otp.exception.InvalidHashException;

/**
 * Represente un Hash MD5.
 *
 * @author Thomas CHEMINEAU
 * @version 0.5
**/

public class OTPHashMD5 extends OTPHash {

// Constructeurs

	public OTPHashMD5( int sequence, String seed ) {
		super( sequence, seed );
	}

	public OTPHashMD5( int sequence, String seed, String hash ) {
		super( sequence, seed, hash );
	}

// Methodes redefinies

	/**
	 * Hashe un tableau d'octets.
	 * @return byte[] Le resultat du hashage.
	**/
    public byte[] hash( byte tab[] ) throws NoHashSupportException {

		byte result[] = null;

		try {
			result = MessageDigest.getInstance( "MD5" ).digest( tab );
		} catch( NoSuchAlgorithmException nosuchalgorithmexception ) {
			throw new NoHashSupportException( "MD5" );
		}

		return result;
	}

	/**
     * Transpose le hash MD5 de 128 bits sur 64 bits.
     * @param h Un hash (tableau d'octets).
     * @return byte[] Un tableau de 8 octets, sur 64 bits.
    **/
    public byte[] parseTo64Bits( byte[] h ) {

		if( h.length!=16 )
			throw new InvalidHashException( "Hash non 128 bits." );

    	byte result[] = new byte[8];
		for( int i = 0; i < result.length; i++ )
			result[i] = (byte) ( h[i] ^ h[i+8] );

		return result;

    }

}
