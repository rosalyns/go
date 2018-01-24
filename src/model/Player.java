package model;

/**
 * Abstract class representing a player in a Go game.
 * @author Rosalyn Sleurink
 */

public abstract class Player {
	private String name;
	protected Stone color;

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setColor(Stone color) {
		this.color = color;
	}

	public Stone getColor() {
		return this.color;
	}

	public abstract void askForMove(Board board);

}
