package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;
/**
 * Errortypes die we gedefinieerd hebben: UNKNOWNCOMMAND, INVALIDMOVE, NAMETAKEN, 
 * INCOMPATIBLEPROTOCOL, OTHER.<br>
 * Format: ERROR type bericht<br>
 * Voorbeeld: ERROR NAMETAKEN de naam piet is al bezet
 */

public class ErrorCommand extends Command {
	
	protected final String commandStr = Protocol.Server.ERROR;
	public static final String INVCOMMAND = Protocol.Server.UNKNOWN;
	public static final String INVMOVE = Protocol.Server.INVALID;
	public static final String INVNAME = Protocol.Server.NAMETAKEN;
	public static final String INVPROTOCOL = Protocol.Server.INCOMPATIBLEPROTOCOL;
	public static final String OTHER = Protocol.Server.OTHER;
	private String errorType;
	private String errorMessage;
	
	public ErrorCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	public ErrorCommand(ClientHandler clientHandler, String errorType, String message) {
		super(clientHandler, true);
		this.errorType = errorType;
		this.errorMessage = message;
	}
	
	public ErrorCommand(GoClient client) {
		super(client, true);
	}
	
	@Override
	public String compose() {
		String command = commandStr + delim1 + errorType + delim1 + errorMessage + commandEnd;
		return command;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 3) {
			throw new InvalidCommandLengthException();
		}
		//altijd van server naar client
		client.handleError(words[1], words[2]);
	}

}
