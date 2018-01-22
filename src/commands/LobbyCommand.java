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
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (toClient) {
			if (words.length != 2) {
				throw new InvalidCommandLengthException();
			}
			availablePlayers = new ArrayList<String>();
			String[] players = words[1].split(delim2);
			for (String player : players) {
				availablePlayers.add(player);
			}
			client.showPlayersInLobby(availablePlayers);
		} else {
			new LobbyCommand(clientHandler, clientHandler.getPlayersInLobby());
		}
		
	}

}
