package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import exceptions.*;

public class Game {
	public static final int PASS = -1;
	
	private List<Player> players;
	private Board board;
	private int consecutivePasses;
	private Map<Player, Integer> scores;

	//first player in list must be Stone.BLACK
	public Game(List<Player> players, int boardSize) throws InvalidBoardSizeException {
		this.consecutivePasses = 0;
		this.players = players;
		if (boardSize != 9 && boardSize != 13 && boardSize != 19) {
			throw new InvalidBoardSizeException(boardSize);
		} else { 
			this.board = new Board(boardSize);
		}
	}
	
	public void doMove(Move move) {
		if (move.getPosition() == PASS) {
			consecutivePasses++;
		} else {
			board.setField(move);
			doCaptures(move); 
			consecutivePasses = 0;
		}
	}
	
	public boolean isGameOver() {
		return consecutivePasses == 2;
	}
	
	public void end() {
		
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
	
	private void removeGroup(Set<Integer> group, Stone color) {
		for (Integer field : group) {
			board.setField(new Move(Stone.EMPTY, field));
		}
		board.getGroups().get(color).remove(group);
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
