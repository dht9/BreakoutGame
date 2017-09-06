package game_dht9;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Bouncer used in Breakout Game
 * 
 * This class inspired by: 
 * https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/Bouncer.java
 * 
 * @author David Tran
 */
public class Bouncer {

	public ImageView myView;
	
	private Point2D myVelocity;
	private double BALL_SIZE = 20; // 16 for symmetry
	private double MAX_BOUNCE_ANGLE = 60;
	private double BOUNCER_SPEED = 350;
	private boolean restarted;

	/**
	 * Create a bouncer from a given image.
	 */
	public Bouncer(Image image, double screenWidth, double screenHeight) {
		myView = new ImageView(image);
		// make sure it stays a circle
		myView.setFitWidth(BALL_SIZE);
		myView.setFitHeight(BALL_SIZE);
		// make sure it stays within the bounds
		myView.setX(-1);
		myView.setY(-1);
		// turn speed into velocity that can be updated on bounces
		myVelocity = new Point2D(0, 0);
		restarted = true;
	}
	
	public double getVelocityX() {
		return myVelocity.getX();
	}
	public double getVelocityY() {
		return myVelocity.getY();
	}
	public  boolean hasRestarted() {
		return restarted;
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
	public void bounce() {
		// collide all bouncers against the walls
		if (myView.getX() < 0 || myView.getX() > GameEngine.SCREEN_WIDTH - myView.getBoundsInLocal().getWidth()) {
			myVelocity = new Point2D(-myVelocity.getX(), myVelocity.getY());
		}
	}
	
	/**
	 * Check if ball is out-of-bounds
	 */
	public boolean outOfBounds() {
		restarted = myView.getY() > GameEngine.SCREEN_HEIGHT || myView.getY() + myView.getFitHeight() < 0 || 
				myView.getX() + myView.getFitWidth()/2 > GameEngine.SCREEN_WIDTH || myView.getX() + myView.getFitWidth()/2 < 0;
		return restarted;
	}

	/**
	 * Bounce off the paddle.
	 */
	public void bounceOffPaddle(Paddle myPaddle, double screenHeight) {
		// check if ball hits top or bottom of paddle
		if (myView.getY() + myView.getFitHeight() <= myPaddle.myView.getY() + myPaddle.myView.getFitHeight() / 2
				|| myView.getY() >= myPaddle.myView.getY() + myPaddle.myView.getFitHeight() / 2) {
			// calculate distance between ball and paddle center
			double distFromCenter = myView.getX() + myView.getFitWidth() / 2
					- (myPaddle.myView.getX() + myPaddle.myView.getFitWidth() / 2);
			if(distFromCenter == 0 && isInBottomHalf()) {
				myVelocity = new Point2D(0, -BOUNCER_SPEED);
				return;
			}
			else if(distFromCenter == 0 && !isInBottomHalf()) {
				myVelocity = new Point2D(0, BOUNCER_SPEED);
				return;
			}
			System.out.println(distFromCenter);
			// normalize the distance [-1,1]
			double normalizedDistFromCenter = distFromCenter / (myPaddle.myView.getFitWidth() / 2);
			// calculate angle ball will bounce
			double bounceAngle = normalizedDistFromCenter * MAX_BOUNCE_ANGLE;
			double ballVx = BOUNCER_SPEED * Math.sin(Math.toRadians(bounceAngle));
			double ballVy = BOUNCER_SPEED * Math.cos(Math.toRadians(bounceAngle));
			myVelocity = new Point2D(ballVx, ballVy*(-getSign(myVelocity.getY())));
		}
		
		// check if ball hits left side of paddle
		else if (myView.getX() + myView.getFitWidth() * 3 / 4 <= myPaddle.myView.getX()) {
			myVelocity = new Point2D((-BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(45),
					BOUNCER_SPEED * Math.cos(45));
			System.out.println((-BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(45));
		}
		
		// check if ball hits right side of paddle
		else if (myView.getX() + myView.getFitWidth() / 4 >= myPaddle.myView.getX() + myPaddle.myView.getFitWidth()) {
			myVelocity = new Point2D((BOUNCER_SPEED + myPaddle.getVelocityX()) * Math.sin(45),
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
	}

	
	/**
	 * Recentering - release ball after SPACEBAR entered
	 * @return
	 */
	public void reposition(double x, double y) {
		myView.setX(x);
		myView.setY(y);
		myVelocity = new Point2D(0,0);
//		System.out.println(myView.getX() + " , " + myView.getY());
	}
	public void releaseBall(Paddle myPaddle) {
		// if SPACEBAR entered, release ball at certain angle
		double distFromCenter = myView.getX() + myView.getFitWidth() / 2
				- (myPaddle.myView.getX() + myPaddle.myView.getFitWidth() / 2);
		if (distFromCenter == 0) {
			if(isInBottomHalf())
				myVelocity = new Point2D(0,-BOUNCER_SPEED);
			else
				myVelocity = new Point2D(0,BOUNCER_SPEED);
		}
		else {
			double normalizedDistFromCenter = distFromCenter / (myPaddle.myView.getFitWidth() / 2);
			// calculate angle ball will bounce
			double bounceAngle = normalizedDistFromCenter * MAX_BOUNCE_ANGLE;
			double ballVx = BOUNCER_SPEED * Math.sin(Math.toRadians(bounceAngle));
			double ballVy = BOUNCER_SPEED * Math.cos(Math.toRadians(bounceAngle));
			
			if(isInBottomHalf())
				myVelocity = new Point2D(ballVx, -ballVy);
			else
				myVelocity = new Point2D(ballVx, ballVy);
		
		}
		System.out.println("release");
		restarted = false;
	}
	
	/**
	 * Miscellaneous methods
	 */
	private int getSign(double num) {
		if (num > 0)
			return 1;
		else if (num == 0)
			return 0;
		else
			return -1;
	}
	public void stop() {
		myVelocity = new Point2D(0,0);
	}
	public void restartBall() {
		restarted = true;
	}
	public boolean isInBottomHalf() { 
		return myView.getY() > GameEngine.SCREEN_HEIGHT/2;
	}

	/**
	 * Returns internal view of bouncer to interact with other JavaFX methods.
	 */
	public Node getView() {
		return myView;
	}
}