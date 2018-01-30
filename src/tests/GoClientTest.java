package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.controller.GoClient;

class GoClientTest {
	
	private GoClient client;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	InputStream socketInput;
	OutputStream socketOutput;
	InputStream consoleInput;
	String wrongDataServer = "NAME$Rosalyn-Server$VERSION$6$EXTENSIONS$0$0$0$0$0$0$0\n";
	String correctDataServer = "NAME$Rosalyn-Server$VERSION$3$EXTENSIONS$0$0$0$0$0$0$0\n";
	String dataTUI = "1\nn\nrequestrandom\n";
	
	@BeforeEach
	void setUp() throws Exception {
		System.setOut(new PrintStream(outContent));
		socketInput = new ByteArrayInputStream(correctDataServer.getBytes());
		socketOutput = new ByteArrayOutputStream();
		consoleInput = new ByteArrayInputStream(dataTUI.getBytes());
		client = new GoClient(consoleInput, socketOutput, socketInput, "Rosalyn");
	}

	@Test
	void testCommandName() {
		client.start();
		System.out.println("voorbij de start");
		String dataClient = "NAME$Rosalyn$VERSION$3$EXTENSIONS$0$0$0$0$0$0$0\n";
		assertEquals(dataClient, socketOutput.toString());
		assertTrue(outContent.toString().contains("Rosalyn-serverkklhkljh"));
	}

	
}