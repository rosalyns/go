package server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nedap.go.gui.InvalidCoordinateException;
import commands.*;
import exceptions.InvalidBoardSizeException;
import exceptions.KoException;
import exceptions.NotYourTurnException;
import model.*;

public class GameController extends Thread {
	private Game game; 
	//private Lobby lobby;
	private List<ClientHandler> clients;
	
	/**
	 * Initializes a new game controller.
	 * @param lobby Lobby that the game started from. Is used to put players back in Lobby after 
	 * the game ends.
	 * @param clients Assume that list of Clients is in order of arrival. So first client is the one
	 * who gets to choose color and board size.
	 */
	public GameController(Lobby lobby, List<ClientHandler> clients) {
		//this.lobby = lobby;
		this.clients = clients;
	}
	
	/**
	 * Sends START command to the player that may choose the color and boardsize. Sets the game in
	 * the ClientHandlers. 
	 */
	public void run() {
		new StartCommand(clients.get(0), 2).send();
		
		for (ClientHandler client : clients) {
			client.setGame(this);
		}
	}
	
	/**
	 * Sets the colors for both players and the boardsize based on what the first player
	 * sent in their SETTINGS command. Then initaliazes a new Game and sends the START
	 * command to both players and the TURN command to ask for the first move.
	 * @param color Color that the first player wants to play with
	 * @param boardSize Boardsize that is chosen by the first player
	 */
	public void setSettings(Stone color, int boardSize) {
		clients.get(0).getPlayer().setColor(color);
		clients.get(1).getPlayer().setColor(color.other());
		List<Player> players = new ArrayList<Player>();
		for (ClientHandler client : clients) {
			players.add(client.getPlayer());
		}
		
		try {
			game = new Game(players, new Board(boardSize));
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		
		for (ClientHandler client : clients) {
			new StartCommand(client, 2, client.getPlayer().getColor(), boardSize, players).send();
			new TurnCommand(client, game.getCurrentPlayer(), new Move(Stone.BLACK, Move.FIRST), 
					getBoardDim(), game.getCurrentPlayer()).send();
		}
	}
	
	/**
	 * Processes a MOVE command from the client. Tries do a turn and if it succeeds,
	 * sends TURN commands to both players. At the end checks if the game has ended.
	 * @param clientPlayer Client that received the command. Is used to send an ERROR
	 * to if the move is not valid.
	 * @param move Move that the client wants to make.
	 */
	public void doMove(ClientHandler clientPlayer, Move move) {
		try {
			game.tryTurn(move);
		} catch (KoException | InvalidCoordinateException e1) {
			new ErrorCommand(clientPlayer, ErrorCommand.INVMOVE, e1.getMessage()).send();
			return;
		} catch (NotYourTurnException e2) {
			new ErrorCommand(clientPlayer, ErrorCommand.OTHER, e2.getMessage()).send();
			return;
		} 
		game.doTurn(move);
		
		for (ClientHandler client : clients) {
			new TurnCommand(client, clientPlayer.getName(), move, getBoardDim(),
					game.getCurrentPlayer()).send();
		}
		
		if (game.ended()) {
			endGame();
		}
	}
	
	/**
	 * Returns if the game has ended. This check is used when the player sends a move.
	 * @return true if the game has ended (finished normally or a player quit)
	 */
	public boolean ended() {
		return game.ended();
	}
	
	/**
	 * Ends the game prematurely when it receives a QUIT command from a player. The quitter
	 * is then given 0 points so it loses. An ENDGAME command is sent to both players.
	 * @param quitter
	 */
	public void endGame(ClientHandler quitter) {
		game.playerQuit(); 
		Map<Player, Integer> scores = game.calculateScores();
		
		ClientHandler winner = null;
		ClientHandler loser = quitter;
		
		for (ClientHandler client : clients) {
			if (!client.getName().equals(loser.getName())) {
				winner = client;
			}	
		}
		
		for (ClientHandler client : clients) {
			new EndGameCommand(client, EndGameCommand.ABORTED, 
					winner.getName(), scores.get(winner.getPlayer()), 
					loser.getName(), 0).send();
		}
	}
	
	/**
	 * Ends the game normally. The scores are calculated with Area scoring and
	 * ENDGAME commands are sent to both players.
	 */
	public void endGame() {
		Map<Player, Integer> scores = game.calculateScores();
		
		int highestScore = -1;
		ClientHandler winner = null;
		ClientHandler loser = null;
		
		for (int i = 0; i < clients.size(); i++) {
			int score = scores.get(clients.get(i).getPlayer());
			if (score > highestScore) {
				highestScore = score;
				winner = clients.get(i);
				loser = getOtherClient(winner);
			}
		}
		
		for (ClientHandler client : clients) {
			new EndGameCommand(client, EndGameCommand.FINISHED, 
					winner.getName(), scores.get(winner.getPlayer()), 
					loser.getName(), scores.get(loser.getPlayer())).send();
		}
	}
	
	/**
	 * Returns the dimension of the board that this game is playing on.
	 * @return board dimension
	 */
	public int getBoardDim() {
		return game.getBoardDim();
	}
	
	/**
	 * Finds the other client in the game.
	 * @param client that you want to find the opponent of
	 * @return opponent client of the given client
	 */
	private ClientHandler getOtherClient(ClientHandler client) {
		if (clients.get(0).equals(client)) {
			return clients.get(1);
		} else {
			return clients.get(0);
		}
	}
}
