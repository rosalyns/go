package model;

/**
 * Abstract class representing a player in a Go game.
 * @author Rosalyn Sleurink
 */

public abstract class Player {
	private String name;
	private Stone color;

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setColor(Stone color) {
		this.color = color;
	}

	public Stone getColor() {
		return this.color;
	}
}
