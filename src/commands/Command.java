package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

public abstract class Command {
	protected final String delim1 = Protocol.General.DELIMITER1;
	protected final String delim2 = Protocol.General.DELIMITER2;
	protected final String commandEnd = Protocol.General.COMMAND_END;
	protected final String commandStr = "";
	protected GoClient client;
	protected ClientHandler clientHandler;
	
	public Command(GoClient client) {
		this.client = client;
	}
	
	public Command(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
	
	public abstract void parse(String command, boolean toClient) 
			throws InvalidCommandLengthException;
	public abstract String compose(boolean toClient);
	
	public void send(boolean toClient) {
		if (toClient) {
			clientHandler.sendCommandToClient(this.compose(toClient));
		} else {
			client.sendCommandToServer(this.compose(toClient));
		}
	}

}
