import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import java.util.ArrayList;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Main Class to Parse Config Files, Specify Tests & Run when needed, Play game,
 * handle interactions/collisions, keep scoreboard, set splash screen and end screen
 * Assumptions: Relies on Tester class for testing, TestReader for test files, ParseFile for config, Splash for splash screen, StatusDisplay,
 * Ball, Brick, Powerup, Paddle, MainScreen objects on the stage
 */
public class PlayLevel {

    //================================================================================
    // Instance Variables | Constructor
    //================================================================================

    // Scene Instance Variables
    private int mySize = MainScreen.WINDOW_SIZE;
    private Group root;
    private Scene scene;
    private Splash mySplash;

    // Input File Instance Variables
    private static final String[] LEVEL_FILES = {"level1.txt", "level2.txt", "level3.txt"};
    private int CURRENT_LEVEL;

    // Scene Objects Instance Variables
    private Ball ball;
    private static final String BALL_IMAGE = "ball.gif";
    private static final int BALL_X_VEL = 50;
    private static final int BALL_Y_VEL = 200;
    private static final int BALL_SIZE = 15;
    private Paddle paddle;
    private Paddle paddle2;
    private String PADDLE_IMAGE = "paddle.gif";
    private static final int PADDLE_SIZE_X = 75;
    private static final int PADDLE_SIZE_Y = 10;
    private static final int PADDLE_SPEED = 10;
    private boolean ENLARGED = false;
    private boolean ENLARGED2 = false;
    private int SCORE_MULTIPLIER = 1;
    private ArrayList<PowerUp> activePowerUps = new ArrayList<>();

    // Status and Dimension Variables
    private int BRICK_WIDTH = MainScreen.BRICK_WIDTH;
    private int BRICK_HEIGHT = MainScreen.BRICK_HEIGHT;
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private int numOfBricks;

    // Animation Specific Instance Variables
    private Timeline myAnimation;
    private boolean IN_GAME = false;
    private static final boolean LEVEL_TEST = true;

    // Test Specific Instance Variables
    private KeyCode[] lastThree = {KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED};
    private boolean paused = false;
    
    private final String TEST_FILE_NAME = "brickType2.txt";
    public static final int INITIAL_LIVES = 3;

    /**
     * Constructs Scene and Instantiate Instance Variables
     */
    public PlayLevel(){
        this.CURRENT_LEVEL = 1;
        // Instantiate Scene and related objects
        this.root = new Group();
        this.scene = new Scene(root, mySize, mySize);
        this.mySplash = new Splash(mySize);

        // instantiate canvas with initial lives
        this.currentStatus = new StatusDisplay(mySize * 4/5, mySize * 7/10, mySize/4, mySize/4, INITIAL_LIVES, CURRENT_LEVEL);
        this.currentCanvas = currentStatus.display();

    }


    //================================================================================
    // Public Functions
    //================================================================================

    /**
     * Creates the initial splash screen
     * @return
     */
    public Scene makeScene(){
        ObservableList<Node> children = mySplash.getSplashChildren();
        root.getChildren().addAll(children);
        scene.setOnKeyPressed(e -> handleMouseClickOrKeyPress());
        scene.setOnMouseClicked(e -> handleMouseClickOrKeyPress());

        return this.scene;
    }

