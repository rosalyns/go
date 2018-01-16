package server.model;

public class ComputerPlayer extends Player {
	private Strategy strategy;
	
	public ComputerPlayer(Mark mark, Strategy strategy) {
		super(mark, strategy.getName() + "-computer-" + mark.toString());
		this.strategy = strategy;
	}
	
	public ComputerPlayer(Mark mark) {
		this(mark, new RandomStrategy());
	}

	@Override
	public int determineMove(Board board) {
		return strategy.determineMove(board, this.getMark());
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

}
