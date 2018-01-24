package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;
/**
 * CLIENT -> SERVER
 * Om een chatbericht te sturen. Als je in een spel zit mogen alleen de spelers het zien. 
 * Als je in de lobby zit mag iedereen in de lobby het zien.<br>
 * Format: CHAT bericht<br>
 * Voorbeeld: CHAT hoi ik ben piet
 */

/**
 * SERVER -> CLIENT
 * Stuurt chatbericht naar relevante clients (in spel of in lobby).<br>
 * Format: CHAT naam bericht<br>
 * Voorbeeld: CHAT piet hallo ik ben piet (Met correcte delimiter ziet dat er dus uit als:
 * CHAT$piet$hallo ik ben piet)
 */

public class ChatCommand extends Command {
	protected final String commandStr = Protocol.Client.CHAT;
	private String name;
	private String message;
	
	public ChatCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}
	
	public ChatCommand(ClientHandler clientHandler, String name, String message) {
		super(clientHandler, true);
		this.name = name;
		this.message = message;
	}

	public ChatCommand(GoClient client) {
		super(client, true);
	}
	
	public ChatCommand(GoClient client, String message) {
		super(client, false);
		this.message = message;
	}

	@Override
	public String compose() {
		return commandStr + delim1 + (toClient ? name + delim1 : "") + message + commandEnd;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (toClient) {
			if (words.length != 3) {
				throw new InvalidCommandLengthException();
			}
			client.showChatMessage(words[1], words[2]);
		} else {
			if (words.length != 2) {
				throw new InvalidCommandLengthException();
			}
			clientHandler.handleChatMessage(words[1]);
		}
	}
}
