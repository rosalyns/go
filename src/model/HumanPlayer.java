package model;

public class HumanPlayer extends LocalPlayer {
	private boolean isTurn;

	public HumanPlayer(Stone color, String name) {
		super(name);
		this.setColor(color);
		this.isTurn = false;
	}
	
	@Override
	public void askForMove(Board board) {
		System.out.println("You are " + this.getColor() + " and it's your turn. "
				+ "Where do you want to place a stone? " 
				+ "Specify by MOVE <row> <column> or MOVE PASS.");
		isTurn = true;
	}

	public void madeMove() {
		isTurn = false;
	}

	public boolean hasToTakeTurn() {
		return isTurn;
	}

}
