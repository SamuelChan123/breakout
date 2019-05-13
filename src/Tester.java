import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Abstract tester class used by other tests as a template, contains most of the needed architecture for tests except various setups and
 * conditions for checking the pass/fail
 * Assumptions: Relies on children classes to incorporate functions and perform their specific tests
 */

public abstract class Tester {

    //================================================================================
    // Instance Variables | Constructor
    //================================================================================

    // Animation Specific Instance Variables
    Timeline testTimeline;
    public static final int MILLISECOND_DELAY = MainScreen.MILLISECOND_DELAY;
    public static final double SECOND_DELAY = MainScreen.SECOND_DELAY;

    // Scene Object Instance Variables
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private int totalBricks;
    private int numOfBricks;
    private Group root;
    private Ball ball;
    private int initXVel;
    private int initYVel;
    private static final int INITIAL_LIVES = PlayLevel.INITIAL_LIVES;


    // Test Specific
    private boolean BRICK_TEST = false;
    private boolean CORNER_TEST = false;
    private boolean LIVES_TEST = false;
    private String TEST_FILE_NAME;
    private String TEST_TYPE;

    // Dimension Instance Variables
    int mySize = MainScreen.WINDOW_SIZE;
    private int BRICK_WIDTH = MainScreen.BRICK_WIDTH;
    private int BRICK_HEIGHT = MainScreen.BRICK_HEIGHT;
    Brick[] bricks;


    // Constructs abstract Tester containing parameters uitilized by specific tests
    public Tester(Group root, Ball ball, StatusDisplay currentStatus, String testFile, Brick[] bricks) {
        // Instantiate Scene and related objects, testfile
        this.currentStatus = currentStatus;
        this.currentCanvas = currentStatus.display();
        this.TEST_FILE_NAME = testFile;
        this.root = root;
        this.ball = ball;
        this.bricks = bricks;
    }

    //================================================================================
    // Getters
    //================================================================================

    // Returns ball
    public Ball getBall() {
        return ball;
    }

    // Returns status
    public StatusDisplay getCurrentStatus() {
        return currentStatus;
    }

    // Get root node for scenee
    public Group getRoot() {
        return root;
    }

    // Get test file
    public String getTestFileName() {
        return TEST_FILE_NAME;
    }

    // Retrieves bricks
    public Brick[] getBricks() {
        return bricks;
    }

    //================================================================================
    // Main Test Functions
    //================================================================================

    /**
     * Generates the test timeline, called when tests are being run, is the base timeline for running tests and uses the
     * testStep function instead of the regular step function.
     *
     * @return The timeline used for testing
     */
    public Timeline makeTestTimeline() {
        var frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> testStep(SECOND_DELAY));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        return animation;
    }

    /**
     * Used to create a temporary timer that expires after x seconds, depending on which test was called
     * calls the required function, starts after a test condition has been met so that the
     * user/person viewing the test has enough time to see that it has completed correctly
     *
     * @param x number of seconds for the test script to continue executing once the condition it is testing has been met
     * @return the timeline which expires after x seconds
     */
    public Timeline secondsTimer(double x){
        var frame = new KeyFrame(Duration.seconds(x), e -> {});
        Timeline timer = new Timeline();
        timer.setCycleCount(1);
        timer.getKeyFrames().add(frame);

        return timer;
    }

    //================================================================================
    // Abstract Methods to be Implemented by Subclasses
    //================================================================================

    // Main test method run by specific test
    public abstract void runTest();

    // Set up function to add corresponding objects to stage
    public abstract void setUpTest();

    // Check if test passed/failed
    public abstract void passFail();


    //================================================================================
    // Stepping through Test
    //================================================================================

    /**
     * Passed into a KeyFrame for the test timeline
     * @param elapsedTime
     */
    public void testStep(double elapsedTime) {
        updateBallPosition(elapsedTime);
        ballBoundsDetect();
    }

    // Update ball position every step
    public void updateBallPosition(double elapsedTime){
        ball.setX(ball.getX() + ball.getVelX() * elapsedTime);
        ball.setY(ball.getY() + ball.getVelY() * elapsedTime);
    }

    // Detects boundaries
    public void ballBoundsDetect() {
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
        }
    }

    /**
     * Detects when ball collides with brick, and if so, return the brick to be removed
     * @return Node
     */
    public Brick breakBrickDetect() {
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
     * Specifies physics for ball when it collides with a brick
     * @return the Brick to be removed
     */
    public Brick brickBounce(){
            Brick toRemove = null;
            for (Node brick : root.getChildren()) {
                if (brick instanceof Brick) {
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
     * Removes the brick specified from the root
     * @param toRemove
     */
    public void removeBrick(Brick toRemove){
        if(toRemove !=null) {
            root.getChildren().remove(toRemove);
        }
    }

    //================================================================================
    // End Test Scenarios
    //================================================================================

    // Creates test complete text when tests finish
    public void makeTestCompleteText(String testType){
        root.getChildren().clear();

        Text completeText = new Text();

        completeText.setFont(Font.font("impact", FontWeight.BOLD, FontPosture.REGULAR, 50));
        completeText.setY((float)mySize/3);

        completeText.setText("Test " + testType + " Passed!");
        completeText.setFill(Color.BLUE);
        completeText.setX((float) (mySize - completeText.getLayoutBounds().getWidth())/2);
        root.getChildren().add(completeText);
    }

    // Creates test failure text when tests finish
    public void makeTestFailureText(String testType) {
        root.getChildren().clear();

        Text failText = new Text();

        failText.setFont(Font.font("impact", FontWeight.BOLD, FontPosture.REGULAR, 50));
        failText.setY((float)mySize/3);

        failText.setText("Test " + testType + " Failed!");
        failText.setFill(Color.RED);
        failText.setX((float) (mySize - failText.getLayoutBounds().getWidth())/2);
        root.getChildren().add(failText);
    }
}
