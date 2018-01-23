package server.controller;

import java.util.List;


import exceptions.InvalidBoardSizeException;
import server.model.*;

public class GameController extends Thread {
	private Game game; 
	private GoServer server;
	private List<Player> players;
	
	public GameController(GoServer server, List<Player> players) {
		this.server = server;
		this.players = players;
		
		
		try {
			game = new Game(this.players, 9);
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		game.start();
	}
	


	
}
