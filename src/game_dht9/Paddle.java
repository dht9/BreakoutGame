package game_dht9;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class Paddle {
	

	public static final int PADDLE1_OFFSET = -75;
	public static final int PADDLE2_OFFSET = 75;
	public ImageView myView;
	
	private double PADDLE_HEIGHT = 12;
	private double PADDLE_WIDTH = 120;
	private Point2D myVelocity;
	private double PADDLE_SPEED = 400;
	private int isExtended;
	private Type currentPaddleType;
	private Type previousPaddleType;
	
	enum Type {
		EXTENDED, MAGNETIC, EDGEWARP, PLAIN;
	}
	
	/**
	 * Create a paddle from a given image.
	 */
	public Paddle(Image image, double y) {
		// set paddle attributes
		myView = new ImageView(image);
		myView.setFitWidth(PADDLE_WIDTH);
		myView.setFitHeight(PADDLE_HEIGHT);
		// set paddle starting position
		myView.setX(GameEngine.SCREEN_WIDTH/2 - PADDLE_WIDTH/2);
		myView.setY(y);
		myVelocity = new Point2D(0, 0);
		isExtended = 0;
		currentPaddleType = Type.PLAIN;
	}
	
	public void chooseAbility(int num) {
		switch(num) {
		case 0:
			this.previousPaddleType = currentPaddleType;
			this.currentPaddleType = Type.EXTENDED;
			break;
		case 1:
			this.previousPaddleType = currentPaddleType;
			this.currentPaddleType = Type.MAGNETIC;
			break;
		case 2:
			this.previousPaddleType = currentPaddleType;
			this.currentPaddleType = Type.EDGEWARP;
			break;
		default:
			this.previousPaddleType = currentPaddleType;
			this.currentPaddleType = Type.PLAIN;
			break;
		}
	}
	

	/**
	 * Give Paddle1 velocity when key pressed.
	 */
	public void startPaddle1(KeyCode code) {
		// allows paddle1 to move and stop at screen edge
		if (code == KeyCode.RIGHT && !isAtRightEdge()) {
			myVelocity = new Point2D(PADDLE_SPEED, 0);
		} else if (code == KeyCode.LEFT && !isAtLeftEdge()) {
			myVelocity = new Point2D(-PADDLE_SPEED, 0);
		}
	}

	/**
	 * Give Paddle2 velocity when key pressed.
	 */
	public void startPaddle2(KeyCode code) {
		if (code == KeyCode.D && !isAtRightEdge()) {
			myVelocity = new Point2D(PADDLE_SPEED, 0);
		} else if (code == KeyCode.A && !isAtLeftEdge()) {
			myVelocity = new Point2D(-PADDLE_SPEED, 0);
		}
	}
	
	/**
	 * Maintain paddle velocity in frames.
	 */
	public void move(double elapsedTime) {
		// if paddle is at edge and is EDGEWARPPED, still enable mobility
		if(isAtEdge(GameEngine.SCREEN_WIDTH) && currentPaddleType == Type.EDGEWARP) {
			myView.setX(myView.getX() + myVelocity.getX() * elapsedTime);
		}
		else {
			myView.setX(myView.getX() + myVelocity.getX() * elapsedTime);
		}
	}

	/**
	 * Stop Paddle1 velocity when key released.
	 */
	public void stopPaddle1(KeyCode code) {
		// if RIGHT release while paddle is going right, then stop paddle
		if (code == KeyCode.RIGHT && myVelocity.getX() == PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		} else if (code == KeyCode.LEFT && myVelocity.getX() == -PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		}
	}

	/**
	 * Stop Paddle2 velocity when key released.
	 */
	public void stopPaddle2(KeyCode code) {
		// if RIGHT release while paddle is going right, then stop paddle
		if (code == KeyCode.D && myVelocity.getX() == PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		} else if (code == KeyCode.A && myVelocity.getX() == -PADDLE_SPEED) {
			myVelocity = new Point2D(0, 0);
		}
	}
	
	/**
	 * Stop Paddle 1 if it hits the screen edge
	 */
	public void stopPaddleAtEdge() {
		// if paddle touches left edge
		if(myView.getX() <= 0 || myView.getX()+myView.getFitWidth() >= GameEngine.SCREEN_WIDTH)
			myVelocity = new Point2D(0,0);
	}
	
	/**
	 * Next methods are paddle ABILITIES
	 */
	public void enablePaddleAbility() {
		if (this.currentPaddleType == Type.EXTENDED && this.isExtended == 0
				) {
			this.doubleExtend();
		}
		else if (this.previousPaddleType == Type.EXTENDED && this.isExtended == 1) {
			// shrink paddle if extended for next level
			this.doubleShrink();
		}
	}
	public void doubleExtend() {
		if (isExtended == 1) {
			doubleShrink();
		}
		else {
			myView.setFitWidth(PADDLE_WIDTH*2);
			myView.setX(myView.getX()-PADDLE_WIDTH/2);
			isExtended = 1;
		}
	}
	public void doubleShrink() {
		myView.setFitWidth(PADDLE_WIDTH);
		myView.setX(myView.getX()+PADDLE_WIDTH/2);
		isExtended = 0;
	}
	public void edgeWarp() {
		if(myView.getX() > GameEngine.SCREEN_WIDTH) {
			myView.setX(0);
		}
		else if(myView.getX() + myView.getFitWidth()	< 0) {
			myView.setX(GameEngine.SCREEN_WIDTH - myView.getFitWidth());
		}
	}

	
	
	public void reposition(double x) {
		myView.setX(x);
		myVelocity = new Point2D(0, 0);
	}
	public boolean isAtEdge(double screenWidth) {
		// check for right screen edge 
		return (isAtRightEdge() || isAtLeftEdge());
	}
	public boolean isAtLeftEdge() {
		// check for left screen edge 
		return myView.getX() <= 0;
	}
	public boolean isAtRightEdge() {
		// check for right screen edge 
		return myView.getX() + myView.getFitWidth() >= GameEngine.SCREEN_WIDTH;
	}
	public double getWidth() {
		return this.myView.getFitWidth();	
	}
	public double getHeight() {
		return this.myView.getFitHeight();	
	}
	public double getVelocityX() {
		return myVelocity.getX();
	}
	public double getVelocityY() {
		return myVelocity.getY();
	}
	public Type getCurrType() {
		return currentPaddleType;
	}
	public boolean isType(Type type) {
		return type != null && type instanceof Type && currentPaddleType == type;
	} 
	
	/**
	 * Returns internal view of bouncer to interact with other JavaFX methods.
	 */
	public Node getView() {
		return myView;
	}
	
	
	
	
//	public void checkExtensionPaddle1(KeyCode code) {
//	// check if 'x' is enter for paddle extension toggle
//	if (code == KeyCode.X) {
//		if(isExtended == 0)
//			doubleExtend();
//		else
//			doubleShrink();
//	}
//}
}
