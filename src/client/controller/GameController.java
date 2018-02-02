package client.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.nedap.go.gui.GOGUI;
import com.nedap.go.gui.InvalidCoordinateException;

import client.view.TUIView;
import commands.MoveCommand;
import commands.QuitCommand;
import exceptions.*;
import model.*;

public class GameController implements Observer {

	private GoClient client;
	private GOGUI gui;
	private TUIView tui;
	private Game game;
	private Board board;
	private List<Player> players;
	private LocalPlayer player;
	private NetworkPlayer opponent;
	
	public GameController(GoClient client, GOGUI gui, TUIView tui) {
		this.client = client;
		this.gui = gui;
		this.tui = tui;
	}
	
	public void start(GoClient.AI useAI, String opponentName, int boardSize, Stone playerColor) {
		opponent = new NetworkPlayer(opponentName);
		
		if (useAI == GoClient.AI.BASIC) {
			this.player = new ComputerPlayer(playerColor, client.getName(), 
					client, new BasicStrategy());
		} else if (useAI == GoClient.AI.RANDOM) {
			this.player = new ComputerPlayer(playerColor, client.getName(), 
					client, new RandomStrategy());
		} else {
			this.player = new HumanPlayer(playerColor, client.getName());
		}
		
		opponent.setColor(playerColor.other());
		players = new ArrayList<Player>();
		players.add(this.player);
		players.add(opponent);
		try {
			board = new Board(boardSize);
			
		} catch (InvalidBoardSizeException e) {
			//comes from server, not likely
			e.printStackTrace();
		}
		game = new Game(players, board);
		game.addObserver(this);
		gui.setBoardSize(boardSize);
		tui.startGame(this);
	}
	
	public int getBoardDim() {
		return game.getBoardDim();
	}
	
	/**
	 * Get the color that the player with this name is using.
	 * @param playerName name of the player you want to know the color of
	 * @return Stone that can be Stone.BLACK or Stone.WHITE.
	 */
	public Stone getColor(String playerName) {
		if (playerName.equals(player.getName())) {
			return player.getColor();
		}
		return player.getColor().other();
	}
	
	public void askForMove() {
		player.askForMove(game);
	}
	
	/**
	 * Performs a move on the board as a response to a TURN command. Adds the stone to the GUI
	 * and places it on the board that this client uses. If the player passed, it shows the pass
	 * on the TUI. It also updates the player so that it can't make a move when it's not their
	 * turn.
	 * @param move Move that the server sent. Contains color of the stone and the position.
	 */
	public void makeMove(Move move) {
		if (move.getPosition() != Move.PASS) {
			game.doTurn(move);
		} else {
			if (game.getCurrentPlayer().equalsIgnoreCase(opponent.getName())) {
				tui.showPass(opponent.getName());
			}
		}
		player.madeMove();
	}
	
	/**
	 * Tries a move that the user entered in the TUI. Sends the move command to the server if:
	 * it's the player turn and it's a valid move. If it's not a valid move the TUI will wait
	 * for a new input.
	 * @param pass true if the player passed
	 * @param row the row the player wants to place a stone in
	 * @param column the column the player wants to place a stone in
	 */
	public void tryMove(boolean pass, int row, int column) {
		if (pass) {
			new MoveCommand(client, true, 0, 0).send();
		} else {
			try {
				game.tryTurn(new Move(player.getColor(), Board.index(row, column, getBoardDim())));
			} catch (KoException | InvalidCoordinateException e) {
				tui.showInvalidMove();
			} catch (NotYourTurnException e) {
				tui.showNotYourTurn();
			}
			new MoveCommand(client, false, row, column).send();
		}
	}
	
	/**
	 * Sets the next player of the game. If the next player is this player, it asks the player to 
	 * make a move. If the player is an AI it will not show on the TUI. It will also place a hint on
	 * the GUI board if an AI is not used.
	 * @param playerName
	 */
	public void nextPlayer(String playerName) {
		if (playerName.equals(player.getName())) {
			player.askForMove(game);
			
			if (player instanceof HumanPlayer) {
				int hint = new RandomStrategy().determineMove(game, player.getColor());
				Point hintPoint = Board.indexToCoordinates(hint, board.dim());
				try {
					gui.removeHintIdicator();
					gui.addHintIndicator(hintPoint.x, hintPoint.y);
				} catch (InvalidCoordinateException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Shows the scores and the player that won.
	 * @param reason Reason that the game ended. FINISHED, ABORTED or TIMEOUT
	 * @param scores The end scores.
	 */
	public void endGame(String reason, Map<String, Integer> scores) {
		tui.endGame(reason, scores);
	}
	
	public void quit() {
		new QuitCommand(client, false).send();
	}

	@Override
	public void update(Observable o, Object arg) {
		Move move = (Move) arg;
		try {
			if (move.getColor() == Stone.EMPTY) {
				Point coordinates = Board.indexToCoordinates(move.getPosition(), getBoardDim());
				gui.removeStone(coordinates.x, coordinates.y);
			} else {
				Point coordinates = Board.indexToCoordinates(move.getPosition(), getBoardDim());
				gui.addStone(coordinates.x, coordinates.y, move.getColor() == Stone.WHITE);
			}
		} catch (InvalidCoordinateException e) {
			//comes from server, not likely
			e.printStackTrace();
		}
		
	}
}
