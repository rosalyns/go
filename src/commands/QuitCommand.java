package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

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
		
		// clientHandler.quitGame(); ofzo 
	}

	@Override
	public String compose() {
		return commandStr + commandEnd;
	}

}
