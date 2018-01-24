package model;

import server.controller.ClientHandler;

public class NetworkPlayer extends Player {
	private ClientHandler clientHandler;
	
	public NetworkPlayer(ClientHandler ch, String name) {
		super(name);
		this.clientHandler = ch;
	}
	
	public ClientHandler getClientHandler() {
		return this.clientHandler;
	}
	
	@Override
	public void askForMove(Board board) {
		// nothing TODO
	}

}
