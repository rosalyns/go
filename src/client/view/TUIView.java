package client.view;

import client.controller.GoClient;
import commands.*;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;


public class TUIView implements Observer, Runnable {
	private GoClient controller;
	
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
			String line = readString("What is your command? "
					+ "Type HELP if you want to see all the commands. ").toUpperCase();
			
			String[] words = line.split(" ");
			if (words.length == 2 && words[0].equals("REQUEST")) {
				//controller.sendRequestToServer(words[1]);
				print("requesting a game with " + words[1]);
			} else if (words.length == 1 && words[0].equals("IETS")) {
				print("iets.");
			} else {
				print("Unknown command");
			}
		}
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
		//Als errortype is INVNAME, kun je om nieuwe naam vragen en opnieuw hallo zeggen. 
		print(message);
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
