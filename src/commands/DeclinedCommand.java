package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import server.controller.ClientHandler; 

public class DeclinedCommand extends Command {

	public DeclinedCommand(ClientHandler clientHandler) {
		super(clientHandler);
		// TODO Auto-generated constructor stub
	}

	public DeclinedCommand(GoClient client) {
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
