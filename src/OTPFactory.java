package otp;

import otp.hash.*;
import java.util.Random;

/**
 * Classe de fabrication d'Hash.
 * Toutes les methodes de cette classe sont statiques, l'objet
 * OTPFactory n'a pas besoin d'etre instancie.
 *
 * @author Thomas CHEMINEAU
 * @version 0.2
**/

public class OTPFactory {

	public static final int INITIAL_SEQUENCE = 9999;
	public static final int MIN_LENGTH_SEED = 8;
	public static final int MAX_LENGTH_SEED = 16;
	public static final int MIN_LENGTH_PASS = 8;
	public static final String HASHID_MD4 = "otp-md4";
	public static final String HASHID_MD5 = "otp-md5";
	public static final String HASHID_SHA1 = "otp-sha1";

// Methodes

	/**
	 * Cree un challenge a partir d'un objet OTPUser.
	 * @param user Un utilisateur.
	 * @return String Un challenge.
	**/
	public static String createChallenge( OTPUser user ) {

		OTPHash hash = user.getHash();

		return ( OTPFactory.getHashID( hash ) + " "
			+ ( hash.getSequence()-1 ) + " "
			+ hash.getSeed() ) ;

	}

	/**
	 * Cree un objet OTPHash a partir d'un HASHID.
	 * @param hashid Un HASHID de cette classe.
	 * @return OTPHash L'objet nouvellement cree.
	**/
	public static OTPHash createOTPHash( String hashid ) {

		if( hashid.compareTo( HASHID_MD5 )==0 ) {
			return new OTPHashMD5( INITIAL_SEQUENCE, createSeed() );
		} else if( hashid.compareTo( HASHID_MD4 )==0 ) {
			return new OTPHashMD4( INITIAL_SEQUENCE, createSeed() );
		} else if( hashid.compareTo( HASHID_SHA1 )==0 ) {
			return new OTPHashSHA1( INITIAL_SEQUENCE, createSeed() );
		}
		
		return null;
    }

	/**
	 * Cree un objet OTPHash a partir d'informations
	 * concernant cet objet.
	 * @param hashid Un HASHID de cette classe.
	 * @param seq Un numero de sequence.
	 * @param seed Une semence.
	 * @param hash Un hash.
	 * @return OTPHash L'objet nouvellement cree.
	**/
	public static OTPHash createOTPHash( String hashid, int seq, String seed, String hash ) {

		OTPHash otphash = createOTPHash( hashid );

		if( otphash != null ) {
			otphash.setSequence( seq );
			otphash.setSeed( seed );
			otphash.setHash( hash );
		}

		return otphash;
	}

	/**
	 * Cree une semence de hash entre 8 et 16 caracteres.
	 * @return String Une semence.
	**/
	public static String createSeed() {

		// On utilise des masques.
		// On sait ce qu'il y a dans le masque.

		String alpha = "abcdefghijklmnopqrstuvwxyz";
		String numer = "0123456789";
		int len = 0;
		StringBuffer stringbuffer = new StringBuffer();

		// On genere une longueur quelconque.
		// On s'assure de la longueur de la semence.

		Random random = new Random( System.currentTimeMillis() );
		while( !( len>MIN_LENGTH_SEED && len<MAX_LENGTH_SEED ) )
			len = random.nextInt() % 16 ;

		for( int i = 0; i < len; i++ ) {

			int r = random.nextInt() % 2 ;
			int pos_alpha = (int) ( random.nextInt() % alpha.length() );
			int pos_numer = (int) ( random.nextInt() % numer.length() );

			// On fait attention que les nombres
			// ne soient pas negatifs.

			r = r<0 ? r+2 : r ;
			pos_alpha = pos_alpha<0 ? pos_alpha+alpha.length() : pos_alpha ;
			pos_numer = pos_numer<0 ? pos_numer+numer.length() : pos_numer ;

            switch( r ) {
				case 0:	// Alpha
					stringbuffer.append( alpha.charAt( pos_alpha ) );
					break;
				case 1: // Numerique
					stringbuffer.append( numer.charAt( pos_numer ) );
					break;
			}
		}

		return stringbuffer.toString();
	}

	/**
	 * Retourne l'HASHID correspondant a l'objet OTPHash.
	 * @param otphash Un objet OTPHash.
	 * @return String L'HASHID correspondant.
	**/
	public static String getHashID( OTPHash otphash ) {

		if( otphash instanceof OTPHashMD5 )
			return HASHID_MD5;
		else if( otphash instanceof OTPHashMD4 )
			return HASHID_MD4;
		else if( otphash instanceof OTPHashSHA1 )
			return HASHID_SHA1;

		return null;
	}

}
