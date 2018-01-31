package commands;

import java.util.List;
import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import model.Player;
import server.controller.ClientHandler;
import model.Stone;

/**
 * SERVER -> CLIENT
 * Een spel starten. Dit stuur je naar de eerste speler. <br>
 * Format: START aantalspelers (naar speler 1)<br>
 * Format: START aantalspelers kleur bordgrootte speler1 speler2 (3, etc..) 
 * (naar alle spelers)<br>
 * Voorbeeld: START 2 of START 2 BLACK 19 jan piet
 */
public class StartCommand extends Command {
	protected final String commandStr = Protocol.Server.START;
	protected final String black = Protocol.General.BLACK;
	protected final String white = Protocol.General.WHITE;
	private int numberOfPlayers;
	private Stone color;
	private int boardSize;
	private List<Player> players;
	private boolean firstPlayer;
	
	public StartCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}
	
	public StartCommand(ClientHandler clientHandler, int numberOfPlayers) {
		super(clientHandler, true);
		this.numberOfPlayers = numberOfPlayers;
		firstPlayer = true;
	}
	
	public StartCommand(ClientHandler clientHandler, int numberOfPlayers, Stone color, 
			int boardSize, List<Player> players) {
		super(clientHandler, true);
		this.numberOfPlayers = numberOfPlayers;
		this.color = color;
		this.boardSize = boardSize;
		this.players = players;
	}

	public StartCommand(GoClient client) {
		super(client, false);
	}

	@Override
	public String compose() {
		String command = commandStr + delim1 + numberOfPlayers;
		if (!firstPlayer) {
			command += delim1 + color + delim1 + boardSize;
			for (Player player : players) {
				command += delim1 + player.getName();
			}
		}
		return command + commandEnd;
	}

	@Override
	public void parse(String[] words) throws InvalidCommandLengthException {
		if (words.length != 2 && words.length != 6) {
			throw new InvalidCommandLengthException();
		}
		if (words.length == 2) {
			client.askForSettings();
		} else if (words.length == 6) {
			if (words[2].equals(black)) {
				color = Stone.BLACK;
			} else {
				color = Stone.WHITE;
			}
			boardSize = Integer.parseInt(words[3]);
			String opponent = "";
			if (words[4].equals(client.getName())) {
				opponent = words[5];
			} else if (words[5].equals(client.getName())) {
				opponent = words[4];
			}
			client.startGame(opponent, boardSize, color);
		}
	}
}