    /**
     * Passed into a KeyFrame for the animation
     * 1) Detects game over condition
     * 2) Detects win condition
     * 3) Detects ball collisions with other Objects
     * @param elapsedTime
     */
    public void step(double elapsedTime) {
        if(IN_GAME) {
            if(currentStatus.getLives() <= 0) {
                root.getChildren().clear();
                root.getChildren().add(makeGameOverText());
                root.getChildren().add(endGameInstructions());

                checkForHighScore();
                myAnimation.pause();

                scene.setOnKeyPressed(e -> handleEndGame(e.getCode()));
            }

            if(numOfBricks == 0) {
                CURRENT_LEVEL++;
                if(CURRENT_LEVEL > LEVEL_FILES.length){
                    root.getChildren().clear();
                    root.getChildren().add(makeWinText());
                    root.getChildren().add(endGameInstructions());
                    scene.setOnKeyPressed(e -> handleEndGame(e.getCode()));

                    checkForHighScore();
                    myAnimation.pause();

                }
                else{
                    makeTransitionScreen();
                }

                return;
            }

            updateBallPosition(elapsedTime);

            if(activePowerUps.size()>0) {
                updatePowerUpPositions(elapsedTime);
                updatePowerUpPositions2(elapsedTime);
            }

            collisionDetect();
        }
    }

    /**
     * Passes animation object from mainscreen to playlevel
     * @param animation
     */
    public void setAnimation(Timeline animation) {
        this.myAnimation = animation;
        myAnimation.pause();
    }

    //================================================================================
    // Game Setup
    //================================================================================

    // Checks and updates high score
    private void checkForHighScore(){
        Text highScoreText = currentStatus.updateHighScore();

        if(highScoreText != null){
            root.getChildren().add(highScoreText);
        }
    }

    // Handles click or key press, launches game
    private void handleMouseClickOrKeyPress () {
        makeTransitionScreen();
    }

    // Make the transition screen between levels
    private void makeTransitionScreen(){
        myAnimation.pause();
        root.getChildren().clear();
        root.getChildren().add(makeTransitionText());
        var frame = new KeyFrame(Duration.seconds(2));
        Timeline transitionTime = new Timeline();
        transitionTime.setCycleCount(1);
        transitionTime.getKeyFrames().add(frame);
        transitionTime.play();
        transitionTime.setOnFinished(e -> loadGame());
    }

    /**
     * Purpose:
     * 1) Sets up the Scene
     * 2) Selects Level
     * 3) Loads Objects
     * 4) Handles Key Press or Mouse Click when game commences
     */
    private void loadGame(){
        //reset powerups at beginning of each level
        IN_GAME = true;
        ENLARGED = false;
        ENLARGED2 = false;
        SCORE_MULTIPLIER = 1;
        root.getChildren().clear();

        ParseFile fileParser = new ParseFile(BRICK_WIDTH, BRICK_HEIGHT, LEVEL_FILES[CURRENT_LEVEL-1]);
        loadBricks(fileParser);

        // reset the ball and paddle
        int levelVelocityAdder = 50 * (CURRENT_LEVEL-1);
        this.ball = new Ball(mySize/2, mySize/5, BALL_SIZE, levelVelocityAdder + BALL_X_VEL, levelVelocityAdder + BALL_Y_VEL, BALL_IMAGE);

        if(LEVEL_TEST){
            System.out.println("Initial ball x velocity was " + Integer.toString(BALL_X_VEL));
            System.out.println("Initial ball y velocity was " + Integer.toString(BALL_Y_VEL));

            System.out.println("For current level, level " + Integer.toString(CURRENT_LEVEL) + ", ball vel should be 50 higher for each level after level 1");
            System.out.println("Ball x velocity is " + Integer.toString(ball.getVelX()));
            System.out.println("Ball y velocity is " + Integer.toString(ball.getVelY()));

        }

        this.paddle = new Paddle(PADDLE_SPEED, mySize/2, mySize-50, PADDLE_SIZE_X, PADDLE_SIZE_Y, PADDLE_IMAGE);
        this.paddle2 = new Paddle(PADDLE_SPEED, mySize/2, mySize/2, PADDLE_SIZE_X, PADDLE_SIZE_Y, PADDLE_IMAGE);
        root.getChildren().add(ball);
        root.getChildren().add(paddle);
        root.getChildren().add(paddle2);
        resetPaddleSpeed();
        resetPaddleSpeed2();


        // reinitialize status with new level, keeping score and lives the same through levels
        int tempLives = currentStatus.getLives();
        int tempScore = currentStatus.getScore();
        if(CURRENT_LEVEL == 1) {
            // If it first starts out, initialize lives to 3 and score to 0
            tempScore = 0;
            tempLives = INITIAL_LIVES;
        }
        this.currentStatus = new StatusDisplay(mySize * 4/5, mySize * 7/10, mySize/4, mySize/4, tempLives, CURRENT_LEVEL);
        this.currentStatus.setScore(tempScore);

        this.currentCanvas = currentStatus.display();
        root.getChildren().add(this.currentCanvas);

        scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
        scene.setOnMouseClicked(e -> pauseGame());
        myAnimation.play();
    }

