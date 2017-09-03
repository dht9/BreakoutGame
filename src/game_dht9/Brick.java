package game_dht9;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle{
	
	public int BRICK_WIDTH = 50;
	public int BRICK_HEIGHT = 25;
	Type brickType;
	
	enum Type {
		HIGH (3, Color.web("#FF007F")),
		MEDIUM (2, Color.web("#FF66B2")),
		LOW (1, Color.web("#FFCCE5")),
		DESTROYED (0, Color.WHITE);
		private int health;
		private Color color;
		
		Type(int health, Color color) {
			this.health = health;
			this.color = color;
//			this.setFill(color);
		}
		
        public Color getColor(){    return color;   }
        public int getHealth(){   return health;    }
	}
	
	
	public Brick(int x, int y, int brickNum) {
		// set brick location
		this.setX(x * BRICK_WIDTH + 5);
		this.setY(y * BRICK_HEIGHT);
		
		// set brick attributes
		this.setWidth(BRICK_WIDTH-5);
		this.setHeight(BRICK_HEIGHT-5);
		
		// set brick health
		this.setBrickType(brickNum);
		System.out.print(this.brickType.getHealth() + " ");
//		this.brickType = brickType;
	}
	
	public void setBrickType(int brickNum) {
		switch(brickNum) {
			case 3:
				this.brickType = Type.HIGH;
				break;
			case 2:
				this.brickType = Type.MEDIUM;
				break;
			case 1:
				this.brickType = Type.LOW;
				break;
			default:
				this.brickType = Type.DESTROYED;
				this.destroyBrick();
				break;
		}
	}
	
	public void decrementType(){
        switch(this.brickType.health){
            case 3:
                this.brickType = Type.MEDIUM;
                System.out.print("med");
                break;
            case 2: 
                this.brickType = Type.LOW;
                break;
            case 1:
            default:
//                brickType = Type.DESTROYED;
//                System.out.println("BRICK DESTROYED");
                this.destroyBrick();
                break;
        }
    }
	
	public void destroyBrick() {
		this.setX(-1);
		this.setY(-1);
		this.setWidth(0);
		this.setHeight(0);
		System.out.println("BRICK DESTROYED");
	}

}
