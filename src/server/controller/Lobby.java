package server.controller;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Lobby extends Thread {
	private List<ClientHandler> players;
	
	public Lobby() {
		players = new ArrayList<ClientHandler>();
	}
	
	public void addPlayer(ClientHandler player) {
		players.add(player);
		System.out.println(thisMoment() + "\"" + player.getName() + "\" entered the lobby.");
	}

	public String thisMoment() {
		return "[" + LocalDateTime.now() + "] ";
	}
	
	public void run() {
		System.out.println(thisMoment() + "Lobby started...");
	}

}
