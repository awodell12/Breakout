import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Austin Odell
 * This class provides the ability to read data in from a text file. This is used to create a 2D array to correspond
 * to the Block types that are initialized for each level.
 * It is assumed the text file contains characters separated by spaces with specified maximum dimensions
 * For the Breakout game it is also assumed these characters are the numbers 1-3
 */
public class InputHandler {

    String fileName;
    int maxRows;
    int maxCols;
    int newScoreAdded;

    public InputHandler(String fName, int rows, int cols) {
        fileName = fName;
        maxCols = cols;
        maxRows = rows;
    }

    /**
     * Accessed the text file in resources folder. Name of text file is specified in the constructor
     * This method parses the text file and reads the integers into an array of given size
     *
     * @return 2D integer array of brick types
     */
    public int[][] readInput() {
        int[][] healthMatrix = new int[maxRows][maxCols];
        int row = 0;
        int col = 0;
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            for (Byte b : resourceAsStream.readAllBytes()) {
                if (b == 32) col++;// if ascii value of a space
                else if (b == 13) {
                    row++;
                    col = 0;
                } else if (b == 10) continue;
                else {
                    //System.out.println(b + "into [" + row+"][" + col+"]");
                    int type = Character.getNumericValue(b);

                    healthMatrix[row][col] = type;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return healthMatrix;
    }

    /**
     *
     */
    public Group addBricks(Group root) {
        newScoreAdded = 0;
        int type = 1;
        int[][] brickType = readInput();
        for (int j = 0; j < Breakout.BRICK_ROWS; j++) {
            for (int i = 0; i < Breakout.BRICK_COLUMNS; i++) {
                type = brickType[j][i];
                if (type > 0) {
                    Brick myBrick = new Brick(i * Breakout.BRICK_WIDTH, j * Breakout.BRICK_HEIGHT, Breakout.BRICK_WIDTH, Breakout.BRICK_HEIGHT);

                    if (type == 1) {
                        myBrick.setFill(Color.GRAY);
                        newScoreAdded += Breakout.SCORE_PER_BRICK * type;
                        myBrick.setHealth(1);
                    } else if (type == 2) {
                        myBrick.setFill(Color.GOLD);
                        newScoreAdded += Breakout.SCORE_PER_BRICK * Breakout.TOUGH_HEALTH;
                        myBrick.setHealth(Breakout.TOUGH_HEALTH);
                    } else {
                        myBrick.setFill(Color.BLACK);
                        myBrick.setHealth(Integer.MAX_VALUE - 100);
                    }

                    root.getChildren().add(myBrick);
                }

            }
        }
        return root;
    }


    public int getNewScoreAdded() {
        return newScoreAdded;
    }
}
