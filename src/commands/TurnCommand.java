package commands;

import java.awt.Point;

import client.controller.GameController;
import client.controller.GoClient;
import model.Board;
import model.Move;
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
	public static final String PASS = Protocol.Server.PASS;
	public static final String FIRST = Protocol.Server.FIRST;
	private String currentPlayer;
	private Move move;
	private int boardDim;
	private String nextPlayer;
	
	public TurnCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}
	
	public TurnCommand(ClientHandler clientHandler, String currentPlayer, Move move, int boardDim, 
			String nextPlayer) {
		super(clientHandler, true);
		this.currentPlayer = currentPlayer;
		this.move = move;
		this.boardDim = boardDim;
		this.nextPlayer = nextPlayer;
	}

	public TurnCommand(GoClient client) {
		super(client, true);
	}

	@Override
	public String compose() {
		String moveStr;
		if (move.getPosition() == Move.PASS) {
			moveStr = PASS;
		} else if (move.getPosition() == Move.FIRST) {
			moveStr = FIRST;
		} else {
			Point coordinates = Board.indexToCoordinates(move.getPosition(), boardDim);
			moveStr = coordinates.y + delim2 + coordinates.x;
		}
		
		return commandStr + delim1 + currentPlayer + delim1 + moveStr + delim1 
				+ nextPlayer + commandEnd;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 4) {
			throw new InvalidCommandLengthException();
		}
		
		GameController game = client.getGameController();
		if (words[2].equals(PASS)) {
			game.makeMove(new Move(game.getColor(words[1]), Move.PASS));
		} else if (!words[2].equals(FIRST)) {
			String[] coordinatesStr = words[2].split(delim2);
			int[] coordinates = new int[2];
			for (int i = 0; i < 2; i++) {
				coordinates[i] = Integer.parseInt(coordinatesStr[i]);
			}
			int dim = game.getBoardDim();
			game.makeMove(new Move(game.getColor(words[1]), 
					coordinates[0] * dim + coordinates[1]));
		}
		game.nextPlayer(words[3]);
	}

}
