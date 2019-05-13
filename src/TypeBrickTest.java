import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Test class used to check whether bricks (not of type 1) will change into bricks of type-1
 * Assumptions: Relies on parent class Tester for methods and instance vairables
 */

public class TypeBrickTest extends Tester {

    // Instance variable, stores bricks list too
    private Group root;
    private Ball ball;
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private String testFile;
    private int totalBricks;
    private int brickType;
    private int initXVel;
    private int initYVel;
    private Brick nextDown = null;
    private int INITIAL_LIVES = PlayLevel.INITIAL_LIVES;
    private Brick[] bricks;

    // Constructor
    public TypeBrickTest(Group root, Ball ball, StatusDisplay currentStatus, String testFile, Brick[] bricks){
        super(root, ball, currentStatus, testFile, bricks);
        this.root = super.getRoot();
        this.ball = super.getBall();
        this.brickType = bricks[0].getType();
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
            if(toBeRemoved.getType() > 1) {
                nextDown = new Brick(toBeRemoved.getType()-1);
                nextDown.setFitWidth(toBeRemoved.getFitWidth());
                nextDown.setFitHeight(toBeRemoved.getFitHeight());
                nextDown.setX(toBeRemoved.getX());
                nextDown.setY(toBeRemoved.getY());
                root.getChildren().add(nextDown);
            }
            root.getChildren().remove(toBeRemoved);

            Timeline tempTimer = secondsTimer(0.5);
            tempTimer.setOnFinished(e -> passFail());
            tempTimer.play();
        }
    }


    // Check if brick was broken
    public void passFail() {
        if (nextDown != null) {
            if(nextDown.getType() == brickType - 1) {
                testTimeline.pause();
                makeTestCompleteText("type_brick");
            } else {
                testTimeline.pause();
                makeTestFailureText("type_brick");
            }
        }
    }

    // If balls hits bottom, check if brick was broken
    public void ballHitsBottomDetectTest() {
        if (ball.getY() > mySize - ball.getBoundsInLocal().getHeight()) {
            passFail();
        }
    }


}
