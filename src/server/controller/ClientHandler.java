package server.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commands.*;
import exceptions.InvalidCommandLengthException;
import exceptions.PlayerNotFoundException;
import general.Protocol;
import server.model.Stone;

public class ClientHandler extends Thread {
	public final boolean toClient = true;
	public final boolean fromClient = false;
	
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
							incomingCommands.get(commandStr).parse(command, fromClient); 
						} catch (InvalidCommandLengthException e) {
							new ErrorCommand(this, ErrorCommand.INVCOMMAND, "Number of arguments is not valid.").send(toClient);
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
	
	public void makeMove(boolean pass, int row, int column) {
		//TODO
	}
	
	public void setGame(Stone color, int boardSize) {
		//TODO
	}
	
	public void quitGame() {
		//TODO
	}
	
	public void challenge(int numberOfPlayers, String playerName) throws PlayerNotFoundException {
		ClientHandler ch = server.getLobby().findPlayer(playerName);
		new RequestCommand(ch, clientName).send(toClient);
	}
	
	public void acceptGame(String playerName) {
		//TODO: start new game
	}
	
	public void declineGame(String playerName) throws PlayerNotFoundException {
		ClientHandler ch = server.getLobby().findPlayer(playerName);
		new DeclinedCommand(ch, clientName).send(toClient);
	}
	
	public Map<Integer, String> getLeaderboard() {
		return this.server.getLobby().getLeaderBoard();
	}
	
	public List<String> getPlayersInLobby() {
		return this.server.getLobby().getFreePlayers();
	}
	
	public void handleChatMessage(String message) {
		if (thisPlayer.inGame()) {
			//send to players in game
		} else {
			//send to players in lobby
			server.getLobby().chat(clientName, message);
		}
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
