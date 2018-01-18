package server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nedap.go.gui.GOGUI;
import com.nedap.go.gui.GoGUIIntegrator;
import com.nedap.go.gui.InvalidCoordinateException;

import exceptions.*;

public class Game {
	public static final int PASS = -1;
	
	private GOGUI gogui;
	private List<Player> players;
	private int currentPlayer;
	private Board board;
	private int consecutivePasses;
	private Map<Player, Integer> scores;

	//first player in list must be Mark.BLACK
	public Game(List<Player> players, int boardSize) throws InvalidBoardSizeException {
		this.gogui = new GoGUIIntegrator(false, true, 9);
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
		gogui.startGUI();
		while (!isGameOver()) {
			Move move = players.get(currentPlayer).determineMove(board); //zend move naar clients
			if (move.getPosition() == PASS) {
				consecutivePasses++;
			} else {
				board.setField(move);
				try {
					gogui.addStone(board.indexToCoordinates(move.getPosition()).y, 
							board.indexToCoordinates(move.getPosition()).x, 
							move.getColor() == Stone.WHITE);
				} catch (InvalidCoordinateException e) {
					e.printStackTrace();
				}
				consecutivePasses = 0;
				doCaptures(move); 
			}
			currentPlayer = (currentPlayer + 1) % players.size();
		}
	}
	
	public boolean isGameOver() {
		return consecutivePasses == 2;
	}
	
	public void doCaptures(Move move) {
		Stone playerColor = move.getColor();
		Stone opponentColor = playerColor.other();
		List<Set<Integer>> groupsToRemove = new ArrayList<Set<Integer>>();
		for (Set<Integer> group : board.getGroups().get(opponentColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(group, opponentColor);
		}
		
		for (Set<Integer> group : board.getGroups().get(playerColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(group, opponentColor);
		}
	}
	
	public void removeGroup(Set<Integer> group, Stone color) {
		for (Integer field : group) {
			board.setField(new Move(Stone.EMPTY, field));
			try {
				gogui.removeStone(board.indexToCoordinates(field).y, 
						board.indexToCoordinates(field).x);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		}
		board.getGroups().get(color).remove(group);
	}
	
	public void calculateScores() {
		// doe boolean bij recalculate groups waar ook group voor Stone.EMPTY berekend wordt. Als buur = zwart, scoreZwart += group.size() 
		int scoreBlack = 0;
		int scoreWhite = 0;
		board.recalculateGroups(true);
		
		for (Player p : players) {
			List<Set<Integer>> groups = board.getGroups().get(p.getColor());
			//check of kleur buur is van de groep -> dan bij de kleur optellen
			
		}
		
	}
	
	
	
	
	

}
