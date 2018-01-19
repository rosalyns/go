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

	public void parse(String command) throws InvalidCommandLengthException {
		
	}

	@Override
	public String compose() {
		return commandStr;
	}

}
