package game_dht9;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartMenu extends VBox {
	
	public StartMenu(Pane start) {
		setPrefSize(GameEngine.SCREEN_WIDTH, GameEngine.SCREEN_HEIGHT*.75);
		setAlignment(Pos.CENTER);
	}
	
	
	public void addLabel(String str, double x, double y, Color color, int size) {
		Label label = new Label(str);
		label.setLayoutX(x);
//		label.setLayoutY(y);
//		label.setAlignment(Pos.BOTTOM_LEFT);
		label.setTextFill(color);
		label.setFont(new Font("Fleftex", size));
		this.getChildren().add(label);
	};
}
