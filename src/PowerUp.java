import javafx.scene.image.ImageView;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Essentially an imageview, the powerup stores size, type, and velocity for use by PlayLevel, based on its type, it changes
 * the effect of the paddle or the scoreboard update
 */
public class PowerUp extends ImageView {

    private int velX;
    private int velY;
    private int size;
    private String type;

    public PowerUp(int x, int y, int size, int velY, String type) {
        this.setX(x);
        this.setY(y);
        this.setVel(velX, velY);
        this.size = size;
        this.setFitHeight(size);
        this.setFitWidth(size);

        this.type = type;

        // Set ball image from file locally under /data directory that matches the provided string + power.gif
        this.setImage(new javafx.scene.image.Image(this.getClass().getClassLoader().getResourceAsStream(type + "power.gif")));

    }

    // Returns y velocity
    public int getVelY() {
        return velY;
    }

    // Sets velocity of powerup
    public void setVel(int velX, int velY) {
        this.velX = velX;
        this.velY = velY;
    }

    // Returns type of the powerup created
    public String getType(){
        return this.type;
    }
}
