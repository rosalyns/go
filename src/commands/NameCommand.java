package commands;

import java.util.HashSet;
import java.util.Set;

import client.controller.GoClient;
import exceptions.InvalidCommandLengthException;
import general.*;
import server.controller.ClientHandler;

/**
 * CLIENT -> SERVER en SERVER -> CLIENT Het eerste commando wat de server naar
 * de client stuurt. Gaat om versie van het protocol. De volgorde van de
 * extensions is als volgt: chat challenge leaderboard security 2+ simultaneous
 * multiplemoves.<br>
 * Format: NAME clientnaam VERSION versienummer EXTENSIONS boolean boolean
 * boolean etc<br>
 * Voorbeeld: NAME piet VERSION 2 EXTENSIONS 0 0 1 1 0 0 0
 */
public class NameCommand extends Command {
	protected final String commandStr = Protocol.Client.NAME;
	protected final String versionStr = Protocol.Client.VERSION;
	protected int versionNumber = Protocol.Client.VERSIONNO;
	protected final String extensionStr = Protocol.Client.EXTENSIONS;
	private String name;
	private Set<Extension> supportedExtensions;

	public NameCommand(ClientHandler clientHandler) {
		super(clientHandler, false);
	}

	public NameCommand(ClientHandler clientHandler, String serverName, 
			Set<Extension> supportedExtensions) {
		super(clientHandler, true);
		this.supportedExtensions = supportedExtensions;
		this.name = serverName;
	}

	public NameCommand(GoClient client) {
		super(client, true);
	}

	public NameCommand(GoClient client, Set<Extension> supportedExtensions) {
		super(client, false);
		this.supportedExtensions = supportedExtensions;
		this.name = client.getName();
	}

	@Override
	public String compose() {
		String command = commandStr + delim1 + name + delim1 + versionStr + delim1 + versionNumber 
				+ delim1 + extensionStr;
		for (Extension e : Extension.values()) {
			command += delim1 + (supportedExtensions.contains(e) ? 1 : 0);
		}
		return command + commandEnd;
	}

	@Override
	public void parse(String[] words) throws InvalidCommandLengthException {
		if (words.length != 12) {
			throw new InvalidCommandLengthException();
		}
		supportedExtensions = new HashSet<Extension>();
		if (toClient) {
			int version = Integer.parseInt(words[3]);
			// words[ 6 t/m 12] bevatten extensions
			for (int i = 5; i < 12; i++) {
				int extension = Integer.parseInt(words[i]);
				if (extension == 1) {
					switch (i) {
						case 5:
							supportedExtensions.add(Extension.CHAT);
							break;
						case 6:
							supportedExtensions.add(Extension.CHALLENGE);
							break;
						case 7:
							supportedExtensions.add(Extension.LEADERBOARD);
							break;
						case 8:
							supportedExtensions.add(Extension.SECURITY);
							break;
						case 9:
							supportedExtensions.add(Extension.MOREPLAYERS);
							break;
						case 10:
							supportedExtensions.add(Extension.SIMULTANEOUS);
							break;
						case 11:
							supportedExtensions.add(Extension.MULTIMOVES);
							break;
						default:
							break;
					}
				}
			}
			client.setServerSettings(words[1], version, supportedExtensions);
		} else {
			clientHandler.setName(words[1]);
			clientHandler.checkVersion(Integer.parseInt(words[3]));
			// words[ 6 t/m 12] bevatten extensions
			for (int i = 5; i < 12; i++) {
				boolean extension = Boolean.parseBoolean(words[i]);
				if (extension) {
					switch (i) {
						case 6:
							supportedExtensions.add(Extension.CHAT);
							break;
						case 7:
							supportedExtensions.add(Extension.CHALLENGE);
							break;
						case 8:
							supportedExtensions.add(Extension.LEADERBOARD);
							break;
						case 9:
							supportedExtensions.add(Extension.SECURITY);
							break;
						case 10:
							supportedExtensions.add(Extension.MOREPLAYERS);
							break;
						case 11:
							supportedExtensions.add(Extension.SIMULTANEOUS);
							break;
						case 12:
							supportedExtensions.add(Extension.MULTIMOVES);
							break;
						default:
							break;
					}
				}
			}
			clientHandler.setExtensions(supportedExtensions);
			clientHandler.addPlayerToLobby();
		}
	}

}
