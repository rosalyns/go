package server.controller;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import commands.ChatCommand;
import commands.ErrorCommand;
import commands.NameCommand;
import commands.RequestCommand;
import exceptions.PlayerNotFoundException;
import general.Extension;
import general.Protocol;
import model.NetworkPlayer;

public class Lobby extends Thread {
	
	private GoServer server;
	private List<ClientHandler> clients;
	private SortedMap<Integer, String> leaderboard;
	private Map<ClientHandler, ClientHandler> pendingChallenges;
	private List<ClientHandler> randomChallenges;
	private List<GameController> games;
	
	public Lobby(GoServer server) {
		this.clients = new ArrayList<ClientHandler>();
		this.server = server;
		this.leaderboard = new TreeMap<Integer, String>();
		this.pendingChallenges = new HashMap<ClientHandler, ClientHandler>();
		this.randomChallenges = new ArrayList<ClientHandler>();
		this.games = new ArrayList<GameController>();
	}
	
	public void addPlayer(ClientHandler client) {
		if (nameInUse(client.getName()) || 
				client.getName().equalsIgnoreCase(RequestCommand.RANDOM)) {
			new ErrorCommand(client, ErrorCommand.INVNAME, 
					"Name cannot be \"Random\" or the same as someone else's .").send();
		} else {
			client.setPlayer(new NetworkPlayer(client, client.getName()));
			this.clients.add(client);
			announceEnter(client.getName());
			new NameCommand(client, server.getName(), server.getExtensions()).send();
		}
		
	}
	
	public void removePlayer(ClientHandler client) {
		clients.remove(client);
		announceLeave(client.getName());
	}

	private String thisMoment() {
		return "[" + LocalDateTime.now() + "] ";
	}
	
	public Map<Integer, String> getLeaderBoard() {
		return leaderboard;
	}
	
	private ClientHandler findPlayer(String playerName) throws PlayerNotFoundException {
		for (ClientHandler client : clients) {
			if (client.getName().equalsIgnoreCase(playerName)) {
				return client;
			}
		}
		throw new PlayerNotFoundException();
	}
	
	public void challenge(ClientHandler challenger, String playerName) 
			throws PlayerNotFoundException {
		if (playerName.equalsIgnoreCase(Protocol.Client.RANDOM)) {
			randomChallenges.add(challenger);
		} else {
			ClientHandler challengee = findPlayer(playerName);
			if (challengee.getExtensions().contains(Extension.CHALLENGE)) {
				new RequestCommand(challengee, challenger.getName()).send();
				pendingChallenges.put(challenger, challengee);
			} else if (randomChallenges.contains(challengee)) {
				randomChallenges.remove(challengee);
				startGame(challenger, challengee);
			} else {
				new ErrorCommand(challenger, ErrorCommand.OTHER, 
						"The player you challenged can't play a game right now.");
			}
		}
	}
	
	public void chat(String name, String message) {
		for (ClientHandler ch : clients) {
			new ChatCommand(ch, name, message).send();
		}
	}
	
	public void announceEnter(String playerName) {
		chat("[Lobby]", "\"" + playerName + "\" entered the lobby.");
		System.out.println(thisMoment() + "\"" + playerName + "\" entered the lobby.");
	}
	
	public void announceLeave(String playerName) {
		chat("[Lobby]", "\"" + playerName + "\" left the lobby.");
		System.out.println(thisMoment() + "\"" + playerName + "\" left the lobby.");
	}
	
	private boolean nameInUse(String name) {
		for (ClientHandler client : clients) {
			if (client.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getFreePlayers() {
		List<String> playerNames = new ArrayList<String>();
		for (ClientHandler client : clients) {
			playerNames.add(client.getName());
		}
		return playerNames;
	}
	
	public void run() {
		System.out.println(thisMoment() + "Lobby started...");
		while (this.server.isRunning()) {
			if (randomChallenges.size() > 1) {
				startGame(randomChallenges.remove(0), randomChallenges.remove(0));
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startGame(ClientHandler player1, ClientHandler player2) {
		System.out.println(thisMoment() + "Starting a game with players " + 
				player1.getName() + " and " + 
				player2.getName());
		List<ClientHandler> players = new ArrayList<ClientHandler>();
		players.add(player1);
		players.add(player2);
		GameController game = new GameController(this, players);
		games.add(game);
		game.start();
	}

}
