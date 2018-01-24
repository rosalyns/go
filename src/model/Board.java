package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
	
	public Point indexToCoordinates(int index) {
		return new Point(index / dim, index % dim);
	}
	
	public int index(int row, int col) {
		return row * dim + col; 
	}
	
	public void setField(Move move) {
		this.fields[move.getPosition()] = move.getColor();
		recalculateGroups(false);
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
	
	//ensures geen dubbelen
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
	
	public Set<Integer> getNeighbours(Set<Integer> group) {
		Set<Integer> neighbours = new HashSet<Integer>();
		for (Integer field : group) {
			neighbours.addAll(getNeighbours(field));
		}
		neighbours.removeAll(group);
		return neighbours;
	}
	
	private Set<Integer> getNeighbours(int index) {
		Set<Integer> neighbours = new HashSet<Integer>();
		if (index == 0) {
			neighbours.add(index + 1);
			neighbours.add(index + dim);
		} else if (index == dim - 1) {
			neighbours.add(index - 1);
			neighbours.add(index + dim);
		} else if (index == dim * dim - dim) {
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
	
	public void recalculateGroups(boolean gameEnded) {
		groups.put(Stone.BLACK, new ArrayList<Set<Integer>>());
		groups.put(Stone.WHITE, new ArrayList<Set<Integer>>());
		if (gameEnded) {
			groups.put(Stone.EMPTY, new ArrayList<Set<Integer>>());
		}
		
		Set<Integer> haveChecked = new HashSet<Integer>();
		
		for (int i = 0; i < dim * dim; i++) {
			for (Stone color : groups.keySet()) {
				if (fields[i] == color && !haveChecked.contains(i)) {
					Set<Integer> newGroup = new HashSet<Integer>();
					Queue<Integer> haveToCheck = new LinkedList<Integer>();
					haveToCheck.add(i);
					newGroup.add(i);
					while (!haveToCheck.isEmpty()) {
						int index = (int) haveToCheck.poll();
						Set<Integer> neighbours = getNeighbours(index);
						for (Integer neighbour : neighbours) {
							if (fields[neighbour] == color && 
									!newGroup.contains(neighbour) && 
									!haveToCheck.contains(neighbour)) {
								newGroup.add(neighbour);
								haveToCheck.add(neighbour);
							}
						}
						haveChecked.add(index);
					}
					groups.get(color).add(newGroup);
				} 
			}
		}
	}
	
	public Map<Stone, List<Set<Integer>>> getGroups() {
		return this.groups;
	}
	
	
	
	
}
