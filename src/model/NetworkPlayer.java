package model;

import server.controller.ClientHandler;

public class NetworkPlayer extends Player {
	private ClientHandler clientHandler;
	
	public NetworkPlayer(ClientHandler ch, Stone color, String name) {
		super(color, name);
		this.clientHandler = ch;
	}

	@Override
	public void askForMove(Board board) {
		//
	}
	
	public ClientHandler getClientHandler() {
		return this.clientHandler;
	}

}
