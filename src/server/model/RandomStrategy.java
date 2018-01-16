package server.model;

import java.util.List;

public class RandomStrategy implements Strategy {

	@Override
	public String getName() {
		return "Random";
	}

	@Override
	public int determineMove(Board b, Stone m) {
		List<Integer> emptyFields = b.getEmptyFields();
		int random = (int) Math.random() * emptyFields.size();
		return emptyFields.get(random);
	}

}
