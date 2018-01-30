package model;

import server.controller.ClientHandler;

public class NetworkPlayer extends Player {
	private ClientHandler clientHandler;
	
	public NetworkPlayer(String name) {
		super(name);
	}
}
