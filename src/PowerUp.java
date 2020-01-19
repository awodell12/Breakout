import javafx.scene.shape.Circle;

public class PowerUp extends Circle {
    public static final int RADIUS = 5;
    public static final int NUM_POWERS = 5;
    public static final int VELOCITY = 200;
    private int powerType = 0;


    public PowerUp(double centerX, double centerY){
        super(centerX, centerY, RADIUS);
        powerType = 3;// (int) Math.floor(Math.random()* (NUM_POWERS)) + 1;
    };

    public int getPowerType(){
        return powerType;
    }
    public double getVelocity(){
        return VELOCITY;
    }
}
