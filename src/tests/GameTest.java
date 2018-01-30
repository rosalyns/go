package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import exceptions.*;
import model.Board;
import model.ComputerPlayer;
import model.Game;
import model.Move;
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
		player1 = new ComputerPlayer(Stone.BLACK, "Player1");
		player2 = new ComputerPlayer(Stone.WHITE, "Player2");
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
	public void testDoTurnNotYourTurn() {
		assertThrows(NotYourTurnException.class, () -> game.doTurn(new Move(Stone.WHITE, 0)));
		
		try {
			game.doTurn(new Move(Stone.BLACK, 0));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDoTurnTwoPasses() {
		assertFalse(game.ended());
		try {
			game.doTurn(new Move(Stone.BLACK, Move.PASS));
			
			assertFalse(game.ended());
			
			game.doTurn(new Move(Stone.WHITE, Move.PASS));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		assertTrue(game.ended());
	}
	
	@Test
	public void testDoTurnViolateKoRule() {
		/*
		 *  ox
		 * oxOx
		 *  ox
		 */
		try {
			game.doTurn(new Move(Stone.BLACK, 1));
			game.doTurn(new Move(Stone.WHITE, 2));
			game.doTurn(new Move(Stone.BLACK, 9));
			game.doTurn(new Move(Stone.WHITE, 10));
			game.doTurn(new Move(Stone.BLACK, 19));
			game.doTurn(new Move(Stone.WHITE, 20));
			game.doTurn(new Move(Stone.BLACK, 80));
			game.doTurn(new Move(Stone.WHITE, 12));
			game.doTurn(new Move(Stone.BLACK, 11));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		assertThrows(KoException.class, () -> game.doTurn(new Move(Stone.WHITE, 10)));
	}
	
	@Test
	public void testDoTurnValid() {
		try {
			game.doTurn(new Move(Stone.BLACK, 0));
			game.doTurn(new Move(Stone.WHITE, 1));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		
		assertEquals(Stone.BLACK, board.getField(0));
		assertEquals(Stone.WHITE, board.getField(1));
	}
	
	@Test
	public void testGetCurrentPlayer() {
		assertEquals("Player1", game.getCurrentPlayer());
		
		try {
			game.doTurn(new Move(Stone.BLACK, Move.PASS));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		
		assertEquals("Player2", game.getCurrentPlayer());
	}
	
	@Test
	public void testGameOverNoMoreStones() {
		try {
			for (int i = 0; i < (board.dim() * board.dim()) / 2; i++) {
				game.doTurn(new Move(Stone.BLACK, i));
				game.doTurn(new Move(Stone.WHITE, Move.PASS));
			}
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		
		assertFalse(game.ended());
		
		try {
			game.doTurn(new Move(Stone.BLACK, 80));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		
		assertTrue(game.ended());
	}
	
	@Test
	public void testRecreatesPreviousSituation() {
		/*
		 *  ox
		 * oxOx
		 *  ox
		 */
		try {
			game.doTurn(new Move(Stone.BLACK, 1));
			game.doTurn(new Move(Stone.WHITE, 2));
			game.doTurn(new Move(Stone.BLACK, 9));
			game.doTurn(new Move(Stone.WHITE, 10));
			game.doTurn(new Move(Stone.BLACK, 19));
			game.doTurn(new Move(Stone.WHITE, 20));
			game.doTurn(new Move(Stone.BLACK, 80));
			game.doTurn(new Move(Stone.WHITE, 12));
			game.doTurn(new Move(Stone.BLACK, 11));
		} catch (KoException | NotYourTurnException | InvalidCoordinateException e) {
			e.printStackTrace();
		}
		assertTrue(game.recreatesPreviousSituation(new Move(Stone.WHITE, 10)));
	}

}
