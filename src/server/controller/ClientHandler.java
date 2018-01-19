package server.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import commands.*;
import exceptions.InvalidCommandLengthException;
import general.Protocol;

public class ClientHandler extends Thread {
	private final boolean toClient = false;
	
	private GoServer server;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private Socket client;
	private boolean[] extensions;
	private Map<String, Command> incomingCommands;
	
	/**
	 * Constructs a ClientHandler object Initialises both Data streams.
	 */
	// @ requires serverArg != null && sockArg != null;
	public ClientHandler(GoServer serverArg, Socket sockArg) throws IOException {
		this.server = serverArg;
		this.client = sockArg;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
		
		incomingCommands = new HashMap<String, Command>();
		incomingCommands.put(Protocol.Server.NAME, new NameCommand(this));
	}

	/**
	 * This method takes care of sending messages from the Client. Every message
	 * that is received, is preprended with the name of the Client, and the new
	 * message is offered to the Server for broadcasting. If an IOException is
	 * thrown while reading the message, the method concludes that the socket
	 * connection is broken and shutdown() will be called.
	 */
	public void run() {
		String command = "";
		try {
			while ((command = in.readLine()) != null) {
				for (String commandStr : incomingCommands.keySet()) {
					if (command.startsWith(commandStr)) {
						try {
							server.handleCommandFromClient(incomingCommands.get(commandStr), command); 
						} catch (InvalidCommandLengthException e) {
							new ErrorCommand(this, ErrorCommand.INVCOMMAND, "Number of arguments is not valid.").send(false);
						}
					}
				}
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	public void checkVersion(int version) {
		if (version != Protocol.Server.VERSIONNO) {
			new ErrorCommand(this, ErrorCommand.INVPROTOCOL, "").send(toClient);
		}
	}
	
	public void setExtensions(boolean[] extensions) {
		this.extensions = extensions;
	}
	
	/**
	 * This method can be used to send a message over the socket connection to the
	 * Client. If the writing of a message fails, the method concludes that the
	 * socket connection has been lost and shutdown() is called.
	 */
	public void sendCommandToClient(String command) {
		try {
			out.write(command + "\n");
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * This ClientHandler signs off from the Server and subsequently sends a last
	 * broadcast to the Server to inform that the Client is no longer participating
	 * in the chat.
	 */
	private void shutdown() {
		server.removeHandler(this);
		server.broadcast("[" + clientName + " has left]");
		sendCommandToClient("Server shut down.");
		try {
			client.close();
		} catch (IOException e) {
		}
	}
}
