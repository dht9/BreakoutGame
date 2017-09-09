package game_dht9;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Bouncer class used in Breakout Game.
 * 
 * This class was inspired by:
 * https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/Bouncer.java
 * 
 * @author David Tran (dht9)
 */
public class Bouncer {

	public static final int BOUNCER_HOVER = -1;
	public static final int TOPLEFTBRICK_COLLISION_BUFFER = 5;
	public static final double BOTRIGHTBRICK_COLLISION_BUFFER = 4 / 5;
	public static final double LEFTPADDLE_COLLISION_BUFFER = 3 / 4;
	public static final int RIGHTPADDLE_COLLISION_BUFFER = 4;
	public static final int OUTOFBOUNDS = -1;
	public static final int SIDE_BOUNCE = 45;

	private ImageView myView;
	private Point2D myVelocity;
	private static final double BOUNCER_SIZE = 18;
	private static final double MAX_BOUNCE_ANGLE = 60;
	private static final double BOUNCER_SPEED = 275;
	private boolean hasReset;
	private static final double SIZE_SCALAR = 1.75;
	private static final double SPEED_SCALAR = 0.75;

	/**
	 * Initialize bouncer attributes.
	 */
	public Bouncer(Image image, double screenWidth, double screenHeight) {
		myView = new ImageView(image);
		myView.setFitWidth(BOUNCER_SIZE);
		myView.setFitHeight(BOUNCER_SIZE);
		myView.setX(OUTOFBOUNDS);
		myView.setY(OUTOFBOUNDS);
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

	public void bounceOffWalls() {
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
		else if (myView.getX() + myView.getFitWidth() * LEFTPADDLE_COLLISION_BUFFER <= myPaddle.getX()) {
			myVelocity = new Point2D((-BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(SIDE_BOUNCE),
					BOUNCER_SPEED * Math.cos(SIDE_BOUNCE));
			System.out.println((-BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(SIDE_BOUNCE));
		}
		// check if ball hits right side of paddle
		else if (myView.getX() + myView.getFitWidth() / RIGHTPADDLE_COLLISION_BUFFER >= myPaddle.getX()
				+ myPaddle.getWidth()) {
			myVelocity = new Point2D((BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(SIDE_BOUNCE),
					BOUNCER_SPEED * Math.cos(SIDE_BOUNCE));
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

	// NOTE: There are two magic numbers in this method because the bouncer movement
	// did not function correctly with the constant BOTRIGHTBRICK_COLLISION_BUFFER =
	// 4/5 (see line 20). This may be due to int or double casting.
	public void bounceOffBrick(Brick myBrick) {
		// ball hits top side of brick
		if (myView.getY() + myView.getFitHeight() <= myBrick.getY()
				+ myBrick.getHeight() / TOPLEFTBRICK_COLLISION_BUFFER) {
			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
		}
		// ball hits bottom side of brick

		else if (myView.getY() >= myBrick.getY() + myBrick.getHeight() * 4 / 5) {
			myVelocity = new Point2D(myVelocity.getX(), -myVelocity.getY());
		}
		// ball hits left side of brick
		else if (myView.getX() + myView.getFitWidth() <= myBrick.getX()
				+ myBrick.getWidth() / TOPLEFTBRICK_COLLISION_BUFFER) {
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
		myVelocity = new Point2D(0, 0);
	}

	public void reset(Paddle myPaddle) {
		// set the ball to be center with myPaddle1
		myView.setX(myPaddle.getX() + myPaddle.getWidth() / 2 - myView.getFitWidth() / 2);
		myView.setY(GameEngine.SCREEN_HEIGHT - myView.getFitHeight() + Paddle.PADDLE1_OFFSET + BOUNCER_HOVER);
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
		myView.setFitWidth(SIZE_SCALAR * BOUNCER_SIZE);
		myView.setFitHeight(SIZE_SCALAR * BOUNCER_SIZE);

	}

	public void slowSpeed() {
		myVelocity = new Point2D(myVelocity.getX() * SPEED_SCALAR, myVelocity.getY() * SPEED_SCALAR);
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

	public double getFitWidth() {
		return myView.getFitWidth();
	}

	public double getFitHeight() {
		return myView.getFitHeight();
	}

	public double getX() {
		return myView.getX();
	}

	public double getY() {
		return myView.getY();
	}

	/**
	 * Returns internal view of bouncer to interact with other JavaFX methods.
	 */
	public Node getView() {
		return myView;
	}

}