package server.controller;

import java.util.ArrayList;
import java.util.List;


import exceptions.InvalidBoardSizeException;
import server.model.*;

public class GameController extends Thread {
	private Game game; 
	
	public GameController(ClientHandler player1, ClientHandler player2) {
		Player player1 = new NetworkPlayer()
		List<Player> players = new ArrayList<Player>();
		players.add(hPlayer1);
		players.add(hPlayer2);
		try {
			game = new Game(players, 9);
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		game.start();
	}
	
	public void run() {
		
	}

	
}
