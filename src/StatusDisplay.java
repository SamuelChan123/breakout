import com.sun.tools.javac.Main;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Specifies status display (scoreboard) for the player, including lives, level, high score, and current score
 * Updates/retrieves high score when needed
 */
public class StatusDisplay {

    private Canvas canvas = new Canvas();
    private GraphicsContext gc;
    private int lives;
    private int level;
    private int score;
    private int highScore;

    public StatusDisplay(int xPos, int yPos, int height, int width, int lives, int level) {
        canvas.setHeight(height);
        canvas.setWidth(width);
        canvas.setLayoutX(xPos);
        canvas.setLayoutY(yPos);
        this.lives = lives;
        this.level = level;
        this.score = 0;
        this.highScore = fetchHighScore();
        gc = canvas.getGraphicsContext2D();
        gc.setFill( Color.RED );
        gc.setStroke( Color.BLACK );
        gc.setLineWidth(0.5);
        Font theFont = Font.font( "Times New Roman", FontWeight.NORMAL, 15 );
        gc.setFont( theFont );

    }

    // Display player's lives, score, and current level
    public Canvas display() {
        gc.fillText( "High Score: " + String.valueOf(highScore), 0, 40 );
        gc.strokeText( "High Score: " + String.valueOf(highScore), 0, 40 );
        gc.fillText( "Score: " + String.valueOf(score), 20, 60 );
        gc.strokeText( "Score: " + String.valueOf(score), 20, 60 );
        gc.fillText( "Lives: " + String.valueOf(lives), 20, 80 );
        gc.strokeText( "Lives: " + String.valueOf(lives), 20, 80 );
        gc.fillText( "Level: " + String.valueOf(level), 20, 100 );
        gc.strokeText( "Level: " + String.valueOf(level), 20, 100 );
        return canvas;
    }

    // Set lives
    public void setLives(int lives) {
        this.lives = lives;
    }

    // Set level
    public void setLevel(int level) {
        this.level = level;
    }

    // Set Score
    public void setScore(int score) {
        this.score = score;
    }

    // Get Lives
    public int getLives() {
        return this.lives;
    }

    // Get Level
    public int getLevel() {
        return this.level;
    }

    // Get Score
    public int getScore() {
        return this.score;
    }

    // Finds highest score from the high score file
    public int fetchHighScore(){
        File highScoreFile = new File("./data/highScore.txt");

        Scanner highScoreScanner = null;
        try {
            highScoreScanner = new Scanner(highScoreFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Integer.parseInt(highScoreScanner.next());
    }

    // Returns high score
    public int getHighScore(){
        return this.highScore;
    }

    public void setHighScore(int score){
        this.highScore = score;
        writeHighScore(score);

    }

    // Updates high score file
    public void writeHighScore(int score){
        File toWrite = new File("./data/highScore.txt");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(toWrite);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print(Integer.toString(score));
        writer.close();

    }

    // Checks condition for high score
    public Text updateHighScore(){
        if(this.score > this.highScore){
            setHighScore(score);

            return makeHighScoreText();
        }
        return null;
    }

    // Makes high score text if one broke the previous record
    public Text makeHighScoreText(){
        Text highScoreText = new Text();
        highScoreText.setText("New high score!");
        highScoreText.setTextAlignment(TextAlignment.CENTER);
        highScoreText.setFont(Font.font("gruppo", FontWeight.NORMAL, FontPosture.REGULAR, 20));

        highScoreText.setX((MainScreen.WINDOW_SIZE - (highScoreText.getLayoutBounds().getWidth()) ) /2);
        highScoreText.setY(((((float) MainScreen.WINDOW_SIZE) - highScoreText.getLayoutBounds().getHeight()) / 2) + 50);
        return highScoreText;
    }


}


