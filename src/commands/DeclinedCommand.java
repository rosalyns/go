package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler; 

/**
 * Server -> Client
 * Stuurt aan de uitdager dat de uitdaging is geweigerd en door wie.<br>
 * Format: DECLINED uitgedaagde<br>
 * Voorbeeld: DECLINED piet
 */
public class DeclinedCommand extends Command {
	protected final String commandStr = Protocol.Server.DECLINED;
	private String challengee;
	
	public DeclinedCommand(ClientHandler clientHandler, String challengee) {
		super(clientHandler);
		this.challengee = challengee;
	}

	public DeclinedCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public DeclinedCommand(GoClient client) {
		super(client);
		
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + delim1 + challengee + commandEnd;
	}

	@Override
	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		// komt nooit van client.
		String[] words = command.split("\\" + delim1);
		if (words.length != 2) {
			throw new InvalidCommandLengthException();
		}
		//TODO: client.declined(); ofzo
		
	}

}
