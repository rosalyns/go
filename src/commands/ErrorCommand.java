package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import server.controller.ClientHandler;

public class ErrorCommand extends Command {

	public ErrorCommand(ClientHandler clientHandler) {
		super(clientHandler);
		// TODO Auto-generated constructor stub
	}

	public ErrorCommand(GoClient client) {
		super(client);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String compose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
		
	}

}
