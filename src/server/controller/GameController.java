package server.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import commands.*;
import exceptions.InvalidBoardSizeException;
import exceptions.KoException;
import exceptions.NotYourTurnException;
import general.Protocol;
import model.*;

public class GameController extends Thread {
	private Game game; 
	//private Lobby lobby;
	private List<ClientHandler> clients;
	
	/**
	 * @param lobby
	 * @param clients Assume that list of Clients is in order of arrival. So first client is the one
	 * who gets to choose color and board size.
	 */
	public GameController(Lobby lobby, List<ClientHandler> clients) {
		//this.lobby = lobby;
		this.clients = clients;
	}
	
	public void run() {
		new StartCommand(clients.get(0), 2).send();
		
		for (ClientHandler client : clients) {
			client.setGame(this);
		}
	}
	
	public void setSettings(Stone color, int boardSize) {
		clients.get(0).getPlayer().setColor(color);
		clients.get(1).getPlayer().setColor(color.other());
		List<Player> players = new ArrayList<Player>();
		for (ClientHandler client : clients) {
			players.add(client.getPlayer());
		}
		
		try {
			game = new Game(players, boardSize);
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		
		for (ClientHandler client : clients) {
			new StartCommand(client, 2, client.getPlayer().getColor(), boardSize, players).send();
			new TurnCommand(client, game.getFirstPlayer(), TurnCommand.FIRST, 
					game.getFirstPlayer()).send();
		}
	}
	
	public void doMove(ClientHandler clientPlayer, Move move) {
		try {
			game.doTurn(move);
		} catch (KoException e1) {
			new ErrorCommand(clientPlayer, ErrorCommand.INVMOVE, e1.getMessage()).send();
			return;
		} catch (NotYourTurnException e2) {
			new ErrorCommand(clientPlayer, ErrorCommand.OTHER, e2.getMessage()).send();
			return;
		}
		
		String moveStr = "";
		if (move.getPosition() == Move.PASS) {
			moveStr = Protocol.Server.PASS;
		} else {
			int dim = this.getBoardDim();
			Point coordinates = Board.indexToCoordinates(move.getPosition(), dim);
			moveStr = coordinates.y + Protocol.General.DELIMITER2 + coordinates.x;
		}
		
		for (ClientHandler client : clients) {
			new TurnCommand(client, clientPlayer.getName(), moveStr, 
					game.getCurrentPlayer()).send();
		}
		
		if (game.isGameOver()) {
			endGame();
		}
		
	}
	
	public void endGame(ClientHandler quitter) {
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
					loser.getName(), scores.get(loser.getPlayer())).send();
		}
	}
	
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
	
	public int getBoardDim() {
		return game.getBoardDim();
	}
	
	private ClientHandler getOtherClient(ClientHandler client) {
		if (clients.get(0).equals(client)) {
			return clients.get(1);
		} else {
			return clients.get(0);
		}
	}
}
