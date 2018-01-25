package server.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import commands.*;
import exceptions.InvalidBoardSizeException;
import general.Protocol;
import model.*;

public class GameController extends Thread {
	private Game game; 
	//private Lobby lobby;
	private List<ClientHandler> clients;
	private ClientHandler playersTurn;
	
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
		
		playersTurn = firstPlayer();
		
		for (ClientHandler client : clients) {
			new StartCommand(client, 2, client.getPlayer().getColor(), boardSize, players).send();
			new TurnCommand(client, firstPlayer().getName(), TurnCommand.FIRST, 
					firstPlayer().getName()).send();
		}
	}
	
	private ClientHandler otherPlayer(ClientHandler thisPlayer) {
		if (clients.get(0).equals(thisPlayer)) {
			return clients.get(1);
		} else if (clients.get(1).equals(thisPlayer)) {
			return clients.get(0);
		} else {
			return null;
		}
		//TODO: exception?
	}
	
	private ClientHandler firstPlayer() {
		for (ClientHandler ch : clients) {
			if (ch.getPlayer().getColor() == Stone.BLACK) {
				return ch;
			}
		}
		return null;
	}
	
	public void doMove(ClientHandler clientPlayer, Move move) {
		if (playersTurn.equals(clientPlayer)) {
			game.doMove(move);
			
			String moveStr = "";
			if (move.getPosition() == Move.PASS) {
				moveStr = Protocol.Server.PASS;
			} else {
				int dim = this.getBoardDim();
				Point coordinates = Board.indexToCoordinates(move.getPosition(), dim);
				moveStr = coordinates.y + Protocol.General.DELIMITER2 + coordinates.x;
			}
			
			for (ClientHandler client : clients) {
				new TurnCommand(client, playersTurn.getName(), moveStr, 
						otherPlayer(playersTurn).getName()).send();
			}
			playersTurn = otherPlayer(playersTurn);
			if (game.isGameOver()) {
				endGameNormal();
			}
		}
		
	}
	
	public void endGameNormal() {
		game.end();
		Map<Player, Integer> scores = game.calculateScores();
		
		int highestScore = -1;
		ClientHandler winner = null;
		ClientHandler loser = null;
		
		for (int i = 0; i < clients.size(); i++) {
			int score = scores.get(clients.get(i).getPlayer());
			if (score > highestScore) {
				highestScore = score;
				winner = clients.get(i);
				loser = clients.get(Math.abs(i - 1)); // 0 becomes 1, 1 becomes 0
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
}
