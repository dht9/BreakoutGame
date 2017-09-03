package game_dht9;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;

public class BrickLevel {
	private Runnable nextSceneHandler;
	
	public void setNextSceneHandler(Runnable handler) {
		nextSceneHandler = handler;
	}
	
	public Scene init(Group root, double width, double height, Paint background) {
		Scene scene = new Scene(root, width, height, background);
		
		scene.setOnKeyPressed(e -> {
			if (nextSceneHandler != null) {
				if (e.getCode() == KeyCode.DIGIT1) {
					nextSceneHandler.run();
				}
			}
		});
		
		return scene;
	}
	
	
	
}
