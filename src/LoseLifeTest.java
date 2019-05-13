import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Test class used to check whether the scoreboard displays a life lost when the ball hits the bottom
 * Assumptions: Relies on parent class Tester for methods and instance vairables
 */
public class LoseLifeTest extends Tester {

    // Instance variables for Testing
    private Group root;
    private Ball ball;
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private String testFile;
    private int initXVel;
    private int initYVel;
    private int INITIAL_LIVES = PlayLevel.INITIAL_LIVES;
    private Brick[] bricks;


    // Constructor that inherits from Tester
    public LoseLifeTest(Group root, Ball ball, StatusDisplay currentStatus, String testFile, Brick[] bricks){
        super(root, ball, currentStatus, testFile, bricks);
        this.root = super.getRoot();
        this.ball = super.getBall();
        this.bricks = super.getBricks();
        this.currentStatus = super.getCurrentStatus();
        this.currentCanvas = currentStatus.display();
        this.testFile = super.getTestFileName();
    }

    /**
     * Passed into a KeyFrame for the test timeline, called every millisecond_delay
     * @param elapsedTime
     */
    @Override
    public void testStep(double elapsedTime) {
        super.updateBallPosition(elapsedTime);
        super.ballBoundsDetect();
        super.removeBrick(breakBrickDetect());
        ballHitsBottomDetectTest();
    }

    // Initializes ball position, runs setup and test timeline
    public void runTest(){
        this.initXVel = ball.getVelX();
        this.initYVel = ball.getVelY();
        setUpTest();
        testTimeline = super.makeTestTimeline();
        testTimeline.play();
    }

    // Setup: Ball, initialize canvas
    public void setUpTest(){
        // reinitialize current status to 3 lives
        root.getChildren().clear();
        root.getChildren().add(ball);
        root.getChildren().addAll(bricks);

        this.currentStatus = new StatusDisplay(mySize * 17/20, mySize * 7/10, mySize / 4, mySize / 4, INITIAL_LIVES, 1);
        this.currentCanvas = currentStatus.display();
        root.getChildren().add(currentCanvas);
    }

    // Condition for ball hitting bottom, calls passFail() if so
    public void ballHitsBottomDetectTest() {
        if (ball.getY() > mySize - ball.getBoundsInLocal().getHeight()) {
            root.getChildren().remove(currentCanvas);
            int tempLives = currentStatus.getLives();
            int tempScore = currentStatus.getScore();
            // Not the most efficient way, but the way we did our status display is each time display() is called, it would layer
            // the new value over the previous value, and make it unreadable, so we created a new instance each time.
            this.currentStatus = new StatusDisplay(mySize * 17/20, mySize * 7/10, mySize / 4, mySize / 4, tempLives - 1, 1);
            this.currentStatus.setScore(tempScore);
            this.currentCanvas = currentStatus.display();
            root.getChildren().add(currentCanvas);
            ball.setX(mySize / 2);
            ball.setY(mySize / 5);

            Timeline testTimer = secondsTimer(1);
            testTimer.setOnFinished(e -> passFail());
            testTimer.play();
        }
    }

    // Tests life is lost when ball hits bottom
    public void passFail() {
        super.secondsTimer(1).play();
        if(currentStatus.getLives() == this.INITIAL_LIVES) {
            testTimeline.pause();
            super.makeTestFailureText("lose_life");
        }
        else {
            testTimeline.pause();
            super.makeTestCompleteText("lose_life");
        }
    }



}
