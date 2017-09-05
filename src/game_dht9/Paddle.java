package game_dht9;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class Paddle {

	ImageView myView;
	public double PADDLE_HEIGHT = 12;
	public double PADDLE_WIDTH = 120;
	public Point2D myVelocity;
	private double PADDLE_SPEED = 400;
	public int isExtended;

	/**
	 * Create a paddle from a given image.
	 */
	public Paddle(Image image, double screenWidth, double screenHeight) {
		// set paddle attributes
		myView = new ImageView(image);
		myView.setFitWidth(PADDLE_WIDTH);
		myView.setFitHeight(PADDLE_HEIGHT);
		// set paddle starting position
		myView.setX(screenWidth - PADDLE_WIDTH/2);
		myView.setY(screenHeight - 75);
		myVelocity = new Point2D(0, 0);
		isExtended = 0;
	}

	/**
	 * Give Paddle1 velocity when key pressed.
	 */
	public void startPaddle1(KeyCode code) {
		if (code == KeyCode.RIGHT) {
			myVelocity = new Point2D(PADDLE_SPEED, 0);
			System.out.println(code);
		} else if (code == KeyCode.LEFT) {
			myVelocity = new Point2D(-PADDLE_SPEED, 0);
			System.out.println(code);
		}
	}

	/**
	 * Give Paddle2 velocity when key pressed.
	 */
	public void startPaddle2(KeyCode code) {
		if (code == KeyCode.D) {
			myVelocity = new Point2D(PADDLE_SPEED, 0);
			System.out.println(code);
		} else if (code == KeyCode.A) {
			myVelocity = new Point2D(-PADDLE_SPEED, 0);
			System.out.println(code);
		}
	}

	/**
	 * Maintain paddle velocity in frames.
	 */
	public void move(double elapsedTime) {
		myView.setX(myView.getX() + myVelocity.getX() * elapsedTime);
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
	 * Next methods are paddle ABILITIES
	 */
	public void doubleExtend() {
		myView.setFitWidth(2*PADDLE_WIDTH);
		myView.setX(myView.getX()-PADDLE_WIDTH/2);
		isExtended = 1;
	}
	public void doubleShrink() {
		myView.setFitWidth(PADDLE_WIDTH);
		myView.setX(myView.getX()+PADDLE_WIDTH/2);
		isExtended = 0;
	}

	
	public void wrapPaddle() {
		
	}
	
	
	public void recenter(double x) {
		myView.setX(x);
		myVelocity = new Point2D(0, 0);
	}
	
	public double getWidth() {
		return PADDLE_WIDTH;
		
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
