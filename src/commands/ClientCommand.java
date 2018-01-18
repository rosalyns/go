package commands;

import client.controller.GoClient;

public abstract class ClientCommand extends Command {
	protected GoClient client;
	
	public ClientCommand(GoClient client) {
		this.client = client;
	}

	@Override
	public void send() {
		client.sendCommandToServer(this.compose());
	}
}
