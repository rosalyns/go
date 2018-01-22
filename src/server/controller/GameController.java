package server.controller;

import java.util.ArrayList;
import java.util.List;


import exceptions.InvalidBoardSizeException;
import server.model.*;

public class GameController extends Thread {
	private Game game; 
	private GoServer server;
	private List<ClientHandler> players;
	
	public GameController(GoServer server, List<ClientHandler> players) {
		this.server = server;
		this.players = players;
		
		HumanPlayer hPlayer1 = new HumanPlayer(Stone.BLACK, "Rosalyn");
		ComputerPlayer cPlayer2 = new ComputerPlayer(Stone.WHITE, new RandomStrategy()); 
		HumanPlayer hPlayer2 = new HumanPlayer(Stone.WHITE, "Pietje");
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
	


	
}
