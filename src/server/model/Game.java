package server.model;

import java.util.List;
import exceptions.*;

public class Game {
	public static final int PASS = -1;
	
	private List<Player> players;
	private int currentPlayer;
	private Board board;
	private int consecutivePasses;

	//first player in list must be Mark.BLACK
	public Game(List<Player> players, int boardSize) throws InvalidBoardSizeException {
		this.currentPlayer = 0;
		this.consecutivePasses = 0;
		this.players = players;
		if (boardSize < 9 || boardSize > 19) {
			throw new InvalidBoardSizeException();
		} else { 
			this.board = new Board(boardSize);
		}
	}
	
	public void start() {
		while (!isGameOver()) {
			//GUI update
			int move = players.get(currentPlayer).determineMove(board); //zend move naar clients
			//board.checkForCaptures();
			if (move == PASS) {
				consecutivePasses++;
			} else {
				consecutivePasses = 0;
			}
			
			currentPlayer = (currentPlayer + 1) % players.size();
		}
	}
	
	public boolean isGameOver() {
		return consecutivePasses == 2;
	}

}
