package model;

public abstract class LocalPlayer extends Player {
	private boolean isTurn;
	
	public LocalPlayer(String name) {
		super(name);
		this.isTurn = false;
	}
	
	public void madeMove() {
		isTurn = false;
	}

	public boolean hasTurn() {
		return isTurn;
	}

	public void askForMove(Game game) {
		isTurn = true;
	}
}
