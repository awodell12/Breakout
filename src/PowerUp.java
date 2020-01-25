import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;


/**
 * @author Austin Odell
 * This class is used to create power-up drop objects. They are updated in the step method of the Breakout class.
 * There is an equal probability to generate each of the power-ups
 * They map as follows:
 * 1: Extra Life
 * 2: Slow Ball to half speed
 * 3: Double width of paddle
 */
public class PowerUp extends Circle {
    public static final int RADIUS = 5;
    public static final int NUM_POWERS = 3;
    public static final int VELOCITY = 200;
    private int powerType = 0;


    public PowerUp(double centerX, double centerY) {
        super(centerX, centerY, RADIUS);
        powerType = (int) Math.floor(Math.random() * (NUM_POWERS)) + 1;
    }

    /**
     * @return integer representation of power-up type
     */
    public int getPowerType() {
        return powerType;
    }

    public double getVelocity() {
        return VELOCITY;
    }

    /**
     * This method updates the location of the power-ups to make them appear as falling.
     * @param root the Group that contains all nodes in current level
     * @param elapsedTime time between each frame
     */
    public static void updatePowerUps(Group root, double elapsedTime) {
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof PowerUp) {
                double newY = (node.getLayoutY() + ((PowerUp) node).getVelocity() * elapsedTime);
                node.setLayoutY(newY);
                if (node.getLayoutY() >= Breakout.SIZE + Breakout.BOTTOM_DISPLAY_SIZE) {
                    node.setVisible(false);
                }
            }
        }
    }

    public static int checkNewPowerUp(Scene myScene, ImageView myPaddle) {
        int powerType = 0;
        for (Node other :myScene.getRoot().getChildrenUnmodifiable()){
            if(other instanceof PowerUp){
                if(myPaddle.getBoundsInParent().intersects(other.getBoundsInParent()) && other.isVisible()) {
                    powerType = ((PowerUp) other).getPowerType();
                    other.setVisible(false);
                    break;
                }
            }
        }

        return powerType;
    }
}
