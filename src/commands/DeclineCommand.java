package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import exceptions.PlayerNotFoundException;
import general.Protocol;
import server.controller.ClientHandler;
/**
 * CLIENT -> SERVER
 * Als je de uitdaging niet accepteert.<br>
 * Format: DECLINEGAME naamuitdager<br>
 * Voorbeeld: DECLINEGAME piet
 */
public class DeclineCommand extends Command {
	protected final String commandStr = Protocol.Client.DECLINEGAME;
	private String challenger;
	
	public DeclineCommand(GoClient client) {
		super(client);
	}
	
	public DeclineCommand(GoClient client, String challenger) {
		super(client);
		this.challenger = challenger;
	}

	public DeclineCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 2) {
			throw new InvalidCommandLengthException();
		}
		try {
			clientHandler.declineGame(words[1]);
		} catch (PlayerNotFoundException e) {
			new ErrorCommand(clientHandler, ErrorCommand.INVCOMMAND, 
					"This player is not in the lobby.").send(toClient);
		}
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + delim1 + challenger + commandEnd;
	}

}
