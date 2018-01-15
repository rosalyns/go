package oud;
/**
 * <!-- Versie 1.3.4.
 *
 * -------------
 * - CHANGELOG -
 * -------------
 * 
 * Versie 1.3.4
 * 
 * + correcte delimiter implemented binnen commando's - Rosalyn
 * 		-example: move & makemove
 * Versie 1.3.3
 * 
 * +verduidelijking commando server MOVE aangaande delimitter gebruik - Thomas & Niek
 * +verduidelijking volgorde stenen MOVE & MAKEMOVE
 * +SECURITY implementation toegevoegd
 * Versie 1.3.2
 * 
 * + fixed verkeerde delimitter MAKEMOVE - Rosalyn
 * + general errorcode 8 - JasperGerth & StephanMB
 *
 * 
 * Versie 1.3
 * + hallo commando overbodige tekst verwijderd
 * + error messages functionaliteit toegevoegd
 * + OKwaitfor functionaliteit uitgelegd
 * 
 * 
 * Versie 1.2
 * 	+ Chat commando updated 
 * 		+ CHAT_playerName_message --peter verzijl
 *  + Defined stone
 *  	+ elke kleur en vorm hebben nu een char toegewezen gekregen -- peter verzijl
 * Versie 1.1
 * 
 *  + consistentie voor de content
 *  + verschillende spelfouten weggewerkt
 * Versie 0.042
 *
 * + Eerste versie protocol
 * -->
*/

public class Protocol2 {
	
	public static class Client {

		public static final String HALLO = "HALLO";
		public static final String QUIT = "QUIT";
		public static final String INVITE = "INVITE";
		public static final String ACCEPTINVITE = "ACCEPTINVITE";
		public static final String DECLINEINVITE = "DECLINEINVITE";
		public static final String MAKEMOVE = "MAKEMOVE";
		public static final String CHAT = "CHAT";
		public static final String REQUESTGAME = "REQUESTGAME";
		public static final String CHANGESTONE = "CHANGESTONE";
		public static final String GETLEADERBOARD = "GETLEADERBOARD";
		public static final String GETSTONESINBAG = "GETSTONESINBAG";
		public static final String ERROR = "ERROR";

	}

	public static class Server {

		public static final String HALLO = "HALLO";
		public static final String ERROR = "ERROR";
		public static final String OKWAITFOR = "OKWAITFOR";
		public static final String STARTGAME = "STARTGAME";
		public static final String GAME_END = "END";
		public static final String MOVE = "MOVE";
		public static final String CHAT = "CHAT";
		public static final String ADDTOHAND = "ADDTOHAND";
		public static final String STONESINBAG = "STONESINBAG";
		public static final String LEADERBOARD = "LEADERBOARD";
		public static final String INVITE = "INVITE";
		public static final String DECLINEINVITE = "DECLINEINVITE";
	}

	public static class Features {
		
		public static final String CHAT = "CHAT";
		public static final String LEADERBOARD = "LEADERBOARD";
		public static final String SECURITY = "SECURITY";
		public static final String CHALLENGE = "CHALLENGE"; 
		// Deze functie wordt nog niet verwacht wordt dat SSLsocket gebruikt gaat worden
	}

	public static class Settings {

		public static final String ENCODING = "UTF-16";
		public static final int TIMEOUTSECONDS = 15;
		public static final short DEFAULT_PORT = 4242;
		public static final char DELIMITER = '_';
		public static final char DELIMITER2 = '*';
		public static final String COMMAND_END = "\n\n";
	}
}