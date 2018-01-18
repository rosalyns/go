package server.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.controller.GoClient;
import commands.*;

public class ClientHandler extends Thread {
	private GoServer server;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private Socket client;

	/**
	 * Constructs a ClientHandler object Initialises both Data streams.
	 */
	// @ requires serverArg != null && sockArg != null;
	public ClientHandler(GoServer serverArg, Socket sockArg) throws IOException {
		this.server = serverArg;
		this.client = sockArg;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
	}

	/**
	 * Reads the name of a Client from the input stream and sends a broadcast
	 * message to the Server to signal that the Client is participating in the chat.
	 * Notice that this method should be called immediately after the ClientHandler
	 * has been constructed.
	 */
	public void announce() throws IOException {
		clientName = in.readLine();
		this.setName(clientName);
	}

	/**
	 * This method takes care of sending messages from the Client. Every message
	 * that is received, is preprended with the name of the Client, and the new
	 * message is offered to the Server for broadcasting. If an IOException is
	 * thrown while reading the message, the method concludes that the socket
	 * connection is broken and shutdown() will be called.
	 */
	public void run() {
		String message = "";
		try {
			while ((message = in.readLine()) != null && !message.equals("exit")) {
				server.broadcast(clientName + ": " + message);
				
				//readCommand
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * This method can be used to send a message over the socket connection to the
	 * Client. If the writing of a message fails, the method concludes that the
	 * socket connection has been lost and shutdown() is called.
	 */
	public void sendMessage(String msg) {
		try {
			out.write(msg + "\n");
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
		try {
			client.close();
		} catch (IOException e) {
		}
	}
}
