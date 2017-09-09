package game_dht9;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Brick class used in Breakout Game.
 * 
 * @author David Tran (dht9)
 */

public class Brick extends Rectangle {

	public static final double BRICK_WIDTH = 47.5;
	public static final double BRICK_HEIGHT = 25;
	public static final int BRICK_GAP = 5;
	
	private BrickType brickType;
	
	enum BrickType {
		BARRIER(10, Color.WHITE), CREATE_BARRIER(9, Color.BLUE), INFINITE(8, Color.GRAY), LIFE(7,
				Color.web("#32cd32")), EXPAND_BOUNCER(6, Color.YELLOW), SLOW_BOUNCER(5, Color.RED), HIGH(3,
						Color.web("#FF007F")), MEDIUM(2,
								Color.web("#FF66B2")), LOW(1, Color.web("#FFCCE5")), DESTROYED(0, Color.WHITE);
		private int health;
		private Color color;

		BrickType(int health, Color color) {
			this.health = health;
			this.color = color;
		}

		public Color getColor() {
			return color;
		}

		public int getHealth() {
			return health;
		}

	}
	
	/**
	 * 
	 * Initialize and control Brick attributes.
	 *
	 */
	public Brick(double x, double y, int brickNum, int gap) {
		this.setX(x * BRICK_WIDTH + gap);
		this.setY(y * BRICK_HEIGHT);
		this.setWidth(BRICK_WIDTH - gap);
		this.setHeight(BRICK_HEIGHT - gap);
		this.setBrickType(brickNum);
	}

	public void setBrickType(int brickNum) {
		switch (brickNum) {
		case 10:
			this.brickType = BrickType.BARRIER;
			break;
		case 9:
			this.brickType = BrickType.CREATE_BARRIER;
			break;
		case 8:
			this.brickType = BrickType.INFINITE;
			break;
		case 7:
			this.brickType = BrickType.LIFE;
			break;
		case 6:
			this.brickType = BrickType.EXPAND_BOUNCER;
			break;
		case 5:
			this.brickType = BrickType.SLOW_BOUNCER;
			break;
		case 3:
			this.brickType = BrickType.HIGH;
			break;
		case 2:
			this.brickType = BrickType.MEDIUM;
			break;
		case 1:
			this.brickType = BrickType.LOW;
			break;
		default:
			this.destroyBrick();
			break;
		}
	}

	public void decrementType() {
		switch (this.brickType.health) {
		case 10:
		case 8:
			break;
		case 9:
		case 7:
		case 6:
		case 5:
			this.destroyBrick();
			break;
		case 3:
			this.brickType = BrickType.MEDIUM;
			break;
		case 2:
			this.brickType = BrickType.LOW;
			break;
		case 1:
		default:
			this.destroyBrick();
			break;
		}
	}

	public void destroyBrick() {
		this.setX(-1);
		this.setY(-1);
		this.setWidth(0);
		this.setHeight(0);
		brickType = BrickType.DESTROYED;
	}

	public boolean isBrickType(BrickType type) {
		return type != null && brickType == type;
	}

	public Color getColor() {
		return brickType.getColor();

	}
}
