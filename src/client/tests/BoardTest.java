package client.tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import client.model.Board;
import client.model.Mark;

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
	public void testSetAndGetFieldRowCol() {
		board.setField(Mark.BLACK, 3, 3);
		assertEquals(Mark.BLACK, board.getField(3, 3));
	}
	
	@Test
	public void testSetAndGetFieldIndex() {
		board.setField(Mark.BLACK, 15);
		assertEquals(Mark.BLACK, board.getField(15));
	}
	
	
	
	@Test
	public void testDeepCopy() {
		board.setField(Mark.BLACK, 3);
		Board copyBoard = board.deepCopy();
		copyBoard.setField(Mark.WHITE, 3);
		assertTrue(copyBoard instanceof Board);
		assertEquals(board.getField(3), Mark.BLACK);
		assertEquals(copyBoard.getField(3), Mark.WHITE);
		assertEquals(copyBoard.getField(5), Mark.EMPTY);
	}
	
	@Test
	public void testIsEmptyFieldIndex() {
		board.setField(Mark.BLACK, 3);
		assertFalse(board.isEmptyField(3));
		assertTrue(board.isEmptyField(2));
	}
	
	@Test
	public void testIsEmptyFieldRowCol() {
		board.setField(Mark.BLACK, 3, 3);
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
		
		board.setField(Mark.BLACK, 0);
		emptiesList = board.getEmptyFields();
		emptiesSet = new HashSet<Integer>(emptiesList);
		assertEquals(emptiesSet.size(), dim * dim - 1);
	}
	
	@Test
	public void testReset() {
		board.setField(Mark.BLACK, 0);
		board.setField(Mark.WHITE, 1);
		board.setField(Mark.BLACK, 2);
		board.reset();
		assertEquals(Mark.EMPTY, board.getField(0));
		assertEquals(Mark.EMPTY, board.getField(1));
		assertEquals(Mark.EMPTY, board.getField(2));
	}
	
	@Test
	public void testChangeDim() {
		board2.changeDim(12);
		assertEquals(board2.dim(), 12);
	}

}
