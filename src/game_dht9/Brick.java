package game_dht9;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle {

	public static final int BRICK_WIDTH = 50;
	public static final int BRICK_HEIGHT = 25;
	public static final int BRICK_GAP = 5;
	
	private BrickType brickType;

	enum BrickType {
		LIFE(11, Color.GREEN), BARRIER(10, Color.WHITE), INFINITE(8, Color.GRAY), HIGH(3, Color.web("#FF007F")), MEDIUM(2, Color.web("#FF66B2")), LOW(1,
				Color.web("#FFCCE5")), DESTROYED(0, Color.WHITE);
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
	
	public Color getColor() {
		return brickType.getColor();
		
	}
	
	public boolean isBrickType(BrickType type) {
		return type != null && brickType == type;
	}

	public Brick(double x, double y, int brickNum , int gap) {
		// set brick location
		this.setX(x * BRICK_WIDTH + gap);
		this.setY(y * BRICK_HEIGHT);

		// set brick attributes
		this.setWidth(BRICK_WIDTH - gap);
		this.setHeight(BRICK_HEIGHT - gap);

		// set brick health
		this.setBrickType(brickNum);
//		System.out.println(this.brickType.toString());
//		 System.out.print(this.brickType.getHealth() + " ");
		// this.brickType = brickType;
	}

	public void setBrickType(int brickNum) {
		switch (brickNum) {
		case 11:
			this.brickType = BrickType.LIFE;
			break;
		case 10:
			this.brickType = BrickType.BARRIER;
			break;
		case 8:
			this.brickType = BrickType.INFINITE;
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
			this.brickType = BrickType.DESTROYED;
			this.destroyBrick();
			break;
		}
	}

	public void decrementType() {
		switch (this.brickType.health) {
		case 11:
			brickType = BrickType.DESTROYED;
			this.destroyBrick();
			break;
		case 10:
			break;
		case 8:
			break;
		case 3:
			this.brickType = BrickType.MEDIUM;
			break;
		case 2:
			this.brickType = BrickType.LOW;
			break;
		case 1:
		default:
			brickType = BrickType.DESTROYED;
//			 System.out.println("BRICK DESTROYED");
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
//		System.out.println("BRICK DESTROYED");
	}

}