    /**
     * Adds each brick to scene
     * @param fileParser
     */
    private void loadBricks(ParseFile fileParser){
        Brick[] bricks = fileParser.makeBricks();
        numOfBricks = bricks.length;

        for(Brick brick : bricks){
            root.getChildren().add(brick);
        }
    }


    //================================================================================
    // Collision Detection
    //================================================================================

    /**
     * Handles Collisions:
     * 1) Ball and Bounds
     * 2) Ball and Paddle
     * 3) Ball and Brick
     * 4) Ball and Bottom
     * 5) Powerup and Paddle
     */
    private void collisionDetect() {
        ballBoundsDetect();
        ballPaddleDetect();
        ballPaddle2Detect();
        ballBrickDetect();
        ballHitsBottomDetect();
    }

    // Handles Ball collision with Scene boundary
    private void ballBoundsDetect() {
        if(ball.getX() < 0) {
            ball.setVel(ball.getVelX()*-1, ball.getVelY());
        }
        if(ball.getX() > mySize-ball.getBoundsInLocal().getWidth()) {
            ball.setVel(ball.getVelX()*-1, ball.getVelY());
        }
        if(ball.getY() < 0) {
            ball.setVel(ball.getVelX(), ball.getVelY()*-1);
        }

    }

    // Handles Ball collision with Paddle
    private void ballPaddleDetect() {
        double upperBounds = paddle.getBoundsInLocal().getMinY();
        if (ball.getBoundsInLocal().intersects(paddle.getBoundsInParent())) {
            // to prevent infinite bounce thing
            ball.setY(upperBounds - ball.getSize());
            ball.setVel(ball.getVelX(), ball.getVelY() * -1);
        }
    }

    // Handles Ball collision with Paddle2
    private void ballPaddle2Detect() {
        double upperBounds = paddle2.getBoundsInLocal().getMinY();
        double lowerBounds = paddle2.getBoundsInLocal().getMaxY();

        if (ball.getBoundsInLocal().intersects(paddle2.getBoundsInParent()) && ball.getVelY() < 0) {
            ball.setVel(ball.getVelX(), ball.getVelY() * -1);
        }
    }

    /**
     * Purpose:
     * 1) Detects when ball collides with brick
     * 2) Removes brick
     * 3) Increments Score
     */
    private void ballBrickDetect() {
        Brick toRemove = null;
        numOfBricks = 0;
        toRemove = breakBrickDetect();
        if (toRemove != null) {
            if(toRemove.getType() > 1) {
                Brick nextDown = new Brick(toRemove.getType()-1);
                nextDown.setFitWidth(BRICK_WIDTH);
                nextDown.setFitHeight(BRICK_HEIGHT);
                nextDown.setX(toRemove.getX());
                nextDown.setY(toRemove.getY());
                root.getChildren().add(nextDown);
            }
            root.getChildren().remove(toRemove);
            root.getChildren().remove(currentCanvas);
            int tempLives = currentStatus.getLives();
            int tempScore = currentStatus.getScore();
            this.currentStatus = new StatusDisplay(mySize * 4/5, mySize * 7/10, mySize / 4, mySize / 4, tempLives, CURRENT_LEVEL);
            currentStatus.setScore(tempScore + SCORE_MULTIPLIER);
            this.currentCanvas = currentStatus.display();
            root.getChildren().add(currentCanvas);
            generatePowerup(toRemove);
        }
    }

