package commands;

public abstract class Command {
	protected static final String COMMAND_STR = "";
	
	public abstract String compose();
	public abstract void send();

}
