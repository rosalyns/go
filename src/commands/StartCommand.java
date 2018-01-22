package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER
 * Een spel starten. Dit stuur je naar de eerste speler. <br>
 * Format: START aantalspelers (naar speler 1)<br>
 * Format: START aantalspelers kleur bordgrootte speler1 speler2 (3, etc..) 
 * (naar alle spelers)<br>
 * Voorbeeld: START 2 of START 2 BLACK 19 jan piet
 */
public class StartCommand extends Command {

	public StartCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}

	public StartCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose(boolean toClient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parse(String command, boolean toClient) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
		
	}

}
