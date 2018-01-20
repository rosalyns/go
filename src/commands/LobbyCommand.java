package commands;

import java.util.List;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * Reactie op LOBBY van de client. Stuurt alle spelers die uitgedaagd kunnen worden 
 * (in de lobby zitten).<br>
 * Format: LOBBY naam1_naam2_naam3<br>
 * Voorbeeld: LOBBY piet jan koos
 */
public class LobbyCommand extends Command {
	protected final String commandStr = Protocol.Server.LOBBY;
	private List<String> availablePlayers;
	
	public LobbyCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	public LobbyCommand(ClientHandler clientHandler, List<String> players) {
		super(clientHandler);
		this.availablePlayers = players;
	}
	
	public LobbyCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose(boolean toClient) {
		String command = commandStr;
		if (toClient) {
			command += delim1;
			for (String player : availablePlayers) {
				command += player;
				if (player == availablePlayers.get(availablePlayers.size() - 1)) {
					command += delim2;
				}
			}
		}
		command += commandEnd;
		return command;
	}

	@Override
	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
		
	}

}
