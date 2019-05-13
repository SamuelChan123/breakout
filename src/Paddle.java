import javafx.scene.image.ImageView;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Paddle used (really mostly an Imageview) that stores velocity for use by PlayLevel
 */
public class Paddle extends ImageView {

    private int vel;

    public Paddle(int speed, int x, int y, int sizeX, int sizeY, String image) {
        this.setX(x);
        this.setY(y);
        this.vel = speed;
        this.setFitWidth(sizeX);
        this.setFitHeight(sizeY);

        // Set paddle image from file locally under /data directory that matches the provided string
        this.setImage(new javafx.scene.image.Image(this.getClass().getClassLoader().getResourceAsStream(image)));
    }

    // Set Velocity
    public void setVel(int vel) {
        this.vel = vel;
    }

    // Get Velocity
    public int getVel(){ return this.vel; }
}
