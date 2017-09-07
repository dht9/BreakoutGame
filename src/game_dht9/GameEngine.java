package game_dht9;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import game_dht9.Brick.BrickType;
import game_dht9.Paddle.Type;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Breakout Game
 * 
 * @author David Tran
 */
public class GameEngine extends Application {
	public static final String TITLE = "JavaFX: Breakout Game";
	public static final String BALL_IMAGE = "ball.gif";
	public static final String PADDLE_IMAGE = "paddle.gif";
	public static final int SCREEN_WIDTH = 1004;
	public static final int SCREEN_HEIGHT = 700;
	public static final Paint BACKGROUND = Color.BLACK;
	public static final int FRAMES_PER_SECOND = 120;
	public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
	// public static final int KEY_INPUT_SPEED = 10;
	// public static final double GROWTH_RATE = 1.1;

	private Scene myScene;
	private Bouncer myBouncer;
	private Team team;
	private Paddle myPaddle1;
	private Paddle myPaddle2;
	private Brick myBrick;
	private List<Brick> myBricks = new ArrayList<>();
	private List<Integer> abilitySequence = new ArrayList<>();
	private Group root;
	private int currentLevel = 1;
	private int numLevels = 4;

	private IntegerProperty teamLives = new SimpleIntegerProperty(0);
	private IntegerProperty level = new SimpleIntegerProperty(0);
	private StringProperty paddleAbility = new SimpleStringProperty();

	/**
	 * Initialize what will be displayed and how it will be updated.
	 * 
	 * start() createLevelScene(), and step() methods inspired by Robert C. Duvall at
	 * https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
	 */

	@Override
	public void start(Stage primaryStage) throws Exception {
		// create one top level collection to organize the things in the scene
		
		root = new Group();
		
		Scene level1 = createLevelScene(root, SCREEN_WIDTH, SCREEN_HEIGHT, BACKGROUND, currentLevel);
		Scene startMenu = createStartMenu(primaryStage, level1);

//		startMenu.getStylesheets().add(getClass().getResource("/fonts/gameFont.css").toExternalForm());

		// attach scene to the stage and display it
		primaryStage.setScene(startMenu);
		primaryStage.setTitle(TITLE);
		primaryStage.show();
	}

