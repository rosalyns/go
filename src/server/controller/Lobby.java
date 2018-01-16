package server.controller;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Lobby extends Thread {
	private GoServer server;
	private List<ClientHandler> players;
	
	public Lobby(GoServer server) {
		this.players = new ArrayList<ClientHandler>();
		this.server = server;
	}
	
	public void addPlayer(ClientHandler player) {
		this.players.add(player);
		System.out.println(thisMoment() + "\"" + player.getName() + "\" entered the lobby.");
	}
	
	public void removePlayer(ClientHandler player) {
		this.players.remove(player);
	}

	public String thisMoment() {
		return "[" + LocalDateTime.now() + "] ";
	}
	
	public void run() {
		System.out.println(thisMoment() + "Lobby started...");
		
		while (this.server.isRunning()) {
			if (players.size() > 1) {
				startGame(players.remove(0), players.remove(0));
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startGame(ClientHandler player1, ClientHandler player2) {
		System.out.println(thisMoment() + "Starting a game with players " + 
				player1.getName() + " and " + 
				player2.getName());
		GameController game = new GameController();
		game.start();
	}

}
