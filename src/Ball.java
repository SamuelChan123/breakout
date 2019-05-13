import javafx.scene.image.ImageView;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Essentially an imageview, it also stores the size and velocity of itself for use by PlayLevel
 */
public class Ball extends ImageView {

    private int velX;
    private int velY;
    private int size;

    public Ball(int x, int y, int size, int velX, int velY, String image) {
        this.setX(x);
        this.setY(y);
        this.setVel(velX, velY);
        this.size = size;
        this.setFitHeight(size);
        this.setFitWidth(size);

        // Set ball image from file locally under /data directory that matches the provided string
        this.setImage(new javafx.scene.image.Image(this.getClass().getClassLoader().getResourceAsStream(image)));
    }

    // Set velocity: x and y
    public void setVel(int velX, int velY) {
        this.velX = velX;
        this.velY = velY;
    }

    // Get x velocity
    public int getVelX() {
        return velX;
    }

    // Get y velocity
    public int getVelY() {
        return velY;
    }

    // Get size of ball
    public int getSize(){ return size; }
}
