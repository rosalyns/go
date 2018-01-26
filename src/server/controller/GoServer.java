package server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import general.Extension;
import general.Protocol;

public class GoServer {

	public static void main(String[] args) {
		print("Enter a name for the server: ");
		String name = readString();
		
		print("On which port do you want to connect? Enter 0 to use the default port.");
		int port = -1;
		boolean validPort = false;
		
		while (!validPort) {
			try {
				port = Integer.parseInt(readString());
				if (port == 0) {
					port = Protocol.General.DEFAULT_PORT;
				}
			} catch (NumberFormatException e) {
				print("Enter a number.");
				continue;
			}
			
			try {
				GoServer server = new GoServer(name, port);
				server.run();
			} catch (IOException e) {
				print("This port is already in use or doesn't exist. Try another one.");
			}
		}
	}
	
	private static Scanner consoleIn = new Scanner(System.in);

	private static String readString() {
		String result = null;
		if (consoleIn.hasNextLine()) {
			result = consoleIn.nextLine();
		}
		return result;
	}
	
	private static void print(String msg) {
		System.out.println(msg);
	}

	// --------------- CLASS METHODS ---------------
	
	private int port;
	private ServerSocket sock;
	private Lobby lobby;
	private String name;
	private Set<Extension> supportedExtensions;
	
	/** Constructs a new Server object. */
	public GoServer(String name, int portArg) {
		this.port = portArg;
		this.name = name;
		this.supportedExtensions = new HashSet<Extension>();
		//TODO: op het eind: extensions nog toevoegen.
	}
	/**
	 * Listens to a port of this Server if there are any Clients that would like to
	 * connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() throws IOException {
		System.out.println("Opening a socket on port " + port);
		sock = new ServerSocket(port);
		
		lobby = new Lobby(this);
		lobby.start();
		
		boolean keepRunning = true;
		while (keepRunning) {
			try {
				System.out.println("Server is waiting on client to connect...");
				Socket client = sock.accept();
				System.out.println("Client connected on the server.");
				ClientHandler clientHandler = new ClientHandler(lobby, client);
				clientHandler.start();
			} catch (IOException e) {
				keepRunning = false;
			}
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<Extension> getExtensions() {
		return this.supportedExtensions;
	}
	
	public boolean isRunning() {
		return !sock.isClosed();
	}

}
