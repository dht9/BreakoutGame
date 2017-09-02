package game_dht9;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Bouncer used in Breakout
 * 
 * @author David Tran
 */
public class Bouncer {

    private ImageView myView;
    private Point2D myVelocity;
    private int BALL_SIZE = 10;
    public double MAX_BOUNCE_ANGLE = 45;
    public double BOUNCER_SPEED = 250;

    /**
     * Create a bouncer from a given image with random attributes.
     */
    public Bouncer (Image image, int screenWidth, int screenHeight) {
        myView = new ImageView(image);
        // make sure it stays a circle
        myView.setFitWidth(BALL_SIZE);
        myView.setFitHeight(BALL_SIZE);
        // make sure it stays within the bounds
        myView.setX(screenWidth / 2 - BALL_SIZE /2);
        myView.setY(screenHeight / 2 - BALL_SIZE /2);
        // turn speed into velocity that can be updated on bounces
        myVelocity = new Point2D(0,BOUNCER_SPEED);
    }
 
    /**
     * Move by taking one step based on its velocity.
     * 
     * Note, elapsedTime is used to ensure consistent speed across different machines.
     */
    public void move (double elapsedTime) {
        myView.setX(myView.getX() + myVelocity.getX() * elapsedTime);
        myView.setY(myView.getY() + myVelocity.getY() * elapsedTime);
    }

    /**
     * Bounce off the walls represented by the edges of the screen.
     */
    public void bounce(double screenWidth, double screenHeight) {
        // collide all bouncers against the 3 walls
        if (myView.getX() < 0 || myView.getX() > screenWidth - myView.getBoundsInLocal().getWidth()) {
            myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
        }
        if(myView.getY() < 0) {
            myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
        }
    }
    
    /**
     * Returns internal view of bouncer to interact with other JavaFX methods.
     */
    public Node getView () {
        return myView;
    }

}