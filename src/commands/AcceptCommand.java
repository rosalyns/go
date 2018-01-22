package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;
/**
 * CLIENT -> SERVER
 * Als je de uitdaging wil accepteren.<br>
 * Format: ACCEPTGAME naamuitdager<br>
 * Voorbeeld: ACCEPTGAME piet
 */
public class AcceptCommand extends Command {
	protected final String commandStr = Protocol.Client.ACCEPTGAME;
	private String challenger;
	
	public AcceptCommand(GoClient client) {
		super(client);
	}

	public AcceptCommand(GoClient client, String challenger) {
		super(client);
		this.challenger = challenger;
	}
	
	public AcceptCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 2) {
			throw new InvalidCommandLengthException();
		}
		clientHandler.acceptGame(words[1]);
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + delim1 + challenger + commandEnd;
	}

}
