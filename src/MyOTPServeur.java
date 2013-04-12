import otp.network.*;
import java.rmi.*;
import java.rmi.registry.*;

/**
 * C'est le serveur d'authentification.<br>
 * Il utilise le protocole RMI de Java pour les echanges sur le reseau.
 * Le programme ne se termine pas.<br>
 * Le fonctionnement est trs simple. Tout d'abord, le programme
 * est signale comme demon et sur le reseau, dans le service de nommage
 * de la JVM. Il se met ensuite en attente de connexions clientes.
 * @author Thomas Chemineau
 * @version 0.1
**/

public class MyOTPServeur {

	public static void main( String [] args ) throws Exception {

		OTPServer serveur = null;
		String name = "MyOTPServeur" ;
		int port = 1099;
		
		if( args.length!=0 ) {
			if( args.length<=2 )
				port = Integer.parseInt( args[0] );
			if( args.length==2 )
				name = args[1];
		}

		// Politique de securite.
		/*
		if( System.getSecurityManager()==null )
			System.setSecurityManager( new RMISecurityManager() );
		*/

		// Enregistrement dans le service de nommage de la JVM.

		try {
			java.rmi.registry.LocateRegistry.createRegistry( port );
		} catch( Exception e ) {
			// un registre RMI est deja existant.
		}

		// Creation du serveur.
		try {

			serveur = new OTPServer( "users.db", 5, 180 );
			java.rmi.Naming.rebind( name, serveur );
			
			System.out.println( "RMI Servername: " + name );
			System.out.println( "Listening on port " + port );
			System.out.println( "-----------------" );

		} catch( RemoteException e ) {
			System.out.println( "Pas de reseau." );
			System.exit( 0 );
		}

    }

}