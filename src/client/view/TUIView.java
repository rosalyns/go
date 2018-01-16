package client.view;

import client.controller.GoClient;

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
				controller.sendRequestToServer(words[1]);
				print("requesting a game with " + words[1]);
			} else if (words.length == 1 && words[0].equals("IETS")) {
				print("iets.");
			} else {
				print("Unknown command");
			}
		}
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

}
