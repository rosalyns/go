package commands;

import java.util.ArrayList;
import java.util.List;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER
 * Om op te vragen wie je allemaal kan uitdagen.<br>
 * Format: LOBBY<br>
 * Voorbeeld: LOBBY
 */

/**
 * SERVER -> CLIENT
 * Reactie op LOBBY van de client. Stuurt alle spelers die uitgedaagd kunnen worden 
 * (in de lobby zitten).<br>
 * Format: LOBBY naam1_naam2_naam3<br>
 * Voorbeeld: LOBBY piet jan koos
 */
public class LobbyCommand extends Command {
	protected final String commandStr = Protocol.Server.LOBBY;
	private List<String> availablePlayers;
	
	public LobbyCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	public LobbyCommand(ClientHandler clientHandler, List<String> players) {
		super(clientHandler, true);
		this.availablePlayers = players;
	}
	
	public LobbyCommand(GoClient client) {
		super(client, true);
	}
	
	public LobbyCommand(GoClient client, boolean toClient) {
		super(client, false);
	}

	@Override
	public String compose() {
		String command = commandStr;
		if (toClient && !availablePlayers.isEmpty()) {
			command += delim1;
			for (String player : availablePlayers) {
				command += player;
				if (!(player == availablePlayers.get(availablePlayers.size() - 1))) {
					command += delim2;
				}
			}
		}
		return command + commandEnd;
	}

	@Override
	public void parse(String[] words) throws InvalidCommandLengthException {
		if (toClient) {
			if (words.length != 1 && words.length != 2) {
				throw new InvalidCommandLengthException();
			}
			
			availablePlayers = new ArrayList<String>();
			if (words.length == 2) {
				String[] players = words[1].split(delim2);
				for (String player : players) {
					availablePlayers.add(player);
				}
			}
			client.showPlayersInLobby(availablePlayers);
		} else {
			new LobbyCommand(clientHandler, clientHandler.getPlayersInLobby()).send();
		}
		
	}

}
