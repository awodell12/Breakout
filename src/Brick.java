
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Brick extends Rectangle {

    public boolean isDead = false;
    public int myHealth = 0;


    public Brick(double x, double y, double width, double height){
        super(x,y, width, height);
    }
    public void kill(){

    }
    public boolean collide(Shape other) {
        return false;
    }
    public void setHealth(int health){
        myHealth = health;
    }
    public int getHealth(){
        return myHealth;
    }
    public void removeHealth(){
        myHealth --;
    }
}
