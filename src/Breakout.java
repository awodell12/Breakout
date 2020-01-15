

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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    private static final double BALL_SPEED = SIZE/4 ;
    private static final double SMALL_ANGLE = 20 * Math.PI / 180;
    private static final double BIGGER_ANGLE = 30 * Math.PI / 180;
    private static final double SMALL_ANGLE_X = Math.cos(SMALL_ANGLE) * BALL_SPEED;
    private static final double SMALL_ANGLE_Y = Math.sin(SMALL_ANGLE) *BALL_SPEED;
    private static final double BIGGER_ANGLE_X = Math.cos(BIGGER_ANGLE) *BALL_SPEED;
    private static final double BIGGER_ANGLE_Y = Math.sin(BIGGER_ANGLE) * BALL_SPEED;
    

    private static double bouncerSpeedX = 0;
    private static double bouncerSpeedY = 0;
    private static int lives = 1;
    private static int myScore = 0;
    private static boolean started = false;

    private Scene myScene;
    private ImageView myBouncer;
    private ImageView myPaddle;
    private Rectangle myRectangle;
    private Brick myBrick;
    private Group root;

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
        root = new Group();
        // make some shapes and set their properties
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(Ball_IMAGE));
        Image paddleImage = new Image(this.getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE));
        myBouncer = new ImageView(image);
        myPaddle = new ImageView(paddleImage);
        myBrick = new Brick(0, 0, SIZE/BRICK_COLUMNS,SIZE/BRICK_ROWS);
        myBrick.setFill(Color.GRAY);

        // x and y represent the top left corner, so center it in window

        myBouncer.setFitHeight(image.getHeight());
        myBouncer.setFitWidth(image.getWidth());
        myBouncer.setX(width/2);

        myPaddle.setFitHeight(paddleImage.getHeight());
        myPaddle.setFitWidth(paddleImage.getWidth());
        myPaddle.setX(width / 2 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(height  - myPaddle.getBoundsInLocal().getHeight());

        myBouncer.setY(height - myPaddle.getFitHeight() - myBouncer.getFitHeight());

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

        if (myPaddle.getBoundsInParent().intersects(myBouncer.getBoundsInParent())) ballHitsPaddle();

        for (Node other: myScene.getRoot().getChildrenUnmodifiable()) {
            if (other instanceof Brick){
                if (myBouncer.getBoundsInParent().intersects(other.getBoundsInParent()) && other.isVisible()){
                    if (myBouncer.getY() + myBouncer.getFitHeight()<= ((Brick) other).getY() + ((Brick) other).getHeight()*(1-TOLERANCE)|| myBouncer.getY()>= ((Brick) other).getY() + ((Brick) other).getHeight() * TOLERANCE) {
                        bouncerSpeedY = -bouncerSpeedY;
                        //System.out.println("bouncer = " + myBouncer.getY() + "Brick = " + ((Brick) other).getY());
                    } else bouncerSpeedX = -bouncerSpeedX;
                    ((Brick) other).removeHealth();
                    if (((Brick) other).getHealth()<=0)
                    other.setVisible(false);
                    myScore += 10;
                    break;
                }
            }
        }
    }

    private void ballHitsPaddle() {
        double paddleWidth = myPaddle.getFitWidth();
        double bouncerX = myBouncer.getX();
        double paddleX = myPaddle.getX();
        if (bouncerX > (paddleX + paddleWidth/3.0) && bouncerX < paddleX + (2.0/3.0)*paddleWidth){ //hit middle third
            bouncerSpeedY = -bouncerSpeedY;
        }
        else if(bouncerX < paddleX + paddleWidth / 6.0){ //hit far left of paddle
            bouncerSpeedY = -SMALL_ANGLE_Y;
            bouncerSpeedX =  -SMALL_ANGLE_X;
        }
        else if (bouncerX >= paddleX + paddleWidth/6 && bouncerX<= paddleX + paddleWidth/3.0){ //hit near left of paddle
            bouncerSpeedY = -BIGGER_ANGLE_Y;
            bouncerSpeedX = -BIGGER_ANGLE_X;
        }
        else if (bouncerX >= paddleX + (2.0/3.0)*paddleWidth && bouncerX <= paddleX + (5.0/6.0) *paddleWidth){
            bouncerSpeedY = -BIGGER_ANGLE_Y;
            bouncerSpeedX = BIGGER_ANGLE_X;
        }
        else{
            bouncerSpeedY = -SMALL_ANGLE_Y;
            bouncerSpeedX =  SMALL_ANGLE_X;
        }

    }

    private void death() {
        lives --;
        started = false;
        if (lives <= 0)
            endGame();
    }

    private void endGame() {
        //Figure out how to end the game and display the score.
        root.getChildren().clear();
        Text endText = new Text(0, SIZE/4,"Game Over");
        endText.setFill(Color.RED);
        endText.setFont(new Font(SIZE/10));
        alignCenter(endText);

        String finalScore = Integer.toString(myScore);
        Text scoreText = new Text(0,SIZE/2,finalScore);
        scoreText.setFill(Color.BLUE);
        scoreText.setFont(new Font(SIZE/10));
        alignCenter(scoreText);

        root.getChildren().add(endText);
        root.getChildren().add(scoreText);
    }

    private void alignCenter(Node curNode) {
        double newX = SIZE/2 - curNode.getBoundsInLocal().getWidth()/2;
        curNode.setLayoutX(newX);
    }


    private void handleKeyInput(KeyCode code) {
        if (started) {
            if (code == KeyCode.RIGHT && myPaddle.getX() <= SIZE - myPaddle.getFitWidth()) {
                myPaddle.setX(myPaddle.getX() + PADDLE_SPEED);
            } else if (code == KeyCode.LEFT && myPaddle.getX() >= 0) {
                myPaddle.setX(myPaddle.getX() - PADDLE_SPEED);
            }
        }
    }

    private void handleMouseInput(double x, double y) {
        if (!started) {
            double dy = y - myBouncer.getY();
            double dx = x - myBouncer.getX();
            double hyp = Math.sqrt(dx * dx + dy * dy);
            bouncerSpeedX = dx * BALL_SPEED / hyp;
            bouncerSpeedY = dy * BALL_SPEED / hyp;
        }
        started = true;
    }

    /**
     * Start the program.
     */
    public static void main (String[] args) {
        launch(args);
    }
}

