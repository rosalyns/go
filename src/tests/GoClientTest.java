package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.controller.GoClient;

class GoClientTest {
	
	private GoClient client;
	
	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void testSetServerSettings() {
		String dataServer = "NAME$Rosalyn-Server$VERSION$3$EXTENSIONS$0$0$0$0$0$0$0\n";
		String dataClient = "NAME$Rosalyn$VERSION$3$EXTENSIONS$0$0$0$0$0$0$0\n";
		String dataTUI = "1\n";
		dataTUI += "n\n";
		dataTUI += "request random\n";
		
		InputStream socketInput = new ByteArrayInputStream(dataServer.getBytes());
		OutputStream socketOutput = new ByteArrayOutputStream();
		InputStream consoleInput = new ByteArrayInputStream(dataTUI.getBytes());
		client = new GoClient(consoleInput, socketOutput, socketInput, "Rosalyn");
	}

	@Test
	void testUseAI() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testStartGame() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testGetColor() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testMakeMove() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testIsValidMove() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testNextPlayer() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testEndGame() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	void testDoCaptures() {
		fail("Not yet implemented"); // TODO
	}

}
