import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.text.*;
import javafx.scene.paint.Color;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Splash screen that loads the instructions for the player upon start of game
 */
public class Splash {

    // Size of splash screen
    private int mySize;

    /**
     * Constructor for windowSize
     * @param windowSize
     */
    public Splash(int windowSize){
        this.mySize = windowSize;
    }

    /**
     * Method to return the splash screen text nodes
     * @return ObservableList<Node>
     */
    public ObservableList<Node> getSplashChildren(){

        Group root = new Group();

        Text titleText = makeTitleText();
        Text instructionText = makeInstructionText();
        Text toContinue = makeToContinueText();

        root.getChildren().add(titleText);
        root.getChildren().add(instructionText);
        root.getChildren().add(toContinue);

        return root.getChildren();
    }

    //================================================================================
    // Create Title Texts
    //================================================================================

    /**
     * Generates title text
     * @return Text
     */
    private Text makeTitleText() {
        Text titleText = new Text();

        titleText.setText("System.Out.BREAK");
        titleText.setFont(Font.font("impact", FontWeight.BOLD, FontPosture.REGULAR, 50));
        titleText.setFill(Color.RED);

        titleText.setX(getMiddlePosition(titleText.getLayoutBounds().getWidth()));
        titleText.setY((float)mySize/4);

        return titleText;
    }

    /**
     * Generates instruction text
     * @return Text
     */
    private Text makeInstructionText() {
        Text instructionText = new Text();

        instructionText.setText("The goal of this game is to break the bricks on\n" +
                "your screen using the ball and paddle. \n" +
                "If you let the ball fall to the ground, you will\n" +
                "lose a life!\n" +
                "You have three lives before the game is over.\n" +
                "If you break all the bricks on the screen, you win!");
        instructionText.setTextAlignment(TextAlignment.CENTER);
        instructionText.setFont(Font.font("gruppo", FontWeight.NORMAL, FontPosture.REGULAR, 20));

        instructionText.setX(getMiddlePosition(instructionText.getLayoutBounds().getWidth()));
        instructionText.setY((((float) mySize) - instructionText.getLayoutBounds().getHeight()) / 2);

        return instructionText;
    }

    /**
     * Generates to Continue text
     * @return Text
     */
    private Text makeToContinueText() {
        Text toContinue = new Text();

        toContinue.setText("Press any key or click to continue!");
        toContinue.setFont(Font.font("gruppo", FontWeight.NORMAL, FontPosture.REGULAR, 30));

        toContinue.setX(getMiddlePosition(toContinue.getLayoutBounds().getWidth()));
        toContinue.setY((((float) mySize) - toContinue.getLayoutBounds().getHeight()) * 3 / 4);

        return toContinue;
    }

    /**
     * Helper function for determining middle position of Window
     * @param sizeOfObject
     * @return
     */
    private double getMiddlePosition(double sizeOfObject){
        return (float) (mySize - sizeOfObject) / 2;
    }

}
