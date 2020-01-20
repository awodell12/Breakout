
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * @author Austin ODell
 * This class is used to create brick objects which are rectangles with a health.
 */
public class Brick extends Rectangle {

    public int myHealth = 0;


    public Brick(double x, double y, double width, double height){
        super(x,y, width, height);
    }

    public void setHealth(int health){
        myHealth = health;
    }

    /**
     *
     * @return current health of a Brick
     */
    public int getHealth(){
        return myHealth;
    }

    /**
     * call this method to decrement the health of a Brick by 1, due to collision with ball.
     */
    public void removeHealth(){
        myHealth --;
    }
}
