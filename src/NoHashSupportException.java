package otp.exception;

import java.lang.RuntimeException;

/**
 *
 * Souleve une exception quand un algorithme de
 * hashage n'est pas support par la JVM.
 *
 * @version 0.1
 *
**/

public class NoHashSupportException extends RuntimeException {

	/**
	 * Cree une nouvelle exception d'echec du support
	 * de l'algorithme de hashage specifie.
	**/
	public NoHashSupportException( String algo ) {
		super( "Algorithme de hashage " + algo + " non supporte." );
	}

}
