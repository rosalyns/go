package server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import commands.*;

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

	public final boolean toClient = true;
	public final boolean fromClient = false;
	
	private int port;
	private List<ClientHandler> threads;
	private ServerSocket sock;
	private Lobby lobby;
	
	
	/** Constructs a new Server object. */
	public GoServer(int portArg) {
		threads = new ArrayList<ClientHandler>();
		
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
				addHandler(clientHandler);
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
		for (ClientHandler ch : threads) {
			ch.sendCommandToClient(msg);
		}
	}
	
	public Map<String, Integer> getLeaderboard() {
		return lobby.getLeaderBoard();
	}
	
	public List<String> getPlayersInLobby() {
		return lobby.getFreePlayers();
	}
	
	public void chatInLobby(String name, String message) {
		for (String player : getPlayersInLobby()) {
			ClientHandler ch = findPlayer(player);
			new ChatCommand(ch, name, message).send(toClient);
		}
	}
	
	public ClientHandler findPlayer(String name) {
		//TODO
		return null;
	}
	

	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * @param handler ClientHandler that will be added
	 */
	public void addHandler(ClientHandler handler) {
		threads.add(handler);
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders.
	 * @param handler ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		threads.remove(handler);
	}
	
	public boolean isRunning() {
		return !sock.isClosed();
	}

}
