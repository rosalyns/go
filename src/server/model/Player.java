package server.model;

/**
 * Abstract class representing a player in a Go game.
 * @author Rosalyn Sleurink
 */

public abstract class Player {
    private String name;
    private Stone mark;
   
    public Player(Stone mark, String name) {
        this.name = name;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public Stone getMark() {
        return mark;
    }

    public abstract int determineMove(Board board);

}
