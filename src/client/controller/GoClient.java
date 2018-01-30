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
	private Map<String, Command> incomingCommands;
	private int protocolVersion;
	private Set<Extension> supportedExtensions;
	private Set<Extension> supportedServerExtensions;
	private InputStream systemIn;
	
	public GoClient(Socket socket, String name) throws IOException {
		this(System.in, socket.getOutputStream(), socket.getInputStream(), name);
	}
	
	public GoClient(InputStream systemIn, OutputStream socketOut, InputStream socketIn, String name) {
		this.protocolVersion = Protocol.Client.VERSIONNO;
		this.supportedExtensions = new HashSet<Extension>();
		//TODO: op het eind: extensions nog toevoegen.
		
		this.gogui = new GoGUIIntegrator(false, true, 9);
		gogui.startGUI();
		
		this.setName(name);
		this.view = new TUIView(this, systemIn);
		this.out = new BufferedWriter(new OutputStreamWriter(socketOut));
		this.in = new BufferedReader(new InputStreamReader(socketIn));
		
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
	
	public void run() {
		Thread viewThread = new Thread(view);
		viewThread.setName(this.getName() + "-view");
		viewThread.start();
		
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
			System.out.println("stahp it");
			shutDown();
		} catch (IOException e) {
			shutDown();
		}	
	}
	
	public void sendCommandToServer(String command) {
		try {
			System.out.println("Sending \"" + command + "\" to server");
			out.write(command);
			out.flush();
		} catch (IOException e) {
			shutDown();
		}
	}
	
	public void showLeaderboard(Map<Integer, String> scores) {
		view.showLeaderboard(scores);
	}
	
	public void showPlayersInLobby(List<String> players) {
		view.showPlayersInLobby(players);
	}
	
	public void showChatMessage(String playerName, String message) {
		view.showChatMessage(playerName, message);
	}
	
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
	
	public void askForSettings() {
		view.askForSettings();
	}
	
	public void handleError(String reason, String message) {
		if (reason.equals(ErrorCommand.INVPROTOCOL)) {
			view.showError(reason, message);
			view.shutdown();
			shutDown();
		} else if (reason.equals(ErrorCommand.INVNAME)) {
			view.askForName();
		} else if (reason.equals(ErrorCommand.INVMOVE)) {
			view.showError(reason, message);
			player.askForMove(gameBoard);
		}
	}
	
	public boolean isRunning() {
		return !sock.isClosed();
	}
	
	public void shutDown() {
		gogui.stopGUI();
		try {
			sock.close();
		} catch (IOException e) {
		} finally {
			System.exit(0);
		}
	}
	
	// -----game interaction methods-------
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
		
		view.startGame(player, boardSize);
		
	}
	
	public int getBoardDim() {
		return gameBoard.dim();
	}
	
	public Stone getColor(String playerName) {
		if (playerName.equals(player.getName())) {
			return player.getColor();
		}
		return player.getColor().other();
	}
	
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
	
	public boolean isValidMove(Move move) {
		return gameBoard.isField(move.getPosition()) && gameBoard.isEmptyField(move.getPosition());
	}
	
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
	
	public void endGame(String reason, Map<String, Integer> scores) {
		view.endGame(reason, scores);
	}
	
	// ------game logic-------
	private void placeStone(Board board, Move move) {
		board.setField(move);
		doCaptures(board, move);
	}
	
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
