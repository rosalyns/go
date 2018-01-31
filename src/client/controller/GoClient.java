package client.controller;

import model.*;
import client.view.*;
import commands.*;
import exceptions.InvalidBoardSizeException;
import exceptions.InvalidCommandLengthException;
import general.Extension;
import general.Protocol;

import java.awt.Point;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.nedap.go.gui.GOGUI;
import com.nedap.go.gui.GoGUIIntegrator;
import com.nedap.go.gui.InvalidCoordinateException;

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
			print("ERROR: invalid hostname!");
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
			print("ERROR: invalid portnummer!");
			System.exit(0);
		}
		
		try {
			Socket sock = new Socket(host, port);
			GoClient client = new GoClient(sock, clientName);
			client.run();
		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
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
	
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private TUIView view;
	private GOGUI gogui;
	private String currentPlayer;
	private String opponentName;
	private Board gameBoard;
	private LocalPlayer player;
	private boolean useAI;
	private boolean streamsClosed;
	private Map<String, Command> incomingCommands;
	private int protocolVersion;
	private Set<Extension> supportedExtensions;
	private Set<Extension> supportedServerExtensions;
	
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
		
		this.gogui = new GoGUIIntegrator(false, true, 9);
		this.setName(name);
		this.view = new TUIView(this, systemIn);
		this.out = new BufferedWriter(new OutputStreamWriter(socketOut));
		this.in = new BufferedReader(new InputStreamReader(socketIn));
		this.streamsClosed = false;
		
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
				System.out.println(this.getName() + " received the command " + socketInput);
				for (String command : incomingCommands.keySet()) {
					if (socketInput.startsWith(command)) {
						try {
							incomingCommands.get(command).parse(socketInput);
						} catch (InvalidCommandLengthException e) {
							e.printStackTrace();
						}
					}
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
			System.out.println("Sending \"" + command + "\" to server");
			out.write(command);
			out.flush();
		} catch (IOException e) {
			shutDown();
		}
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
		this.useAI = ai;
	}
	
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
			player.askForMove(gameBoard);
		}
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
	
	// -----game interaction methods-------
	
	/**
	 * Starts a game with the user. Initializes a human player or an AI, the user
	 * already decided this in the TUI. The GUI Board is cleared to remove any previous
	 * board situations and the view is updated so it can take game related input.
	 * @param opponent The name of the opponent
	 * @param boardSize The size of the board
	 * @param playerColor The color the local player will be during this game
	 */
	public void startGame(String opponent, int boardSize, Stone playerColor) {
		this.opponentName = opponent;
		try {
			gameBoard = new Board(boardSize);
		} catch (InvalidBoardSizeException e1) {
			//comes from server, not likely
		}
		if (useAI) {
			this.player = new ComputerPlayer(playerColor, this.getName(), this);
		} else {
			this.player = new HumanPlayer(playerColor, this.getName());
		}

		gogui.clearBoard();
		try {
			gogui.setBoardSize(boardSize);
		} catch (InvalidCoordinateException e) {
		}
		
		view.startGame();
	}
	
	public int getBoardDim() {
		return gameBoard.dim();
	}
	
	/**
	 * Get the color that the player with this name is using.
	 * @param playerName name of the player you want to know the color of
	 * @return Stone that can be Stone.BLACK or Stone.WHITE.
	 */
	public Stone getColor(String playerName) {
		if (playerName.equals(player.getName())) {
			return player.getColor();
		}
		return player.getColor().other();
	}
	
	/**
	 * Performs a move on the board as a response to a TURN command. Adds the stone to the GUI
	 * and places it on the board that this client uses. If the player passed it shows the pass
	 * on the TUI. It also updates the player so that it can't make a move.
	 * @param move Move that the server sent. Contains color of the stone and the position.
	 */
	public void makeMove(Move move) {
		if (move.getPosition() != Move.PASS) {
			try {
				Point coordinates = Board.indexToCoordinates(move.getPosition(), getBoardDim());
				gogui.addStone(coordinates.x, coordinates.y, move.getColor() == Stone.WHITE);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
			placeStone(gameBoard, move);
		} else {
			if (currentPlayer.equalsIgnoreCase(opponentName)) {
				view.showPass(opponentName);
			}
		}
		player.madeMove();
	}
	
	/**
	 * Tries a move that the user entered in the TUI. Sends the move command to the server if:
	 * it's the player turn and it's a valid move. If it's not a valid move the TUI will wait
	 * for a new input.
	 * @param pass true if the player passed
	 * @param row the row the player wants to place a stone in
	 * @param column the column the player wants to place a stone in
	 */
	public void tryMove(boolean pass, int row, int column) {
		if (player.hasTurn()) {
			if (pass) {
				new MoveCommand(this, true, 0, 0).send();
			} else if (isValidMove(new Move(player.getColor(), 
					Board.index(row, column, getBoardDim())))) {
				new MoveCommand(this, false, row, column).send();
			} else {
				view.showInvalidMove();
				
			}
		} else {
			view.showNotYourTurn();
		}
	}
	
	/**
	 * Checks if the move is valid.
	 * @param move Move to be checked. Contains color of the stone and position.
	 * @return true if the field on the board is empty
	 */
	public boolean isValidMove(Move move) {
		return gameBoard.isField(move.getPosition()) && gameBoard.isEmptyField(move.getPosition());
	}
	
	/**
	 * Sets the next player of the game. If the next player is this player, it asks the player to 
	 * make a move. If the player is an AI it will not show on the TUI. It will also place a hint on
	 * the GUI board if an AI is not used.
	 * @param playerName
	 */
	public void nextPlayer(String playerName) {
		if (playerName.equals(player.getName())) {
			player.askForMove(gameBoard);
			
			if (!useAI) {
				int hint = new RandomStrategy().determineMove(gameBoard, player.getColor());
				Point hintPoint = Board.indexToCoordinates(hint, gameBoard.dim());
				try {
					gogui.removeHintIdicator();
					gogui.addHintIndicator(hintPoint.x, hintPoint.y);
				} catch (InvalidCoordinateException e) {
					e.printStackTrace();
				}
			}
		}
		currentPlayer = playerName;
	}
	
	/**
	 * Shows the scores and the player that won.
	 * @param reason Reason that the game ended. FINISHED, ABORTED or TIMEOUT
	 * @param scores The end scores.
	 */
	public void endGame(String reason, Map<String, Integer> scores) {
		view.endGame(reason, scores);
	}
	
	// ------game logic-------
	/**
	 * Places the stone on the board, and if the stone captured a group or committed 
	 * suicide, the relevant stones will be captured.
	 * @param board Board that the stone should be placed on
	 * @param move The move that was made
	 */
	private void placeStone(Board board, Move move) {
		board.setField(move);
		doCaptures(board, move);
	}
	
	/**
	 * Checks if any groups are captured, first for the opponent (the color that is not
	 * in move) and then for the move itself (this means it was a suicide). Then removes
	 * the groups that were captured. 
	 * @param board To be checked for captures
	 * @param move The move that was made
	 */
	public void doCaptures(Board board, Move move) {
		Stone playerColor = move.getColor();
		Stone opponentColor = playerColor.other();
		List<Set<Integer>> groupsToRemove = new ArrayList<Set<Integer>>();
		for (Set<Integer> group : board.getGroups().get(opponentColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(board, group, opponentColor);
		}
		
		for (Set<Integer> group : board.getGroups().get(playerColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(board, group, opponentColor);
		}
	}
	
	/**
	 * Removes the group from the board.
	 * @param board Board that the stones should be removed from
	 * @param group That should be removed
	 * @param color Of the group that should be removed.
	 */
	private void removeGroup(Board board, Set<Integer> group, Stone color) {
		for (Integer field : group) {
			board.setField(new Move(Stone.EMPTY, field));
			try {
				Point coordinates = Board.indexToCoordinates(field, getBoardDim());
				gogui.removeStone(coordinates.x, coordinates.y);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		}
		board.getGroups().get(color).remove(group);
	}
}
