import java.util.*;
import java.awt.Point;

/**
 * @author Samuel Chan
 * @author Jaiveer Katariya
 * Purpose: Parses Config file for bricks and ball position
 */
public class ParseFile {

    private String file;
    int BRICK_WIDTH;
    int BRICK_HEIGHT;
    String FILE_NAME;


    public ParseFile(int brickWidth, int brickHeight, String fileName){
        this.BRICK_WIDTH = brickWidth;
        this.BRICK_HEIGHT = brickHeight;
        this.FILE_NAME = fileName;
        if(this.FILE_NAME == null){
            System.out.println("Couldn't load level file, using test file instead");
            this.FILE_NAME = "level3.txt";
        }
    }

    /**
     * Parses bricks input file, outputs points for each brick
     * @return Point[]
     */
    public HashMap<Integer, List<Point>> generateArray() {

        HashMap<Integer, List<Point>> map = new HashMap<>();
        Scanner scan = new Scanner(ParseFile.class.getClassLoader().getResourceAsStream(FILE_NAME));
        int numOfBricks = Integer.parseInt(scan.nextLine());
        int counter = 0;
        while (scan.hasNext()) {
            if(counter >= numOfBricks) {
                break;
            }
            int type = scan.nextInt();
            int x = scan.nextInt();
            int y = scan.nextInt();
            Point point = new Point(x,y);
            map.putIfAbsent(type, new ArrayList<Point>());
            map.get(type).add(point);
            counter++;
        }
        return map;
    }

    /**
     * Create brick (ImageView) for passed in Point value
     * @param p
     * @return
     */
    public Brick makeBrickFromPoint(Point p, int type){

        Brick brick = new Brick(type);
        brick.setFitWidth(BRICK_WIDTH);
        brick.setFitHeight(BRICK_HEIGHT);
        brick.setX(p.x);
        brick.setY(p.y);

        return brick;
    }

    /**
     * Loads bricks from input files, makes each brick
     *
     * @return array of bricks from points
     */
    public Brick[] makeBricks(){
        HashMap<Integer, List<Point>> allBricks = generateArray();
        List<Brick> madeBricks = new ArrayList<>();

        Brick brick;
        for (int type : allBricks.keySet()){
            for(Point p : allBricks.get(type)) {
                brick = makeBrickFromPoint(p, type);
                madeBricks.add(brick);
            }
        }

        Brick[] bricks = new Brick[madeBricks.size()];
        bricks = madeBricks.toArray(bricks);

        return bricks;
    }

    //================================================================================
    // Manual Scanning
    //================================================================================

    public void run() {
        Scanner user = new Scanner(System.in);
        System.out.println("Enter filename (under /data directory):");
        file = user.nextLine().trim();
    }

    private Scanner rawScanner() {
        System.out.println(file);
        try{
            Scanner scan = new Scanner(ParseFile.class.getClassLoader().getResourceAsStream(file));
            return scan;

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println("No such file.");
        }
        return null;
    }

}
