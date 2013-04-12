package otp.hash;

import otp.OTPDico;
import otp.OTPFactory;
import otp.exception.NoHashSupportException;
import otp.exception.InvalidHashException;

/**
 * Represente un Hash.
 * Les hash necessaire au protocole OTP sont codes
 * sur 64 bits.
 *
 * @author Thomas CHEMINEAU
 * @version 0.5
**/

public abstract class OTPHash {

	/** Le numero de sequence du hash. **/
    private int sequence;
    /** La semence du hash. **/
    private String seed;
    /** Le hash. **/
    private String hash;

// Constructeurs

	public OTPHash( int sequence, String seed ) {
		this.sequence = sequence;
		this.seed = seed.toLowerCase();
	}

	public OTPHash( int sequence, String seed, String hash ) {
		this.sequence = sequence;
		this.seed = seed.toLowerCase();
		this.hash = hash;
	}

// Methodes abstraites

	/**
	 * Hashe un tableau d'octets.
	 * @param tab Un tableau d'octets a hasher.
	 * @return byte[] Le resultat du hashage.
	**/
    public abstract byte[] hash( byte tab[] ) throws NoHashSupportException;
    
    /**
     * Transpose le hash sur 64 bits.
     * @param h Un hash (tableau d'octets).
     * @return byte[] Un tableau de 8 octets, sur 64 bits.
    **/
    public abstract byte[] parseTo64Bits( byte[] h );

// Methodes

	/**
	 * Clone cet objet.
	 * @return Object Un clone de cet objet.
	**/
	public Object clone() {

		OTPHash h = OTPFactory.createOTPHash( OTPFactory.getHashID( this ) );
		h.setSequence( sequence );
		h.setSeed( new String( seed ) );
		h.setHash( new String( hash ) );

		return h;
	}

	/**
	 * Compare si cet objet est egal a un autre objet OTPHash.
	 * @param  otphash Un objet OTPHash.
	 * @return boolean True si les deux objets sont egaux.
	**/
	public boolean equal( OTPHash otphash ) {
		return otphash.getHash().compareTo(hash)==0 ;
	}

	/**
	 * Genere le hash.
	 * @param pass Une pass-phrase.
	**/
	public void generate( String pass ) {

		byte tab[] = ( seed + pass ).getBytes();
		for( int i = 0; i <= sequence; i++ )
			tab = parseTo64Bits( hash( tab ) );

		hash = parseBytesToHexString( tab );
	}

	/**
	 * Hashe une fois.
	 * @return boolean True si le hashage s'est bien effectue.
	**/
	public boolean hashOneTime() {

		if( hash==null || hash.length()!=16 )
			return false;

		byte tab[] = parseHexStringToBytes( hash );
		tab = parseTo64Bits( hash( tab ) );
		hash = parseBytesToHexString( tab );
		sequence++;
		
		return true;
	}

	/**
	 * Traduit un tableau d'octets en son équivalent en
	 * hexadécimal sous forme de chaine de caracteres.
	 * @param tab Un tableau d'octets.
	 * @return String Sa representation en hexadecimal.
	**/
	public static String parseBytesToHexString( byte tab[] ) {

		StringBuffer stringbuffer = new StringBuffer();

		for( int i = 0; i < tab.length; i++ ) {
			int e = tab[i];
			String hex = Integer.toHexString( e );

			if( hex.length() == 1 ) {
				stringbuffer.append( '0' );
				stringbuffer.append( hex.charAt( hex.length()-1 ) );
			} else {
				stringbuffer.append( hex.substring( hex.length()-2 ) );
			}
		}

		return stringbuffer.toString();
	}

	/**
	 * Traduit un hash hexadecimal ( sous forme de chaine
	 * de 16 caracteres ) en un tableau de 8 octets.
	 * @param str La representation hexadecimale.
	 * @return byte[] Sa representation en octets.
	**/
	public static byte[] parseHexStringToBytes( String str ) {

		if( str.length()>16 )
			throw new InvalidHashException( "Hash trop long." );

		byte[] result = new byte[8];

		for( int i=0; i<8; i++ ) {
			result[i] = (byte) Integer.decode( "0x" + str.substring(i*2, i*2 + 2) ).intValue();
		}

		return result;
	}

