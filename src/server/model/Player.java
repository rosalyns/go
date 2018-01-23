package server.model;

/**
 * Abstract class representing a player in a Go game.
 * @author Rosalyn Sleurink
 */

public abstract class Player {
    private String name;
    private Stone color;
   
    public Player(Stone color, String name) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Stone getColor() {
        return color;
    }

    public abstract void askForMove(Board board);

}
