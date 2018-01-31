package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER
 * Als je midden in een spel zit en wil stoppen. Wordt niet gestuurd als client abrupt 
 * afgesloten wordt.<br>
 * Format: QUIT<br>
 * Voorbeeld: QUIT
 */
public class QuitCommand extends Command {
	protected final String commandStr = Protocol.Client.QUIT;
	
	public QuitCommand(GoClient client) {
		super(client, true);
	}
	
	public QuitCommand(GoClient client, boolean toClient) {
		super(client, false);
	}
	
	public QuitCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	public void parse(String[] words) throws InvalidCommandLengthException {
		if (words.length != 1) {
			throw new InvalidCommandLengthException();
		}
		clientHandler.quitGame(); 
	}

	@Override
	public String compose() {
		return commandStr + commandEnd;
	}

}
