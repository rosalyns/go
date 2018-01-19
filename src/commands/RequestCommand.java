package commands;


import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;

public class RequestCommand extends Command {
	protected final String commandStr = Protocol.Client.REQUESTGAME;
	private int numberOfPlayers;
	private String challengee;
	
	public RequestCommand(GoClient client, int numberOfPlayers, String challengee) {
		super(client);
		this.numberOfPlayers = numberOfPlayers;
		this.challengee = challengee;
	}
	
	public RequestCommand(GoClient client) {
		super(client);
	}
	
	public String compose() {
		String gameRequest = commandStr;
		gameRequest += Protocol.General.DELIMITER1 + numberOfPlayers;
		gameRequest += Protocol.General.DELIMITER1 + challengee;
		gameRequest += Protocol.General.COMMAND_END;
		return gameRequest;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
		
	}

}
