import javafx.scene.image.*;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Brick class that is essentially an ImageView, has a type that is specified by brick config and changes appearance based on type
 */
public class Brick extends ImageView {

    private int type;

    public Brick(int type) {
        this.type = type;
        this.setImage(new Image(this.getClass().getClassLoader().getResourceAsStream("brick" + type + ".gif")));
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }


}
