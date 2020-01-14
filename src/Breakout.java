

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *  JavaFX Breakout game developed for CS 308. Based on ExampleBounce code from Prof. Duvall
 * @author Austin Odell
 */
public class Breakout extends Application{
    public static final String TITLE = "Example JavaFX";
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.AZURE;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String Ball_IMAGE= "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final int PADDLE_SPEED = 20;
    public static final int BRICK_COLUMNS = 13;
    public static final int BRICK_ROWS = 20;
    public static final int BRICK_WIDTH = SIZE/BRICK_COLUMNS;
    public static final int BRICK_HEIGHT = SIZE/BRICK_ROWS;
    private static final double TOLERANCE = 0.9;

    private static int bouncerSpeedX = 200;
    private static int bouncerSpeedY = -120;
    private static int lives = 3;
    private static int myScore = 0;

    private Scene myScene;
    private ImageView myBouncer;
    private ImageView myPaddle;
    private Rectangle myRectangle;
    private Brick myBrick;

    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        myScene = setupGame(SIZE, SIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY, myScene));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }



    private Scene setupGame (int width, int height, Paint background) {
        // create one top level collection to organize the things in the scene
        Group root = new Group();
        // make some shapes and set their properties
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(Ball_IMAGE));
        Image paddleImage = new Image(this.getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE));
        myBouncer = new ImageView(image);
        myPaddle = new ImageView(paddleImage);
        myBrick = new Brick(0, 0, SIZE/BRICK_COLUMNS,SIZE/BRICK_ROWS);
        myBrick.setFill(Color.GRAY);

        // x and y represent the top left corner, so center it in window
        myBouncer.setX(width / 2 - myBouncer.getBoundsInLocal().getWidth() / 2);
        myBouncer.setY(height / 2 - myBouncer.getBoundsInLocal().getHeight() / 2);
        myBouncer.setFitHeight(image.getHeight());
        myBouncer.setFitWidth(image.getWidth());

        myPaddle.setFitHeight(paddleImage.getHeight());
        myPaddle.setFitWidth(paddleImage.getWidth());
        myPaddle.setX(width / 2 - myBouncer.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(height  - myBouncer.getBoundsInLocal().getHeight() / 2);

        root.getChildren().add(myBouncer);
        root.getChildren().add(myPaddle);
        //root.getChildren().add(myBrick);

        root = addBricks(root);


        Scene scene = new Scene(root, width, height, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    private Group addBricks(Group root) {
        for (int j =0; j <= 5; j ++) {
            for (int i = 1; i <= BRICK_COLUMNS; i++) {
                myBrick = new Brick(i * BRICK_WIDTH, j * BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT);
                myBrick.setHealth(1);
                myBrick.setFill(Color.GRAY);
                root.getChildren().add(myBrick);
            }
        }
        return root;
    }

    private void step(double elapsedTime, Scene myScene) {
        myBouncer.setX(myBouncer.getX() + bouncerSpeedX * elapsedTime);
        myBouncer.setY(myBouncer.getY() + bouncerSpeedY* elapsedTime);
        if (myBouncer.getX() >= (SIZE - myBouncer.getFitWidth())) bouncerSpeedX = -bouncerSpeedX;
        if (myBouncer.getX() <= 0) bouncerSpeedX = -bouncerSpeedX;
        if (myBouncer.getY() <= 0) bouncerSpeedY = - bouncerSpeedY;
        if(myBouncer.getY() > SIZE) {
            death();
        }

        if (myPaddle.getBoundsInParent().intersects(myBouncer.getBoundsInParent())) {
           if (myBouncer.getX() > (myPaddle.getX() + myPaddle.getFitWidth()/2)){ //hit right side of paddle
               if (bouncerSpeedX <= 0) {
                   bouncerSpeedX = -bouncerSpeedX;
               }

           }
           else {
               if (bouncerSpeedX > 0){
                   bouncerSpeedX = -bouncerSpeedX;
               }

           }
            bouncerSpeedY = -bouncerSpeedY;
            //}
        }
        for (Node other: myScene.getRoot().getChildrenUnmodifiable()) {
            if (other instanceof Brick){
                if (myBouncer.getBoundsInParent().intersects(other.getBoundsInParent()) && other.isVisible()){
                    if (myBouncer.getY() + myBouncer.getFitHeight()<= ((Brick) other).getY() + ((Brick) other).getHeight()*(1-TOLERANCE)|| myBouncer.getY()>= ((Brick) other).getY() + ((Brick) other).getHeight() * TOLERANCE) {
                        bouncerSpeedY = -bouncerSpeedY;
                        //System.out.println("bouncer = " + myBouncer.getY() + "Brick = " + ((Brick) other).getY());
                    } else bouncerSpeedX = -bouncerSpeedX;
                    other.removeHealth();
                    if (((Brick) other).getHealth()<=0)
                    other.setVisible(false);
                    myScore += 10;
                    break;
                }
            }
        }
    }

    private void death() {
        lives --;
        if (lives <= 0)
            endGame();
    }

    private void endGame() {
        //Figure out how to end the game and display the score.
    }

    private void handleKeyInput(KeyCode code) {
        if (code == KeyCode.RIGHT && myPaddle.getX() <= SIZE - myPaddle.getFitWidth() ) {
            myPaddle.setX(myPaddle.getX() + PADDLE_SPEED);
        }
        else if (code == KeyCode.LEFT && myPaddle.getX() >= 0) {
            myPaddle.setX(myPaddle.getX() - PADDLE_SPEED);
        }
    }

    private void handleMouseInput(double x, double y) {
        System.out.print(myScore);
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}

