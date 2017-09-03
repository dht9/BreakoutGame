package game_dht9;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle{
	
	public int BRICK_WIDTH = 50;
	public int BRICK_HEIGHT = 25;
	public int myHealth;
	
	
	public Brick(int x, int y, int health) {
		// set brick location
		this.setX(x * BRICK_WIDTH + 5);
		this.setY(y * BRICK_HEIGHT);
		
		// set brick attributes
		this.setWidth(BRICK_WIDTH-5);
		this.setHeight(BRICK_HEIGHT-5);
		
		// set brick health
		myHealth = health;
	}
	
	public void changeBrickState() {
		
	}
	
	public void destroyBrick() {
		this.setX(-1);
		this.setY(-1);
		this.setWidth(0);
		this.setHeight(0);
	}

}
