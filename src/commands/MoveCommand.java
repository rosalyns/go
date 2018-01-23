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
		super(client);
	}
	
	public MoveCommand(GoClient client, boolean pass, int row, int column) {
		super(client);
		this.pass = pass;
		this.row = row;
		this.column = column;
	}

	public MoveCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 2) {
			throw new InvalidCommandLengthException();
		}
		clientHandler.makeMove(pass, row, column);
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + delim1 + (pass ? passStr : row + delim1 + column) + commandEnd;
	}
}
