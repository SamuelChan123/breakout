import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Test class used to check whether the brick is destroyed when the ball hits it
 * Assumptions: Relies on parent class Tester for methods and instance vairables
 */
public class DestroyBrickTest extends Tester {

    // Instance variable, stores bricks list too

    private Group root;
    private Ball ball;
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private String testFile;
    private int totalBricks;
    private int initXVel;
    private int initYVel;
    private int INITIAL_LIVES = PlayLevel.INITIAL_LIVES;
    private Brick[] bricks;

    // Constructor
    public DestroyBrickTest(Group root, Ball ball, StatusDisplay currentStatus, String testFile, Brick[] bricks){
        super(root, ball, currentStatus, testFile, bricks);
        this.root = super.getRoot();
        this.ball = super.getBall();
        this.currentStatus = super.getCurrentStatus();
        this.currentCanvas = currentStatus.display();
        this.testFile = super.getTestFileName();
        this.bricks = super.getBricks();
    }

    // Main test method called
    public void runTest(){
        this.initXVel = ball.getVelX();
        this.initYVel = ball.getVelY();
        setUpTest();
        testTimeline = super.makeTestTimeline();
        testTimeline.play();
    }

    // Sets up bricks and balls
    public void setUpTest() {
        root.getChildren().clear();
        root.getChildren().addAll(bricks);
        root.getChildren().add(ball);
        totalBricks = root.getChildren().size()-1;
    }

    // Called every millisecond_delay in test timeline
    public void testStep(double elapsedTime){
        super.testStep(elapsedTime);
        Brick removed = super.breakBrickDetect();
        checkTest(removed);
        ballHitsBottomDetectTest();
    }

    // Removes brick if ball hits it, sets a timer and checks for broken brick condition later
    private void checkTest(Brick toBeRemoved) {
        if(toBeRemoved != null) {
            root.getChildren().remove(toBeRemoved);
            Timeline tempTimer = secondsTimer(0.5);
            tempTimer.setOnFinished(e -> passFail());
            tempTimer.play();
        }
    }


    // Check if brick was broken
    public void passFail() {
        if(totalBricks > root.getChildren().size()-1) {
            testTimeline.pause();
            makeTestCompleteText("destroy_brick");
        } else {
            testTimeline.pause();
            makeTestFailureText("destroy_brick");
        }
    }

    // If balls hits bottom, check if brick was broken
    public void ballHitsBottomDetectTest() {
        if (ball.getY() > mySize - ball.getBoundsInLocal().getHeight()) {
            passFail();
        }
    }


}
