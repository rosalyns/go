package commands;

import java.util.List;

import client.controller.GoClient;
import general.Protocol;

public class QuitCommand extends ClientCommand {

	public final String commandStr = Protocol.Client.QUIT;
	
	public QuitCommand(GoClient client) {
		super(client);
	}

	public static void execute() {
		// TODO Auto-generated method stub
		
	}

	public static List<Object> parse(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String compose() {
		return commandStr;
	}

}
