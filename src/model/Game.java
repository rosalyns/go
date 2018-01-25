package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import exceptions.*;

public class Game {
	public static final int PASS = -1;
	
	private List<Player> players;
	private int currentPlayer;
	private Board board;
	private int consecutivePasses;
	private Map<Player, Integer> scores;

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
	
	public void doMove(Move move) {
		if (move.getPosition() == PASS) {
			consecutivePasses++;
		} else {
			board.setField(move);
			board.doCaptures(move); 
			consecutivePasses = 0;
		}
	}
	
	public boolean isGameOver() {
		return consecutivePasses == 2;
	}
	
	public void end() {
		
	}
	
	public Map<Player, Integer> calculateScores() {
		board.recalculateGroups(true);
		scores = new HashMap<Player, Integer>(); 
		scores.put(players.get(0), 0);
		scores.put(players.get(1), 0);
		
		List<Set<Integer>> emptyGroups = board.getGroups().get(Stone.EMPTY);
		for (Player p : players) {
			List<Set<Integer>> groups = board.getGroups().get(p.getColor());
			for (Set<Integer> group : groups) {
				scores.put(p, scores.get(p) + group.size());
			}
			for (Set<Integer> emptyGroup : emptyGroups) {
				boolean captured = true;
				Set<Integer> neighbours = board.getNeighbours(emptyGroup);
				for (Integer neighbour : neighbours) {
					if (board.getField(neighbour) != p.getColor()) {
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
	
	public int getBoardDim() {
		return board.dim();
	}
}
