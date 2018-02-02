package model;

import java.awt.Point;

import client.controller.GoClient;
import commands.MoveCommand;

public class ComputerPlayer extends LocalPlayer {
	private Strategy strategy;
	private GoClient client;
	
	public ComputerPlayer(Stone color, String name, Strategy strategy) {
		super(name);
		this.setColor(color);
		this.strategy = strategy;
	}
	
	public ComputerPlayer(Stone color, String name) {
		this(color, name, new BasicStrategy());
	}
	
	public ComputerPlayer(Stone color, String name, GoClient client, Strategy strategy) {
		this(color, name, strategy);
		this.client = client;
	}

	@Override
	public void askForMove(Game game) {
		int position = strategy.determineMove(game, this.getColor());
		if (position == Move.PASS) {
			new MoveCommand(client, true, 0, 0).send();
		} else {
			Point coordinates = Board.indexToCoordinates(position, 
					client.getGameController().getBoardDim());
			new MoveCommand(client, false, coordinates.y, coordinates.x).send();
		}
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

}
