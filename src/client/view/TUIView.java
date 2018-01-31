package client.view;

import client.controller.GameController;
import client.controller.GoClient;
import commands.*;
import general.Extension;
import general.Protocol;
import model.Stone;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * TUIView handles the input from the user and is used to show all output to the user. Communicates
 * with the GoClient about the game state and what should be sent to the server.
 * @author Rosalyn.Sleurink
 *
 */
public class TUIView implements Runnable {
	/**
	 * State is used to decide what user input is allowed in what state of the menu.
	 * @author Rosalyn.Sleurink
	 *
	 */
	public enum State {
		INVALIDNAME, INMENU, INOPTIONMENU, SETTIMELIMIT, PICKPLAYERTYPE, WAITFORREQUEST, 
		ASKFORSETTINGS, INGAME
	}

	private State state;
	private GoClient controller;
	private GameController game;
	private String thisPlayerName; // you need the player's name before there even is a player.
	private Scanner in;

	private String menuText = "\nMENU\n" 
			+ "1: Start a new Game\n" 
			+ "2: Options\n" 
			+ "3: Show leaderboard\n" 
			+ "4: Quit";
	
	private String optionMenuText = "OPTIONS\n"
			+ "1: Set timelimit for AI\n"
			+ "2: Back";
			
	public TUIView(GoClient controller, InputStream systemIn) {
		this.controller = controller;
		this.thisPlayerName = controller.getName();
		this.state = State.INVALIDNAME;
		this.in = new Scanner(systemIn);
	}

	@Override
	public void run() {
		boolean isAI = false;
		while (controller.isRunning()) {
			boolean wrongInput = false;
			String line = readString();
			String[] words = line.split(" ");
			
			if (state == State.INVALIDNAME) {
				controller.setName(words[0]);
				new NameCommand(controller, controller.getExtensions()).send();
			} else if (state == State.INMENU) {
				if (words.length == 1 && words[0].equalsIgnoreCase("1")) {
					print("Do you want to use a computerplayer? y/n");
					state = State.PICKPLAYERTYPE;
				} else if (words.length == 1 && words[0].equalsIgnoreCase("2")) {
					state = State.INOPTIONMENU;
					showOptionMenu();
				} else if (words.length == 1 && words[0].equalsIgnoreCase("3")) {
					new LeadCommand(controller, false).send();
				} else if (words.length == 1 && words[0].equalsIgnoreCase("4")) {
					controller.shutDown();
					new ExitCommand(controller, false).send();
				} else {
					print("Enter 1, 2, 3 or 4.");
				}
			} else if (state == State.INOPTIONMENU) {
				if (words.length == 1 && words[0].equalsIgnoreCase("1")) {
					state = State.SETTIMELIMIT;
					print("The current time limit is " + controller.getTimeLimit());
					print("What do you want it to be? (in microseconds)");
				} else if (words.length == 1 && words[0].equalsIgnoreCase("2")) {
					state = State.INMENU;
					showMenu();
				} else {
					print("Enter 1 or 2.");
				}
			} else if (state == State.SETTIMELIMIT) {
				if (words.length == 1) {
					try {
						controller.setTimeLimit(Integer.parseInt(words[0]));
						state = State.INOPTIONMENU;
						showOptionMenu();
					} catch (NumberFormatException e) {
						print("Enter a number.");
					}
				}
			} else if (state == State.PICKPLAYERTYPE) {
				if (words.length == 1 && words[0].equalsIgnoreCase("y")) {
					isAI = true;
					controller.useAI(true);
					new LobbyCommand(controller, false).send();
					state = State.WAITFORREQUEST;
				} else if (words.length == 1 && words[0].equalsIgnoreCase("n")) {
					isAI = false;
					controller.useAI(false);
					new LobbyCommand(controller, false).send();
					state = State.WAITFORREQUEST;
				} else {
					print("Type \"y\" or \"n\"");
				}
			} else if (state == State.WAITFORREQUEST) {
				if (words.length == 2 && words[0].equalsIgnoreCase("REQUEST")) {
					controller.sendRequest(words[1]);
				} else {
					print("Type REQUEST <playername> or REQUEST RANDOM.");
				}
			} else if (state == State.ASKFORSETTINGS) {
				if (words.length == 3 && words[0].equalsIgnoreCase("SETTINGS")) {
					Stone color = null;
					if (words[1].equalsIgnoreCase(Protocol.General.BLACK)) {
						color = Stone.BLACK;
					} else if (words[1].equalsIgnoreCase(Protocol.General.WHITE)) {
						color = Stone.WHITE;
					} else {
						print("Specify the color as BLACK or WHITE.");
						wrongInput = true;
					}
					int boardSize = Integer.parseInt(words[2]);
					if (boardSize < 5 || boardSize > 19) {
						print("Pick a size between 5 and 19.");
						wrongInput = true;
					}
					if (!wrongInput) {
						new SettingsCommand(controller, color, boardSize).send();
					}
				} else {
					print("SETTINGS <color> <boardSize>");
				}
			} else if (state == State.INGAME) {
				if (!isAI) {
					if ((words.length == 2 || words.length == 3) 
							&& words[0].equalsIgnoreCase("MOVE")) {
						if (words.length == 2 && words[1].equalsIgnoreCase("PASS")) {
							game.tryMove(true, 0, 0);
						} else if (words.length == 3) {
							int row = Integer.parseInt(words[1]);
							int column = Integer.parseInt(words[2]);
							game.tryMove(false, row, column);
						}
					} else if (words.length == 1 && words[0].equalsIgnoreCase("QUIT")) {
						game.quit();
					} else {
						print("Type MOVE <row> <column>, MOVE PASS or QUIT.");
					}
				} else {
					if (words.length == 1 && words[0].equalsIgnoreCase("QUIT")) {
						game.quit();
					} else {
						print("You can quit by typing QUIT.");
					}
				}
			}
		}
	}

