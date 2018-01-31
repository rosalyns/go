package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import model.*;
import exceptions.InvalidBoardSizeException;

public class BoardTest {
	private Board board;
	private Board board2;
	private int dim;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(9);
		dim = board.dim();
		board2 = new Board(11);
	}
	
	@Test
	public void testSetUpWrongDimension() {
		assertThrows(InvalidBoardSizeException.class, () -> new Board(4));
		assertThrows(InvalidBoardSizeException.class, () -> new Board(20));
		
		try {
			new Board(5);
			new Board(19);
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSetupOneArgument() {
		assertTrue(board2.isEmpty());
		assertEquals(board2.dim(), 11);
	}
	
	@Test
	public void testIndex() {
		assertEquals(board.dim() * 3 + 3, Board.index(3, 3, board.dim()));
	}
	
	@Test
	public void testIndexToCoordinates() {
		Point topLeftCorner = new Point(0, 0);
		Point topRightCorner = new Point(board.dim() - 1, 0);
		assertEquals(topLeftCorner, Board.indexToCoordinates(0, 9));
		assertEquals(topRightCorner, Board.indexToCoordinates(8, 9));
	}
	
	
	@Test
	public void testGetFieldIndex() {
		board.setField(new Move(Stone.BLACK, 3));
		assertEquals(Stone.BLACK, board.getField(3));
	}
	
	@Test
	public void testDeepCopy() {
		board.setField(new Move(Stone.BLACK, 3));
		Board copyBoard = board.deepCopy();
		copyBoard.setField(new Move(Stone.WHITE, 3));
		assertTrue(copyBoard instanceof Board);
		assertEquals(board.getField(3), Stone.BLACK);
		assertEquals(copyBoard.getField(3), Stone.WHITE);
		assertEquals(copyBoard.getField(4), Stone.EMPTY);
	}
	
	@Test
	public void testIsEmptyFieldIndex() {
		board.setField(new Move(Stone.BLACK, 3));
		assertFalse(board.isEmptyField(3));
		assertTrue(board.isEmptyField(2));
	}
	
	@Test
	public void testIsFieldIndex() {
		assertFalse(board.isField(1234));
		assertFalse(board.isField(dim * dim));
		assertTrue(board.isField((dim * dim) - 1));
		assertFalse(board.isField(-1));
		
	}
	
	@Test
	public void testGetEmptyFields() {
		List<Integer> emptiesList = board.getEmptyFields();
		Set<Integer> emptiesSet = new HashSet<Integer>(emptiesList);
		assertEquals(emptiesSet.size(), dim * dim);
		
		board.setField(new Move(Stone.BLACK, 0));
		emptiesList = board.getEmptyFields();
		emptiesSet = new HashSet<Integer>(emptiesList);
		assertEquals(emptiesSet.size(), dim * dim - 1);
	}
	
	@Test
	public void testIsEmpty() {
		board.setField(new Move(Stone.BLACK, 3));
		assertFalse(board.isEmpty());
		
		for (int i = 0; i < board.dim() * board.dim(); i++) {
			board.setField(new Move(Stone.EMPTY, i));
		}
		assertTrue(board.isEmpty());
	}
	
	@Test
	public void testRecalculateGroups() {
		/*
		 * oooo
		 * oxxxo
		 *  ooo
		 */

		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		
		board.recalculateGroups(false);
		List<Set<Integer>> blackgroups = board.getGroups().get(Stone.BLACK);
		List<Set<Integer>> whitegroups = board.getGroups().get(Stone.WHITE);
		assertEquals(3, blackgroups.size());
		assertEquals(1, whitegroups.size(), 1);
		
		Set<Integer> blackgroup1 = blackgroups.get(0);
		Set<Integer> blackgroup2 = blackgroups.get(1);
		Set<Integer> blackgroup3 = blackgroups.get(2);
		assertEquals(5, blackgroup1.size());
		assertEquals(1, blackgroup2.size());
		assertEquals(3, blackgroup3.size());
	}
	
	@Test
	public void testHasLiberties() {
		/*
		 * oooo
		 * oxxxo
		 *  ooo
		 */

		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		
		List<Set<Integer>> blackgroups = board.getGroups().get(Stone.BLACK);
		List<Set<Integer>> whitegroups = board.getGroups().get(Stone.WHITE);
		Set<Integer> blackgroup1 = blackgroups.get(0);
		Set<Integer> blackgroup2 = blackgroups.get(1);
		Set<Integer> blackgroup3 = blackgroups.get(2);
		Set<Integer> whitegroup1 = whitegroups.get(0);
		
		assertTrue(board.hasLiberties(blackgroup1));
		assertTrue(board.hasLiberties(blackgroup2));
		assertTrue(board.hasLiberties(blackgroup3));
		assertFalse(board.hasLiberties(whitegroup1));
	}
	
	@Test
	public void testGetNeighboursGroup() {
		/*
		 * oooo
		 * oxxxo
		 *  ooo
		 */

		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		
		List<Set<Integer>> blackgroups = board.getGroups().get(Stone.BLACK);
		List<Set<Integer>> whitegroups = board.getGroups().get(Stone.WHITE);
		Set<Integer> blackgroup1 = blackgroups.get(0);
		Set<Integer> blackgroup2 = blackgroups.get(1);
		Set<Integer> blackgroup3 = blackgroups.get(2);
		Set<Integer> whitegroup1 = whitegroups.get(0);
		
		assertEquals(5, board.getNeighbours(blackgroup1).size());
		assertEquals(4, board.getNeighbours(blackgroup2).size());
		assertEquals(8, board.getNeighbours(blackgroup3).size());
		assertEquals(8, board.getNeighbours(whitegroup1).size());
	}
	
	@Test
	public void testGetNeighbours() {
		Set<Integer> topLeftCorner = board.getNeighbours(0);
		Set<Integer> topRightCorner = board.getNeighbours(8);
		Set<Integer> bottomLeftCorner = board.getNeighbours(9 * 9 - 9);
		Set<Integer> bottomRightCorner = board.getNeighbours(9 * 9 - 1);
		Set<Integer> leftSide = board.getNeighbours(9);
		Set<Integer> topSide = board.getNeighbours(3);
		Set<Integer> bottomSide = board.getNeighbours(77);
		Set<Integer> rightSide = board.getNeighbours(9 * 3 - 1);
		Set<Integer> middle = board.getNeighbours(12);
		
		assertEquals(2, topLeftCorner.size());
		assertEquals(2, topRightCorner.size());
		assertEquals(2, bottomLeftCorner.size());
		assertEquals(2, bottomRightCorner.size());
		assertEquals(3, leftSide.size());
		assertEquals(3, topSide.size());
		assertEquals(3, bottomSide.size());
		assertEquals(3, rightSide.size());
		assertEquals(4, middle.size());
		
	}
	
	@Test
	public void testClear() {
		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		
		board.clear();
		
		assertTrue(board.isEmpty());
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		assertFalse(board.equals(new Move(Stone.BLACK, 3)));
		assertFalse(board.equals(board2));
		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		
		Board sameBoard = board.deepCopy();
		assertTrue(board.equals(sameBoard));
		assertTrue(sameBoard.equals(board));
		
		Board differentBoard = null;
		try {
			differentBoard = new Board(board.dim());
		} catch (InvalidBoardSizeException e) {
			e.printStackTrace();
		}
		differentBoard.setField(new Move(Stone.WHITE, 10));
		differentBoard.setField(new Move(Stone.WHITE, 11));
		differentBoard.setField(new Move(Stone.WHITE, 12));
		
		assertFalse(board.equals(differentBoard));
		assertFalse(differentBoard.equals(sameBoard));
		
	}
}
