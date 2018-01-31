package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.nedap.go.gui.InvalidCoordinateException;

import exceptions.*;

public class Game extends Observable {
	public static final int NO_OF_PLAYERS = 2;
	
	private List<Player> players;
	private Board gameBoard;
	private int consecutivePasses;
	private Map<Player, Integer> scores;
	private List<Move> moves;
	private int currentPlayerIndex;
	private int blackStonesLeft;
	private int whiteStonesLeft;
	private boolean playerQuit;

	/**
	 * Initializes a new game object with the given players and board. The first
	 * player is determined by finding the player that uses black stones. 
	 * Both players are given half of the total stones (number of fields on the board)
	 * where black has the advantage if the number is odd.
	 * @param players that want to play the game. Should be of size 2
	 * @param board that you want to play the game on. Will be cleared before using.
	 */
	public Game(List<Player> players, Board board) {
		this.gameBoard = board;
		this.consecutivePasses = 0;
		this.players = players;
		this.playerQuit = false;
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
		
		this.gameBoard.clear();
	}
	
	
	
	public void tryTurn(Move move) throws KoException, NotYourTurnException, 
		InvalidCoordinateException {
		
		if (ended()) {
			return;
		} else if (move.getColor() != players.get(currentPlayerIndex).getColor()) {
			throw new NotYourTurnException("It's " + players.get(currentPlayerIndex).getName() 
					+ "'s turn.");
		}
		
		if (move.getPosition() == Move.PASS) {
			return;
		} else if (!gameBoard.isField(move.getPosition()) 
				|| !gameBoard.isEmptyField(move.getPosition())) {
			throw new InvalidCoordinateException(move.getPosition() + " is not a valid coordinate");
		} else if (recreatesPreviousSituation(move)) {
			throw new KoException("This move recreates a previous board situation.");
		} 
	}
	
	/**
	 * Checks if the move doesn't violate any game rules or exceeds the board size. If
	 * the move is valid it places the stone on the board and reduces the stones of that
	 * player by one.
	 * @param move Move the player wants to make
	 * @throws KoException if performing this move results in a violation of the Ko Rule
	 * @throws NotYourTurnException if it's not this players turn
	 * @throws InvalidCoordinateException if an invalid coordinate is passed in the move
	 */
	public void doTurn(Move move) {
		if (move.getPosition() == Move.PASS) {
			consecutivePasses++;
		} else {
			placeStone(gameBoard, move); 
			reduceStone(move.getColor());
			consecutivePasses = 0;
			moves.add(move);
		}
		
		currentPlayerIndex = (currentPlayerIndex + 1) % NO_OF_PLAYERS;
	}

	/**
	 * Performs the move on the given board and if the move results in any captured groups, it will
	 * remove these groups from the board.
	 * @param board Board you want to place a stone on
	 * @param move Move you want to perform
	 */
	private void placeStone(Board board, Move move) {
		board.setField(move);
		doCaptures(board, move);
		setChanged();
		notifyObservers(move);
	}
	
	/**
	 * Reduces the stone count of the given color by one.
	 * @param color Color that the count is reduced of
	 */
	private void reduceStone(Stone color) {
		if (color == Stone.BLACK) {
			blackStonesLeft--;
		} else if (color == Stone.WHITE) {
			whiteStonesLeft--;
		}
	}	
	
	/**
	 * Returns if the game is game over (two consecutive passes happened or there are no stones 
	 * left) or if a player has quit the game. No more moves can be made.
	 * @return true if the game ended
	 */
	public boolean ended() {
		return consecutivePasses == 2 || blackStonesLeft <= 0 || whiteStonesLeft <= 0 || playerQuit;
	}
	
	/**
	 * Ends the game so no more moves can be made.
	 */
	public void playerQuit() {
		playerQuit = true;
	}
	
	/**
	 * 
	 * Checks if any groups are captured, first for the opponent (the color that is not
	 * in move) and then for the move itself (this means it was a suicide). Then removes
	 * the groups that were captured. 
	 * @param board To be checked for captures
	 * @param move The move that was made
	 */
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
	
	/**
	 * Removes the group from the board.
	 * @param board Board that the stones should be removed from
	 * @param group That should be removed
	 * @param color Of the group that should be removed.
	 */
	private void removeGroup(Board board, Set<Integer> group, Stone color) {
		for (Integer field : group) {
			board.setField(new Move(Stone.EMPTY, field));
			setChanged();
			notifyObservers(new Move(Stone.EMPTY, field));
		}
		board.getGroups().get(color).remove(group);
	}
	
	/**
	 * Calculates the score by using Area scoring. An empty group is captured
	 * when all the surrounding stones are of one color.
	 * @return Map of scores by player
	 */
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
	
	/**
	 * Tests if the Ko Rule has been violated by performing this move.
	 * First places the stone on a copy of the real board, and then checks
	 * if the new situation has appeared before by simulating all the moves that
	 * were made before one by one.
	 * @param move Move to check for Ko Rule
	 * @return true if the situation appeared before.
	 */
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
	
	/**
	 * Get the name of the player who has to make a move.
	 * @return Name of the player
	 */
	public String getCurrentPlayer() {
		return players.get(currentPlayerIndex).getName();
	}
	
	/**
	 * Get the dimension of the board from this game.
	 * @return board dimension
	 */
	public int getBoardDim() {
		return gameBoard.dim();
	}
	
}