    /**
     * Detects when ball collides with brick, and if so, return the brick to be removed
     * @return Node
     */
    private Brick breakBrickDetect() {
        Brick toRemove = null;
        for (Node brick : root.getChildren()) {
            if (brick instanceof Brick) {
                numOfBricks++;
                if (brick.intersects(ball.getBoundsInParent())) {
                    toRemove = (Brick) brick;

                    // if it hits either of the sides, change x velocity
                    if(Math.floor(ball.getBoundsInParent().getMaxX()) == brick.getBoundsInParent().getMinX() || Math.ceil(ball.getBoundsInParent().getMinX()) == brick.getBoundsInParent().getMaxX()
                        && Math.ceil(ball.getY()) > brick.getBoundsInParent().getMinY() &&  Math.ceil(ball.getY()) < brick.getBoundsInParent().getMaxY()){
                        ball.setVel(ball.getVelX()*-1, ball.getVelY());
                    }
                    // or, if we just hit a brick and it's not from the sides, just go the other way
                    else {
                        ball.setVel(ball.getVelX(), ball.getVelY()*-1);
                    }
                }
            }
        }
        return toRemove;
    }

    /**
     * Purpose:
     * 1) Detects when Ball hits bottom
     * 2) Removes life
     * 3) Reset ball position
     */
    private void ballHitsBottomDetect() {
        if (ball.getY() > mySize - ball.getBoundsInLocal().getHeight()) {
            root.getChildren().remove(currentCanvas);
            int tempLives = currentStatus.getLives();
            int tempScore = currentStatus.getScore();
            // Not the most efficient way, but the way we did our status display is each time display() is called, it would layer
            // the new value over the previous value, and make it unreadable, so we created a new instance each time.
            this.currentStatus = new StatusDisplay(mySize * 4/5, mySize * 7/10, mySize / 4, mySize / 4, tempLives - 1, CURRENT_LEVEL);
            this.currentStatus.setScore(tempScore);
            this.currentCanvas = currentStatus.display();
            root.getChildren().add(currentCanvas);
            ball.setX(mySize / 2);
            ball.setY(mySize / 5);
        }

    }

    // Update Ball position
    private void updateBallPosition(double elapsedTime){
        ball.setX(ball.getX() + ball.getVelX() * elapsedTime);
        ball.setY(ball.getY() + ball.getVelY() * elapsedTime);
    }

    //================================================================================
    // Paddle Properties
    //================================================================================

    /**
     * This function is called when the size power up hits the paddle and makes the paddle twice the size
     * for 20 seconds.
     */
    private void enlargePaddle(){
        Timeline powerTimeline = makePowerUpTimeline();

        Scale paddleScale = new Scale();
        paddleScale.setX(2);
        paddle.getTransforms().add(paddleScale);
        paddle.setX(paddle.getLayoutBounds().getMinX()/2);
        powerTimeline.setOnFinished(e -> shrinkPaddle());
        ENLARGED = true;

    }

    /**
     * This function is called when the size power up hits the paddle2 and makes the paddle2 twice the size
     * for 20 seconds.
     */
    private void enlargePaddle2(){
        Timeline powerTimeline = makePowerUpTimeline();

        Scale paddleScale = new Scale();
        paddleScale.setX(2);
        paddle2.getTransforms().add(paddleScale);
        paddle2.setX(paddle2.getLayoutBounds().getMinX()/2);
        powerTimeline.setOnFinished(e -> shrinkPaddle2());
        ENLARGED2 = true;

    }

    /**
     * Restores the paddle to its original size after it has been enlarged with the power up
     */
    private void shrinkPaddle(){
        paddle.getTransforms().clear();
        paddle.setX(paddle.getLayoutBounds().getMinX() + PADDLE_SIZE_X*2);
        ENLARGED = false;
    }

