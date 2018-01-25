package exceptions;

public class InvalidCommandLengthException extends Exception {

	public InvalidCommandLengthException() {
	}

	public InvalidCommandLengthException(String message) {
		super(message);
	}

}
