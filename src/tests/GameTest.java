package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import model.Board;
import model.Game;
import model.Move;
import model.NetworkPlayer;
import model.Player;
import model.Stone;

public class GameTest {
	private Game game;
	private Board board;
	private Player player1;
	private Player player2;
	
	@Before
	public void setUp() throws Exception {
		List<Player> players = new ArrayList<Player>();
		player1 = new NetworkPlayer(null, "Player1");
		player2 = new NetworkPlayer(null, "Player2");
		player1.setColor(Stone.BLACK);
		player2.setColor(Stone.WHITE);
		players.add(player1);
		players.add(player2);
		
		board = new Board(9);
		game = new Game(players, board);
	}
	
	@Test
	public void testCalculateScores() {
		/*
		 * oooox
		 * oxxx x
		 *  ooox
		 */
		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.WHITE, 4));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.WHITE, 14));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 22));
		
		game.doCaptures(board, new Move(Stone.WHITE, 12));
		
		Map<Player, Integer> scores = game.calculateScores();
		assertEquals(8, (int) scores.get(player1));
		assertEquals(7, (int) scores.get(player2));
	}
	
	@Test
	public void testDoCapturesSuicide() {
		/*
		 * oooo
		 * oXxxo
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
		
		game.doCaptures(board, new Move(Stone.WHITE, 10));
		
		assertEquals(Stone.EMPTY, board.getField(10));
		assertEquals(Stone.EMPTY, board.getField(11));
		assertEquals(Stone.EMPTY, board.getField(12));
	}
	
	@Test
	public void testDoCapturesOpponent() {
		/*
		 * oooo
		 * oxxxO
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
		
		game.doCaptures(board, new Move(Stone.BLACK, 13));
		
		assertEquals(Stone.EMPTY, board.getField(10));
		assertEquals(Stone.EMPTY, board.getField(11));
		assertEquals(Stone.EMPTY, board.getField(12));
	}
	
	@Test
	public void testDoCapturesAlmostSuicide() {
		/*
		 * oooox
		 * oxxXox
		 *  ooox
		 */
		board.setField(new Move(Stone.BLACK, 0));
		board.setField(new Move(Stone.BLACK, 1));
		board.setField(new Move(Stone.BLACK, 2));
		board.setField(new Move(Stone.BLACK, 3));
		board.setField(new Move(Stone.WHITE, 4));
		board.setField(new Move(Stone.BLACK, 9));
		board.setField(new Move(Stone.WHITE, 10));
		board.setField(new Move(Stone.WHITE, 11));
		board.setField(new Move(Stone.WHITE, 12));
		board.setField(new Move(Stone.BLACK, 13));
		board.setField(new Move(Stone.WHITE, 14));
		board.setField(new Move(Stone.BLACK, 19));
		board.setField(new Move(Stone.BLACK, 20));
		board.setField(new Move(Stone.BLACK, 21));
		board.setField(new Move(Stone.WHITE, 22));
		
		game.doCaptures(board, new Move(Stone.WHITE, 12));
		
		assertEquals(Stone.EMPTY, board.getField(13));
		assertEquals(Stone.WHITE, board.getField(10));
		assertEquals(Stone.WHITE, board.getField(11));
		assertEquals(Stone.WHITE, board.getField(12));
	}
	
	@Test
	public void testDoTurn() {
		fail("Not yet implmeented");
	}
	
	@Test
	public void testGetCurrentPlayer() {
		fail("Not yet implmeented");
	}
	
	@Test
	public void testGameOverPasses() {
		fail("Not yet implmeented");
	}
	
	@Test
	public void testGameOverNoMoreStones() {
		fail("Not yet implmeented");
	}
	
	@Test
	public void testRecreatesPreviousSituation() {
		fail("Not yet implmeented");
	}

}
