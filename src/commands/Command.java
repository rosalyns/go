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
	
	public abstract void parse(String command, boolean fromServer) 
			throws InvalidCommandLengthException;
	public abstract String compose();
	
	public void send(boolean toServer) {
		if (toServer) {
			client.sendCommandToServer(this.compose());
		} else {
			clientHandler.sendCommandToClient(this.compose());
		}
	}

}