    /**
     * Restores the paddle2 to its original size after it has been enlarged with the power up
     */
    private void shrinkPaddle2(){
        paddle2.getTransforms().clear();
        paddle2.setX(paddle2.getLayoutBounds().getMinX() + PADDLE_SIZE_X*2);
        ENLARGED2 = false;
    }


    // Double paddle2 speed for specified time when powerup is acquired
    private void increasePaddleSpeed2(){
        paddle2.setVel(PADDLE_SPEED*2);

        Timeline powerTimeline = makePowerUpTimeline();
        powerTimeline.setOnFinished(e -> resetPaddleSpeed2());
    }

    // Reset paddle speed when finished with powerup
    private void resetPaddleSpeed(){
        paddle.setVel(PADDLE_SPEED);
    }
    // Reset paddle2 speed when finished with powerup
    private void resetPaddleSpeed2(){
        paddle2.setVel(PADDLE_SPEED);
    }



    //================================================================================
    // Creation and Monitoring Powerups
    //================================================================================

    // Create a random powerup (
    private void generatePowerup(Brick toRemove) {
        int random = (int) Math.floor(Math.random() * 15);
        PowerUp powerUp = null;
        if(random == 2) {
            powerUp = new PowerUp((int) toRemove.getLayoutBounds().getMinX() + BRICK_WIDTH / 2, (int) toRemove.getLayoutBounds().getMaxY(), BALL_SIZE, BALL_Y_VEL / 3, "size");
        }
        else if(random == 9){
            powerUp = new PowerUp((int) toRemove.getLayoutBounds().getMinX() + BRICK_WIDTH / 2, (int) toRemove.getLayoutBounds().getMaxY(), BALL_SIZE, BALL_Y_VEL / 3, "paddlespeed");
        }
        else if(random == 5){
            powerUp = new PowerUp((int) toRemove.getLayoutBounds().getMinX() + BRICK_WIDTH / 2, (int) toRemove.getLayoutBounds().getMaxY(), BALL_SIZE, BALL_Y_VEL / 3, "points");
        }

        if(powerUp!=null){
            root.getChildren().add(powerUp);
            activePowerUps.add(powerUp);
        }
    }

    /**
     * Update the position of the power ups on screen in the step function so that they slowly "fall" to the ground
     *
     * @param elapsedTime
     */
    private void updatePowerUpPositions(double elapsedTime) {
        PowerUp toRemove = null;

        for (PowerUp powerUp: activePowerUps){
            powerUp.setY(powerUp.getY() + powerUp.getVelY() * elapsedTime);

            if(powerUp.intersects(paddle.getBoundsInParent()) && !powerUp.intersects(ball.getBoundsInParent())){
                root.getChildren().remove(powerUp);
                toRemove = powerUp;

                if(powerUp.getType().equals("size") && !ENLARGED) {
                    enlargePaddle();
                }

                else if(powerUp.getType().equals("points")){
                    increaseScoreMultiplier();
                }

                else if(powerUp.getType().equals("paddlespeed")){
                    increasePaddleSpeed();
                }
            }
        }
        if(toRemove!=null) {
            activePowerUps.remove(toRemove);
        }
    }

    /**
     * Update the position of the power ups on screen in the step function so that they slowly "fall" to the ground
     *
     * @param elapsedTime
     */
    private void updatePowerUpPositions2(double elapsedTime) {
        PowerUp toRemove = null;

        for (PowerUp powerUp: activePowerUps){
            powerUp.setY(powerUp.getY() + powerUp.getVelY() * elapsedTime);

            if(powerUp.intersects(paddle2.getBoundsInParent()) && !powerUp.intersects(ball.getBoundsInParent())){
                root.getChildren().remove(powerUp);
                toRemove = powerUp;

                if(powerUp.getType().equals("size") && !ENLARGED2) {
                    enlargePaddle2();
                }

                else if(powerUp.getType().equals("points")){
                    increaseScoreMultiplier();
                }

                else if(powerUp.getType().equals("paddlespeed")){
                    increasePaddleSpeed2();
                }
            }
        }
        if(toRemove!=null) {
            activePowerUps.remove(toRemove);
        }
    }

