package client.model;

public class ComputerPlayer extends Player {
	private Strategy strategy;
	
	public ComputerPlayer(Stone mark, Strategy strategy) {
		super(mark, strategy.getName() + "-computer-" + mark.toString());
		this.strategy = strategy;
	}
	
	public ComputerPlayer(Stone mark) {
		this(mark, new RandomStrategy());
	}

	@Override
	public Move determineMove(Board board) {
		int position = strategy.determineMove(board, this.getColor());
		return new Move(this.getColor(), position);
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

}
