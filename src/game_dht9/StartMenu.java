package game_dht9;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * StartMenu class used in Breakout Game to add text in the starting screen.
 * 
 * @author David Tran (dht9)
 */

public class StartMenu extends VBox {

	public static double LENGTH_AVOID_START_BUTTON = 0.95;

	public StartMenu(Pane start) {
		setPrefSize(GameEngine.SCREEN_WIDTH, GameEngine.SCREEN_HEIGHT * LENGTH_AVOID_START_BUTTON);
		setAlignment(Pos.CENTER);
	}

	public void addLabel(String str, Color color, int fontSize) {
		Label label = new Label(str);
		label.setTextFill(color);
		label.setFont(new Font("Fleftex", fontSize));
		this.getChildren().add(label);
	};
}
