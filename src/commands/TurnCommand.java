package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import server.controller.ClientHandler;
/**
 * SERVER -> CLIENT
 * Vertelt aan de spelers welke beurt er gedaan is. Speler1 is de speler die de beurt heeft
 * gedaan, speler 2 de speler die nu aan de beurt is om een MOVE door te geven. Als dit de
 * eerste beurt is zijn speler1 en speler2 allebei de speler die nu aan de beurt is, en dan
 * stuur je FIRST i.p.v. de integers. Als de speler past geeft je PASS door ip.v. de 
 * integers.<br>
 * Format: TURN speler1 rij_kolom speler2<br>
 * Voorbeeld: TURN piet 1_3 jan of TURN piet FIRST piet
 */
public class TurnCommand extends Command {

	public TurnCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	public TurnCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose(boolean toClient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
		
	}

}
