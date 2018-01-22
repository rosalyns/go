package server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GoServer {

	private static final String USAGE = "usage: " + GoServer.class.getName() + " <port>";

	/** Start een Server-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}

		GoServer server = new GoServer(Integer.parseInt(args[0]));
		server.run();
	}

	private int port;
	private List<ClientHandler> clients;
	private ServerSocket sock;
	private Lobby lobby;
	
	/** Constructs a new Server object. */
	public GoServer(int portArg) {
		clients = new ArrayList<ClientHandler>();
		this.port = portArg;
		lobby = new Lobby(this);
		lobby.start();
	}
	/**
	 * Listens to a port of this Server if there are any Clients that would like to
	 * connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() {
		try {
			System.out.println("Opening a socket on port " + port);
			sock = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean keepRunning = true;
		while (keepRunning) {
			try {
				System.out.println("Server is waiting on client to connect...");
				Socket client = sock.accept();
				System.out.println("Client connected on the server.");
				ClientHandler clientHandler = new ClientHandler(this, client);
				clientHandler.start();
				lobby.addPlayer(clientHandler);
			} catch (IOException e) {
				//e.printStackTrace();
				keepRunning = false;
			}
		}
	}

	public void print(String message) {
		System.out.println(message);
	}

	/**
	 * Sends a message using the collection of connected ClientHandlers to all
	 * connected Clients.
	 * 
	 * @param msg message that is send
	 */
	public synchronized void broadcast(String msg) {
		print(msg);
		for (ClientHandler ch : clients) {
			ch.sendCommandToClient(msg);
		}
	}
	
	public Lobby getLobby() {
		return lobby;
	}
	
	public boolean isRunning() {
		return !sock.isClosed();
	}

}
