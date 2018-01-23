package client.model;

public class Move {
	public static final int PASS = -1;
	
	private int position;
	private Stone color;
	
	public Move(Stone color, int position) {
		this.position = position;
		this.color = color;
	}
	
	public int getPosition() {
		return this.position;
	}

	public Stone getColor() {
		return color;
	}
}
