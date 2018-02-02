package model;

public interface Strategy {
	public String getName();
	public int determineMove(Game game, Stone color);
}
