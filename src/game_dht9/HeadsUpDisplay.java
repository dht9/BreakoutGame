package game_dht9;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * HeadsUpDisplay (HUD) class used in Breakout Game.
 * 
 * Purpose: to define a status display to be implemented in the Breakout
 * gameplay screen.
 * 
 * Design Justification for Sprint 3 Analysis:
 * 
 * This class is what I regard as a well designed class due to the relationship
 * between its purpose and size and how it is implemented. As discussed in
 * Analysis.md, this class was created from the refactoring of code from
 * GameEngine.java which defined as well as implemented the status display for
 * the game, all in one class. Learning from refactoring methods discussed in
 * class, such as with lab_hangman and example_bins, and reading the design
 * checklist that each class should have a small, well-defined purpose
 * (modularity), I made the design change that the status display should be
 * defined in its own class (HeadsUpDisplay.java). Since this class extends
 * BorderPane, I learned that I could create a HeadsUpDisplay object and call methods
 * on that instance in order to create HBox's and labels to be aligned on that
 * instance.
 * 
 * This design is flexible in one way because the constructor uses public
 * constants to set the size of the HeadsUpDisplay. Thus, if the screen width and
 * height or the brick height changes in GameEngine.java/Brick.java, the new
 * size of the HeadsUpDisplay would would automatically be adapted.
 * 
 * This design is flexible in another way given by the parameters of
 * createHUDLabel. Initially, I had two almost identical methods, each handling
 * the case where 'Object value' was either an IntegerProperty or
 * StringProperty. At first, I did not think I could combine the two methods
 * because 'value' was of a different type for each method. However, after
 * seeing the equals method in Disk.java from lab_bins
 * (https://coursework.cs.duke.edu/CompSci308_2017Spring/lab_bins/blob/master/src/Disk.java),
 * I noticed that I could use an 'Object value' parament and use an 'if'
 * statement to check for what the object is an instance of and make
 * corresponding changes. This effectively reduced two nearly identical methods
 * into one.
 * 
 * Shown from the previous points made, I think this class encapsulates a
 * substantial portion of what I have learned so far in CS 308 regarding project
 * design.
 * 
 * @author David Tran (dht9)
 */

public class HeadsUpDisplay extends BorderPane {

	public HeadsUpDisplay() {
		this.setPrefSize(GameEngine.SCREEN_WIDTH, GameEngine.SCREEN_HEIGHT - Brick.BRICK_HEIGHT);
	}

	/**
	 * Initialize game status display with HBox and Label objects.
	 * 
	 * @param description
	 *            non-dynamic label, describes the parameter 'value'
	 * @param value
	 *            dynamic number or string displayed
	 * @param position
	 *            location of the HBox on the HeadsUpDisplay
	 */
	public void createHUDLabel(String description, Object value, String position) {
		HBox hbox = new HBox();
		Label label = new Label(description);
		Label val = new Label();

		if (value != null && value instanceof IntegerProperty) {
			val.textProperty().bind(((IntegerProperty) value).asString());
		} else if (value != null && value instanceof StringProperty) {
			val.textProperty().bind(((StringProperty) value));
		}

		label.setTextFill(Color.WHITE);
		val.setTextFill(Color.WHITE);
		hbox.getChildren().addAll(label, val);

		if (position.equalsIgnoreCase("left"))
			this.setLeft(hbox);
		else if (position.equalsIgnoreCase("right"))
			this.setRight(hbox);
		else if (position.equalsIgnoreCase("top"))
			this.setTop(hbox);
		else if (position.equalsIgnoreCase("bottom"))
			this.setBottom(hbox);
	}

}
