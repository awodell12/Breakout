

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
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

    private static int bouncerSpeedX = 100;
    private static int bouncerSpeedY = -50;
    private static int lives = 3;

    private Scene myScene;
    private ImageView myBouncer;
    private ImageView myPaddle;
    private Rectangle myRectangle;

    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        myScene = setupGame(SIZE, SIZE, BACKGROUND);
        stage.setScene(myScene);
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
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
        myRectangle = new Rectangle(0, 0, SIZE/13,SIZE/20);
        myRectangle.setFill(Color.GRAY);
        System.out.print(myRectangle.getWidth());
        System.out.println(myRectangle.getHeight());

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
        root.getChildren().add(myRectangle);

        Scene scene = new Scene(root, width, height, background);
        // respond to input
        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    private void step(double elapsedTime) {
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
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}

