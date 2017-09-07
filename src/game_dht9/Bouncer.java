package game_dht9;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Bouncer class used in Breakout Game
 * 
 * This class was inspired by:
 * https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/Bouncer.java
 * 
 * @author David Tran (dht9)
 */
public class Bouncer {

	public ImageView myView;
	private Point2D myVelocity;
	private static final double BOUNCER_SIZE = 18; // 16 for symmetry
	private static final double MAX_BOUNCE_ANGLE = 60;
	private static final double BOUNCER_SPEED = 250;
	private boolean hasReset;

	/**
	 * Initialize bouncer attributes.
	 */
	public Bouncer(Image image, double screenWidth, double screenHeight) {
		myView = new ImageView(image);
		// make sure it stays a circle
		myView.setFitWidth(BOUNCER_SIZE);
		myView.setFitHeight(BOUNCER_SIZE);
		// make sure it stays within the bounds
		myView.setX(-1);
		myView.setY(-1);
		// turn speed into velocity that can be updated on bounces
		myVelocity = new Point2D(0, 0);
		hasReset = true;
	}

	/**
	 * 
	 * Update the movement of the bouncer.
	 * 
	 */
	public void move(double elapsedTime) {
		myView.setX(myView.getX() + myVelocity.getX() * elapsedTime);
		myView.setY(myView.getY() + myVelocity.getY() * elapsedTime);
	}

	public void bounce() {
		// collide all bouncers against the walls
		if (myView.getX() < 0 || myView.getX() > GameEngine.SCREEN_WIDTH - myView.getBoundsInLocal().getWidth()) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
	}

	public void bounceOffPaddle(Paddle myPaddle, double screenHeight) {
		// check if ball hits top or bottom of paddle
		if (myView.getY() + myView.getFitHeight() <= myPaddle.getY() + myPaddle.getHeight() / 2
				|| myView.getY() >= myPaddle.getY() + myPaddle.getHeight() / 2) {
			calcBounceVelocity(myPaddle);
		}
		// check if ball hits left side of paddle
		else if (myView.getX() + myView.getFitWidth() * 3 / 4 <= myPaddle.getX()) {
			myVelocity = new Point2D((-BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(45),
					BOUNCER_SPEED * Math.cos(45));
			System.out.println((-BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(45));
		}
		// check if ball hits right side of paddle
		else if (myView.getX() + myView.getFitWidth() / 4 >= myPaddle.getX() + myPaddle.getWidth()) {
			myVelocity = new Point2D((BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(45),
					BOUNCER_SPEED * Math.cos(45));
		}
	}

	private void calcBounceVelocity(Paddle myPaddle) {
		double distFromCenter = myView.getX() + myView.getFitWidth() / 2 - (myPaddle.getX() + myPaddle.getWidth() / 2);
		double normalizedDistFromCenter = distFromCenter / (myPaddle.getWidth() / 2);
		double bounceAngle = normalizedDistFromCenter * MAX_BOUNCE_ANGLE;
		double ballVx = BOUNCER_SPEED * Math.sin(Math.toRadians(bounceAngle));
		double ballVy = BOUNCER_SPEED * Math.cos(Math.toRadians(bounceAngle));

		if (isStartingAtPaddle1())
			myVelocity = new Point2D(ballVx, -ballVy);
		else
			myVelocity = new Point2D(ballVx, ballVy);
	}

	public void bounceOffBrick(Brick myBrick) {
		// ball hits top side of brick
		if (myView.getY() + myView.getFitHeight() <= myBrick.getY() + myBrick.getHeight() / 5) {
			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
		}
		// ball hits bottom side of brick
		else if (myView.getY() >= myBrick.getY() + myBrick.getHeight() * 4 / 5) {
			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
		}
		// ball hits left side of brick
		else if (myView.getX() + myView.getFitWidth() <= myBrick.getX() + myBrick.getWidth() / 5) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
		// ball hits right side of brick
		else if (myView.getX() >= myBrick.getX() + myBrick.getWidth() * 4 / 5) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
	}

	public void releaseBall(Paddle myPaddle) {
		calcBounceVelocity(myPaddle);
		hasReset = false;
	}

	public boolean checkIfOutOfBounds() {
		this.hasReset = myView.getY() > GameEngine.SCREEN_HEIGHT || myView.getY() + myView.getFitHeight() < 0
				|| myView.getX() + myView.getFitWidth() / 2 > GameEngine.SCREEN_WIDTH
				|| myView.getX() + myView.getFitWidth() / 2 < 0;
		return this.hasReset;
	}

	public void repositionAndStop(double x, double y) {
		myView.setX(x);
		myView.setY(y);
		stop();
	}

	public void stop() {
		myVelocity = new Point2D(0, 0);
	}

	public void resetBouncer(Paddle myPaddle) {
		// set the ball to be center with myPaddle1
		myView.setX(myPaddle.getX() + myPaddle.getWidth() / 2 - myView.getFitWidth() / 2);
		myView.setY(GameEngine.SCREEN_HEIGHT - myView.getFitHeight() + Paddle.PADDLE1_OFFSET - 1);
		myView.setFitWidth(BOUNCER_SIZE);
		myView.setFitHeight(BOUNCER_SIZE);
		myVelocity = new Point2D(0, 0);
		hasReset = true;
	}

	public boolean hasReset() {
		return hasReset;
	}

	/**
	 * 
	 * Update bouncer attributes due to power ups.
	 * 
	 */
	public void expand() {
		myView.setFitWidth(2 * BOUNCER_SIZE);
		myView.setFitHeight(2 * BOUNCER_SIZE);

	}

	public void slowSpeed() {
		myVelocity = new Point2D(myVelocity.getX() * 0.5, myVelocity.getY() * 0.5);
	}

	public boolean isStartingAtPaddle1() {
		return myView.getY() > GameEngine.SCREEN_HEIGHT / 2;
	}

	/**
	 * 
	 * Access some attributes of the bouncer.
	 * 
	 */

	public double getVelocityX() {
		return myVelocity.getX();
	}

	public double getVelocityY() {
		return myVelocity.getY();
	}

	/**
	 * Returns internal view of bouncer to interact with other JavaFX methods.
	 */
	public Node getView() {
		return myView;
	}

}