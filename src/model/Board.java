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

import exceptions.InvalidBoardSizeException;

public class Board {
	private int dim;
	private Stone[] fields;
	private Map<Stone, List<Set<Integer>>> groups;
	
	public Board(int dim) throws InvalidBoardSizeException {
		if (dim < 5 || dim > 19) {
			throw new InvalidBoardSizeException(dim);
		}
		
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
		Board copyBoard = null;
		try {
			copyBoard = new Board(this.dim);
		} catch (InvalidBoardSizeException e) {
			//not possible
			e.printStackTrace();
		}
		for (int i = 0; i < dim * dim; i++) {
			copyBoard.setField(new Move(fields[i], i));
		}
		return copyBoard;
	}
	
	public void clear() {
		for (int i = 0; i < dim * dim; i++) {
			fields[i] = Stone.EMPTY;
		}
	}
	
	public int dim() {
		return this.dim;
	}
	
	public static Point indexToCoordinates(int index, int boardDim) {
		return new Point(index % boardDim, index / boardDim);
	}
	
	public static int index(int row, int col, int boardDim) {
		return row * boardDim + col; 
	}
	
	public void setField(Move move) {
		this.fields[move.getPosition()] = move.getColor();
		recalculateGroups(false);
	}
	
	public Stone getField(int index) {
		return this.fields[index];
	}
	
	public boolean isField(int index) {
		return 0 <= index && index < this.dim * this.dim;
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
	
//	public Set<Integer> getLiberties(Set<Integer> group) {
//		Set<Integer> liberties = new HashSet<Integer>();
//		for (Integer stone : group) {
//			Set<Integer> neighbours = getNeighbours(stone);
//			for (Integer neighbour : neighbours) {
//				if (isEmptyField(neighbour)) {
//					liberties.add(neighbour);
//				}
//			}
//		}
//		return liberties;
//	}
	
	public boolean hasLiberties(Set<Integer> group) {
		for (Integer stone : group) {
			Set<Integer> neighbours = getNeighbours(stone);
			for (Integer neighbour : neighbours) {
				if (isEmptyField(neighbour)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Set<Integer> getNeighbours(Set<Integer> group) {
		Set<Integer> neighbours = new HashSet<Integer>();
		for (Integer field : group) {
			neighbours.addAll(getNeighbours(field));
		}
		neighbours.removeAll(group);
		return neighbours;
	}
	
	public Set<Integer> getNeighbours(int index) {
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
			neighbours.add(index + dim);
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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Board)) {
			return false;
		}
		Board boardToCheck = (Board) o;
		if (boardToCheck.dim() != dim) {
			return false;
		}
		
		for (int i = 0; i < dim * dim; i++) {
			if (fields[i] != boardToCheck.getField(i)) {
				return false;
			}
		}
		return true;
	}
}
