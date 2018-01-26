package model;

public abstract class LocalPlayer extends Player {
	
	public LocalPlayer(String name) {
		super(name);
	}

	public abstract void askForMove(Board board);
}
