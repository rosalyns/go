package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.controller.GoClient;

class GoClientTest {
	
	private GoClient client;
	private BufferedReader clientToServer;
	private PipedOutputStream serverToClient;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	PrintStream stdout = System.out;
	
	String dataTUI = "1\nn\nrequestrandom\n";
	
	@BeforeEach
	void setUp() throws Exception {
		
		System.setOut(new PrintStream(outContent));
		
		serverToClient = new PipedOutputStream();
        PipedInputStream inputPis = new PipedInputStream(serverToClient);
        PipedOutputStream outputPos = new PipedOutputStream();
        PipedInputStream outputPis = new PipedInputStream(outputPos);
        clientToServer = new BufferedReader(new InputStreamReader(outputPis));
        client = new GoClient(System.in, outputPos, inputPis, "Rosalyn");

		client.start();
        
	}

	private void sendAsServer(String msg) {
		new PrintStream(serverToClient).println(msg);
	}
	
	/**
	 * When the protocols are not compatible, the Client shuts down altogether with 
	 * a System.exit(0). This also terminates the whole test. Therefore the first
	 * line is commented out. If there would be a better solution then System.exit(0)
	 * it can be executed and the test should pass.
	 */
	@Test
	void testCommandNameWrongProtocol() {
		//sendAsServer("NAME$Rosalyn-Server$VERSION$5$EXTENSIONS$0$0$0$0$0$0$0\n");
		
        try {
			System.out.println("test read: " + clientToServer.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        assertTrue(outContent.toString().contains("The protocols of the server "
        		+ "and client are incompatible."));
	}
	
	@Test
	void testNameCommandCorrect() {
		sendAsServer("NAME$Rosalyn-Server$VERSION$6$EXTENSIONS$0$0$0$0$0$0$0");
		System.setOut(stdout);
		System.out.println(outContent.toString());
		//assertTrue(outContent.toString().contains("Rosalyn-Server"));
		
	}
	
	@AfterEach
	void tearDown() {
		System.setOut(stdout);
	}

	
}