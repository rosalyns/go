package server.model;

/**
 * Abstract class representing a player in a Go game.
 * @author Rosalyn Sleurink
 */

public abstract class Player {

    private String name;
    private Mark mark;

   
    public Player(Mark mark, String name) {
        this.name = name;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public Mark getMark() {
        return mark;
    }

    public abstract int determineMove(Board board);

}
