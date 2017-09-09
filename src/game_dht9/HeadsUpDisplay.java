package game_dht9;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class HeadsUpDisplay extends BorderPane {
	
	public HeadsUpDisplay() {
		this.setPrefSize(GameEngine.SCREEN_WIDTH, GameEngine.SCREEN_HEIGHT - Brick.BRICK_HEIGHT);
	}
	
	public void createHUDLabel(String description, Object value, String position) {
		HBox hbox = new HBox();
		Label label = new Label(description);
		label.setTextFill(Color.WHITE);
		Label val = new Label();
		if (value != null && value instanceof IntegerProperty) {
			val.textProperty().bind(((IntegerProperty) value).asString());
			hbox.getChildren().addAll(label, val);
		} else if (value != null && value instanceof StringProperty) {
			val.textProperty().bind(((StringProperty) value));
			hbox.getChildren().addAll(label, val);
		}
		val.setTextFill(Color.WHITE);
		
		// set position of hbox on borderpane
		if(position.equalsIgnoreCase("left"))
			this.setLeft(hbox);
		else if(position.equalsIgnoreCase("right"))
			this.setRight(hbox);
		else if(position.equalsIgnoreCase("top"))
			this.setTop(hbox);
		else if(position.equalsIgnoreCase("bottom"))
			this.setBottom(hbox);
	}

}
