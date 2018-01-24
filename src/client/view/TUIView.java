package client.view;

import client.controller.GoClient;
import commands.*;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;


public class TUIView implements Observer, Runnable {
	public final boolean toServer = false;
	public final boolean fromServer = true;
	
	private GoClient controller;
	
	private String helpText = "You can use the following commands....";
	
	private String menuText = "MENU\n"
			+ "1: Start a new Game\n"
			+ "2: Options\n"
			+ "3: Show leaderboard\n"
			+ "4: Chat\n"
			+ "5: Quit";
	
	
	
	
	public TUIView(GoClient controller) {
		this.controller = controller;
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Action of type \"" + arg.toString() + "\" was done.");
	}

	@Override
	public void run() {
		boolean running = true;
		while (running) {
			String line = readString("").toUpperCase();
			
			String[] words = line.split(" ");
			if (words.length == 2 && words[0].equals("REQUEST")) {
				new RequestCommand(controller, 2, words[1]).send();
				print("requesting a game with " + words[1]);
			} else if (words.length == 1 && words[0].equals("IETS")) {
				print("iets.");
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
				new LobbyCommand(controller, false).send();
			} else if (words.length == 1 && words[0].equals("MENU")) {
				showMenu();
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else if (words.length == 1 && words[0].equals("LOBBY")) {
			} else {
				print("Unknown command. Type HELP to see all possible commands.");
			}
		}
	}
	
	public void askForSettings() {
		print("A game is starting with you as the first player. Please choose a color and "
				+ "boardsize by entering: SETTINGS <color> <boardSize>. Possible colors are "
				+ "BLACK or WHITE, possible boardsizes 9, 13 or 19.");
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
		for (String player : players) {
			print(player);
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

	private static String readString(String prompt) {
		String result = null;
		System.out.println(prompt);
		if (in.hasNextLine()) {
			result = in.nextLine();
		}
		return result;
	}
	
	private static void print(String message) {
		System.out.println(message);
	}
	
	public void shutdown() {
		print("Quitting game, closing connection to server");
	}

}
