package commands;

import server.controller.GoServer;

public abstract class ServerCommand extends Command {
	protected GoServer server;
	
	public ServerCommand(GoServer server) {
		this.server = server;
	}

	@Override
	public void send() {
		server.sendCommandToClient(this.compose());
	}

}
