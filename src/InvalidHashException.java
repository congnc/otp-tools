package otp.exception;

import java.lang.RuntimeException;

/**
 *
 * Souleve une exception quand un hash est invalide.
 *
 * @version 0.1
 *
**/

public class InvalidHashException extends RuntimeException {

	/**
	 * Cree une nouvelle exception d'invalidite d'un hash.
	**/
	public InvalidHashException( String erreur ) {
		super( erreur );
	}

}
