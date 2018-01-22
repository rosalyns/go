package server.controller;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import commands.ChatCommand;
import exceptions.PlayerNotFoundException;

public class Lobby extends Thread {
	public final boolean toClient = true;
	public final boolean fromClient = false;
	
	private GoServer server;
	private List<ClientHandler> clients;
	private SortedMap<Integer, String> leaderboard;
	
	public Lobby(GoServer server) {
		this.clients = new ArrayList<ClientHandler>();
		this.server = server;
		this.leaderboard = new TreeMap<Integer, String>();
	}
	
	public void addPlayer(ClientHandler player) {
		this.clients.add(player);
		System.out.println(thisMoment() + "\"" + player.getName() + "\" entered the lobby.");
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
	
	public ClientHandler findPlayer(String playername) throws PlayerNotFoundException {
		for (ClientHandler client : clients) {
			if (client.getName().equals(playername)) {
				return client;
			}
		}
		throw new PlayerNotFoundException();
	}
	
	public void chat(String name, String message) {
		for (ClientHandler ch : clients) {
			new ChatCommand(ch, name, message).send(toClient);
		}
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
			if (clients.size() > 1) {
				startGame(clients.remove(0), clients.remove(0));
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
		GameController game = new GameController(player1, player2);
		game.start();
	}

}
