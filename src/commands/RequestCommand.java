package commands;

import java.util.List;

import client.controller.GoClient;
import general.Protocol;

public class RequestCommand extends ClientCommand {
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

	public static void execute() {
		// TODO Auto-generated method stub
		
	}

	public static List<Object> parse(String command) {
		return null;
	}
	
	public String compose() {
		String gameRequest = commandStr;
		gameRequest += Protocol.General.DELIMITER1 + numberOfPlayers;
		gameRequest += Protocol.General.DELIMITER1 + challengee;
		gameRequest += Protocol.General.COMMAND_END;
		return gameRequest;
	}

}
