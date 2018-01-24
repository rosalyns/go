package server.controller;

import java.util.ArrayList;
import java.util.List;

import model.Stone;
import commands.*;


import exceptions.InvalidBoardSizeException;
import model.*;

public class GameController extends Thread {
	private Game game; 
	private Lobby lobby;
	private List<ClientHandler> clients;
	
	public GameController(Lobby lobby, List<ClientHandler> clients) {
		this.lobby = lobby;
		this.clients = clients;
		
		new StartCommand(clients.get(0), 2).send();
	}
	
	public void setSettings(Stone color, int boardSize) {
		NetworkPlayer player1 = new NetworkPlayer(clients.get(0), color, clients.get(0).getName());
		NetworkPlayer player2 = new NetworkPlayer(clients.get(1), color.other(), 
				clients.get(1).getName()); 
		List<Player> players = new ArrayList<Player>();
		players.add(player1);
		players.add(player2);
		
		try {
			game = new Game(players, boardSize);
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		
		for (ClientHandler client : clients) {
			//ClientHandler clientHandler, int numberOfPlayers, Stone color, int boardSize, List<Player> players
			new StartCommand(client, 2, client.getPlayer().getColor(), boardSize, players);
		}
	}
	


	
}
