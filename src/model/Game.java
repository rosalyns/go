package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import exceptions.*;

public class Game {
	public static final int NO_OF_PLAYERS = 2;
	
	private List<Player> players;
	private Board gameBoard;
	private int consecutivePasses;
	private Map<Player, Integer> scores;
	private List<Move> moves;
	private int currentPlayerIndex;
	private int blackStonesLeft;
	private int whiteStonesLeft;

	public Game(List<Player> players, Board board) throws InvalidBoardSizeException {
		this.gameBoard = board;
		this.consecutivePasses = 0;
		this.players = players;
		this.moves = new ArrayList<Move>();
		
		if (players.get(0).getColor() == Stone.BLACK) {
			this.currentPlayerIndex = 0;
		} else {
			this.currentPlayerIndex = 1;
		}
		
		int totalStones = gameBoard.dim() * gameBoard.dim(); 
		if (totalStones % 2 == 0) {
			blackStonesLeft = totalStones / 2;
			whiteStonesLeft = totalStones / 2;
		} else {
			whiteStonesLeft = totalStones / 2;
			blackStonesLeft = totalStones - whiteStonesLeft;
		}
		
	}
	
	public void doTurn(Move move) throws KoException, NotYourTurnException, 
																		InvalidCoordinateException {
		if (move.getColor() != players.get(currentPlayerIndex).getColor()) {
			throw new NotYourTurnException("It's " + players.get(currentPlayerIndex).getName() 
					+ "'s turn.");
		}
		
		if (move.getPosition() == Move.PASS) {
			consecutivePasses++;
		} else if (!gameBoard.isField(move.getPosition()) 
				&& !gameBoard.isEmptyField(move.getPosition())) {
			throw new InvalidCoordinateException(move.getPosition() + " is not a valid coordinate");
		} else if (recreatesPreviousSituation(move)) {
			throw new KoException("This move recreates a previous board situation.");
		} else {
			placeStone(gameBoard, move); 
			reduceStone(move.getColor());
			consecutivePasses = 0;
			moves.add(move);
		}
		
		currentPlayerIndex = (currentPlayerIndex + 1) % NO_OF_PLAYERS;
	}

	private void placeStone(Board board, Move move) {
		board.setField(move);
		doCaptures(board, move);
	}
	
	private void reduceStone(Stone color) {
		if (color == Stone.BLACK) {
			blackStonesLeft--;
		} else if (color == Stone.WHITE) {
			whiteStonesLeft--;
		}
	}	
	
	public boolean isGameOver() {
		return consecutivePasses == 2 || blackStonesLeft == 0 || whiteStonesLeft == 0;
	}
	
	public void doCaptures(Board board, Move move) {
		Stone playerColor = move.getColor();
		Stone opponentColor = playerColor.other();
		List<Set<Integer>> groupsToRemove = new ArrayList<Set<Integer>>();
		for (Set<Integer> group : board.getGroups().get(opponentColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(board, group, opponentColor);
		}
		
		for (Set<Integer> group : board.getGroups().get(playerColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(board, group, opponentColor);
		}
	}
	
	private void removeGroup(Board board, Set<Integer> group, Stone color) {
		for (Integer field : group) {
			board.setField(new Move(Stone.EMPTY, field));
		}
		board.getGroups().get(color).remove(group);
	}
	
	public Map<Player, Integer> calculateScores() {
		gameBoard.recalculateGroups(true);
		scores = new HashMap<Player, Integer>(); 
		scores.put(players.get(0), 0);
		scores.put(players.get(1), 0);
		
		if (gameBoard.isEmpty()) {
			return scores;
		}
		
		List<Set<Integer>> emptyGroups = gameBoard.getGroups().get(Stone.EMPTY);
		for (Player p : players) {
			List<Set<Integer>> groups = gameBoard.getGroups().get(p.getColor());
			for (Set<Integer> group : groups) {
				scores.put(p, scores.get(p) + group.size());
			}
			for (Set<Integer> emptyGroup : emptyGroups) {
				boolean captured = true;
				Set<Integer> neighbours = gameBoard.getNeighbours(emptyGroup);
				for (Integer neighbour : neighbours) {
					if (gameBoard.getField(neighbour) != p.getColor()) {
						captured = false;
					}
				}
				if (captured) {
					scores.put(p, scores.get(p) + emptyGroup.size());
				}
			}
		}
		return scores;
	}
	
	public boolean recreatesPreviousSituation(Move move) {
		Board copiedBoard = gameBoard.deepCopy();
		Board simulationBoard = null;
		try {
			simulationBoard = new Board(gameBoard.dim());
		} catch (InvalidBoardSizeException e) {
			// not possible
		}
		
		placeStone(copiedBoard, move);
		
		for (Move m : moves) {
			placeStone(simulationBoard, m);
			
			if (simulationBoard.equals(copiedBoard)) {
				return true;
			}
		}
		return false;
	}
	
	public String getCurrentPlayer() {
		return players.get(currentPlayerIndex).getName();
	}
	
	public int getBoardDim() {
		return gameBoard.dim();
	}
	
}
