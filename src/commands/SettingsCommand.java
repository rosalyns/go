package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;
import server.model.Stone;

/**
 * CLIENT -> SERVER
 * Als de server een START met aantal spelers heeft gestuurd mag je je voorkeur doorgeven 
 * voor kleur en grootte van het bord. Dit wordt gevraagd aan de speler die er als eerst 
 * was.<br>
 * Format: SETTINGS kleur bordgrootte<br>
 * Voorbeeld: SETTINGS BLACK 19
 */
public class SettingsCommand extends Command {
	protected final String commandStr = Protocol.Client.SETTINGS;
	protected final String black = Protocol.General.BLACK;
	protected final String white = Protocol.General.WHITE;
	private Stone color;
	private int boardSize;
	
	public SettingsCommand(GoClient client) {
		super(client, true);
	}

	public SettingsCommand(GoClient client, Stone color, int boardSize) {
		super(client, false);
		this.color = color;
		this.boardSize = boardSize;
	}
	
	public SettingsCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 3) {
			throw new InvalidCommandLengthException();
		}
		this.color = words[1].equals(Protocol.General.BLACK) ? Stone.BLACK : Stone.WHITE;
		this.boardSize = Integer.parseInt(words[2]);
		clientHandler.setGame(color, boardSize);
	}

	@Override
	public String compose() {
		return commandStr + delim1 + color + delim1 + boardSize + commandEnd;
	}
}
