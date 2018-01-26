package model;

public class BasicStrategy implements Strategy {

	@Override
	public String getName() {
		return "Basic";
	}

	@Override
	public int determineMove(Board b, Stone m) {
		// TODO slimmere AI
		return 0;
	}

}
