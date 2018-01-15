package client.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	private int dim;
	private Mark[] fields;
	
	public Board() {
		this(9);
	}
	
	public Board(int dim) {
		this.dim = dim;
		this.fields = new Mark[dim * dim];
		for (int i = 0; i < dim * dim; i++) {
			this.fields[i] = Mark.EMPTY;
		}
	}
	
	public Board deepCopy() {
		Board copyBoard = new Board(this.dim);
		for (int i = 0; i < dim * dim; i++) {
			copyBoard.setField(fields[i], i);
		}
		return copyBoard;
	}
	
	public int dim() {
		return this.dim;
	}
	
	public int index(int row, int col) {
		return row * dim + col; 
	}
	
	public void setField(Mark mark, int index) {
		this.fields[index] = mark;
	}
	
	public void setField(Mark mark, int row, int col) {
		this.fields[index(row, col)] = mark;
	}
	
	public Mark getField(int index) {
		return this.fields[index];
	}
	
	public Mark getField(int row, int col) {
		return this.fields[index(row, col)];
	}
	
	public boolean isField(int index) {
		return 0 <= index && index < this.dim * this.dim;
	}
	
	public boolean isField(int row, int col) {
		return 0 <= row && row < dim && 0 <= col && col < dim;
	}
	
	public boolean isEmpty() {
		for (Mark c : this.fields) {
			if (c != Mark.EMPTY) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmptyField(int index) {
		return this.fields[index] == Mark.EMPTY;
	}
	
	public boolean isEmptyField(int row, int col) {
		return this.fields[index(row, col)] == Mark.EMPTY;
	}
	
	//@ ensures geen dubbelen.
	public List<Integer> getEmptyFields() {
		List<Integer> emptyFields = new ArrayList<Integer>();
		for (int i = 0; i < dim * dim; i++) {
			if (isEmptyField(i)) {
				emptyFields.add(i);
			}
		}
		return emptyFields;
	}
	
	public void reset() {
		for (int i = 0; i < dim * dim; i++) {
			this.fields[i] = Mark.EMPTY;
		}
	}
	
	public void changeDim(int newDim) {
		this.dim = newDim;
	}
}
