

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  JavaFX Breakout game developed for CS 308. Based on ExampleBounce code from Prof. Duvall
 * @author Austin Odell
 */
public class Breakout extends Application{
    public static final String TITLE = "Example JavaFX";
    public static final int SIZE = 400;
    public static final int FRAMES_PER_SECOND = 120;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final Paint BACKGROUND = Color.AZURE;
    public static final Paint HIGHLIGHT = Color.OLIVEDRAB;
    public static final String Ball_IMAGE= "ball.gif";
    public static final String PADDLE_IMAGE = "paddle.gif";
    public static final int PADDLE_SPEED = 20;
    public static final int BRICK_COLUMNS = 13;
    public static final int BRICK_ROWS = 20;
    public static final double BRICK_WIDTH = (1.0) * SIZE/ ((1.0)*BRICK_COLUMNS);
    public static final double BRICK_HEIGHT = (1.0) * SIZE/((1.0)*BRICK_ROWS);
    public static final double BALL_SPEED = (1.0) * SIZE /1.5;

    private static final double TOLERANCE = 0.9;
    private static final double SMALL_ANGLE = 20 * Math.PI / 180;
    private static final double BIGGER_ANGLE = 30 * Math.PI / 180;
    private static final double SMALL_ANGLE_X = Math.cos(SMALL_ANGLE) * BALL_SPEED;
    private static final double SMALL_ANGLE_Y = Math.sin(SMALL_ANGLE) *BALL_SPEED;
    private static final double BIGGER_ANGLE_X = Math.cos(BIGGER_ANGLE) *BALL_SPEED;
    private static final double BIGGER_ANGLE_Y = Math.sin(BIGGER_ANGLE) * BALL_SPEED;
    private static final double POWER_UP_CHANCE = 0.25;
    private static final long POWER_UP_DURATION = 15;
    private static final int SCORE_PER_BRICK = 10;
    private static final int MAX_LEVEL = 5;
    private static final int TOUGH_HEALTH = 5;
    private static final int BOTTOM_DISPLAY_SIZE = 50 ;


    private static double bouncerSpeedX = 0;
    private static double bouncerSpeedY = 0;
    private static int myLives = 3;
    private static int myScore = 0;
    private static boolean started = false;
    private static int currentPower = 0;
    private static int maxScore = 0;
    private static int currentLevel = 1;


    private Scene myScene;
    private ImageView myBouncer;
    private ImageView myPaddle;
    private Brick myBrick;
    private Group root;
    private Stage myStage;
    private Label myScoreDisplay;
    private Label myLivesDisplay;

