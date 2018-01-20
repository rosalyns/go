package commands;


import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * Sturen als je een spel wilt spelen. De eerste keer en als een spel afgelopen is opnieuw.
 * Als je de Challenge extensie niet ondersteunt, stuur dan RANDOM in plaats van een naam.
 * <br>
 * Format: REQUESTGAME aantalspelers naamtegenspeler (RANDOM als je geen challenge doet)<br>
 * Voorbeeld: REQUESTGAME 2 RANDOM of REQUESTGAME 2 piet
 */
public class RequestCommand extends Command {
	protected final String commandStr = Protocol.Client.REQUESTGAME;
	protected final String randomPlayer = Protocol.Client.RANDOM;
	private int numberOfPlayers;
	//only challengee can be RANDOM
	private String challengee;
	private String challenger;
	
	public RequestCommand(GoClient client, int numberOfPlayers, String challengee) {
		super(client);
		this.numberOfPlayers = numberOfPlayers;
		this.challengee = challengee;
	}
	
	public RequestCommand(GoClient client) {
		super(client);
	}
	
	public RequestCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public RequestCommand(ClientHandler clientHandler, String challenger) {
		super(clientHandler);
		this.challenger = challenger;
	}
	
	public String compose(boolean toClient) {
		String command = commandStr;
		if (toClient) {
			command += delim1 + challenger;
		} else {
			command += delim1 + numberOfPlayers;
			command += delim1 + challengee;
		}
		command += commandEnd;
		return command;
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
	}

}
