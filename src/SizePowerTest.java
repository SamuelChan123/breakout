import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import java.util.ArrayList;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Test class used to check whether powerups work
 * Assumptions: Relies on parent class Tester for methods and instance vairables
 */

public class SizePowerTest extends Tester {

    // Instance variable, stores bricks list too
    private Group root;
    private Ball ball;
    private Paddle paddle;
    private StatusDisplay currentStatus;
    private Canvas currentCanvas;
    private String testFile;
    private int totalBricks;
    private ArrayList<PowerUp> powerUps = new ArrayList<>();
    private int initXVel;
    private int initYVel;
    private int INITIAL_LIVES = PlayLevel.INITIAL_LIVES;
    private ImageView[] bricks;
    private int ballVel;

    private String PADDLE_IMAGE = "paddle.gif";
    private static final int PADDLE_SIZE_X = 75;
    private static final int PADDLE_SIZE_Y = 10;
    private static final int PADDLE_SPEED = 10;
    private boolean ENLARGED = false;

    // Constructor
    public SizePowerTest(Group root, Ball ball, StatusDisplay currentStatus, String testFile, Brick[] bricks){
        super(root, ball, currentStatus, testFile, bricks);
        this.root = super.getRoot();
        this.ball = super.getBall();
        this.ballVel = ball.getVelY();
        this.currentStatus = super.getCurrentStatus();
        this.currentCanvas = currentStatus.display();
        this.testFile = super.getTestFileName();
        this.bricks = super.getBricks();

    }

    public void setPaddle(Paddle paddle){
        this.paddle = paddle;
    }

    // Main test method called
    public void runTest(){
        this.initXVel = ball.getVelX();
        this.initYVel = ball.getVelY();
        setUpTest();
        testTimeline = super.makeTestTimeline();
        testTimeline.play();
    }


    // Sets up bricks, balls, and paddle
    public void setUpTest() {
        root.getChildren().clear();
        root.getChildren().addAll(bricks);
        root.getChildren().add(ball);
        root.getChildren().add(paddle);
        totalBricks = root.getChildren().size()-1;
    }

    // Called every millisecond_delay in test timeline
    public void testStep(double elapsedTime){
        super.testStep(elapsedTime);
        Brick removed = super.breakBrickDetect();
        generatePowerUp(removed);
        dropPowerUps(elapsedTime);
    }

    // Generate powerup for the test
    public void generatePowerUp(Brick removed){
        if(removed!=null) {
            root.getChildren().remove(removed);
            PowerUp powerUp = new PowerUp((int) removed.getX(), (int) removed.getY(), ball.getSize(), ballVel/2, "size");
            powerUps.add(powerUp);
            root.getChildren().add(powerUp);
        }
    }

    // Checks powerup/paddle interaction and specifies result
    public void dropPowerUps(double elapsedTime){
        PowerUp toRemove = null;
        for(PowerUp powerUp: powerUps){
            powerUp.setY(powerUp.getY() - powerUp.getVelY() * elapsedTime);
            if(powerUp.intersects(paddle.getBoundsInParent()) && !powerUp.intersects(ball.getBoundsInParent())){
                toRemove = powerUp;
                enlargeTestPaddle();
                ENLARGED = true;
            }
        }
        if(toRemove != null){
            root.getChildren().remove(toRemove);
            powerUps.remove(toRemove);
        }
    }

    /**
     * This function is called when the size power up hits the paddle and makes the paddle twice the size
     * and tests to see if it is the correct width
     */
    private void enlargeTestPaddle(){
        Timeline testSizeTimeline = secondsTimer(1);

        if(!ENLARGED) {
            ENLARGED = true;
            Scale paddleScale = new Scale();
            paddleScale.setX(2);
            paddle.getTransforms().add(paddleScale);
            paddle.setX(paddle.getLayoutBounds().getMinX() / 2);

            testSizeTimeline.setOnFinished(e -> passFail());
            testSizeTimeline.play();
        }

    }

    // Checks if paddle was enlarged to twice its size after intersects power up
    public void passFail() {
        System.out.println("Gets here");
        if(paddle.getBoundsInParent().getWidth() == PADDLE_SIZE_X * 2){
            testTimeline.pause();
            makeTestCompleteText("size_power");
        }
        else{
            testTimeline.pause();
            makeTestFailureText("size_power");
        }
    }



}
