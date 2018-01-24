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
import java.util.Set;

import commands.*;
import exceptions.InvalidCommandLengthException;
import exceptions.PlayerNotFoundException;
import general.Extension;
import general.Protocol;
import model.Move;
import model.NetworkPlayer;
import model.Stone;

public class ClientHandler extends Thread {
	public final boolean toClient = true;
	public final boolean fromClient = true;
	
	private Lobby lobby;
	private GameController game;
	private boolean inGame;
	private NetworkPlayer player;
	private BufferedReader in;
	private BufferedWriter out;
	private Socket client;
	private Set<Extension> supportedExtensions;
	private Map<String, Command> incomingCommands;
	
	/**
	 * Constructs a ClientHandler object Initialises both Data streams.
	 */
	public ClientHandler(Lobby lobby, Socket sockArg) throws IOException {
		this.lobby = lobby;
		
		this.client = sockArg;
		this.inGame = false;
		
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
		
		incomingCommands = new HashMap<String, Command>();
		incomingCommands.put(Protocol.Client.NAME, new NameCommand(this));
		incomingCommands.put(Protocol.Client.MOVE, new MoveCommand(this));
		incomingCommands.put(Protocol.Client.SETTINGS, new SettingsCommand(this));
		incomingCommands.put(Protocol.Client.QUIT, new QuitCommand(this));
		incomingCommands.put(Protocol.Client.REQUESTGAME, new RequestCommand(this));
		incomingCommands.put(Protocol.Client.ACCEPTGAME, new AcceptCommand(this));
		incomingCommands.put(Protocol.Client.DECLINEGAME, new DeclineCommand(this));
		incomingCommands.put(Protocol.Client.LOBBY, new LobbyCommand(this));
		incomingCommands.put(Protocol.Client.CHAT, new ChatCommand(this));
		incomingCommands.put(Protocol.Client.LEADERBOARD, new LeadCommand(this));
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
				System.out.println(this.getName() + " received the command " + command);
				for (String commandStr : incomingCommands.keySet()) {
					if (command.startsWith(commandStr)) {
						try {
							incomingCommands.get(commandStr).parse(command); 
						} catch (InvalidCommandLengthException e) {
							new ErrorCommand(this, ErrorCommand.INVCOMMAND, 
									"Number of arguments is not valid.").send();
						}
					}
				}
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	
	// -------lobby interaction methods--------
	public void checkVersion(int version) {
		if (version != Protocol.Server.VERSIONNO) {
			new ErrorCommand(this, ErrorCommand.INVPROTOCOL, "").send();
		}
	}
	
	public void setExtensions(Set<Extension> extensions) {
		this.supportedExtensions = extensions;
	}
	
	public Set<Extension> getExtensions() {
		return supportedExtensions;
	}
	
	public void challenge(int numberOfPlayers, String playerName) throws PlayerNotFoundException {
		lobby.challenge(this, playerName);
	}
	
	public void acceptGame(String playerName) {
		//TODO: start new game
	}
	
	public void declineGame(String playerName) throws PlayerNotFoundException {
		//TODO
	}
	
	public void announce() {
		lobby.addPlayer(this);
		lobby.announce(this.getName());
	}
	
	public Map<Integer, String> getLeaderboard() {
		return lobby.getLeaderBoard();
	}
	
	public List<String> getPlayersInLobby() {
		return lobby.getFreePlayers();
	}
	
	public void handleChatMessage(String message) {
//		if (thisPlayer.inGame()) {
//			//send to players in game
//		} else {
//			//send to players in lobby
//			server.getLobby().chat(clientName, message);
//		}
	}
	
	// --------game interaction methods---------
	public void makeMove(boolean pass, int row, int column) {
		game.doMove(this, new Move(player.getColor(), row * game.getBoardDim() + column));
	}
	
	public void setGame(GameController game) {
		this.game = game;
		this.inGame = true;
	}
	
	public void setGameSettings(Stone color, int boardSize) {
		game.setSettings(color, boardSize);
	}
	
	public void setPlayer(NetworkPlayer player) {
		this.player = player;
	}
	
	public NetworkPlayer getPlayer() {
		return player;
	}
	
	public void quitGame() {
		this.inGame = false;
		//TODO en nog wat
	}
	
	//-------other methods------
	public boolean isInGame() {
		return this.inGame;
	}
	
	/**
	 * This method can be used to send a message over the socket connection to the
	 * Client. If the writing of a message fails, the method concludes that the
	 * socket connection has been lost and shutdown() is called.
	 */
	public void sendCommandToClient(String command) {
		try {
			System.out.println("Sending \"" + command + "\" to client " + this.getName());
			out.write(command);
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
		lobby.removePlayer(this);
		// TODO: replace with something lobby
		// server.broadcast("[" + this.getName() + " has left]");
		// sendCommandToClient("Server shut down.");
		try {
			client.close();
		} catch (IOException e) {
		}
	}
}
