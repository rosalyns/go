package client.controller;

import client.view.*;
import general.Protocol;

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
		if (args.length != 3) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
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
	
	private static void print(String msg) {
		System.out.println(msg);
	}
	
	// --------------- CLASS METHODS ---------------
	
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	//private String clientName;
	private TUIView view;
	
	public GoClient(String name, InetAddress host, int port) throws IOException {
		sock = new Socket(host, port);
		//this.clientName = name;
		this.view = new TUIView(this);
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), Protocol.General.ENCODING));
		in = new BufferedReader(new InputStreamReader(sock.getInputStream(), Protocol.General.ENCODING));
		
		//addObservers to relevant classes (that are Observables)
	}
	
	public void run() {
		Thread viewThread = new Thread(view);
		viewThread.start();
		boolean keepGoing = true;
		
		//sendHello.. ?
		
		while (keepGoing) {
			try {
				String message = in.readLine();
				if (message != null) {
					System.out.println(message);
					readCommand(message);
				} else {
					//keepGoing = false;
				}
			} catch (IOException e) {
				keepGoing = false;
			}
		}
		shutdown();
	}
	
	public void sendRequestToServer(String playerName) {
		String gameRequest = Protocol.Client.REQUESTGAME;
		gameRequest += Protocol.General.DELIMITER1 + 2;
		gameRequest += Protocol.General.DELIMITER1 + Protocol.Client.RANDOM;
		gameRequest += Protocol.General.COMMAND_END;
		
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
