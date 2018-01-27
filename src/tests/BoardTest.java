package tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import model.*;

public class BoardTest {
	private Board board;
	private Board board2;
	private int dim;
	
	@Before
	public void setUp() throws Exception {
		board = new Board();
		dim = board.dim();
		board2 = new Board(11);
	}

	@Test
	public void testSetupNoArguments() {
		assertTrue(board.isEmpty());
		int minDim = 9;
		int maxDim = 19;
		assertTrue(minDim <= dim && dim <= maxDim);
	}
	
	@Test
	public void testSetupOneArgument() {
		assertTrue(board2.isEmpty());
		assertEquals(board2.dim(), 11);
	}
	
	@Test
	public void testIndex() {
		assertEquals(board.dim() * 3 + 3, board.index(3, 3));
	}
	
	@Test
	public void testGetFieldRowCol() {
		board.setField(new Move(Stone.BLACK, board.index(3, 3)));
		assertEquals(Stone.BLACK, board.getField(3, 3));
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
	public void testIsEmptyFieldRowCol() {
		board.setField(new Move(Stone.BLACK, board.index(3, 3)));
		assertFalse(board.isEmptyField(3, 3));
		assertTrue(board.isEmptyField(2, 2));
	}
	
	@Test
	public void testIsFieldIndex() {
		assertFalse(board.isField(1234));
		assertFalse(board.isField(dim * dim));
		assertTrue(board.isField((dim * dim) - 1));
	}
	
	@Test
	public void testIsFieldRowCol() {
		assertFalse(board.isField(dim + 1, dim + 5));
		assertFalse(board.isField(dim, dim));
		assertFalse(board.isField(dim, dim - 1));
		assertFalse(board.isField(dim - 1, dim));
		assertTrue(board.isField(dim - 1, dim - 1));
		assertFalse(board.isField(-5, 1));
		assertFalse(board.isField(5, -1));
		assertTrue(board.isField(0, 0));
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
	public void testReset() {
		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.WHITE, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.reset();
		assertEquals(Stone.EMPTY, board.getField(0));
		assertEquals(Stone.EMPTY, board.getField(1));
		assertEquals(Stone.EMPTY, board.getField(2)); 
	}
	
	@Test
	public void testRecalculateGroups() {
		/*
		 * oooo
		 * oxxxo
		 *  ooo
		 */
		
		board.setField(new Move(Stone.BLACK, board.index(0, 1)));
		board.setField(new Move(Stone.BLACK, board.index(0, 2)));
		board.setField(new Move(Stone.BLACK, board.index(0, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 0)));
		board.setField(new Move(Stone.BLACK, board.index(2, 1)));
		board.setField(new Move(Stone.BLACK, board.index(2, 2)));
		board.setField(new Move(Stone.BLACK, board.index(2, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 4)));
		board.setField(new Move(Stone.BLACK, board.index(0, 0)));
		board.setField(new Move(Stone.WHITE, board.index(1, 1)));
		board.setField(new Move(Stone.WHITE, board.index(1, 2)));
		board.setField(new Move(Stone.WHITE, board.index(1, 3)));
		
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
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNeighboursField() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNeighboursGroup() {
		fail("Not yet implmemented");
	}
	
	@Test
	public void testDoCapturesSuicide() {
		/*
		 * oooo
		 * oxxxo
		 *  ooo
		 */
		board.setField(new Move(Stone.BLACK, board.index(0, 1)));
		board.setField(new Move(Stone.BLACK, board.index(0, 2)));
		board.setField(new Move(Stone.BLACK, board.index(0, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 0)));
		board.setField(new Move(Stone.BLACK, board.index(2, 1)));
		board.setField(new Move(Stone.BLACK, board.index(2, 2)));
		board.setField(new Move(Stone.BLACK, board.index(2, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 4)));
		board.setField(new Move(Stone.BLACK, board.index(0, 0)));
		board.setField(new Move(Stone.WHITE, board.index(1, 1)));
		board.setField(new Move(Stone.WHITE, board.index(1, 2)));
		board.setField(new Move(Stone.WHITE, board.index(1, 3)));
		
		board.recalculateGroups(false);
		board.doCaptures(new Move(Stone.WHITE, board.index(1, 1)));
		
		assertEquals(Stone.EMPTY, board.getField(1, 1));
		assertEquals(Stone.EMPTY, board.getField(1, 2));
		assertEquals(Stone.EMPTY, board.getField(1, 3));
	}
	
	@Test
	public void testDoCapturesOpponent() {
		/*
		 * oooo
		 * oxxxo
		 *  ooo
		 */
		board.setField(new Move(Stone.BLACK, board.index(0, 1)));
		board.setField(new Move(Stone.BLACK, board.index(0, 2)));
		board.setField(new Move(Stone.BLACK, board.index(0, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 0)));
		board.setField(new Move(Stone.BLACK, board.index(2, 1)));
		board.setField(new Move(Stone.BLACK, board.index(2, 2)));
		board.setField(new Move(Stone.BLACK, board.index(2, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 4)));
		board.setField(new Move(Stone.BLACK, board.index(0, 0)));
		board.setField(new Move(Stone.WHITE, board.index(1, 1)));
		board.setField(new Move(Stone.WHITE, board.index(1, 2)));
		board.setField(new Move(Stone.WHITE, board.index(1, 3)));
		
		board.recalculateGroups(false);
		board.doCaptures(new Move(Stone.BLACK, board.index(1, 4)));
		
		assertEquals(Stone.EMPTY, board.getField(1, 1));
		assertEquals(Stone.EMPTY, board.getField(1, 2));
		assertEquals(Stone.EMPTY, board.getField(1, 3));
	}
	
	@Test
	public void testDoCapturesAlmostSuicide() {
		/*
		 * oooox
		 * oxxXox
		 *  ooox
		 */
		board.setField(new Move(Stone.BLACK, board.index(0, 0)));
		board.setField(new Move(Stone.BLACK, board.index(0, 1)));
		board.setField(new Move(Stone.BLACK, board.index(0, 2)));
		board.setField(new Move(Stone.BLACK, board.index(0, 3)));
		board.setField(new Move(Stone.WHITE, board.index(0, 4)));
		board.setField(new Move(Stone.BLACK, board.index(1, 0)));
		board.setField(new Move(Stone.WHITE, board.index(1, 1)));
		board.setField(new Move(Stone.WHITE, board.index(1, 2)));
		board.setField(new Move(Stone.WHITE, board.index(1, 3)));
		board.setField(new Move(Stone.BLACK, board.index(1, 4)));
		board.setField(new Move(Stone.WHITE, board.index(1, 5)));
		board.setField(new Move(Stone.BLACK, board.index(2, 1)));
		board.setField(new Move(Stone.BLACK, board.index(2, 2)));
		board.setField(new Move(Stone.BLACK, board.index(2, 3)));
		board.setField(new Move(Stone.WHITE, board.index(2, 4)));
		
		board.recalculateGroups(false);
		board.doCaptures(new Move(Stone.WHITE, board.index(1, 3)));
		
		assertEquals(Stone.EMPTY, board.getField(1, 4));
		assertEquals(Stone.WHITE, board.getField(1, 1));
		assertEquals(Stone.WHITE, board.getField(1, 2));
		assertEquals(Stone.WHITE, board.getField(1, 3));
	}

}