    /**
     * Function to make a 20 second timer for which the active power up will be active, after which its effect will be
     * undone.
     *
     * @return a Timeline that lasts 20 seconds
     */
    private Timeline makePowerUpTimeline(){
        KeyFrame frame = new KeyFrame(Duration.seconds(20));
        Timeline powerTimeline = new Timeline();
        powerTimeline.setCycleCount(1);
        powerTimeline.getKeyFrames().add(frame);
        powerTimeline.play();

        return powerTimeline;
    }

    // Increase score multiplier after hitting powerup
    private void increaseScoreMultiplier(){
        this.SCORE_MULTIPLIER *= 2;

        Timeline powerTimeline = makePowerUpTimeline();
        powerTimeline.setOnFinished(e -> decreaseScoreMultiplier());

    }

    // Decrease score multiplier after 20 seconds
    private void decreaseScoreMultiplier(){
        this.SCORE_MULTIPLIER /= 2;
    }

    // Double paddle speed for specified time when powerup is acquired
    private void increasePaddleSpeed(){
        paddle.setVel(PADDLE_SPEED*2);

        Timeline powerTimeline = makePowerUpTimeline();
        powerTimeline.setOnFinished(e -> resetPaddleSpeed());
    }

    //================================================================================
    // Key Input Handling
    //================================================================================

    /**
     * Handles key inputs
     * 1) Saves last three keys (including order)
     * 2) Checks paddle bounds
     * 3) Handles paddle input
     * 4) Handles cheat codes
     * @param code
     */
    private void handleKeyInput (KeyCode code) {
        handleLastThree(code);
        paddleInput(code);
        paddleInput2(code);
        cheatCodes(code);
    }

    // Saves last three keys in order
    private void handleLastThree(KeyCode code) {
        lastThree[0] = lastThree[1];
        lastThree[1] = lastThree[2];
        lastThree[2] = code;
    }

    // Moves paddle left or right based on input, ensures paddle stays in bounds
    private void paddleInput(KeyCode code) {
        if (code == KeyCode.RIGHT) {

            if(!ENLARGED && paddle.getBoundsInParent().getMaxX() + paddle.getVel() > mySize) {
                paddle.setX(mySize - paddle.getFitWidth());
            }

            else if(ENLARGED && paddle.getBoundsInParent().getMaxX() + paddle.getVel()/2 + 10 > mySize) {
                paddle.setX((mySize - (PADDLE_SIZE_X * 2)) / 2);
            }

            else {
                paddle.setX(paddle.getX() + paddle.getVel());
            }
        }
        else if (code == KeyCode.LEFT && paddle.getBoundsInParent().getMinX() >= 0) {
            if(paddle.getLayoutBounds().getMinX() - paddle.getVel()< 0) {
                paddle.setX(0);
            }
            else {
                paddle.setX(paddle.getX() - paddle.getVel());
            }
        }
    }

    // Moves paddle2 left or right based on input, ensures paddle2 stays in bounds
    private void paddleInput2(KeyCode code) {
        if (code == KeyCode.D) {

            if(!ENLARGED2 && paddle2.getBoundsInParent().getMaxX() + paddle2.getVel() > mySize) {
                paddle2.setX(mySize - paddle2.getFitWidth());
            }

            else if(ENLARGED2 && paddle2.getBoundsInParent().getMaxX() + paddle2.getVel()/2 + 10 > mySize) {
                paddle2.setX((mySize - (PADDLE_SIZE_X * 2)) / 2);
            }

            else {
                paddle2.setX(paddle2.getX() + paddle2.getVel());
            }
        }
        else if (code == KeyCode.A && paddle2.getBoundsInParent().getMinX() >= 0) {
            if(paddle2.getLayoutBounds().getMinX() - paddle2.getVel()< 0) {
                paddle2.setX(0);
            }
            else {
                paddle2.setX(paddle2.getX() - paddle2.getVel());
            }
        }
    }

