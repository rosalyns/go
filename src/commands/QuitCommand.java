package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * Als je midden in een spel zit en wil stoppen. Wordt niet gestuurd als client abrupt 
 * afgesloten wordt.<br>
 * Format: QUIT<br>
 * Voorbeeld: QUIT
 */
public class QuitCommand extends Command {
	public final String commandStr = Protocol.Client.QUIT;
	
	public QuitCommand(GoClient client) {
		super(client);
	}
	
	public QuitCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 1) {
			throw new InvalidCommandLengthException();
		}
		
		// TODO: clientHandler.quitGame(); ofzo 
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + commandEnd;
	}

}