    /**
     * Initialize what will be displayed and how it will be updated.
     */
    @Override
    public void start (Stage stage) {
        myStage = stage;
        myScene = setupLevel(BACKGROUND, currentLevel);
        Group titleRoot;
        titleRoot = showStartScreen();
        Scene titleScene = new Scene(titleRoot, SIZE, SIZE);
        titleScene.setOnMouseClicked(e -> stage.setScene(myScene));
        stage.setScene(titleScene);
        stage.setTitle(TITLE);
        stage.show();

        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY, myScene));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    private Group showStartScreen() {
        Group startScreen = new Group();
        Text startText = new Text(0, SIZE/5,"Breakout");
        startText.setFont(new Font(SIZE/10));
        alignCenter(startText);
        startScreen.getChildren().add(startText);

        Text pressToStart = new Text(0, SIZE/4, "(Click anywhere to start)");
        pressToStart.setFont(new Font(12));
        alignCenter(pressToStart);
        startScreen.getChildren().add(pressToStart);

        Text rules = new Text(0, SIZE/3, "Rules: \n1. Use Left and Right arrow keys to move paddle. \n" +
                "2. Use Up arrow key to fire lasers \n3. Click to launch ball off paddle \n4. If ball falls below paddle you lose a life, if you run out of lives the game ends\n" +
                "5. Collect falling power-ups with the paddle");
        rules.setFont(new Font(SIZE/20));
        rules.setWrappingWidth(TOLERANCE * SIZE);
        rules.setTextAlignment(TextAlignment.LEFT);
        alignCenter(rules);
        startScreen.getChildren().add(rules);

        return startScreen;
    }


    private Scene setupLevel(Paint background, int levelNum) {
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
        myBouncer.setX(Breakout.SIZE /2.0);

        myPaddle.setFitHeight(paddleImage.getHeight());
        myPaddle.setFitWidth(paddleImage.getWidth());
        myPaddle.setX(Breakout.SIZE / 2.0 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(Breakout.SIZE - myPaddle.getBoundsInLocal().getHeight());

        myBouncer.setY(Breakout.SIZE - myPaddle.getFitHeight() - myBouncer.getFitHeight());

        root.getChildren().add(myBouncer);
        root.getChildren().add(myPaddle);
        //root.getChildren().add(myBrick);

        root = addBricks(root,levelNum);
        root.getChildren().add(setUpDisplay());


        Scene scene = new Scene(root, Breakout.SIZE , Breakout.SIZE + BOTTOM_DISPLAY_SIZE, background);
        // respond to input

        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> handleMouseInput(e.getX(), e.getY()));
        return scene;
    }

    private VBox setUpDisplay() {
        VBox vbox = new VBox();
        vbox.setLayoutY(SIZE);
        vbox.setMinSize(SIZE, BOTTOM_DISPLAY_SIZE);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(5));
        vbox.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW,
                CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-border-color: black");

        myScoreDisplay = new Label("Score: " + myScore);
        myLivesDisplay = new Label("Lives: " +myLives);
        myLivesDisplay.setTextFill(Color.BLUE);
        myScoreDisplay.setTextFill(Color.BLUE);
        vbox.getChildren().add(myScoreDisplay);
        vbox.getChildren().add(myLivesDisplay);
    return vbox;
    }

    private Group addBricks(Group root, int level) {
        int type = 1;
        int[][] brickType = readInBrickTypes(level);
        for (int j = 0; j < BRICK_ROWS; j++) {
            for (int i = 0; i < BRICK_COLUMNS; i++) {
                type = brickType[j][i];
                if (type > 0) {
                    myBrick = new Brick(i * BRICK_WIDTH, j * BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT);

                    if (type == 1) {
                        myBrick.setFill(Color.GRAY);
                        maxScore += SCORE_PER_BRICK * type;
                        myBrick.setHealth(1);
                    }
                    else if (type == 2){
                        myBrick.setFill(Color.GOLD);
                        maxScore += SCORE_PER_BRICK * TOUGH_HEALTH;
                        myBrick.setHealth(TOUGH_HEALTH);
                    }
                    else {
                        myBrick.setFill(Color.BLACK);
                        myBrick.setHealth(Integer.MAX_VALUE - 100);
                    }

                    root.getChildren().add(myBrick);
                }

            }
        }
        return root;
    }

    private int[][] readInBrickTypes(int level){
        int [] [] healthMat = new int[BRICK_ROWS][BRICK_COLUMNS];
        String fileName = "lvl" + level + ".txt";
        //System.out.println(fileName);
        InputHandler input = new InputHandler(fileName, BRICK_ROWS, BRICK_COLUMNS);
        healthMat = input.readInput();

        return healthMat;

    }

    private void step(double elapsedTime, Scene curScene) {
        if (myScene == curScene) {
            updateBouncer(elapsedTime);
            updatePowerUps(elapsedTime);

            if (myPaddle.getBoundsInParent().intersects(myBouncer.getBoundsInParent())) ballHitsPaddle();

            checkCollisions(curScene);
            if (currentPower == 0)
                currentPower = checkNewPowerUp(curScene);
            if (myScore >= maxScore)
                nextLevel(); //change level

        }

    }

    private void nextLevel() {
        currentLevel++;
        if (currentLevel < MAX_LEVEL){
            myScene = setupLevel(BACKGROUND, currentLevel);
            myStage.setScene(myScene);
            started = false;
            bouncerSpeedY = 0;
            bouncerSpeedX = 0;
            //System.out.println("Next Level");
        }
        else if (currentLevel == MAX_LEVEL){
            myPaddle.setVisible(false);
        }
        else {
            endGame(true);
        }
    }

    public int checkNewPowerUp(Scene myScene) {
        int powerType = 0;
        for (Node other :myScene.getRoot().getChildrenUnmodifiable()){
            if(other instanceof PowerUp){
                if(myPaddle.getBoundsInParent().intersects(other.getBoundsInParent()) && other.isVisible()) {
                    powerType = ((PowerUp) other).getPowerType();
                    switch (powerType){
                        case 1:
                            addLife();
                            break;
                        case 2:
                            slowBall();
                            break;
                        case 3:
                            growPaddle();
                            break;
                        case 4:
                            System.out.println("Power-Up 4");
                            break;

                        case 5:
                            System.out.println("Power-Up 5");
                            break;
                    }
                    other.setVisible(false);
                    Timer timer = new Timer();
                    TimerTask task = new removePowerUp(powerType);
                    timer.schedule(task, POWER_UP_DURATION * 1000 );
                    break;
                }
            }
        }

        return powerType;
    }

    private void growPaddle() {
        myPaddle.setFitWidth(myPaddle.getFitWidth()*2);
    }

    private void slowBall() {
        bouncerSpeedX = 0.5 * bouncerSpeedX;
        bouncerSpeedY = 0.5 * bouncerSpeedY;
    }

    private void addLife() {
        myLives++;
        myLivesDisplay.setText("Lives: " + myLives);
    }

    private void updatePowerUps(double elapsedTime) {
        for (Node node: root.getChildrenUnmodifiable()) {
            if (node instanceof PowerUp) {
                double newY = (node.getLayoutY() + ((PowerUp) node).getVelocity() * elapsedTime);
                node.setLayoutY(newY);
                if (node.getLayoutY() >= SIZE + BOTTOM_DISPLAY_SIZE) {
                    node.setVisible(false);
                }
            }
            }
        }

    private void updateBouncer(double elapsedTime) {
        myBouncer.setX(myBouncer.getX() + bouncerSpeedX * elapsedTime);
        myBouncer.setY(myBouncer.getY() + bouncerSpeedY* elapsedTime);
        if (myBouncer.getX() >= (SIZE - TOLERANCE*myBouncer.getFitWidth())) bouncerSpeedX = -bouncerSpeedX;
        if (myBouncer.getX() <= 0) bouncerSpeedX = -bouncerSpeedX;
        if (myBouncer.getY() <= 0) bouncerSpeedY = - bouncerSpeedY;
        if(myBouncer.getY() > SIZE) {
            death();
        }
    }

    private void checkCollisions(Scene myScene) {
        for (Node other: myScene.getRoot().getChildrenUnmodifiable()) {
            if (other instanceof Brick){
                if (myBouncer.getBoundsInParent().intersects(other.getBoundsInParent()) && other.isVisible()){
                    {
                        if (myBouncer.getY() + myBouncer.getFitHeight() <= ((Brick) other).getY() + ((Brick) other).getHeight() * (1 - TOLERANCE) || myBouncer.getY() >= ((Brick) other).getY() + ((Brick) other).getHeight() * TOLERANCE) {
                            bouncerSpeedY = -bouncerSpeedY;
                            //System.out.println("bouncer = " + myBouncer.getY() + "Brick = " + ((Brick) other).getY());
                        } else bouncerSpeedX = -bouncerSpeedX;
                        ((Brick) other).removeHealth();
                        if (((Brick) other).getHealth() <= 0) {
                            other.setVisible(false);

                            if (Math.random() < POWER_UP_CHANCE) {
                                PowerUp myPowerUp = new PowerUp(((Brick) other).getX() + BRICK_WIDTH / 2, ((Brick) other).getY());
                                root.getChildren().add(myPowerUp);
                            }
                        }
                    }
                    // if hitting a solid brick this helps eliminate a bug where ball would get stuck inside one
                    if (myBouncer.getX() > ((Brick) other).getX() && myBouncer.getX() < ((Brick) other).getX() + ((Brick) other).getWidth()*TOLERANCE){
                        if (myBouncer.getY() > ((Brick) other).getY() && myBouncer.getY() < ((Brick) other).getY() + ((Brick) other).getHeight()*TOLERANCE) {
                            myBouncer.setX(myBouncer.getX() + BRICK_WIDTH / 2);
                            myBouncer.setY(myBouncer.getY() + BRICK_HEIGHT / 2);
                        }}


                    if (((Brick) other).myHealth <100){
                        myScore += SCORE_PER_BRICK;
                        myScoreDisplay.setText("Score: " + myScore);
                    }
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
        myLives--;
        myLivesDisplay.setText("Lives: " + myLives);
        started = false;
        if (myLives <= 0) {
            if (currentLevel == MAX_LEVEL)
                endGame(true);
            else
            endGame(false);
        }
        else resetToStartPosition();
    }

    private void endGame(boolean hasWon) {
        String winner = "";
        if (hasWon) winner = " You Won!";
        root.getChildren().clear();
        Text endText = new Text(0, SIZE/4,"Game Over" + winner);
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
        char codeChar = code.getChar().charAt(0);
        if (codeChar > '0' && codeChar<'6'){
            skipToLevel(Integer.parseInt("" + codeChar));
        }
        if(codeChar > '5' && codeChar < ':')
            skipToLevel(MAX_LEVEL);
        
        if (code == KeyCode.L) {
            addLife();
        }
        if (code == KeyCode.R)
            resetToStartPosition();
        if (code == KeyCode.DOLLAR)
            myScore += 100;
    }

    private void resetToStartPosition() {
        myPaddle.setX(Breakout.SIZE / 2.0 - myPaddle.getBoundsInLocal().getWidth() / 2);
        myPaddle.setY(Breakout.SIZE - myPaddle.getBoundsInLocal().getHeight());
        myBouncer.setX(SIZE/2);
        myBouncer.setY(Breakout.SIZE - myPaddle.getFitHeight() - myBouncer.getFitHeight());
        bouncerSpeedX = 0;
        bouncerSpeedY = 0;

        started = false;
    }


    private void skipToLevel(int level){
        myScene = setupLevel(BACKGROUND, level);
        currentLevel = level;
        myStage.setScene(myScene);
        started = false;
        bouncerSpeedY = 0;
        bouncerSpeedX = 0;
    }

    private void handleMouseInput(double x, double y) {
        if (!started) { //!started ****
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

    private class removePowerUp extends TimerTask {
        int myPower; 
        public removePowerUp(int powerType) {
            myPower = powerType;
        }

        @Override
        public void run() {
            switch (myPower){
                case 1: 
                    break;
                case 2:
                    endSlowBall();
                    break;
                case 3:
                    endGrowPaddle();
                default:
                    currentPower = 0;
                    break;

            }
        }
    }

    private void endGrowPaddle() {
        myPaddle.setFitWidth(myPaddle.getFitWidth()*0.5);
        currentPower = 0;
    }

    private void endSlowBall() {
        bouncerSpeedX = 2.0 * bouncerSpeedX;
        bouncerSpeedY = 2.0 * bouncerSpeedY;
        currentPower = 0;
    }
}