    //================================================================================
    // Cheat Codes
    //================================================================================

    // Handles and activates cheat codes
    // L = Add Lives, R = Reset Position, ',./' = Run Tests, Digit = jump to specified level
    private void cheatCodes(KeyCode code) {
        if(code.isDigitKey()) {
            jumpLevel(code);
        }
        if(code == KeyCode.L) {
            cheatAddLives();
        }
        if(code == KeyCode.R) {
            resetPosition();
        }
        if(lastThree[0] == KeyCode.COMMA && lastThree[1] == KeyCode.PERIOD && lastThree[2] == KeyCode.SLASH) {
            myAnimation.pause();
            runTests();
        }
        if(lastThree[0] == KeyCode.S && lastThree[1] == KeyCode.A && lastThree[2] == KeyCode.M) {
            root.getChildren().clear();
            root.getChildren().add(makeWinText());
            root.getChildren().add(endGameInstructions());
            scene.setOnKeyPressed(e -> handleEndGame(e.getCode()));

            checkForHighScore();
            myAnimation.pause();
        }
        if(lastThree[0] == KeyCode.J && lastThree[1] == KeyCode.V && lastThree[2] == KeyCode.R) {
            root.getChildren().clear();
            root.getChildren().add(makeGameOverText());
            root.getChildren().add(endGameInstructions());

            checkForHighScore();
            myAnimation.pause();

            scene.setOnKeyPressed(e -> handleEndGame(e.getCode()));
        }
    }

    // Jump to the specified level
    private void jumpLevel(KeyCode code) {
        int level = Integer.parseInt(code.getName());
        if (level <= LEVEL_FILES.length) {
            CURRENT_LEVEL = level;
            this.loadGame();
        }
    }

    // Adds one life to display
    private void cheatAddLives() {
        root.getChildren().remove(currentCanvas);
        int tempLives = currentStatus.getLives();
        int tempScore = currentStatus.getScore();
        // Not the most efficient way, but the way we did our status display is each time display() is called, it would layer
        // the new value over the previous value, and make it unreadable, so we created a new instance each time.
        this.currentStatus = new StatusDisplay(mySize * 4/5, mySize * 7/10, mySize/4, mySize/4, tempLives+1, CURRENT_LEVEL);
        this.currentStatus.setScore(tempScore);
        this.currentCanvas = currentStatus.display();
        root.getChildren().add(currentCanvas);
    }

    // Reset ball and paddle position
    private void resetPosition(){
        this.ball.setX(mySize/2);
        this.ball.setY(mySize/4);
        this.ball.setVel(BALL_X_VEL + 50*(CURRENT_LEVEL-1), BALL_Y_VEL + 50*(CURRENT_LEVEL-1));
        if(ENLARGED) {
            this.paddle.setX((mySize / 4) - PADDLE_SIZE_X);
        }
        if(ENLARGED2) {
            this.paddle2.setX((mySize / 4) - PADDLE_SIZE_X);
        }
        else{
            this.paddle.setX(mySize/2);
            this.paddle2.setX(mySize/2);
        }
    }

    // On key click in game, method called to pause animation, resumes on second click
    private void pauseGame(){
        if(!paused) {
            this.paused = true;
            this.myAnimation.pause();
        }
        else {
            this.paused = false;
            this.myAnimation.play();
        }
    }

    //================================================================================
    // Test
    //================================================================================

