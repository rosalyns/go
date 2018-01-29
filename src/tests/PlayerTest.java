package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.*;

public class PlayerTest {
	private LocalPlayer hPlayer;
	private LocalPlayer cPlayer;
	private Board board;
	
	@Before
	public void setUp() throws Exception {
		cPlayer = new ComputerPlayer(Stone.BLACK, new RandomStrategy());
		hPlayer = new HumanPlayer(Stone.WHITE, "Dummy");
		board = new Board(9);
	}

	@Test
	public void testComputerDetermineMove() {
		Move move = cPlayer.askForMove(board);
		assertTrue(0 <= move.getPosition() && move.getPosition() < board.dim() * board.dim());
		assertTrue(board.isEmptyField(move.getPosition()));
	}
	
	/* is pas nuttig als View of Controller controleert of het een geldige move is.
	@Test
	public void testHumanDetermineMove() {
		int move = hPlayer.determineMove(board);
		assertTrue(0 <= move && move < board.dim() * board.dim());
		assertTrue(board.isEmptyField(move));
	}
	*/
	
	@Test
	public void testGetMark() {
		assertTrue(hPlayer.getColor() == Stone.WHITE);
		assertTrue(cPlayer.getColor() == Stone.BLACK);
	}

	@Test
	public void testGetName() {
		//System.out.println("Computername: " + cPlayer.getName());
		//System.out.println("Humanname: " + hPlayer.getName());
		assertEquals(hPlayer.getName(), "Dummy");
		assertTrue(cPlayer.getName().contains("Random") && cPlayer.getName().contains("BLACK"));
	}
	
	@Test
	public void testComputerSetup() {
		assertTrue(((ComputerPlayer) cPlayer).getStrategy() instanceof RandomStrategy);
		assertTrue(cPlayer.getName().contains("computer"));
	}
	
	@Test
	public void testComputerSetAndGetStrategy() {
		((ComputerPlayer) cPlayer).setStrategy(new BasicStrategy());
		assertTrue(((ComputerPlayer) cPlayer).getStrategy() instanceof BasicStrategy);
	}
}