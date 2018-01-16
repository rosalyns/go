package server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Board {
	private int dim;
	private Stone[][] fields;
	
	public Board() {
		this(9);
	}
	
	public Board(int dim) {
		this.dim = dim;
		this.fields = new Stone[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				this.fields[i][j] = Stone.EMPTY;
			}
		}
	}
	
	public Board deepCopy() {
		Board copyBoard = new Board(this.dim);
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				copyBoard.setField(new Move(fields[i][j], i, j));
			}
		}
		return copyBoard;
	}
	
	public int dim() {
		return this.dim;
	}
	
	public int index(int row, int col) {
		return row * dim + col; 
	}
	
	public int[] indexToCoordinates(int index) {
		return new int[] {index / dim, index % dim };
	}
	
	public void setField(Move move) {
		this.fields[move.getRow()][move.getCol()] = move.getColor();
	}
	
	public Stone getField(int index) {
		int[] position = indexToCoordinates(index);
		return this.fields[position[0]][position[1]];
	}
	
	public Stone getField(int row, int col) {
		return this.fields[row][col];
	}
	
	public boolean isField(int index) {
		return 0 <= index && index < this.dim * this.dim;
	}
	
	public boolean isField(int row, int col) {
		return 0 <= row && row < dim && 0 <= col && col < dim;
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				if (fields[i][j] != Stone.EMPTY) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isEmptyField(int index) {
		int[] position = indexToCoordinates(index);
		return this.fields[position[0]][position[1]] == Stone.EMPTY;
	}
	
	public boolean isEmptyField(int row, int col) {
		return this.fields[row][col] == Stone.EMPTY;
	}
	
	//@ ensures geen dubbelen.
	public List<Integer> getEmptyFields() {
		List<Integer> emptyFields = new ArrayList<Integer>();
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				if (isEmptyField(i, j)) {
					emptyFields.add(index(i, j));
				}
			}
		}
		return emptyFields;
	}
	
	public void reset() {
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				this.fields[i][j] = Stone.EMPTY;
			}
		}
	}
	
	public void changeDim(int newDim) {
		this.dim = newDim;
	}
	
	
	public List<Integer> capturedGroup(Move move) {
		/*
		 * check voor move.getColor() of die color.other() buren heeft.
		 * dan moet je voor die color.other() stenen checken in welke groep die zitten
		 * voor de groepen kijken of die overal omringd zijn door color.other()
		*/
		
		List<Integer> neighbours = getNeighbourGroupsOfOtherColor(move);
		
		return null;
	}
	
	public List<List<Integer>> getNeighbourGroupsOfOtherColor(Move move) {
		List<List<Integer>> neighboursgroups = new ArrayList<List<Integer>>();
		
		return null;
	}
	
	public int getNumberOfNeighbourFieldsOfGroup(Set<Integer> group) {
		int result = 0;
		for (Integer stone : group) {
			//buren.....
			if (true || false) {
				result++;
			}
		}
		return result;
	}
}
