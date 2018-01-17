package server.model;

public class Move {
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
