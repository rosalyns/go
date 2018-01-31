package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

public class ExitCommand extends Command {
	protected final String commandStr = Protocol.Client.EXIT;
	
	public ExitCommand(GoClient client) {
		super(client, true);
	}
	
	public ExitCommand(GoClient client, boolean toClient) {
		super(client, false);
	}

	public ExitCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	@Override
	public void parse(String[] words) throws InvalidCommandLengthException {
		if (words.length != 1) {
			throw new InvalidCommandLengthException();
		}
		clientHandler.clientShutDown();
	}

	@Override
	public String compose() {
		return commandStr + commandEnd;
	}

}
