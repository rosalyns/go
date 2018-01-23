package server.model;

import server.controller.ClientHandler;

public class NetworkPlayer extends Player {

	public NetworkPlayer(ClientHandler ch, Stone color, String name) {
		super(color, name);
	}

	@Override
	public void askForMove(Board board) {
		// TODO Auto-generated method stub
	}

}