	public Scene createStartMenu(Stage primaryStage, Scene firstLevel) {
		Pane start = new Pane();
		start.setStyle("-fx-background-color: darkslateblue;-fx-padding: 10px;");
		HBox hbox = new HBox();
		hbox.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		Button startGameBtn = new Button("START GAME");
		startGameBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				primaryStage.setScene(firstLevel);
				// attach "game loop" to timeline to play it
				KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
				Timeline animation = new Timeline();
				animation.setCycleCount(Timeline.INDEFINITE);
				animation.getKeyFrames().add(frame);
				animation.play();

			}
		});
		startGameBtn.setLayoutX(SCREEN_WIDTH/2);
		hbox.getChildren().add(startGameBtn);
		start.getChildren().add(hbox);
		start.getStylesheets().add(getClass().getResource("/gameFont.css").toExternalForm());
		return new Scene(start);
	}

	private void createPaddleAbilitySequence() {
		// set random order of paddle abilities for each level
		for (int i = 0; i < 3; i++) {
			abilitySequence.add(i);
		}
		Collections.shuffle(abilitySequence);
	}

	private HBox createLabel(String description, Object value) {
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
		return hbox;
	}

	// Create the game's "scene": what shapes will be in the game and their starting
	// properties
	private Scene createLevelScene(Group root, int width, int height, Paint background, int levelNum) {

		// create objects and set their properties
		Image ballImage = new Image(getClass().getClassLoader().getResourceAsStream(BALL_IMAGE));
		Image paddleImage = new Image(getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE));

		myBouncer = new Bouncer(ballImage, width, height);
		myPaddle1 = new Paddle(paddleImage, SCREEN_HEIGHT + Paddle.PADDLE1_OFFSET);
		myPaddle2 = new Paddle(paddleImage, Paddle.PADDLE2_OFFSET);
		createPaddleAbilitySequence();
		team = new Team();

		// load bricks into scene
		loadBricks(root, levelNum);

		// create labels for the interface
		BorderPane border = new BorderPane();
		border.setPrefHeight(SCREEN_HEIGHT - Brick.BRICK_HEIGHT);
		border.setPrefWidth(SCREEN_WIDTH);
		HBox hboxLevel = createLabel("Level: ", level);
		border.setLeft(hboxLevel);
		HBox hboxLives = createLabel("Team Lives Remaining: ", teamLives);
		border.setRight(hboxLives);
		HBox hboxPaddle = createLabel("Paddle Ability: ", paddleAbility);
		border.setBottom(hboxPaddle);

		root.getChildren().add(myBouncer.getView());
		root.getChildren().add(myPaddle1.getView());
		root.getChildren().add(myPaddle2.getView());
		root.getChildren().addAll(border);
		root.getStylesheets().add(getClass().getResource("/gameFont.css").toExternalForm());
		updateHUD();

		// create a place to see the shapes
		myScene = new Scene(root, width, height, background);
		myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		myScene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));

		return myScene;
	}

	/**
	 * Update attributes
	 */
	private void step(double elapsedTime) {

		// if ball is not moving, reposition it in the center of paddle1
		if (myBouncer.getVelocityY() == 0 && myBouncer.hasRestarted()) {
			myBouncer.reposition(
					myPaddle1.myView.getX() + myPaddle1.getWidth() / 2 - myBouncer.myView.getFitWidth() / 2,
					SCREEN_HEIGHT - myBouncer.myView.getFitHeight() + Paddle.PADDLE1_OFFSET - 1);
		}

		// if ball is magnetized by paddle, stick it to paddle
		else if (myBouncer.getVelocityY() == 0 && !myBouncer.hasRestarted()) {
			if (myBouncer.isInBottomHalf())
				myBouncer.reposition(myBouncer.myView.getX() + myPaddle1.getVelocityX() * elapsedTime,
						myBouncer.myView.getY());
			else
				myBouncer.reposition(myBouncer.myView.getX() + myPaddle2.getVelocityX() * elapsedTime,
						myBouncer.myView.getY());
		}

		// if ball is not magnetized or has not recentered, move the ball
		else {
			myBouncer.move(elapsedTime);
			myBouncer.bounce();

			if (team.livesEqualTo(0)) {
				loadBricks(root, 1);
				System.out.println("YOU LOSE");
				team.resetLives();
				updateCurrentLivesDisplayed();
			}

			checkOutOfBounds();
			checkBallPaddleCollision();
			checkBallBrickCollision();
		}

		// update paddle position/speed
		myPaddle1.move(elapsedTime);
		myPaddle2.move(elapsedTime);

		// Edge Warp Paddles if paddle has edge-warp ability
		if (myPaddle1.isType(Type.EDGEWARP)) {
			myPaddle1.edgeWarp();
			myPaddle2.edgeWarp();
		} else {
			myPaddle1.stopPaddleAtEdge();
			myPaddle2.stopPaddleAtEdge();
		}
	}

	private void checkBallPaddleCollision() {
		if (myBouncer.getView().getBoundsInParent().intersects(myPaddle1.getView().getBoundsInParent())) {

			// if paddle is not magnetic, bounce ball
			if (!(myPaddle1.isType(Type.STICKY)))
				myBouncer.bounceOffPaddle(myPaddle1, SCREEN_HEIGHT);
			else {
				myBouncer.reposition(myBouncer.myView.getX(),
						myPaddle1.myView.getY() - myBouncer.myView.getFitHeight());
			}

		}
		if (myBouncer.getView().getBoundsInParent().intersects(myPaddle2.getView().getBoundsInParent())) {

			// if paddle is not magnetic, bounce ball
			if (!(myPaddle1.isType(Type.STICKY)))
				myBouncer.bounceOffPaddle(myPaddle2, SCREEN_HEIGHT);
			else
				myBouncer.reposition(myBouncer.myView.getX(),
						myPaddle2.myView.getY() + myPaddle2.myView.getFitHeight());
		}
	}

	/**
	 * Handle key press event
	 */
	private void handleKeyInput(KeyCode code) {
		if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
			myPaddle1.startPaddle1(code);
		else if (code == KeyCode.D || code == KeyCode.A)
			myPaddle2.startPaddle2(code);
		else if (code == KeyCode.SPACE) {
			if (myBouncer.getVelocityX() == 0 && myBouncer.getVelocityY() == 0) {
				if (myBouncer.isInBottomHalf())
					myBouncer.releaseBall(myPaddle1);
				else
					myBouncer.releaseBall(myPaddle2);
			}
		} else if (code == KeyCode.B)
			loadBricks(root, 'B');
		else if (code == KeyCode.N)
			destroyBarrier();
		else if (code == KeyCode.L) {
			team.addLife();
			updateCurrentLivesDisplayed();
		} else if (code == KeyCode.SHIFT) {
			myPaddle1.doubleExtend();
			myPaddle2.doubleExtend();
		} else if (code == KeyCode.DIGIT1)
			recedeToPreviousLevel();
		else if (code == KeyCode.DIGIT2) {
			advanceToNextLevel();
			// currentLevel++;
		}
	}

	/**
	 * Handle key release event
	 */
	private void handleKeyRelease(KeyCode code) {
		if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
			myPaddle1.stopPaddle1(code);
		else if (code == KeyCode.D || code == KeyCode.A)
			myPaddle2.stopPaddle2(code);
	}

	/**
	 * Load a level with bricks
	 */
	private void loadBricks(Group root, int levelNum) {
		// update attributes if barrier is not being loaded
		if (levelNum != 'B') {
			destroyAllBricks();
			resetBallPaddle();
			decodePaddleAbility(levelNum);
			currentLevel = levelNum;
			updateHUD();
			System.out.println("Welcome to Level: " + levelNum);
		}
		readBrickFile(root, levelNum);
	}

	private void updateHUD() {
		updateCurrentAbilityDisplayed();
		updateCurrentLivesDisplayed();
		updateCurrentLevelDisplayed();
	}

	private void readBrickFile(Group root, int levelNum) {
		Scanner s;
		int rows, cols, brickGap = Brick.BRICK_GAP;
		try {
			switch (levelNum) {
			case 0:
			case 1:
				s = new Scanner(new File("Level1.txt"));
				break;
			case 2:
				s = new Scanner(new File("Level2.txt"));
				break;
			case 3:
				s = new Scanner(new File("Level3.txt"));
				break;
			case 'B':
				s = new Scanner(new File("Barrier.txt"));
				break;
			default:
				s = new Scanner(new File("YOU_WIN.txt"));
				brickGap = 0;
				break;
			}
			rows = s.nextInt();
			cols = s.nextInt();
			int board[][] = new int[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					board[i][j] = s.nextInt();

					// set bricks in scene, distribute bricks across the screen
					if (board[i][j] != 0) {
						myBrick = new Brick(j, i, board[i][j], brickGap);
						myBricks.add(myBrick);
						myBrick.setFill(myBrick.getColor());
						root.getChildren().add(myBrick);
					}
				}
			}
			s.close();

		} catch (FileNotFoundException e) {
			System.out.print("Error in text file.");
		}
	}

	private void destroyAllBricks() {
		for (Brick b : myBricks) {
			b.destroyBrick();
		}
	}

	private void decodePaddleAbility(int levelNum) {
		if (levelNum - 1 >= 0 && levelNum - 1 < abilitySequence.size()) {
			myPaddle1.chooseAbility(abilitySequence.get(levelNum - 1));
			myPaddle2.chooseAbility(abilitySequence.get(levelNum - 1));
			System.out.println("Paddle Ability: " + myPaddle1.getCurrType());
		}
		myPaddle1.enablePaddleAbility();
		myPaddle2.enablePaddleAbility();
	}

	public void checkOutOfBounds() {
		if (myBouncer.outOfBounds()) {
			myBouncer.stop();
			team.decrementLives();
			updateCurrentLivesDisplayed();
			System.out.println("TEAM LIVES LEFT: " + team.getLives());
			resetBallPaddle();
		}
	}

	/**
	 * Update attributes when ball hits brick
	 */
	public void checkBallBrickCollision() {
		int bricksHit = 0;
		int bricksLeft = 0;
		for (Brick myBrick : myBricks) {
			if (myBrick.getBoundsInParent().intersects(myBouncer.getView().getBoundsInParent())) {
				bricksHit++;
				// simulate only 1 bounce even if ball hits 2 bricks
				if (bricksHit == 1) {
					myBouncer.bounceOffBrick(myBrick);
				}
				// Extra Life Power-Up
				if (myBrick.isBrickType(BrickType.LIFE)) {
					team.addLife();
					updateHUD();
				}
				myBrick.decrementType();
				myBrick.setFill(myBrick.getColor());
			}
			// count non-permanent bricks left
			if ((!myBrick.isBrickType(BrickType.INFINITE)) && !(myBrick.isBrickType(BrickType.DESTROYED))
					&& !(myBrick.isBrickType(BrickType.BARRIER))) {
				bricksLeft++;
			}
		}
		if (bricksLeft == 0) {
			if (currentLevel != numLevels) {
				System.out.println("YOU WIN");
				destroyAllBricks();
				currentLevel++;
				loadBricks(root, currentLevel);
				resetBallPaddle();
			}
			return;
		}
	}

	/**
	 * Reset the ball and paddles to starting positions
	 */
	public void resetBallPaddle() {
		myPaddle1.reposition(SCREEN_WIDTH / 2 - myPaddle1.getWidth() / 2);
		myPaddle2.reposition(SCREEN_WIDTH / 2 - myPaddle2.getWidth() / 2);
		myBouncer.reposition(
				myPaddle1.myView.getX() + myPaddle1.myView.getFitWidth() / 2 - myBouncer.myView.getFitWidth() / 2,
				SCREEN_HEIGHT - myBouncer.myView.getFitHeight() + Paddle.PADDLE1_OFFSET - 1);
		myBouncer.restartBall();
	}

	/**
	 * Cheat Key Methods
	 */
	public void destroyBarrier() {
		for (Brick b : myBricks) {
			if (b.isBrickType(BrickType.BARRIER))
				b.destroyBrick();
		}
	}

	public void recedeToPreviousLevel() {
		// if not at first level, load previous level
		if (currentLevel >= 2) {
			currentLevel--;
			loadBricks(root, currentLevel);
		}
	}

	public void advanceToNextLevel() {
		// if not at last level, load next level
		if (currentLevel < numLevels) {
			currentLevel++;
			loadBricks(root, currentLevel);
		}
	}

	/**
	 * Add text to Scene
	 */
	private TextFlow addSceneText() {
		TextFlow textFlow = new TextFlow();
		textFlow.setLayoutX(5);
		textFlow.setLayoutY(0);
		Text p1Lives = new Text("Team Lives: ");
		p1Lives.setFont(Font.font("Calibri", 20));
		p1Lives.setFill(Color.WHITE);
		textFlow.getChildren().addAll(p1Lives);
		return textFlow;
	}

	public final void updateCurrentLevelDisplayed() {
		level.set(currentLevel);
	}

	public final void updateCurrentLivesDisplayed() {
		teamLives.set(team.getLives());
	}

	public final void updateCurrentAbilityDisplayed() {
		paddleAbility.set(myPaddle1.getCurrType().toString());
	}

	/**
	 * Start the program.
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
