import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Test class used to check whether ball bounces on corner
 * Assumptions: Relies on parent class Tester for methods and instance vairables
 */

public class CornerBounceTest extends Tester{

    // Instance Variables
    private Group root;
    private Ball ball;
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private String testFile;
    private int initXVel;
    private int initYVel;
    private int INITIAL_LIVES = PlayLevel.INITIAL_LIVES;
    private Brick[] bricks;


    // Constructor
    public CornerBounceTest(Group root, Ball ball, StatusDisplay currentStatus, String testFile, Brick[] bricks){
        super(root, ball, currentStatus, testFile, bricks);
        this.root = super.getRoot();
        this.ball = super.getBall();
        this.bricks = super.getBricks();
        this.currentStatus = super.getCurrentStatus();
        this.currentCanvas = currentStatus.display();
        this.testFile = super.getTestFileName();
    }


    // Main test method run, calls setup, initialization, and starts test timeline
    public void runTest(){
        this.initXVel = ball.getVelX();
        this.initYVel = ball.getVelY();
        setUpTest();

        testTimeline = super.makeTestTimeline();
        testTimeline.play();
    }

    // Adds ball only to scene to test corner
    public void setUpTest() {
        root.getChildren().clear();
        root.getChildren().addAll(bricks);
        root.getChildren().add(ball);
    }

    // Called every millisecond delay, checks if ball bounces from corner
    public void testStep(double elapsedTime){
        super.testStep(elapsedTime);
        ballHitsBottomDetectTest();
        checkTest();
    }

    // If hits corner, start a timer and check if the velocity is flipped
    public void checkTest(){
        if(ball.getLayoutBounds().getMinX() - ball.getBoundsInLocal().getWidth() <= 0 || ball.getLayoutBounds().getMinY() - ball.getBoundsInLocal().getHeight() <= 0
                || ball.getLayoutBounds().getMaxY() >= mySize - ball.getBoundsInLocal().getHeight()
                || ball.getLayoutBounds().getMaxX() >= mySize - ball.getBoundsInLocal().getWidth()) {
            Timeline time = secondsTimer(1);
            time.setOnFinished(e->passFail());
            time.play();
        }
    }

    // If 1 second after bounce, velocity is not flipped/negative, fail, otherwise, success
    public void passFail(){
        if(ball.getVelX() != this.initXVel*-1 || ball.getVelY() != this.initYVel*-1) {
            testTimeline.pause();
            super.makeTestFailureText("corner_bounce");
        }
        else {
            testTimeline.pause();
            super.makeTestCompleteText("corner_bounce");
        }
    }

    // checks if velocity is negative if hits bottom
    public void ballHitsBottomDetectTest(){
        checkTest();
    }

}
