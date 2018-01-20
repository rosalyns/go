package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;
/**
 * Als het spel klaar is om welke reden dan ook. Reden kan zijn FINISHED (normaal einde), 
 * ABORTED (abrupt einde) of TIMEOUT (geen respons binnen redelijke tijd)<br>
 * Format: ENDGAME reden winspeler score verliesspeler score<br>
 * Voorbeeld: ENDGAME FINISHED piet 12 jan 10
 */
public class EndGameCommand extends Command {
	protected final String commandStr = Protocol.Server.ENDGAME;
	public static final String FINISHED = Protocol.Server.FINISHED;
	public static final String ABORTED = Protocol.Server.ABORTED;
	public static final String TIMEOUT = Protocol.Server.TIMEOUT;
	private String endReason;
	private String winningPlayer;
	private String losingPlayer;
	private int winningScore;
	private int losingScore;
	
	public EndGameCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public EndGameCommand(ClientHandler clientHandler, String reason, 
			String winningPlayer, int winningScore, 
			String losingPlayer, int losingScore) {
		super(clientHandler);
		this.endReason = reason;
		this.winningPlayer = winningPlayer;
		this.winningScore = winningScore;
		this.losingPlayer = losingPlayer;
		this.losingScore = losingScore;
	}

	public EndGameCommand(GoClient client) {
		super(client);
	}

	@Override
	public String compose(boolean toClient) {
		String command = commandStr + delim1 + endReason + delim1 + winningPlayer + delim1 + 
						winningScore + delim1 + losingPlayer + delim1 + losingScore + commandEnd;
		return command;
	}

	@Override
	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		// TODO Auto-generated method stub
		
	}

}
