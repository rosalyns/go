package server.model;

public class Move {
	private int row;
	private int col;
	private Stone color;
	
	public Move(Stone color, int row, int col) {
		this.row = row;
		this.col = col;
		this.color = color;
	}


	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}

	public Stone getColor() {
		return color;
	}
}
