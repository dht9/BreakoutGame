package game_dht9;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;

/**
 * Paddle class used in Breakout Game.
 * 
 * @author David Tran (dht9)
 */

public class Paddle extends Rectangle {

	public static final int PADDLE1_OFFSET = -75;
	public static final int PADDLE2_OFFSET = 75;

	private double PADDLE_WIDTH = 120;
	private double PADDLE_HEIGHT = 12;
	private double PADDLE_SPEED = 250;
	private int isExtended;
	private Point2D myVelocity;
	private PaddleAbility currentAbility;
	private PaddleAbility previousAbility;

	enum PaddleAbility {
		EXTENDED, STICKY, EDGEWARP, NORMAL;
	}

	/**
	 * Initialize paddle attributes.
	 */
	public Paddle(Image image, double y) {
		this.setWidth(PADDLE_WIDTH);
		this.setHeight(PADDLE_HEIGHT);
		this.setX(GameEngine.SCREEN_WIDTH / 2 - PADDLE_WIDTH / 2);
		this.setY(y);
		myVelocity = new Point2D(0, 0);
		isExtended = 0;
		currentAbility = PaddleAbility.NORMAL;
	}

	/**
	 * 
	 * Update the movement of the paddles.
	 * 
	 */
	public void startPaddle1(KeyCode code) {
		// allows paddle1 to move and stop at screen edge
		if (code == KeyCode.RIGHT && !isAtRightEdge()) {
			myVelocity = new Point2D(PADDLE_SPEED, 0);
		} else if (code == KeyCode.LEFT && !isAtLeftEdge()) {
			myVelocity = new Point2D(-PADDLE_SPEED, 0);
		}
	}

	public void startPaddle2(KeyCode code) {
		if (code == KeyCode.D && !isAtRightEdge()) {
			myVelocity = new Point2D(PADDLE_SPEED, 0);
		} else if (code == KeyCode.A && !isAtLeftEdge()) {
			myVelocity = new Point2D(-PADDLE_SPEED, 0);
		}
	}

	public void move(double elapsedTime) {
		// if paddle is at edge and is EDGEWARPPED, still enable mobility
		if (isAtEdge(GameEngine.SCREEN_WIDTH) && currentAbility == PaddleAbility.EDGEWARP) {
			this.setX(this.getX() + myVelocity.getX() * elapsedTime);
		} else {
			this.setX(this.getX() + myVelocity.getX() * elapsedTime);
		}
	}

	public void stopPaddle1(KeyCode code) {
		// if RIGHT release while paddle is going right, then stop paddle
		if (code == KeyCode.RIGHT && myVelocity.getX() == PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		} else if (code == KeyCode.LEFT && myVelocity.getX() == -PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		}
	}

	public void stopPaddle2(KeyCode code) {
		// if RIGHT release while paddle is going right, then stop paddle
		if (code == KeyCode.D && myVelocity.getX() == PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		} else if (code == KeyCode.A && myVelocity.getX() == -PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		}
	}

	public void stopPaddleAtEdge() {
		// if paddle touches left edge
		if (this.getX() <= 0 || this.getX() + this.getWidth() >= GameEngine.SCREEN_WIDTH)
			myVelocity = new Point2D(0, 0);
	}

	public void reposition(double x) {
		this.setX(x);
		myVelocity = new Point2D(0, 0);
	}
	
	public void reset() {
		this.setX(GameEngine.SCREEN_WIDTH/2 - this.getWidth()/2);
		myVelocity = new Point2D(0, 0);
	}

	/**
	 * 
	 * Initialize and control the paddles' abilities.
	 *
	 */
	public void setAbility(int num) {
		switch (num) {
		case 0:
			this.previousAbility = currentAbility;
			this.currentAbility = PaddleAbility.EXTENDED;
			break;
		case 1:
			this.previousAbility = currentAbility;
			this.currentAbility = PaddleAbility.STICKY;
			break;
		case 2:
			this.previousAbility = currentAbility;
			this.currentAbility = PaddleAbility.EDGEWARP;
			break;
		default:
			this.previousAbility = currentAbility;
			this.currentAbility = PaddleAbility.NORMAL;
			break;
		}
	}

	public void enablePaddleAbility() {
		if (this.currentAbility == PaddleAbility.EXTENDED && this.isExtended == 0) {
			this.doubleExtend();
		} else if (this.previousAbility == PaddleAbility.EXTENDED && this.isExtended == 1) {
			// shrink paddle if extended for next level
			this.doubleShrink();
		}
	}

	public boolean hasAbility(PaddleAbility type) {
		return type != null && type instanceof PaddleAbility && currentAbility == type;
	}

	public void edgeWarp() {
		if (this.getX() > GameEngine.SCREEN_WIDTH) {
			this.setX(0);
		} else if (this.getX() + this.getWidth() < 0) {
			this.setX(GameEngine.SCREEN_WIDTH - this.getWidth());
		}
	}

	public void doubleExtend() {
		if (isExtended == 1) {
			doubleShrink();
		} else {
			this.setWidth(PADDLE_WIDTH * 2);
			this.setX(this.getX() - PADDLE_WIDTH / 2);
			isExtended = 1;
		}
	}

	public void doubleShrink() {
		this.setWidth(PADDLE_WIDTH);
		this.setX(this.getX() + PADDLE_WIDTH / 2);
		isExtended = 0;
	}

	/**
	 * 
	 * Check if paddle-edge collision
	 * 
	 */

	public boolean isAtEdge(double screenWidth) {
		return (isAtRightEdge() || isAtLeftEdge());
	}

	public boolean isAtLeftEdge() {
		return this.getX() <= 0;
	}

	public boolean isAtRightEdge() {
		return this.getX() + this.getWidth() >= GameEngine.SCREEN_WIDTH;
	}

	/**
	 * 
	 * Access some attributes of the paddle.
	 * 
	 */
	
	public double getVelocityX() {
		return myVelocity.getX();
	}

	public double getVelocityY() {
		return myVelocity.getY();
	}

	public PaddleAbility getCurrentAbility() {
		return currentAbility;
	}
}
