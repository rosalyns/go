package client.controller;

import model.*;
import client.view.*;
import commands.*;
import exceptions.InvalidCommandLengthException;
import general.Extension;
import general.Protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.nedap.go.gui.GOGUI;
import com.nedap.go.gui.GoGUIIntegrator;

public class GoClient extends Thread {
	
	// --------------- MAIN METHOD ---------------
	
	/**
	 * Asks the user for name, IP address and port number. This method will try to connect 
	 * on the given IP address and port number. If it can't connect it will terminate.
	 * @param args
	 */
	public static void main(String[] args) {
		print("Enter your name: ");
		String clientName = readString();
		
		print("On which IP Address do you want to connect?");
		InetAddress host = null;
		
		try {
			host = InetAddress.getByName(readString());
		} catch (UnknownHostException e) {
			System.err.println("ERROR: invalid hostname!");
			System.exit(0);
		}
		
		print("On which port do you want to connect? Enter 0 to use the default port.");
		int port = -1;
		try {
			port = Integer.parseInt(readString());
			if (port == 0) {
				port = Protocol.General.DEFAULT_PORT;
			}
		} catch (NumberFormatException e) {
			System.err.println("ERROR: invalid portnummer!");
			System.exit(0);
		}
		
		try {
			Socket sock = new Socket(host, port);
			GoClient client = new GoClient(sock, clientName);
			client.run();
		} catch (IOException e) {
			System.err.println("ERROR: couldn't construct a client object!");
			System.exit(0);
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
	
	public enum AI {
		NONE, BASIC, RANDOM
	}
	
	private AI useAI;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private TUIView view;
	private GOGUI gogui;
	private GameController game;
	private boolean streamsClosed;
	private Map<String, Command> incomingCommands;
	private int protocolVersion;
	private Set<Extension> supportedExtensions;
	private Set<Extension> supportedServerExtensions;
	private int timeLimit;
	
	/**
	 * Constructs a client object with the standard System in for user input and
	 * the input and outputstream of the given socket.
	 * @param socket The socket that is connected to a server. 
	 * @param name Name of the user
	 * @throws IOException if it can't get the input and outputstream of the socket.
	 */
	public GoClient(Socket socket, String name) throws IOException {
		this(System.in, socket.getOutputStream(), socket.getInputStream(), name);
		this.sock = socket;
	}
	
	/**
	 * Constructs a client object. Initializes the GUI for the Board
	 * state and the TUI to process user input from systemIn.
	 * @param systemIn Where the user input should be read from.
	 * @param socketOut Where the commands are sent to.
	 * @param socketIn Where the Server input should be read from.
	 * @param name Name of the user
	 */
	public GoClient(InputStream systemIn, OutputStream socketOut, InputStream socketIn, 
			String name) {
		this.protocolVersion = Protocol.Client.VERSIONNO;
		this.supportedExtensions = new HashSet<Extension>();
		//TODO: op het eind: extensions nog toevoegen.
		supportedExtensions.add(Extension.CHAT);
		
		this.gogui = new GoGUIIntegrator(false, true, 9);
		this.setName(name);
		this.view = new TUIView(this, systemIn);
		this.out = new BufferedWriter(new OutputStreamWriter(socketOut));
		this.in = new BufferedReader(new InputStreamReader(socketIn));
		this.streamsClosed = false;
		this.timeLimit = 2000;
		
		incomingCommands = new HashMap<String, Command>();
		incomingCommands.put(Protocol.Server.NAME, new NameCommand(this));
		incomingCommands.put(Protocol.Server.START, new StartCommand(this));
		incomingCommands.put(Protocol.Server.TURN, new TurnCommand(this));
		incomingCommands.put(Protocol.Server.ENDGAME, new EndGameCommand(this));
		incomingCommands.put(Protocol.Server.ERROR, new ErrorCommand(this));
		incomingCommands.put(Protocol.Server.REQUESTGAME, new RequestCommand(this));
		incomingCommands.put(Protocol.Server.DECLINED, new DeclinedCommand(this));
		incomingCommands.put(Protocol.Server.LOBBY, new LobbyCommand(this));
		incomingCommands.put(Protocol.Server.CHAT, new ChatCommand(this));
		incomingCommands.put(Protocol.Server.LEADERBOARD, new LeadCommand(this));
	}
	
	/**
	 * Starts the GUI and the TUI. Sends the name of the client to the Server
	 * along with the extensions this client supports and the version of the protocol
	 * it uses. After that it will read from the socket inputstream to parse any commands
	 * that come from the Server. When it receives null, the socket is closed or it catches
	 * an IOException when trying to read from the socketinput it will shut down.
	 */
	public void run() {
		Thread viewThread = new Thread(view);
		viewThread.setName(this.getName() + "-view");
		viewThread.start();
		gogui.startGUI();
		
		new NameCommand(this, supportedExtensions).send();
		
		try {
			String socketInput = "";
			while ((socketInput = in.readLine()) != null && this.isRunning()) {
				String[] words = socketInput.split("\\" + Protocol.General.DELIMITER1);
				try {
					Command command = incomingCommands.get(words[0]);
					if (command != null) {
						command.parse(words);
					}
				} catch (InvalidCommandLengthException e) {
					e.printStackTrace();
				}
			}
			shutDown();
		} catch (IOException e) {
			shutDown();
		}	
	}
	
	/**
	 * Tries to send a command to the Server. When it gives an exception it shuts down.
	 * @param command Command to be sent to the server
	 */
	public void sendCommandToServer(String command) {
		try {
			out.write(command);
			out.flush();
		} catch (IOException e) {
			shutDown();
		}
	}
	
	/**
	 * 
	 */
	public void setTimeLimit(int microseconds) {
		this.timeLimit = microseconds;
	}
	
	public int getTimeLimit() {
		return timeLimit;
	}
	/**
	 * Shows the leaderboard in the TUI.
	 * @param scores collection of scores and playernames from the server
	 */
	public void showLeaderboard(Map<Integer, String> scores) {
		view.showLeaderboard(scores);
	}
	
	/**
	 * Shows the players available for challenges.
	 * @param players in lobby
	 */
	public void showPlayersInLobby(List<String> players) {
		view.showPlayersInLobby(players);
	}
	
	/**
	 * Show chat message in the TUI.
	 * @param playerName Player that sent the cat
	 * @param message Message that the player sent
	 */
	public void showChatMessage(String playerName, String message) {
		view.showChatMessage(playerName, message);
	}
	
	/**
	 * Is called when the client receives a NAME command from the Server.
	 * @param serverName name of the server
	 * @param serverVersion protocol version that the server uses
	 * @param serverExtensions extensions that the server supports
	 */
	public void setServerSettings(String serverName, int serverVersion, 
			Set<Extension> serverExtensions) {
		this.supportedServerExtensions = serverExtensions;
		if (this.protocolVersion != serverVersion) {
			handleError(ErrorCommand.INVPROTOCOL, "Protocol versions from client and "
					+ "server are incompatible. Update your protocol.");
		}
		
		view.showConnectedTo(serverName);
	}
	
	public Set<Extension> getExtensions() {
		return this.supportedExtensions;
	}
	
	public void useAI(boolean ai) {
		if (ai) {
			this.useAI = AI.RANDOM;
		} else {
			this.useAI = AI.NONE;
		}
	}
	
	public void setStrategy(AI ai) {
		this.useAI = ai;
	}
	
	public void sendRequest(String challengee) {
		if (!supportedServerExtensions.contains(Extension.CHALLENGE)) {
			if (!challengee.equalsIgnoreCase(RequestCommand.RANDOM)) {
				view.showUnsupportedExtension(Extension.CHALLENGE);
			}
			new RequestCommand(this, 2, RequestCommand.RANDOM).send();
		} else {
			new RequestCommand(this, 2, challengee).send();
		}
	}
	
	public void sendChat(String msg) {
		if (supportedServerExtensions.contains(Extension.CHAT)) {
			new ChatCommand(this, msg).send();
		} else {
			view.showUnsupportedExtension(Extension.CHAT);
		}
	}
	
	/**
	 * Challenge extension is not fully implemented.
	 * @param playerName
	 */
	public void challengedBy(String playerName) {
		view.showChallengedBy(playerName);
	}
	
	public void declined(String playerName) {
		view.showChallengeDeclined(playerName);
	}
	
	/**
	 * Asks the user to enter the size of the board and the color it wants to play with 
	 * and sends this command to the server.
	 */
	public void askForSettings() {
		view.askForSettings();
	}
	
	/**
	 * Handles the ERROR from the server depending on the type of Error. The 
	 * error will always be shown to the view.
	 * @param type The type of error
	 * @param message An error message that the server constructed
	 */
	public void handleError(String type, String message) {
		view.showError(type, message);
		if (type.equals(ErrorCommand.INVPROTOCOL) || type.equals(ErrorCommand.INVCOMMAND)) {
			view.shutdown();
			shutDown();
		} else if (type.equals(ErrorCommand.INVMOVE)) {
			game.askForMove();
		}
	}
	
	public void handleError(String type) {
		view.showError(type, "Something went wrong on the server.");
	}
	
	/**
	 * Queries the state of the socket. If it is not closed, it means you can still send and 
	 * receives input and output on the streams.
	 * @return true if socket is not closed, false if it is closed.
	 */
	public boolean isRunning() {
		return !streamsClosed;
	}
	
	/**
	 * Tries to stop the GUI and closes the socket. Then terminates the whole program because
	 * the GUI can't close properly.
	 */
	public void shutDown() {
		gogui.stopGUI();
		streamsClosed = true;
		try {
			if (sock != null) {
				sock.close();
			}
		} catch (IOException e) {
		} finally {
			System.exit(0);
		}
	}
	
	/**
	 * Starts a game with the user. The user has already decided if he wants to
	 * use an AI in the TUI. 
	 * @param opponent The name of the opponent
	 * @param boardSize The size of the board
	 * @param playerColor The color the local player will use during this game
	 */
	public void startGame(String opponent, int boardSize, Stone playerColor) {
		game = new GameController(this, gogui, view);
		game.start(useAI, opponent, boardSize, playerColor);
	}
	
	public GameController getGameController() {
		return game;
	}
}
