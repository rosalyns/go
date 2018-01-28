package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import exceptions.*;

public class Game {
	public static final int PASS = -1;
	public static final int NO_OF_PLAYERS = 2;
	
	private List<Player> players;
	private Board gameBoard;
	private int consecutivePasses;
	private Map<Player, Integer> scores;
	private List<Move> moves;
	private int currentPlayerIndex;

	//first player in list must be Stone.BLACK
	public Game(List<Player> players, int boardSize) throws InvalidBoardSizeException {
		this.consecutivePasses = 0;
		this.currentPlayerIndex = 0;
		this.players = players;
		this.moves = new ArrayList<Move>();
		if (boardSize != 9 && boardSize != 13 && boardSize != 19) {
			throw new InvalidBoardSizeException(boardSize);
		} else { 
			this.gameBoard = new Board(boardSize);
		}
	}
	
	public void doTurn(Move move) throws KoException, NotYourTurnException {
		if (move.getColor() != players.get(currentPlayerIndex).getColor()) {
			throw new NotYourTurnException("It's " + players.get(currentPlayerIndex).getName() 
					+ "'s turn.");
		}
		if (move.getPosition() == PASS) {
			consecutivePasses++;
		} else {
			if (recreatesPreviousSituation(move)) {
				throw new KoException("This move recreates a previous board situation.");
			} else {
				placeStone(gameBoard, move); 
				consecutivePasses = 0;
				moves.add(move);
			}
		}
		currentPlayerIndex = (currentPlayerIndex + 1) % NO_OF_PLAYERS;
	}

	private void placeStone(Board board, Move move) {
		board.setField(move);
		doCaptures(board, move);
	}
	
	public boolean isGameOver() {
		return consecutivePasses == 2;
	}
	
	public void end() {
		
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
		Board simulationBoard = new Board(gameBoard.dim());
		
		placeStone(copiedBoard, move);
		
		for (Move m : moves) {
			placeStone(simulationBoard, m);
			
			if (simulationBoard.equals(copiedBoard)) {
				return true;
			}
		}
		return false;
	}
	
	public String getFirstPlayer() {
		return players.get(0).getName();
	}
	
	public String getCurrentPlayer() {
		return players.get(currentPlayerIndex).getName();
	}
	
	public int getBoardDim() {
		return gameBoard.dim();
	}
}
