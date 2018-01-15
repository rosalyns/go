package client.model;

public enum Mark {
	BLACK, WHITE, EMPTY;

	/*
	 * @ ensures this == Mark.BLACK ==> \result == Mark.WHITE; ensures this == Mark.WHITE ==>
	 * \result == Mark.BLACK; ensures this == Mark.EMPTY ==> \result == Mark.EMPTY;
	 */
	/**
	 * Returns the other mark.
	 * @return the other mark is this mark is not EMPTY or EMPTY
	 */
	public Mark other() {
		if (this == BLACK) {
			return WHITE;
		} else if (this == WHITE) {
			return BLACK;
		} else {
			return EMPTY;
		}
	}
}
