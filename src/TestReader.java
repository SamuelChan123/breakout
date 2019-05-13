import java.util.Scanner;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Test class used to check whether powerups work
 * Assumptions: Called by Tester class to parse Test config files.
 */

public class TestReader {

    // Instance variables
    private String testType;
    private int ballXPos;
    private int ballYPos;
    private int ballXVel;
    private int ballYVel;
    private Brick[] bricks;

    /**
     * Constructor for the TestReader class that gets used by the PlayLevel class to set up a test case from a text file
     *
     * @param filename the text file from which to configure the test
     * @param brickWidth the width of bricks to generate, specified in PlayLevel
     * @param brickHeight the height of bricks to generate, specified in PlayLevel
     */
    public TestReader(String filename, int brickWidth, int brickHeight){
        Scanner scan = new Scanner(TestReader.class.getClassLoader().getResourceAsStream(filename));
        this.testType = scan.next();
        System.out.println("Test type is "+ this.testType);
        this.ballXPos = Integer.parseInt(scan.next());
        this.ballYPos = Integer.parseInt(scan.next());
        this.ballXVel = Integer.parseInt(scan.next());
        this.ballYVel = Integer.parseInt(scan.next());

        if(testType.equals("corner_bounce") && (this.ballYVel != -1*Math.abs(this.ballXVel) || this.ballYVel >= 0)){
            System.out.println("Ball Y velocity must be negative to bounce off a corner and must have same speed " +
                    "as X vel, and absolute value of X Vel must be equal to absolute value of ys vel to make sure ball hits " +
                    "the corner. Setting x and y velocities to specified values");
        }

        String bricksFile = scan.next();
        System.out.println("BRICKS FILE: " + bricksFile);
        this.bricks = new ParseFile(brickWidth, brickHeight, bricksFile).makeBricks();


    }

    /**
     * Function to get test type
     *
     * @return test type as is specified by text file
     */

    public String getTestType(){
        return testType;
    }

    /**
     * Function to get ball's starting x position
     *
     * @return ball's starting x position as is specified by text file
     */

    public int getBallXPos() {
        return ballXPos;
    }

    /**
     * Function to get ball's starting y position
     *
     * @return ball's starting y position as is specified by text file
     */

    public int getBallYPos() {
        return ballYPos;
    }

    /**
     * Function to get ball's starting x velocity
     *
     * @return ball's starting x velocity as is specified by text file
     */

    public int getBallXVel() {
        return ballXVel;
    }

    /**
     * Function to get ball's starting y velocity
     *
     * @return ball's starting y velocity as is specified by text file
     */
    public int getBallYVel() {
        return ballYVel;
    }

    /**
     * Function to get bricks from text file. This is useful primarily if a brick test is being done.
     *
     * @return array of bricks arranged according to the text file in the test configuration file
     */
    public Brick[] getBricks() {
        return bricks;
    }
}
