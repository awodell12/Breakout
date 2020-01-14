
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Brick extends Rectangle {

    public boolean isDead = false;

    public void kill(){

    }
    public boolean collide(Shape other) {
        return false;
    }
}
