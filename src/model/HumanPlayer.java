package model;

public class HumanPlayer extends LocalPlayer {
	

	public HumanPlayer(Stone color, String name) {
		super(name);
		this.setColor(color);
	}
	
	@Override
	public void askForMove(Game game) {
		super.askForMove(game);
		System.out.println("You are " + this.getColor() + " and it's your turn. "
				+ "Where do you want to place a stone? " 
				+ "Specify by MOVE <row> <column> or MOVE PASS.");
	}

	

}
