package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER
 * Om een move te communiceren. Bord begint linksboven met 0,0.<br>
 * Format: MOVE rij_kolom of MOVE PASS<br>
 * Voorbeeld: MOVE 1_3
 */
public class MoveCommand extends Command {
	protected final String commandStr = Protocol.Client.MOVE;
	protected final String passStr = Protocol.Client.PASS;
	private boolean pass;
	private int row;
	private int column;
	
	public MoveCommand(GoClient client) {
		super(client, true);
	}
	
	public MoveCommand(GoClient client, boolean pass, int row, int column) {
		super(client, false);
		this.pass = pass;
		this.row = row;
		this.column = column;
	}

	public MoveCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	@Override
	public void parse(String[] words) throws InvalidCommandLengthException {
		if (words.length != 2) {
			throw new InvalidCommandLengthException();
		}
		if (words[1].equals(passStr)) {
			pass = true;
			row = 0;
			column = 0;
		} else {
			pass = false;
			String[] move = words[1].split(delim2);
			row = Integer.parseInt(move[0]);
			column = Integer.parseInt(move[1]);
		}
		if (clientHandler.isInGame()) {
			clientHandler.makeMove(pass, row, column);
		}
	}

	@Override
	public String compose() {
		return commandStr + delim1 + (pass ? passStr : row + delim2 + column) + commandEnd;
	}
}
