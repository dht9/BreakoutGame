package game_dht9;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StartMenu extends VBox {
	
	public StartMenu(Pane start) {
		setPrefSize(GameEngine.SCREEN_WIDTH, GameEngine.SCREEN_HEIGHT*.95);
		setAlignment(Pos.CENTER);
	}
	
	
	public void addLabel(String str, Color color, int size) {
		Label label = new Label(str);
		label.setTextFill(color);
		label.setFont(new Font("Fleftex", size));
		this.getChildren().add(label);
	};
}
