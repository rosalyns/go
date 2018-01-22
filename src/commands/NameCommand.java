package commands;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.Protocol;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER en SERVER -> CLIENT
* Het eerste commando wat de server naar de client stuurt. Gaat om versie
* van het protocol. De volgorde van de extensions is als volgt: 
* chat challenge leaderboard security 2+ simultaneous multiplemoves.<br>
* Format: NAME clientnaam VERSION versienummer EXTENSIONS boolean boolean boolean etc<br>
* Voorbeeld: NAME piet VERSION 2 EXTENSIONS 0 0 1 1 0 0 0
*/
public class NameCommand extends Command {
	protected final String commandStr = Protocol.Client.NAME;
	protected final String versionStr = Protocol.Client.VERSION;
	protected int versionNumber = Protocol.Client.VERSIONNO;
	protected final String extensionStr = Protocol.Client.EXTENSIONS;
	private String name;
	private boolean[] extensions;
	

	public NameCommand(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
	public NameCommand(ClientHandler clientHandler, boolean[] extensions) {
		super(clientHandler);
		this.extensions = extensions;
		this.name = clientHandler.getName();
	}

	public NameCommand(GoClient client) {
		super(client);
	}
	
	public NameCommand(GoClient client, boolean[] extensions) {
		super(client);
		this.extensions = extensions;
		this.name = client.getName();
	}

	@Override
	public String compose(boolean toClient) {
		//commando zelfde voor server en client.
		String command = commandStr + delim1 + name + delim1 +  versionStr 
						+ delim1 + versionNumber + delim1 + extensionStr;
		for (int i = 0; i < extensions.length; i++) {
			command += delim1 + (extensions[i] ? 1 : 0);
		}
		return command + commandEnd;
	}

	@Override
	public void parse(String command, boolean fromServer) throws InvalidCommandLengthException {
		String[] words = command.split("\\" + delim1);
		if (words.length != 12) {
			throw new InvalidCommandLengthException();
		}
		
		if (fromServer) {
			client.setServerName(words[1]);
			client.checkVersion(Integer.parseInt(words[3]));
			extensions = new boolean[7];
			for (int i = 0; i < extensions.length; i++) {
				extensions[i] = Boolean.parseBoolean(words[i + 5]);
			}
			client.setServerExtensions(extensions);
		} else {
			clientHandler.setName(words[1]);
			clientHandler.checkVersion(Integer.parseInt(words[3]));
			extensions = new boolean[7];
			for (int i = 0; i < extensions.length; i++) {
				extensions[i] = Boolean.parseBoolean(words[i + 5]);
			}
			clientHandler.setExtensions(extensions);
		}
	}

}
