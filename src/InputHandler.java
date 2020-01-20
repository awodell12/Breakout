import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class InputHandler {

    String fileName;
    FileReader fileReader;
    int maxRows;
    int maxCols;

    public InputHandler(String fName, int rows, int cols) {
        fileName = fName;
        maxCols = cols;
        maxRows = rows;
    }


    public int[][] readInput() {
        int [] [] healthMatrix= new int [maxRows][maxCols];
        int row = 0;
        int col = 0;
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            for (Byte b : resourceAsStream.readAllBytes()){
                if (b == 32) col ++;// if ascii value of a space
                else if (b==13) {
                    row ++;
                    col = 0;
                }
                else if (b==10) continue;
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
}
