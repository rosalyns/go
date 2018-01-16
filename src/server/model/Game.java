package server.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
	public static final int PASS = -1;
	
	private List<Player> players;
	private int currentPlayer;
	private Board board;

	//first player in list must be Mark.BLACK
	public Game(List<Player> players, int boardSize) throws InvalidBoardSizeException {
		this.currentPlayer = 0;
		this.players = players;
		if (boardSize < 9 || boardSize > 19) {
			throw new InvalidBoardSizeException();
		} else { 
			this.board = new Board(boardSize);
		}
	}
	
	public void start() {
		while (!gameOver()) {
			//GUI update
			int move = players.get(currentPlayer).determineMove(board); //zend move naar clients
			board.checkForCaptures();
			currentPlayer = (currentPlayer + 1) % players.size();
		}
	}

}
