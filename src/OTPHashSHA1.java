package otp.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import otp.exception.NoHashSupportException;
import otp.exception.InvalidHashException;

/**
 * Represente un Hash SHA1.
 *
 * @author Thomas CHEMINEAU
 * @version 0.1
**/

public class OTPHashSHA1 extends OTPHash {

// Constructeurs

	public OTPHashSHA1( int sequence, String seed ) {
		super( sequence, seed );
	}

	public OTPHashSHA1( int sequence, String seed, String hash ) {
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
			result = MessageDigest.getInstance("SHA1").digest( tab );
		} catch( NoSuchAlgorithmException nosuchalgorithmexception ) {
			throw new NoHashSupportException( "SHA1" );
		}

		return result;
	}

	/**
     * Transpose le hash SHA-1 de 160 bits sur 64 bits.
     * @param h Un hash (tableau d'octets).
     * @return byte[] Un tableau de 8 octets, sur 64 bits.
    **/
    public byte[] parseTo64Bits( byte[] h ) {

		if( h.length!=20 )
			throw new InvalidHashException( "Hash SHA-1 invalide : non 160 bits." );

    	byte result[] = new byte[8];
		for( int i = 0; i < result.length; i++ )
			result[i] = (byte) ( h[i] ^ h[i+8] );
		for( int i = 0; i<4; i++ )
			result[i] = ( byte ) ( result[i] ^ h[i+16] );
		
		return result;

    }

}
