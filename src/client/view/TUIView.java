package client.view;

import client.controller.GoClient;
import commands.*;
import general.Protocol;
import model.Board;
import model.LocalPlayer;
import model.Move;
import model.Stone;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TUIView implements Runnable {
	private static final int THISPLAYER = 1;
	private static final int NONE = 0;
	private static final int OTHERPLAYER = -1;

	public enum State {
		INVALIDNAME, INMENU, INOPTIONMENU, PICKPLAYERTYPE, WAITFORREQUEST, ASKFORSETTINGS, INGAME
	}

	private State state;
	private GoClient controller;
	private LocalPlayer player;
	private String thisPlayerName; // you need the player's name before there even is a player.
	// private String helpText = "You can use the following commands....";
	private int boardDim;

	private String menuText = "MENU\n" 
			+ "1: Start a new Game\n" 
			+ "2: Options\n" 
			+ "3: Show leaderboard\n" 
			+ "4: Quit";
	
	private String optionMenuText = "OPTIONS\n"
			+ "1: Set timeout time"
			+ "2: Back";
			
	public TUIView(GoClient controller) {
		this.controller = controller;
		this.thisPlayerName = controller.getName();
		this.state = State.INVALIDNAME;
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
					new QuitCommand(controller, false).send();
				}
			} else if (state == State.INOPTIONMENU) {
				if (words.length == 1 && words[0].equalsIgnoreCase("1")) {
					//TODO timeout Time;
				} else if (words.length == 1 && words[0].equalsIgnoreCase("2")) {
					state = State.INMENU;
					showMenu();
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
					if (words[1].equalsIgnoreCase(RequestCommand.RANDOM)) {
						new RequestCommand(controller, 2, RequestCommand.RANDOM).send();
					} else {
						new RequestCommand(controller, 2, words[1]).send();
					}
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
				}
			} else if (state == State.INGAME) {
				if (!isAI) {
					if ((words.length == 2 || words.length == 3) 
							&& words[0].equalsIgnoreCase("MOVE")) {
						if (player.hasTurn()) {
							if (words.length == 2 && words[1].equalsIgnoreCase("PASS")) {
								new MoveCommand(controller, true, 0, 0).send();
							} else if (words.length == 3) {
								int row = Integer.parseInt(words[1]);
								int column = Integer.parseInt(words[2]);
								if (controller.isValidMove(new Move(player.getColor(), 
										Board.index(row, column, boardDim)))) {
									new MoveCommand(controller, false, row, column).send();
								} else {
									print("This is not a valid move. Try again.");
								}
							}
						} else {
							print("Wait till it is your turn.");
						}
					} else if (words.length == 1 && words[0].equalsIgnoreCase("QUIT")) {
						new QuitCommand(controller, false).send();
					} else {
						print("Unknown command. Type HELP to see all possible commands.");
					}
				} else {
					if (words.length == 1 && words[0].equalsIgnoreCase("QUIT")) {
						new QuitCommand(controller).send();
					}
				}
			}
		}
	}

	public void startGame(LocalPlayer thisPlayer, int boardSize) {
		state = State.INGAME;
		this.boardDim = boardSize;
		this.player = thisPlayer;
	}

	public void endGame(String reason, Map<String, Integer> scores) {
		state = State.INMENU;
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
					result = THISPLAYER;
				} else {
					result = OTHERPLAYER;
				}
			} else if (score == highestScore) {
				result = NONE;
			}
		}

		if (result == THISPLAYER) {
			print("You won! :)");
		} else if (result == OTHERPLAYER) {
			print("You didn't win. :(");
		} else if (result == NONE) {
			print("It was a draw.");
		}
		
		showMenu();
	}

	public void askForName() {
		print("The name you entered is already in use on the server. Enter a different name: ");
	}
	
	public void askForSettings() {
		print("A game is starting with you as the first player. Please choose a color and "
				+ "boardsize by entering: SETTINGS <color> <boardSize>. Possible colors are "
				+ "BLACK or WHITE, possible boardsizes 9, 13 or 19.");
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

	public void showError(String type, String message) {
		print(message);
	}

	public void showMenu() {
		print(menuText);
	}
	
	public void showOptionMenu() {
		print(optionMenuText);
	}

	private static Scanner in = new Scanner(System.in);

	private static String readString() {
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
