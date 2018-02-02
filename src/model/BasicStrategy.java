package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicStrategy implements Strategy {

	private int turn = 0;
	// niet elke keer dezelfde beurt opsturen als die invalid is
	// 
	@Override
	public String getName() {
		return "Basic";
	}

	@Override
	public int determineMove(Game game, Stone color) {
		turn++;
		List<Integer> possibleMoves = new ArrayList<Integer>();
		List<Integer> validFields = game.getValidMoves(color);
		
		possibleMoves.addAll(validFields);
		int random = (int) (Math.random() * possibleMoves.size());
		if (turn < game.getBoardDim() * game.getBoardDim() / 8) {
			return validFields.get(random); 
		}
		
		for (int i : possibleMoves) {
			Board copiedBoard = game.getBoard().deepCopy();
			Move move = new Move(color, i);
			copiedBoard.setField(move);
			if (game.capturedGroup(copiedBoard, move)) {
				return i;
			}
		}
		
		return possibleMoves.get(random);
	}

}
