package game_dht9;

//import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

/**
 * Bouncer used in Breakout
 * 
 * @author David Tran
 */
public class Bouncer {

	public ImageView myView;
	private Point2D myVelocity;
	private double BALL_SIZE = 20; // 16 for symmetry
	public double MAX_BOUNCE_ANGLE = 60;
	public double BOUNCER_SPEED = 350;

	/**
	 * Create a bouncer from a given image.
	 */
	public Bouncer(Image image, double screenWidth, double screenHeight) {
		myView = new ImageView(image);
		// make sure it stays a circle
		myView.setFitWidth(BALL_SIZE);
		myView.setFitHeight(BALL_SIZE);
		// make sure it stays within the bounds
		myView.setX(0);
		myView.setY(0);
//		recenter()
		// turn speed into velocity that can be updated on bounces
		myVelocity = new Point2D(0, 0);
//		System.out.println(myView.getX() + " , " + myView.getY());
	}
	
	public double getVelocityX() {
		return myVelocity.getX();
	}
	public double getVelocityY() {
		return myVelocity.getY();
	}

	/**
	 * Move by taking one step based on its velocity.
	 * 
	 * Note, elapsedTime is used to ensure consistent speed across different
	 * machines.
	 */
	public void move(double elapsedTime) {
		myView.setX(myView.getX() + myVelocity.getX() * elapsedTime);
		myView.setY(myView.getY() + myVelocity.getY() * elapsedTime);
	}

	/**
	 * Bounce off the walls represented by the edges of the screen.
	 */
	public void bounce(double screenWidth, double screenHeight) {
		// collide all bouncers against the walls
		if (myView.getX() < 0 || myView.getX() > screenWidth - myView.getBoundsInLocal().getWidth()) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
//		if (myView.getY() < 0) {
//			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
//		}
	}
	
	/**
	 * Check if ball is out-of-bounds
	 */
	public boolean outOfBounds(double screenWidth, double screenHeight) {
		if (myView.getY() > screenHeight || myView.getY() + myView.getFitHeight() < 0)
			return true;
		if (myView.getX() > screenWidth || myView.getX() + myView.getFitWidth() < 0)
			return true;
		return false;	
	}

	/**
	 * Bounce off the paddle.
	 */
	public void bounceOffPaddle(Paddle myPaddle) {
		// check if ball hits top or bottom of paddle
		if (myView.getY() + myView.getFitHeight() <= myPaddle.myView.getY() + myPaddle.myView.getFitHeight() / 2
				|| myView.getY() >= myPaddle.myView.getY() + myPaddle.myView.getFitHeight() / 2) {
			// calculate distance between ball and paddle center
			double distFromCenter = myView.getX() + myView.getFitWidth() / 2
					- (myPaddle.myView.getX() + myPaddle.myView.getFitWidth() / 2);
			System.out.println(distFromCenter);
			// normalize the distance [-1,1]
			double normalizedDistFromCenter = distFromCenter / (myPaddle.myView.getFitWidth() / 2);
			// calculate angle ball will bounce
			double bounceAngle = normalizedDistFromCenter * MAX_BOUNCE_ANGLE;
			double ballVx = BOUNCER_SPEED * Math.sin(Math.toRadians(bounceAngle));
			myVelocity = new Point2D(ballVx, -myVelocity.getY());
		}
		// check if ball hits left side of paddle
		else if (myView.getX() + myView.getFitWidth() / 2 <= myPaddle.myView.getX()) {
			myVelocity = new Point2D((-BOUNCER_SPEED + myPaddle.myVelocity.getX()) * Math.sin(45),
					BOUNCER_SPEED * Math.cos(45));
			System.out.println((-BOUNCER_SPEED + myPaddle.myVelocity.getX()) * Math.sin(45));
		}
		// check if ball hits right side of paddle
		else if (myView.getX() + myView.getFitWidth() / 2 >= myPaddle.myView.getX() + myPaddle.myView.getFitWidth()) {
			myVelocity = new Point2D((BOUNCER_SPEED + myPaddle.myVelocity.getX()) * Math.sin(45),
					BOUNCER_SPEED * Math.cos(45));
		}
	}

	/**
	 * Bounce off the bricks.
	 */
	public void bounceOffBrick(Brick myBrick) {
		// check if ball hits on top of brick
		if (myView.getY() + myView.getFitHeight() <= myBrick.getY() + myBrick.getHeight() / 5) {
			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
		}
		// check if ball hits on bottom of brick
		else if (myView.getY() >= myBrick.getY() + myBrick.getHeight() * 4 / 5) {
			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
		}
		// check if ball hits on left of brick
		else if (myView.getX() + myView.getFitWidth() <= myBrick.getX() + myBrick.getWidth() / 5) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
		// check if ball hits on right of brick
		else if (myView.getX() >= myBrick.getX() + myBrick.getWidth() * 4 / 5) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
		// System.out.println("hit n/a");
	}

	
	/**
	 * Recentering - release ball after SPACEBAR entered
	 * @return
	 */
	public void recenter(double x, double y) {
		myView.setX(x);
		myView.setY(y);
		myVelocity = new Point2D(0,0);
//		System.out.println(myView.getX() + " , " + myView.getY());
	}
	public void releaseBall(KeyCode code) {
		// if SPACEBAR entered, release ball
		myVelocity = new Point2D(0, -BOUNCER_SPEED);
		System.out.println("release");
	}

	/**
	 * Returns internal view of bouncer to interact with other JavaFX methods.
	 */
	public Node getView() {
		return myView;
	}
}