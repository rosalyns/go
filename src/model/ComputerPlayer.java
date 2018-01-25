package model;

public class ComputerPlayer extends Player {
	private Strategy strategy;
	
	public ComputerPlayer(Stone color, Strategy strategy) {
		super(strategy.getName() + "-computer-" + color.toString());
		this.setColor(color);
		this.strategy = strategy;
	}
	
	public ComputerPlayer(Stone color) {
		this(color, new RandomStrategy());
	}

	@Override
	public void askForMove(Board board) {
		int position = strategy.determineMove(board, this.getColor());
		// TODO new MoveCommand(this.getColor(), position);
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

}
