package server.model;

public interface Strategy {
	public String getName();
	public int determineMove(Board b, Mark m);
}
