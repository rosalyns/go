package server.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private Set<ClientHandler> clients;
	private SortedMap<Integer, String> leaderboard;
	private Map<ClientHandler, ClientHandler> pendingChallenges;
	private List<ClientHandler> randomChallenges;
	private List<GameController> games;
	
	/**
	 * Initializes a lobby at the given server.
	 * @param server that you started this lobby from.
	 */
	public Lobby(GoServer server) {
		this.clients = new HashSet<ClientHandler>();
		this.server = server;
		this.leaderboard = new TreeMap<Integer, String>();
		this.pendingChallenges = new HashMap<ClientHandler, ClientHandler>();
		this.randomChallenges = new ArrayList<ClientHandler>();
		this.games = new ArrayList<GameController>();
	}
	
	/**
	 * When a player entered the lobby it is added to a list of clients. When a player
	 * tries to enter with a name that is already present, it sends an ERROR command.
	 * A new NAME command can then be sent to try again. A new player is initialized for
	 * every client and it is announced in the lobby that a player entered the lobby.
	 * The server sends a NAME command back.
	 * @param client
	 */
	public void addPlayer(ClientHandler client) {
		if (nameInUse(client.getName()) || 
				client.getName().equalsIgnoreCase(RequestCommand.RANDOM)) {
			new ErrorCommand(client, ErrorCommand.INVNAME, 
					"Name cannot be \"Random\" or the same as someone else's .").send();
		} else {
			clients.add(client);
			client.setPlayer(new NetworkPlayer(client.getName()));
			new NameCommand(client, server.getName(), server.getExtensions()).send();
			enter(client);
		}
		
	}
	
	public void enter(ClientHandler client) {
		announceEnter(client.getName());
	}
	
	/**
	 * Remove player from the lobby. This happens when a game is started as well. It is
	 * announced in the lobby that the player has left.
	 * @param client
	 */
	public void removePlayer(ClientHandler client) {
		announceLeave(client.getName());
		clients.remove(client);
	}
	
	/**
	 * Return the leaderboard from this lobby.
	 * @return leaderboard
	 */
	public Map<Integer, String> getLeaderBoard() {
		return leaderboard;
	}
	
	/**
	 * Finds the player with the given name in the lobby. If it is not found a 
	 * PlayerNotFoundException is thrown.
	 * @param playerName that you want to find.
	 * @return ClientHandler of that player
	 * @throws PlayerNotFoundException when player is not found
	 */
	private ClientHandler findPlayer(String playerName) throws PlayerNotFoundException {
		synchronized (clients) {
			for (ClientHandler client : clients) {
				if (client.getName().equalsIgnoreCase(playerName)) {
					return client;
				}
			}
		}
		throw new PlayerNotFoundException();
	}
	
	/**
	 * Is called when a REQUESTGAME command is received. If a random player
	 * is requested, the client is added to the random challenges.
	 * If a specific player is requested, it checks if this in the random
	 * challenges list and then starts a game. If the requested player is
	 * not found it throws a PlayerNotFoundException.
	 * @param challenger client that challanges another player
	 * @param playerName player that the client wants to challenge, can be RANDOM
	 * @throws PlayerNotFoundException when the requested player is not found in the lobby
	 */
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
	
	/**
	 * Sends a CHAT command to all the players in the lobby.
	 * @param name of the client that sent the chat
	 * @param message that the client sent
	 */
	public void chat(String name, String message) {
		synchronized (clients) {
			for (ClientHandler ch : clients) {
				new ChatCommand(ch, name, message).send();
			}
		}
	}
	
	/**
	 * Announce the entering of a player to the lobby.
	 * @param playerName Player that entered the lobby.
	 */
	public void announceEnter(String playerName) {
		chat("[GoServer]", "\"" + playerName + "\" entered the lobby.");
		System.out.println("[GoServer] " + "\"" + playerName + "\" entered the lobby.");
	}
	
	/**
	 * Announce the leaving of a player that left the lobby and prints it on the server TUI.
	 * @param playerName Player that left the lobby.
	 */
	public void announceLeave(String playerName) {
		synchronized (clients) {
			for (ClientHandler ch : clients) {
				if (!ch.getName().equals(playerName)) {
					new ChatCommand(ch, "[GoServer]", 
							"\"" + playerName + "\" left the lobby.").send();
				}
			}
		}
		System.out.println("[GoServer] " + "\"" + playerName + "\" left the lobby.");
	}
	
	/**
	 * Announces the message to all clients in the lobby and prints it on the server TUI.
	 * @param msg you want to announce
	 */
	public void announce(String msg) {
		chat("[GoServer]", msg);
		System.out.println("[GoServer] " + msg);
	}
	
	/**
	 * Checks if there is already a client with that name in this lobby.
	 * @param name to check
	 * @return true if this name is already in use
	 */
	private boolean nameInUse(String name) {
		synchronized (clients) {
			for (ClientHandler client : clients) {
				if (client.getName().equalsIgnoreCase(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns all the players that are available for challenges.
	 * @return a list of available players by name
	 */
	public List<String> getFreePlayers() {
		List<String> playerNames = new ArrayList<String>();
		synchronized (clients) {
			for (ClientHandler client : clients) {
				if (!client.isInGame()) {
					playerNames.add(client.getName());
				}
			}
		}
		return playerNames;
	}
	
	/**
	 * Checks every couple of seconds if a game can be started with players
	 * that have requested random players.
	 */
	public void run() {
		System.out.println("[GoServer] Lobby started...");
		while (this.server.isRunning()) {
			if (randomChallenges.size() > 1) {
				startGame(randomChallenges.remove(0),
						randomChallenges.remove(0));
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Starts a new game with the given clients.
	 * @param player1 client 1
	 * @param player2 client 2
	 */
	public void startGame(ClientHandler player1, ClientHandler player2) {
		announce("Starting a game with players " + 
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
