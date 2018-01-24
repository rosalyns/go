package commands;

import java.util.List;
import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;
import client.model.Stone;

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
	private List<String> players;
	private boolean firstPlayer;
	
	public StartCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public StartCommand(ClientHandler clientHandler, int numberOfPlayers) {
		super(clientHandler);
		this.numberOfPlayers = numberOfPlayers;
		firstPlayer = true;
		this.toClient = true;
	}
	
	public StartCommand(ClientHandler clientHandler, int numberOfPlayers, Stone color, 
			int boardSize, List<String> players) {
		super(clientHandler);
		this.numberOfPlayers = numberOfPlayers;
		this.color = color;
		this.boardSize = boardSize;
		this.players = players;
		this.toClient = true;
	}

	public StartCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose() {
		String command = commandStr + delim1 + numberOfPlayers;
		if (!firstPlayer) {
			command += delim1 + color + delim1 + boardSize;
			for (String player : players) {
				command += delim1 + player;
			}
		}
		return command + commandEnd;
	}

	@Override
	public void parse(String command) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 2 && words.length != 6) {
			throw new InvalidCommandLengthException();
		}
		if (words.length == 2) {
			client.askForSettings();
		} else if (words.length == 6) {
			boardSize = Integer.parseInt(words[2]);
			if (words[3].equals(black)) {
				color = Stone.BLACK;
			} else {
				color = Stone.WHITE;
			}
			client.startGame(words[1], boardSize, color);
		}
	}
}
