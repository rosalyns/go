package client.controller;

import client.view.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GoClient extends Thread {
	
	// --------------- MAIN METHOD ---------------
	
	private static final String USAGE = "usage: java week7.cmdchat.Client <name> <address> <port>";

	public static void main(String[] args) {
		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			print("ERROR: invalid hostname!");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			print("ERROR: invalid portnummer!");
			System.exit(0);
		}
		
		try {
			GoClient client = new GoClient(args[0], host, port);
			client.sendMessage(args[0]);
			client.start();

			/*
			do {
				//TODO: misschien nodig?
				client.sendMessage("iets?");
			} while (!client.isConnected());
			*/

		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
			System.exit(0);
		}

	}
	
	// --------------- CLASS METHODS ---------------
	
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private TUIView view;
	
	public GoClient(String name, InetAddress host, int port) throws IOException {
		sock = new Socket(host, port);
		this.clientName = name;
		this.view = new TUIView(this);
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		//addObservers to relevant classes (that are Observables)
	}
	
	public void run() {
		Thread viewThread = new Thread(view);
		viewThread.start();
		
		boolean keepGoing = true;
		while (keepGoing) {
			try {
				String message = in.readLine();
				if (message != null) {
					print(message);
				} else {
					keepGoing = false;
				}
			} catch (IOException e) {
				keepGoing = false;
			}
		}
		shutdown();
	}
	
	public boolean isConnected() {
		return sock.isClosed();
	}
	
	private static void print(String message) {
		System.out.println(message);
	}
	
	public void sendMessage(String msg) {
		//TODO: aanpassen naar iets dat naar server stuurt.
		try {
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}
	public void sendRequestToServer(String playerName) {
		String gameRequest = "REQUEST";
		gameRequest += "_" + playerName;
		gameRequest += "\n\n";
		
		sendCommandToServer(gameRequest);
	}
	
	public void sendCommandToServer(String command) {
		try {
			System.out.println("Sending \"" + command + "\" to server");
			out.write(command);
			out.flush();
		} catch (IOException e) {
			System.out.println("In sendCommandToServer: There is no server to send a message to.");
		}
	}
	
	public void shutdown() {
		
	}
}
