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
import commands.RequestCommand;
import exceptions.PlayerNotFoundException;
import general.Extension;
import general.Protocol;
import server.model.NetworkPlayer;
import server.model.Player;
import server.model.Stone;

public class Lobby extends Thread {
	public final boolean toClient = true;
	public final boolean fromClient = false;
	
	private GoServer server;
	private List<ClientHandler> clients;
	private SortedMap<Integer, String> leaderboard;
	private Map<ClientHandler, ClientHandler> pendingChallenges;
	private List<ClientHandler> randomChallenges;
	
	public Lobby(GoServer server) {
		this.clients = new ArrayList<ClientHandler>();
		this.server = server;
		this.leaderboard = new TreeMap<Integer, String>();
		this.pendingChallenges = new HashMap<ClientHandler, ClientHandler>();
		this.randomChallenges = new ArrayList<ClientHandler>();
	}
	
	public void addPlayer(ClientHandler player) {
		this.clients.add(player);
	}
	
	public void removePlayer(ClientHandler player) {
		this.clients.remove(player);
	}

	private String thisMoment() {
		return "[" + LocalDateTime.now() + "] ";
	}
	
	public Map<Integer, String> getLeaderBoard() {
		return leaderboard;
	}
	
	private ClientHandler findPlayer(String playerName) throws PlayerNotFoundException {
		for (ClientHandler client : clients) {
			if (client.getName().equals(playerName)) {
				return client;
			}
		}
		throw new PlayerNotFoundException();
	}
	
	public void challenge(ClientHandler challenger, String playerName) 
			throws PlayerNotFoundException {
		/* if challenged player doesn't support challenges && has said RANDOM, start game.
		   if challenged player doesn't support challenges && has not said RANDOM, decline game.
		   if challenged player supports challenges, send REQUESTGAME command to him
		   if playerName == RANDOM, put in randomChallenges
		ClientHandler challengee = findPlayer(playerName);
		pendingChallenges.put(challenger, challengee);
		new RequestCommand(challengee, challenger.getName()).send(toClient);
		*/
		if (playerName.equals(Protocol.Client.RANDOM)) {
			randomChallenges.add(challenger);
		} else {
			ClientHandler challengee = findPlayer(playerName);
			if (challengee.getExtensions().contains(Extension.CHALLENGE)) {
				new RequestCommand(challengee, challenger.getName()).send(toClient);
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
			new ChatCommand(ch, name, message).send(toClient);
		}
	}
	
	public void announce(String playerName) {
		chat("[Lobby]", "\"" + playerName + "\" entered the lobby.");
		System.out.println(thisMoment() + "\"" + playerName + "\" entered the lobby.");
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
		List<Player> players = new ArrayList<Player>();
		NetworkPlayer x = new NetworkPlayer(player1, Stone.BLACK, "noname");
		NetworkPlayer y = new NetworkPlayer(player2, Stone.WHITE, "noname2");
		players.add(x);
		players.add(y);
		GameController game = new GameController(server, players);
		game.start();
	}

}