    // Run tests
    private void runTests() {
        Tester myTester = null;
        myAnimation.pause();
        root.getChildren().clear();

        TestReader infoGetter = new TestReader(TEST_FILE_NAME, BRICK_WIDTH, BRICK_HEIGHT);
        String testType = infoGetter.getTestType();
        ball.setX(infoGetter.getBallXPos());
        ball.setY(infoGetter.getBallYPos());
        ball.setVel(infoGetter.getBallXVel(), infoGetter.getBallYVel());

        if(testType.equals("lose_life")){
            myTester = new LoseLifeTest(root, ball, currentStatus, TEST_FILE_NAME, infoGetter.getBricks());
        }
        else if(testType.equals("corner_bounce")){
            myTester = new CornerBounceTest(root, ball, currentStatus, TEST_FILE_NAME, infoGetter.getBricks());
        }
        else if(testType.equals("destroy_brick")){
            myTester = new DestroyBrickTest(root, ball, currentStatus, TEST_FILE_NAME, infoGetter.getBricks());
        }
        else if(testType.equals("type_brick")){
            myTester = new TypeBrickTest(root, ball, currentStatus, TEST_FILE_NAME, infoGetter.getBricks());
        }

        else if(testType.equals("size_power")){
            myTester = new SizePowerTest(root, ball, currentStatus, TEST_FILE_NAME, infoGetter.getBricks());
            ((SizePowerTest) myTester).setPaddle(paddle);
        }

        myTester.runTest();
    }


    //================================================================================
    // End Game Scenarios
    //================================================================================

    // Creates Game Over Text when no lives remain
    private Text makeGameOverText(){
        ball.setVel(0,0);
        Text gameOver = new Text();
        gameOver.setText("Game Over! Score: " + currentStatus.getScore());
        gameOver.setFill(Color.RED);
        gameOver.setFont(Font.font("impact", FontWeight.BOLD, FontPosture.REGULAR, 50));
        gameOver.setX((float) (mySize - gameOver.getLayoutBounds().getWidth())/2);
        gameOver.setY((float)mySize/3);
        return gameOver;
    }

    // Creates win text when all bricks are destroyed
    private Text makeWinText(){
        ball.setVel(0,0);
        Text winText = new Text();
        winText.setText("You Win! Score: " + currentStatus.getScore());
        winText.setFill(Color.GREEN);
        winText.setFont(Font.font("impact", FontWeight.BOLD, FontPosture.REGULAR, 50));
        winText.setX((float) (mySize - winText.getLayoutBounds().getWidth())/2);
        winText.setY((float)mySize/3);
        return winText;
    }

    // Make text to show level that's loading
    private Text makeTransitionText(){
        Text transitionText = new Text();
        transitionText.setText("Now Loading Level " + Integer.toString(CURRENT_LEVEL));
        transitionText.setFill(Color.BLACK);
        transitionText.setFont(Font.font("impact", FontWeight.BOLD, FontPosture.REGULAR, 50));
        transitionText.setX((float) (mySize - transitionText.getLayoutBounds().getWidth())/2);
        transitionText.setY((float)mySize/3);
        return transitionText;
    }

    // Make end game instructions
    private Text endGameInstructions() {
        Text instructionText = new Text();
        instructionText.setText("Click 'R' to Reset the Game!");
        instructionText.setTextAlignment(TextAlignment.CENTER);
        instructionText.setFont(Font.font("gruppo", FontWeight.NORMAL, FontPosture.REGULAR, 20));

        instructionText.setX((mySize - (instructionText.getLayoutBounds().getWidth()) ) /2);
        instructionText.setY((((float) mySize) - instructionText.getLayoutBounds().getHeight()) / 2);
        return instructionText;
    }

    // In end screen, clicking the R button triggers a restart of the game
    private void handleEndGame(KeyCode code) {
        if(code == KeyCode.R) {
            CURRENT_LEVEL = 1;
            this.loadGame();
        }
    }
}