	/**
	 * Traduit un hash hexadecimal ( sous forme de chaine
	 * de 16 caracteres ) en une chaine OTP ( sous forme
	 * de 6 blocs de 4 lettres ).
	 * Cette representation est une representation de 6 blocs
	 * de 4 lettres. Chaque bloc represente 11 bits de la chaine.
	 * Les 2 derniers bits manquants sont un checksum.
	 * @param str La representation hexadecimale.
	 * @return String[] Sa representation OTP.
	**/
	public static String parseHexStringToOTPString( String str ) {
		
		// On transforme la chaine de caracteres en entier long.
		
		long l = 0L;
		byte[] tab = parseHexStringToBytes( str );
		for( int i = 0; i < tab.length; i++ ) {
			l <<= 8;
			l |= ( tab[i] & 0xff );
		}

		// Il faut tout d'abord calculer le checksum du hash.
		// Ca consiste a ajouter bits a bits les bits du hash.

		byte checksum = 0;
		long tmp = l;
		for( int i = 0; i < 64; i += 2 ) {
			checksum = (byte) ( checksum + ( tmp & 0x3 ) );
			tmp >>= 2;
		}

		// Ensuite, il faut transformer grace au dico.

		String result = new String();
		for( int i = 4; i >= 0; i-- )
            result += OTPDico.getVal( (int) ( ( l >> ( i * 11 + 9 ) ) & 0x7ff ) ) + " ";

		result += OTPDico.getVal( (int) ( ( ( l << 2 ) & 0x7fc ) | ( checksum & 0x3 ) ) ) ;

		return result;
		
	}

	/**
	 * Traduit une chaine OTP ( sous forme de 6 blocs de 4
	 * lettres ) en un hash hexadecimal ( sous forme de chaine
	 * de 16 caracteres ).
	 * @param str La representation OTP.
	 * @return String[] Sa representation hexadecimale.
	**/
	public static String parseOTPStringToHexString( String str ) {

		String[] s = str.split( " " );

		// On verifie que c'est une chaine OTP.
		
		if( s.length!=6 )
			throw new InvalidHashException( "Chaine OTP invalide." );
		for( int i=0; i<s.length; i++ )
			if( s[i].length()>4 )
				throw new InvalidHashException( "Chaine OTP invalide." );

		// On traduit la chaine en entier long.
		// On vire le checksum, sans verification.

		long l = 0L;
		OTPDico dico = new OTPDico();

		for( int i = 0; i <= 4; i++ ) {
			l <<= 11;
			l |= dico.getKey( s[i] ) & 0x7ff;
		}
		l <<= 9;
		l |= ( dico.getKey( s[5] ) & 0x7fc ) >> 2;

		// On transforme l'entier long en chaine de caracteres.

		byte[] result = new byte[8];
		for( int i = 0; i < result.length; i++ ) {
			result[i] = (byte) ( ( l >> ( 7 - i ) * 8 ) & 0xff );
		}

		return parseBytesToHexString( result );

	}

	/**
	 * Retourne la representation normale du hash.
	 * Cette representation est une representation de 6 blocs
	 * de 4 lettres. Chaque bloc represente 11 bits du hash.
	 * Les 2 derniers bits manquants sont un checksum.
	 * @return String Le resultat.
	**/
	public String toString() {
		return parseHexStringToOTPString( hash );
	}

// Accesseurs et Modifieurs

	public int getSequence() { return sequence; }
	public String getSeed() { return seed; }
	public String getHash() { return hash; }

	public void setSequence( int seq ) { sequence = seq; }
	public void setSeed( String s ) { seed = s.toLowerCase(); }
	public void setHash( String h ) { hash = h; }

}
