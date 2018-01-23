package commands;

import client.controller.GoClient;
import client.model.Move;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
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
	protected final String commandStr = Protocol.Server.TURN;
	protected final String pass = Protocol.Server.PASS;
	protected final String first = Protocol.Server.FIRST;
	private String currentPlayer;
	private String turn;
	private String nextPlayer;
	
	public TurnCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public TurnCommand(ClientHandler clientHandler, String currentPlayer, String turn, 
			String nextPlayer) {
		super(clientHandler);
		this.currentPlayer = currentPlayer;
		this.turn = turn;
		this.nextPlayer = nextPlayer;
	}

	public TurnCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + delim1 + currentPlayer + turn + nextPlayer + commandEnd;
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 4) {
			throw new InvalidCommandLengthException();
		}
		
		if (words[2].equals(pass)) {
			client.makeMove(new Move(client.getColor(words[1]), Move.PASS));
		} else if (!words[2].equals(first)) {
			String[] coordinatesStr = words[2].split(delim2);
			int[] coordinates = new int[2];
			for (int i = 0; i < 2; i++) {
				coordinates[i] = Integer.parseInt(coordinatesStr[i]);
			}
			int dim = client.getBoardDim();
			client.makeMove(new Move(client.getColor(words[1]), 
					coordinates[0] * dim + coordinates[1]));
		}
		client.nextPlayer(words[3]);
	}

}
