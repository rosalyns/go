package model;

import java.awt.Point;

import client.controller.GoClient;
import commands.MoveCommand;

public class ComputerPlayer extends Player {
	private Strategy strategy;
	private GoClient client;
	
	public ComputerPlayer(Stone color, String name, Strategy strategy) {
		super(name);
		this.setColor(color);
		this.strategy = strategy;
	}
	
	public ComputerPlayer(Stone color, String name) {
		this(color, name, new RandomStrategy());
	}
	
	public ComputerPlayer(Stone color, Strategy strategy) {
		this(color, strategy.getName() + "-computer-" + color.toString(), strategy);
	}
	
	public ComputerPlayer(Stone color, String name, GoClient client) {
		this(color, name, new RandomStrategy());
		this.client = client;
	}
	
	public ComputerPlayer(Stone color) {
		this(color, new RandomStrategy());
	}

	@Override
	public void askForMove(Board board) {
		int position = strategy.determineMove(board, this.getColor());
		if (position == Move.PASS) {
			new MoveCommand(client, true, 0, 0).send();
		} else {
			Point coordinates = Board.indexToCoordinates(position, client.getBoardDim());
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
