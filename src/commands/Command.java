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
	protected boolean toClient;
	
	public Command(GoClient client) {
		this.client = client;
		this.toClient = true;
	}
	
	public Command(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
		this.toClient = false;
	}
	
	public abstract void parse(String command) throws InvalidCommandLengthException;
	public abstract String compose();
	
	public void send() {
		if (toClient) {
			clientHandler.sendCommandToClient(this.compose());
		} else {
			client.sendCommandToServer(this.compose());
		}
	}

}
