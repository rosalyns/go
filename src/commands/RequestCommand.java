package commands;


import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import exceptions.PlayerNotFoundException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER
 * Sturen als je een spel wilt spelen. De eerste keer en als een spel afgelopen is opnieuw.
 * Als je de Challenge extensie niet ondersteunt, stuur dan RANDOM in plaats van een naam.
 * <br>
 * Format: REQUESTGAME aantalspelers naamtegenspeler (RANDOM als je geen challenge doet)<br>
 * Voorbeeld: REQUESTGAME 2 RANDOM of REQUESTGAME 2 piet
 */

/**
 * SERVER -> CLIENT
 * Stuurt aan één client wie hem heeft uitgedaagd.<br>
 * Format: REQUESTGAME uitdager<br>
 * Voorbeeld: REQUESTGAME piet
 */
public class RequestCommand extends Command {
	protected final String commandStr = Protocol.Client.REQUESTGAME;
	public static final String RANDOM = Protocol.Client.RANDOM;
	private int numberOfPlayers;
	//only challengee can be RANDOM
	private String challengee;
	private String challenger;
	
	public RequestCommand(GoClient client) {
		super(client, true);
	}
	
	public RequestCommand(GoClient client, int numberOfPlayers, String challengee) {
		super(client, false);
		this.numberOfPlayers = numberOfPlayers;
		this.challengee = challengee;
	}
	
	public RequestCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}
	
	public RequestCommand(ClientHandler clientHandler, String challenger) {
		super(clientHandler, true);
		this.challenger = challenger;
	}
	
	public String compose() {
		return commandStr + delim1
				+ (toClient ? challenger : numberOfPlayers + delim1 + challengee) 
				+ commandEnd;
	}

	@Override
	public void parse(String[] words) throws InvalidCommandLengthException {
		if (toClient) {
			if (words.length != 2) {
				throw new InvalidCommandLengthException();
			}
			client.challengedBy(words[1]);
		} else {
			if (words.length != 3) {
				throw new InvalidCommandLengthException();
			}
			
			try {
				clientHandler.challenge(Integer.parseInt(words[1]), words[2]);
			} catch (PlayerNotFoundException e) {
				new ErrorCommand(clientHandler, ErrorCommand.INVCOMMAND, 
						"This player is not in the lobby.").send();
			}
		}
	}
}
