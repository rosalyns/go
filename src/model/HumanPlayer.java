package model;

import java.util.Scanner;

public class HumanPlayer extends Player {

    public HumanPlayer(Stone color, String name) {
        super(name);
    }

    public void askForMove(Board board) {
        String prompt = "> " + getName() + " (" + getColor().toString() + ")"
                + ", what is your choice? ";
        int choice = readInt(prompt);
        boolean valid = (board.isField(choice) && board.isEmptyField(choice)) 
        		|| choice == Game.PASS;
        while (!valid) {
            System.out.println("ERROR: field " + choice
                    + " is no valid choice.");
            choice = readInt(prompt);
            valid = board.isField(choice) && board.isEmptyField(choice);
        }
        // new Move(this.getColor(), choice);
    }

    private int readInt(String prompt) {
        int value = 0;
        boolean intRead = false;
        @SuppressWarnings("resource")
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine());) {
                if (scannerLine.hasNextInt()) {
                    intRead = true;
                    value = scannerLine.nextInt();
                }
            }
        } while (!intRead);
        return value;
    }

}
