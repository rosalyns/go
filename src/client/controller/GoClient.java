package client.controller;

import model.*;
import client.view.*;
import commands.*;
import exceptions.InvalidCommandLengthException;
import general.Extension;
import general.Protocol;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nedap.go.gui.GOGUI;
import com.nedap.go.gui.GoGUIIntegrator;
import com.nedap.go.gui.InvalidCoordinateException;

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
		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
			System.exit(0);
		}
	}
	
	private static void print(String msg) {
		System.out.println(msg);
	}
	
	// --------------- CLASS METHODS ---------------
	
	public final boolean toServer = false;
	public final boolean fromServer = true;
	
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private TUIView view;
	private String name;
	private String serverName;
	private int protocolVersion;
	private Map<String, Command> incomingCommands;
	private Set<Extension> extensions;
	private Set<Extension> serverExtensions;
	//private Map<String, Command> outgoingCommands;
	private Board board;
	private Player player;
	private String opponentName;

	private GOGUI gogui;
	
	public GoClient(String name, InetAddress host, int port) throws IOException {
		this.protocolVersion = Protocol.Client.VERSIONNO;
		this.extensions = new HashSet<Extension>();
		//TODO: op het eind: extensions nog toevoegen.
		
		this.sock = new Socket(host, port);
		this.setName(name);
		this.view = new TUIView(this);
		this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), 
				Protocol.General.ENCODING));
		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream(), 
				Protocol.General.ENCODING));
		
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
		
		this.gogui = new GoGUIIntegrator(false, true, 9);
		//TODO: addObservers to relevant classes (that are Observables)
	}
	
	public void run() {
		Thread viewThread = new Thread(view);
		viewThread.start();
		
		boolean keepGoing = true;
		
		new NameCommand(this, extensions).send();
		
		while (keepGoing) {
			try {
				String socketInput = in.readLine();
				System.out.println(this.getName() + " received the command " + socketInput);
				if (socketInput != null) {
					//System.out.println(message);
					
					for (String command : incomingCommands.keySet()) {
						if (socketInput.startsWith(command)) {
							try {
								incomingCommands.get(command).parse(socketInput);
							} catch (InvalidCommandLengthException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					System.out.println("stahp it");
					keepGoing = false;
				}
			} catch (IOException e) {
				keepGoing = false;
			}
		}
		shutdown();
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
	
	public void showLeaderboard(Map<Integer, String> scores) {
		view.showLeaderboard(scores);
	}
	
	public void showPlayersInLobby(List<String> players) {
		view.showPlayersInLobby(players);
	}
	
	public void showChatMessage(String playerName, String message) {
		view.showChatMessage(playerName, message);
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public void setServerExtensions(Set<Extension> supportedExtensions) {
		this.serverExtensions = supportedExtensions;
	}
	
	public void checkVersion(int serverVersion) {
		if (this.protocolVersion != serverVersion) {
			handleError(ErrorCommand.INVPROTOCOL, "Protocol versions from client and "
					+ "server are incompatible. Update your protocol.");
		}
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
		view.showError(reason, message);
		if (reason.equals(ErrorCommand.INVPROTOCOL)) {
			view.shutdown();
			shutdown();
		}
	}
	
	public void shutdown() {
		//TODO
	}
	
	// -----game interaction methods-------
	public void startGame(String opponent, int boardSize, Stone playerColor) {
		this.opponentName = opponent;
		board = new Board(boardSize);
		//TODO: hier ergens AI aangeven?
		this.player = new HumanPlayer(playerColor, name);
		gogui.startGUI();
	}
	
	public int getBoardDim() {
		return board.dim();
	}
	
	public Stone getColor(String playerName) {
		if (playerName.equals(player.getName())) {
			return player.getColor();
		}
		return player.getColor().other();
	}
	
	public void makeMove(Move move) {
		if (move.getPosition() != Move.PASS) {
			board.setField(move);
			try {
				Point coordinates = Board.indexToCoordinates(move.getPosition(), getBoardDim());
				gogui.addStone(coordinates.y, coordinates.x, move.getColor() == Stone.WHITE);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void nextPlayer(String playerName) {
		if (playerName.equals(player.getName())) {
			player.askForMove(board);
		}
	}
	
	public void endGame(String reason, Map<String, Integer> scores) { 
		//TODO
	}
	
	// ------game logic-------
	public void doCaptures(Move move) {
		Stone playerColor = move.getColor();
		Stone opponentColor = playerColor.other();
		List<Set<Integer>> groupsToRemove = new ArrayList<Set<Integer>>();
		for (Set<Integer> group : board.getGroups().get(opponentColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(group, opponentColor);
		}
		
		for (Set<Integer> group : board.getGroups().get(playerColor)) {
			if (!board.hasLiberties(group)) {
				groupsToRemove.add(group);
			}
		}
		
		for (Set<Integer> group : groupsToRemove) {
			removeGroup(group, opponentColor);
		}
	}
	
	public void removeGroup(Set<Integer> group, Stone color) {
		for (Integer field : group) {
			board.setField(new Move(Stone.EMPTY, field));
			try {
				Point coordinates = Board.indexToCoordinates(field, getBoardDim());
				gogui.removeStone(coordinates.y, 
						coordinates.x);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		}
		board.getGroups().get(color).remove(group);
	}
}
