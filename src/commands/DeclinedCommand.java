package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler; 

/**
 * SERVER -> CLIENT
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
		this.toClient = true;
	}

	public DeclinedCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public DeclinedCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose() {
		return commandStr + delim1 + challengee + commandEnd;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		// komt nooit van client.
		String[] words = command.split("\\" + delim1);
		if (words.length != 2) {
			throw new InvalidCommandLengthException();
		}
		client.declined(words[1]);
	}

}
