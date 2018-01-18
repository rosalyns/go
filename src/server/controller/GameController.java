package server.controller;

import java.util.ArrayList;
import java.util.List;


import exceptions.InvalidBoardSizeException;
import server.model.*;

public class GameController extends Thread {
	private Game game; 
	
	public GameController() {
		HumanPlayer hPlayer1 = new HumanPlayer(Stone.BLACK, "Rosalyn");
		ComputerPlayer cPlayer2 = new ComputerPlayer(Stone.WHITE, new RandomStrategy());
		List<Player> players = new ArrayList<Player>();
		players.add(hPlayer1);
		players.add(cPlayer2);
		try {
			game = new Game(players, 9);
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		game.start();
	}
	
	public static void main(String[] args) {
		GameController gc = new GameController();
	}

	
}
