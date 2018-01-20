package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

public class ChatCommand extends Command {
	protected final String commandStr = Protocol.Client.CHAT;
	private String name;
	private String message;
	
	public ChatCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public ChatCommand(ClientHandler clientHandler, String name, String message) {
		super(clientHandler);
		this.name = name;
		this.message = message;
	}

	public ChatCommand(GoClient client) {
		super(client);
	}
	
	public ChatCommand(GoClient client, String message) {
		super(client);
		this.message = message;
	}

	@Override
	public String compose(boolean toClient) {
		return commandStr + delim1 + name + delim1 + message + commandEnd;
	}

	@Override
	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		
		if (fromServer) {
			if (words.length != 3) {
				throw new InvalidCommandLengthException();
			}
			//client.handleChatMessage(words[1], words[2]);
		} else {
			if (words.length != 2) {
				throw new InvalidCommandLengthException();
			}
			//clientHandler.handleChatMessage(clientHandler.getName(), words[1]);
		}
		
		
	}

}