	public void startGame(GameController gc) {
		state = State.INGAME;
		this.game = gc;
	}

	public void endGame(String reason, Map<String, Integer> scores) {
		final int thisPlayer = 1;
		final int none = 0;
		final int otherPlayer = -1;
		
		if (reason.equalsIgnoreCase(EndGameCommand.ABORTED)) {
			print("The other player quit unexpectedly.");
		} else if (reason.equalsIgnoreCase(EndGameCommand.FINISHED)) {
			print("The game has finished.");
		} else if (reason.equalsIgnoreCase(EndGameCommand.TIMEOUT)) {
			print("The other player didn't respond and the game ended.");
		}
		
		int highestScore = -1;
		int result = 0;

		for (String playerStr : scores.keySet()) {
			int score = scores.get(playerStr);
			print(playerStr + " ended with " + scores.get(playerStr) + " points");
			if (score > highestScore) {
				highestScore = score;
				if (thisPlayerName.equals(playerStr)) {
					result = thisPlayer;
				} else {
					result = otherPlayer;
				}
			} else if (score == highestScore) {
				result = none;
			}
		}

		if (result == thisPlayer) {
			print("You won! :)");
		} else if (result == otherPlayer) {
			print("You didn't win. :(");
		} else if (result == none) {
			print("It was a draw.");
		}
		
		state = State.INMENU;
		showMenu();
	}

	public void askForName() {
		print("The name you entered is already in use on the server. Enter a different name: ");
	}
	
	public void askForSettings() {
		print("A game is starting with you as the first player. Please choose a color and "
				+ "boardsize by entering: SETTINGS <color> <boardSize>. Possible colors are "
				+ "BLACK or WHITE, boardsize should be between 5 and 19.");
		state = State.ASKFORSETTINGS;
	}

	public void showConnectedTo(String serverName) {
		print("You are connected to server \"" + serverName + "\".");
		state = State.INMENU;
		showMenu();
	}

	public void showLeaderboard(Map<Integer, String> scores) {
		print("The current leaderboard is:");
		int rank = 1;
		for (Integer score : scores.keySet()) {
			print(rank + ": " + scores.get(score) + "score");
		}
	}
	
	public void showUnsupportedExtension(Extension type) {
		if (type == Extension.CHALLENGE) {
			print("The server doesnt support challenges.");
		}
	}

	public void showPlayersInLobby(List<String> players) {
		String playersStr = "Players:";
		boolean playersAdded = false;
		for (String playerName : players) {
			if (!thisPlayerName.equalsIgnoreCase(playerName)) {
				playersStr += " " + playerName;
				playersAdded = true;
			}
		}
		if (playersAdded) {
			print("You can challenge one of the following players by typing REQUEST <playername>.\n"
					+ "Type REQUEST RANDOM if you don't want to challenge a specific player. ");
			print(playersStr);
		} else {
			print("There are currently no players you can challenge. You can type REQUEST RANDOM"
					+ " to be next in line to play a game.");
		}
	}

	public void showChatMessage(String playerName, String message) {
		print(playerName + ": " + message);
	}

	public void showChallengedBy(String playerName) {
		print("You have been challenged by " + playerName + ". ACCEPT or DECLINE?");
	}

	public void showChallengeDeclined(String playerName) {
		print(playerName + " declined your challenge.");
	}

	public void showPass(String playerName) {
		print(playerName + " passed.");
	}
	
	public void showInvalidMove() {
		print("This is not a valid move. Try again.");
	}
	
	public void showNotYourTurn() {
		print("Wait till it is your turn.");
	}

	public void showError(String type, String message) {
		//print("[Server] ERROR ");
		if (type.equals(ErrorCommand.INVPROTOCOL)) {
			print("The protocols of the server and client are incompatible.");
		} else if (type.equals(ErrorCommand.INVCOMMAND)) {
			print("An unknown command was sent to the server");
		} else if (type.equals(ErrorCommand.INVNAME)) {
			print("The name you entered is already in use on the server. Enter a different name: ");
		} else if (type.equals(ErrorCommand.INVMOVE)) {
			showInvalidMove();
		} else if (type.equals(ErrorCommand.OTHER)) {
			print(message);
		}
	}

	public void showMenu() {
		print(menuText);
	}
	
	public void showOptionMenu() {
		print(optionMenuText);
	}

	
	private String readString() {
		String result = null;
		if (in.hasNextLine()) {
			result = in.nextLine();
		}
		return result;
	}

	private static void print(String message) {
		System.out.println(message);
	}

	public void shutdown() {
		print("Closing connection to server.");
	}
}
