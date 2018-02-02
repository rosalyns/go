package model;

import java.util.List;

public class RandomStrategy implements Strategy {

	@Override
	public String getName() {
		return "Random";
	}

	@Override
	public int determineMove(Game game, Stone color) {
		List<Integer> emptyFields = game.getBoard().getEmptyFields();
		int random = (int) (Math.random() * emptyFields.size());
		return emptyFields.get(random);
	}

}
