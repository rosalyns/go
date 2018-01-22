package commands;

import java.util.HashMap;
import java.util.Map;
import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER
 * Om de leaderboard op te vragen. Overige queries moet je afspreken met anderen die ook 
 * leaderboard willen implementeren.<br>
 * Format: LEADERBOARD<br>
 * Voorbeeld: LEADERBOARD
 */

/**
 * SERVER -> CLIENT
 * Reactie op LEADERBOARD van client. Stuurt de beste 10 scores naar één client.
 * Overige queries moet je afspreken met anderen die ook 
 * leaderboard willen implementeren.<br>
 * Format: LEADERBOARD naam1 score1 naam2 score2 naam3 score3 enz<br>
 * Voorbeeld: LEADERBOARD piet 1834897 jan 2 koos 1
 */
public class LeadCommand extends Command {
	protected final String commandStr = "";
	private Map<Integer, String> scores;
	
	public LeadCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public LeadCommand(ClientHandler clientHandler, Map<Integer, String> scores) {
		super(clientHandler);
		this.scores = scores;
	}

	public LeadCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose(boolean toClient) {
		String command = commandStr;
		
		if (toClient) {
			int playersAdded = 0;
			for (Integer score : scores.keySet()) {
				command += delim1 + scores.get(score) + delim1 + score;
				playersAdded++;
				if (playersAdded == 10) {
					break;
				}
			}
		}
		return command + commandEnd;
	}

	@Override
	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (fromServer) {
			if (words.length < 21) {
				throw new InvalidCommandLengthException();
			}
			scores = new HashMap<Integer, String>();
			for (int i = 0; i < 10; i += 2) {
				scores.put(Integer.parseInt(words[2 + i]), words[1 + i]);
			}
			client.showLeaderboard(this.scores);
		} else {
			new LeadCommand(clientHandler, 
					clientHandler.getLeaderboard()).send(clientHandler.toClient);
		}
		
	}

}
