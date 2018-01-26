package client.view;

import client.controller.GoClient;
import commands.*;
import general.Protocol;
import model.Board;
import model.Move;
import model.Player;
import model.HumanPlayer;
import model.Stone;

import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class TUIView implements Runnable {
	private static final int THISPLAYER = 1;
	private static final int NONE = 0;
	private static final int OTHERPLAYER = -1;
	
	private GoClient controller;
	private boolean inGame = false;
	private Player player;
	private int boardDim;
	private String helpText = "You can use the following commands....";
	
	private String menuText = "MENU\n"
			+ "1: Start a new Game\n"
			+ "2: Show leaderboard\n"
			+ "3: Quit";
	
	public TUIView(GoClient controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		boolean running = true;
		inGame = false;
		boolean isAI = false;
		showMenu();
		while (running) {
			boolean wrongInput = false;
			String line = readString();
			String[] words = line.split(" ");
			
			if (!inGame) {
				if (words.length == 2 && words[0].equalsIgnoreCase("REQUEST")) {
					new RequestCommand(controller, 2, words[1]).send();
				} else if (words.length == 3 && words[0].equalsIgnoreCase("SETTINGS")) {
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
					if (boardSize != 9 && boardSize != 13 && boardSize != 19) {
						print("Possible boardsizes are: 9, 13, 19.");
						wrongInput = true;
					}
					if (!wrongInput) {
						new SettingsCommand(controller, color, boardSize).send();
					}
				} else if (words.length == 1 && words[0].equalsIgnoreCase("1")) {
					print("Do you want to use a computerplayer? y/n");
				} else if (words.length == 1 && words[0].equalsIgnoreCase("2")) {
					new LeadCommand(controller, false).send();
				} else if (words.length == 1 && words[0].equalsIgnoreCase("3")) {
					controller.shutdown();
					new QuitCommand(controller, false).send();
				} else if (words.length == 1 && words[0].equalsIgnoreCase("y")) {
					isAI = true;
					controller.useAI(true);
					new LobbyCommand(controller, false).send();
				} else if (words.length == 1 && words[0].equalsIgnoreCase("n")) {
					isAI = false;
					controller.useAI(false);
					new LobbyCommand(controller, false).send();
				} else {
					print("Unknown command. Type HELP to see all possible commands.");
				}
			} else if (!isAI) {
				HumanPlayer hPlayer = (HumanPlayer) player;
				if ((words.length == 2 || words.length == 3) && words[0].equalsIgnoreCase("MOVE")) {
					if (hPlayer.hasToTakeTurn()) {
						if (words.length == 2 && words[1].equalsIgnoreCase("PASS")) {
							controller.makeMove(new Move(hPlayer.getColor(), Move.PASS));
							hPlayer.madeMove();
							new MoveCommand(controller, true, 0, 0).send();
						} else {
							int row = Integer.parseInt(words[1]); 
							int column = Integer.parseInt(words[2]);
							//TODO check if valid Move....
							controller.makeMove(new Move(hPlayer.getColor(), 
									Board.index(row, column, boardDim)));
							hPlayer.madeMove();
							new MoveCommand(controller, false, row, column).send();
						}
					} else {
						print("Wait till it is your turn.");
					}
				} else if (words.length == 1 && words[0].equalsIgnoreCase("QUIT")) {
					controller.quitGame();
					new QuitCommand(controller, false).send();
				} else {
					print("Unknown command. Type HELP to see all possible commands.");
				}
			} else {
				if (words.length == 1 && words[0].equalsIgnoreCase("QUIT")) {
					controller.quitGame();
					new QuitCommand(controller).send();
				} 
			}
		}
	}
	
	public void startGame(Player thisPlayer, int boardSize) {
		this.boardDim = boardSize;
		inGame = true;
		this.player = thisPlayer;
	}
	
	public void endGame(String reason, Map<String, Integer> scores) {
		inGame = false;
		if (reason.equalsIgnoreCase(EndGameCommand.ABORTED)) {
			print("The other player quit unexpectedly.");
		} else if (reason.equalsIgnoreCase(EndGameCommand.FINISHED)) {
			print("The game has finished.");
		} else if (reason.equalsIgnoreCase(EndGameCommand.TIMEOUT)) {
			print("The other player didn't respond and the game ended.");
		}
		
		int highestScore = -1;
		int result = 0;
		String winner = "";
		
		for (String playerName : scores.keySet()) {
			int score = scores.get(playerName);
			print(playerName + " ended with " + scores.get(playerName) + " points");
			if (score > highestScore) {
				highestScore = score;
				if (controller.getName().equals(playerName)) {
					result = THISPLAYER;
					winner = playerName;
				} else {
					result = OTHERPLAYER;
					winner = playerName;
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
	
	public void askForSettings() {
		print("A game is starting with you as the first player. Please choose a color and "
				+ "boardsize by entering: SETTINGS <color> <boardSize>. Possible colors are "
				+ "BLACK or WHITE, possible boardsizes 9, 13 or 19.");
	}
	
	public void showConnectedTo(String serverName) {
		print("You are connected to server \"" + serverName + "\".");
	}
	
	public void showLeaderboard(Map<Integer, String> scores) {
		print("The current leaderboard is:");
		int rank = 1;
		for (Integer score : scores.keySet()) {
			print(rank + ": " + scores.get(score) + "score");
		}
	}
	
	public void showPlayersInLobby(List<String> players) {
		print("Players in lobby: ");
		for (String playerName : players) {
			print(playerName);
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

	public void showError(String type, String message) {
		//TODO: Als errortype is INVNAME, kun je om nieuwe naam vragen en opnieuw hallo zeggen. 
		print(message);
	}
	
	public void showMenu() {
		print(menuText);
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
