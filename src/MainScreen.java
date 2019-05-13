import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.util.Duration;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Main class to run the game
 * Assumptions: Relies on PlayLevel to start the game
 * dependencies (what other classes or packages it depends on)
 * an example of how to use it
 */
public class MainScreen extends Application {

    // Global Variables defined
    public static final int WINDOW_SIZE = 600;
    public static final int BRICK_WIDTH = 100;
    public static final int BRICK_HEIGHT = 25;
    public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;

    /**
     * Runs program:
     * 1) Generates playLevel, which first shows Splash, then the levels themselves
     * 2) Creates Animation for the level
     * 3) If program is in restart mode, recreate the new level.
     * @param primaryStage
     */
    public void start(Stage primaryStage){

        PlayLevel playLevel = new PlayLevel();

        Scene scene = playLevel.makeScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle("System.Out.BREAK");
        primaryStage.show();

        var frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> playLevel.step(SECOND_DELAY));
        var animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();

        playLevel.setAnimation(animation);

    }

    /**
     * Start the program.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
