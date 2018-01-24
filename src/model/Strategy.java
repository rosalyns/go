package model;

public interface Strategy {
	public String getName();
	public int determineMove(Board b, Stone m);
}
