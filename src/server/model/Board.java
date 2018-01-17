package server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {
	private int dim;
	private Stone[] fields;
	private Map<Stone, List<Set<Integer>>> groups;
	
	public Board() {
		this(9);
	}
	
	public Board(int dim) {
		this.dim = dim;
		this.fields = new Stone[dim * dim];
		for (int i = 0; i < dim * dim; i++) {
			this.fields[i] = Stone.EMPTY;
		}
		this.groups = new HashMap<Stone, List<Set<Integer>>>();
		this.groups.put(Stone.BLACK, new ArrayList<Set<Integer>>());
		this.groups.put(Stone.WHITE, new ArrayList<Set<Integer>>());
	}
	
	public Board deepCopy() {
		Board copyBoard = new Board(this.dim);
		for (int i = 0; i < dim * dim; i++) {
			copyBoard.setField(new Move(fields[i], i));
		}
		return copyBoard;
	}
	
	public int dim() {
		return this.dim;
	}
	
	public int index(int row, int col) {
		return row * dim + col; 
	}
	
	public void setField(Move move) {
		this.fields[move.getPosition()] = move.getColor();
	}
	
	public Stone getField(int index) {
		return this.fields[index];
	}
	
	public Stone getField(int row, int col) {
		return this.fields[index(row, col)];
	}
	
	public boolean isField(int index) {
		return 0 <= index && index < this.dim * this.dim;
	}
	
	public boolean isField(int row, int col) {
		return 0 <= row && row < dim && 0 <= col && col < dim;
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < dim * dim; i++) {
			if (fields[i] != Stone.EMPTY) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmptyField(int index) {
		return this.fields[index] == Stone.EMPTY;
	}
	
	public boolean isEmptyField(int row, int col) {
		return this.fields[index(row, col)] == Stone.EMPTY;
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
			this.fields[i] = Stone.EMPTY;
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
		
		
		return null;
	}
	
	public boolean hasLiberties(Set<Integer> group) {
		Set<Integer> liberties = new HashSet<Integer>();
		for (Integer stone : group) {
			Set<Integer> neighbours = getNeighbours(stone);
			for (Integer neighbour : neighbours) {
				if (isEmptyField(neighbour)) {
					liberties.add(neighbour);
				}
			}
		}
		return liberties.size() > 0;
	}
	
	private Set<Integer> getNeighbours(int index) {
		Set<Integer> neighbours = new HashSet<Integer>();
		if (index == 0) {
			neighbours.add(index + 1);
			neighbours.add(index + dim);
		} else if (index == dim - 1) {
			neighbours.add(index - 1);
			neighbours.add(index + dim);
		} else if (index / dim == dim - 1) {
			neighbours.add(index - dim);
			neighbours.add(index + 1);
		} else if (index == dim * dim - 1) {
			neighbours.add(index - dim);
			neighbours.add(index - 1);
		}  else if (index < dim) {
			neighbours.add(index - 1);
			neighbours.add(index + 1);
			neighbours.add(index + dim);
		} else if (index % dim == 0) {
			neighbours.add(index - dim);
			neighbours.add(index + dim);
			neighbours.add(index + 1);
		} else if (index % dim == dim - 1) {
			neighbours.add(index - dim);
			neighbours.add(index - dim);
			neighbours.add(index - 1);
		} else if (index > dim * (dim - 1)) {
			neighbours.add(index - 1);
			neighbours.add(index + 1);
			neighbours.add(index - dim);
		} else {
			neighbours.add(index - 1);
			neighbours.add(index + 1);
			neighbours.add(index - dim);
			neighbours.add(index + dim);
		}
		return neighbours;
	}
	
	public boolean isNeighbour(Move move) {
		return false;
	}
}
