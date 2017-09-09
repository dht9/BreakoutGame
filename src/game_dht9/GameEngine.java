package game_dht9;

import game_dht9.Brick.BrickType;
import game_dht9.Paddle.PaddleAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Game Engine for Breakout Game for Fall 2017, Computer Science 308.
 * 
 * start() createLevelScene(), and step() methods inspired by Robert Duvall at
 * https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
 * 
 * @author David Tran (dht9)
 */
public class GameEngine extends Application {
	public static final String TITLE = "JavaFX: Breakout Game";
	public static final int SCREEN_WIDTH = 1004;
	public static final int SCREEN_HEIGHT = 700;
	public static final Paint BACKGROUND = Color.BLACK;
	public static final int FRAMES_PER_SECOND = 120;
	public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
	public static final String BALL_IMAGE = "ball.gif";
	public static final String PADDLE_IMAGE = "paddle.gif";
	public static final int NUM_LEVELS = 3;
	public static final int STATUS_LABEL_OFFSETX = SCREEN_WIDTH / 2 - 100;
	public static final int STATUS_LABEL_OFFSETY = SCREEN_HEIGHT - 150;
	public static final int LARGE_FONT = 26;
	public static final int MEDIUM_FONT = 18;
	public static final int SMALL_FONT = 15;
	public static final int NEWLINE_FONT = 8;
	public static final int NUM_ABILITIES = 3;

	private Scene myScene;
	private Bouncer myBouncer;
	private Team team;
	private Paddle myPaddle1;
	private Paddle myPaddle2;
	private Brick myBrick;
	private List<Brick> myBricks = new ArrayList<>();
	private Iterator<Brick> iter = myBricks.iterator();
	private List<Integer> abilitySequence = new ArrayList<>();
	private Group root;
	private int currentLevel = 1;
	private IntegerProperty teamLives = new SimpleIntegerProperty(0);
	private IntegerProperty level = new SimpleIntegerProperty(0);
	private StringProperty paddleAbility = new SimpleStringProperty();
	private StringProperty playerStatus = new SimpleStringProperty();

	/**
	 * 
	 * Initialize what scenes be displayed and how the stage will be updated.
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new Group();
		Scene level1 = createGameScene(root, SCREEN_WIDTH, SCREEN_HEIGHT, BACKGROUND, currentLevel);
		Scene startMenu = createStartScene(primaryStage, level1);

		primaryStage.setScene(startMenu);
		primaryStage.setTitle(TITLE);
		primaryStage.show();
	}

	public Scene createStartScene(Stage primaryStage, Scene firstLevel) {
		Pane start = new Pane();
		start.setStyle("-fx-background-color: darkslateblue;-fx-padding: 10px;");

		StartMenu menu = new StartMenu(start);
		addStartMenuText(menu);

		HBox hbox = new HBox();
		hbox.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		Button startGameBtn = new Button("START GAME");
		startGameBtn.setFont(new Font("Fleftex", SMALL_FONT));
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
		hbox.setAlignment(Pos.BOTTOM_CENTER);
		hbox.getChildren().addAll(startGameBtn);
		start.getChildren().addAll(hbox, menu);

		return new Scene(start);
	}

	/**
	 * Initialize objects in the scene for Breakout levels
	 */
	private Scene createGameScene(Group root, int width, int height, Paint background, int levelNum) {

		// create objects and set their properties
		Image ballImage = new Image(getClass().getClassLoader().getResourceAsStream(BALL_IMAGE));
		Image paddleImage = new Image(getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE));
		myBouncer = new Bouncer(ballImage, width, height);
		myPaddle1 = new Paddle(paddleImage, SCREEN_HEIGHT + Paddle.PADDLE1_OFFSET);
		myPaddle1.setFill(Color.BLUE);
		myPaddle2 = new Paddle(paddleImage, Paddle.PADDLE2_OFFSET);
		myPaddle2.setFill(Color.RED);
		createPaddleAbilitySequence();
		team = new Team();

		// Set up text interface for brick level
		HeadsUpDisplay hud = new HeadsUpDisplay();
		hud.createHUDLabel(" Level ", level, "left");
		hud.createHUDLabel("Team Lives Remaining: ", teamLives, "right");
		hud.createHUDLabel(" Paddle Ability: ", paddleAbility, "bottom");
		updateHUD("");
		
		loadBricks(root, levelNum);

		// Set up text for power-up activation or "you lose"
		Label statusLabel = new Label();
		statusLabel.textProperty().bind(((StringProperty) playerStatus));
		statusLabel.setLayoutX(STATUS_LABEL_OFFSETX);
		statusLabel.setLayoutY(STATUS_LABEL_OFFSETY);
		statusLabel.setTextFill(Color.WHITE);

		// create a place to see the shapes
		root.getChildren().addAll(myBouncer.getView(), myPaddle1, myPaddle2, hud, statusLabel);
		root.getStylesheets().add(getClass().getResource("/gameFont.css").toExternalForm());
		myScene = new Scene(root, width, height, background);
		myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		myScene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));

		return myScene;
	}

	/**
	 * Update Ball, Paddle, and Brick attributes for each frame.
	 */
	private void step(double elapsedTime) {

		// when ball resets, update position
		if (myBouncer.getVelocityY() == 0 && myBouncer.hasReset()) {
			stickBallToCenterOfPaddle();
		}
		// when ball intersects sticky pad, update position
		else if (myBouncer.getVelocityY() == 0 && !myBouncer.hasReset()) {
			stickBallToPaddle(elapsedTime);
		}
		// when ball is in movement, update position and check conditions
		else {
			myBouncer.move(elapsedTime);
			myBouncer.bounceOffWalls();
			checkBallOutOfBounds();
			checkBallPaddleCollision();
			checkBallBrickCollision();

			if (team.livesEqualTo(0)) {
				team.resetLives();
				loadBricks(root, 1);
				updateHUD("You Lose! Press\n[SPACE] To Play Again!");
				;
			}
		}
		myPaddle1.move(elapsedTime);
		myPaddle2.move(elapsedTime);
		checkPaddleOutOfBounds();
	}

	private void checkPaddleOutOfBounds() {
		// control edge collision depending on paddle ability
		if (myPaddle1.hasAbility(PaddleAbility.EDGEWARP)) {
			myPaddle1.edgeWarp();
			myPaddle2.edgeWarp();
		} else {
			myPaddle1.stopPaddleAtEdge();
			myPaddle2.stopPaddleAtEdge();
		}
	}

	private void stickBallToCenterOfPaddle() {
		myBouncer.repositionAndStop(myPaddle1.getX() + myPaddle1.getWidth() / 2 - myBouncer.getFitWidth() / 2,
				SCREEN_HEIGHT - myBouncer.getFitHeight() + Paddle.PADDLE1_OFFSET + Bouncer.BOUNCER_HOVER);
	}

	private void stickBallToPaddle(double elapsedTime) {
		if (myBouncer.isStartingAtPaddle1())
			myBouncer.repositionAndStop(myBouncer.getX() + myPaddle1.getVelocityX() * elapsedTime,
					myBouncer.getY());
		else
			myBouncer.repositionAndStop(myBouncer.getX() + myPaddle2.getVelocityX() * elapsedTime,
					myBouncer.getY());
	}

	public void checkBallOutOfBounds() {
		if (myBouncer.checkIfOutOfBounds()) {
			team.decrementLives();
			updateHUD("Lost a Life!");
			updateCurrentLivesDisplayed();
			resetBallPaddle();
			// System.out.println("TEAM LIVES LEFT: " + team.getLives());
		}
	}

	public void resetBallPaddle() {
		myPaddle1.reset();
		myPaddle2.reset();
		myBouncer.reset(myPaddle1);
	}

	private void checkBallPaddleCollision() {
		if (myBouncer.getView().getBoundsInParent().intersects(myPaddle1.getBoundsInParent())) {
			// if paddle is not sticky, bounce ball
			if (!(myPaddle1.hasAbility(PaddleAbility.STICKY)))
				myBouncer.bounceOffPaddle(myPaddle1, SCREEN_HEIGHT);
			else {
				myBouncer.repositionAndStop(myBouncer.getX(),
						myPaddle1.getY() - myBouncer.getFitHeight());
			}
			// clear power-up activation text once ball hits paddle1.
			updateHUD("");
		} else if (myBouncer.getView().getBoundsInParent().intersects(myPaddle2.getBoundsInParent())) {
			// if paddle is not sticky, bounce ball.
			if (!(myPaddle2.hasAbility(PaddleAbility.STICKY)))
				myBouncer.bounceOffPaddle(myPaddle2, SCREEN_HEIGHT);
			else
				myBouncer.repositionAndStop(myBouncer.getX(), myPaddle2.getY() + myPaddle2.getHeight());
			// clear any power-up activation text once ball hits paddle1.
			updateHUD("");
		}
	}

	public void checkBallBrickCollision() {
		int bricksHit = 0;
		int bricksLeft = 0;

		iter = myBricks.iterator();

		while (iter.hasNext()) {
			Brick myBrick = iter.next();
			if (myBrick.getBoundsInParent().intersects(myBouncer.getView().getBoundsInParent())) {
				bricksHit++;
				// simulate only 1 bounce even if ball hits 2 bricks.
				if (bricksHit == 1) {
					myBouncer.bounceOffBrick(myBrick);
				}
				// Check CREATE_BARRIER power up first to avoid throwing
				// ConcurrentModificationException (i.e. adding to myBricks while iterating.
				if (myBrick.isBrickType(BrickType.CREATE_BARRIER)) {
					myBrick.decrementType();
					createBarrier();
					updateHUD("1-hit Barrier!");
					return;
				}
				checkForOtherPowerUps(myBrick);
				myBrick.decrementType();
				myBrick.setFill(myBrick.getColor());
			}
			// count breakable bricks left
			if ((!myBrick.isBrickType(BrickType.INFINITE)) && !(myBrick.isBrickType(BrickType.DESTROYED))
					&& !(myBrick.isBrickType(BrickType.BARRIER))) {
				bricksLeft++;
			}
		}
		if (bricksLeft == 0) {
			destroyAllBricks();
			loadBricks(root, currentLevel + 1);
			resetBallPaddle();
		}
	}

	/**
	 * Handle key press events.
	 */
	private void handleKeyInput(KeyCode code) {
		if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
			myPaddle1.startPaddle1(code);
		else if (code == KeyCode.D || code == KeyCode.A)
			myPaddle2.startPaddle2(code);
		else if (code == KeyCode.SPACE && currentLevel != NUM_LEVELS + 1) {
			// release the ball from paddle
			if (myBouncer.getVelocityX() == 0 && myBouncer.getVelocityY() == 0) {
				if (myBouncer.isStartingAtPaddle1())
					myBouncer.releaseBall(myPaddle1);
				else
					myBouncer.releaseBall(myPaddle2);
				updateHUD("");
			}
		} else if (code == KeyCode.B)
			createBarrier();
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
		}
	}

	public void recedeToPreviousLevel() {
		if (currentLevel > 1) {
			loadBricks(root, currentLevel - 1);
		}
	}

	public void advanceToNextLevel() {
		if (currentLevel <= NUM_LEVELS) {
			loadBricks(root, currentLevel + 1);
		}
	}

	/**
	 * Handle key release events.
	 */
	private void handleKeyRelease(KeyCode code) {
		if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
			myPaddle1.stopPaddle1(code);
		else if (code == KeyCode.D || code == KeyCode.A)
			myPaddle2.stopPaddle2(code);
	}

	/**
	 * 
	 * Initialize and control Brick objects.
	 *
	 */
	private void loadBricks(Group root, int levelNum) {
		// clear and reset objects if barrier is not being loaded
		if (levelNum != 'B') {
			destroyAllBricks();
			resetBallPaddle();
			decodePaddleAbility(levelNum);
			currentLevel = levelNum;
			if (levelNum <= NUM_LEVELS)
				updateHUD("Level " + levelNum + "!");
		}
		readBrickFile(root, levelNum);
	}

	private void readBrickFile(Group root, int levelNum) {
		Scanner s;
		int rows, cols;
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
			case 4:
			default:
				s = new Scanner(new File("YOU_WIN.txt"));
				updateHUD("");
				break;
			}
			rows = s.nextInt();
			cols = s.nextInt();
			int board[][] = new int[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					board[i][j] = s.nextInt();
					// add bricks to scene
					if (board[i][j] != 0) {
						myBrick = new Brick(j, i, board[i][j], Brick.BRICK_GAP);
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

	private void createBarrier() {
		loadBricks(root, 'B');
	}

	public void destroyBarrier() {
		for (Brick b : myBricks) {
			if (b.isBrickType(BrickType.BARRIER))
				b.destroyBrick();
		}
	}

	private void destroyAllBricks() {
		for (Brick b : myBricks) {
			b.destroyBrick();
		}
	}

	private void checkForOtherPowerUps(Brick myBrick) {
		if (myBrick.isBrickType(BrickType.LIFE)) {
			team.addLife();
			updateHUD("Extra Life!");
		} else if (myBrick.isBrickType(BrickType.EXPAND_BOUNCER)) {
			myBouncer.expand();
			updateHUD("Big Ball!");
		} else if (myBrick.isBrickType(BrickType.BARRIER))
			destroyBarrier();
		else if (myBrick.isBrickType(BrickType.SLOW_BOUNCER)) {
			myBouncer.slowSpeed();
			updateHUD("Slow Ball!");
		}
	}

	/**
	 * Initialize and Control Paddle Abilities for each level.
	 */
	private void createPaddleAbilitySequence() {
		for (int i = 0; i < NUM_ABILITIES; i++) {
			abilitySequence.add(i);
		}
		Collections.shuffle(abilitySequence);
	}

	private void decodePaddleAbility(int levelNum) {
		if (levelNum > 0 && levelNum <= abilitySequence.size()) {
			myPaddle1.setAbility(abilitySequence.get(levelNum - 1));
			myPaddle2.setAbility(abilitySequence.get(levelNum - 1));
			// System.out.println("Paddle Ability: " + myPaddle1.getCurrentAbility());
		}
		myPaddle1.enablePaddleAbility();
		myPaddle2.enablePaddleAbility();
	}

	/**
	 * 
	 * Initialize and control HUD & Starting Screen.
	 *
	 */
	private void updateHUD(String str) {
		updateCurrentAbilityDisplayed();
		updateCurrentLivesDisplayed();
		updateCurrentLevelDisplayed();
		updatePlayerStatusDisplayed(str);
	}

	public final void updateCurrentLevelDisplayed() {
		level.set(currentLevel);
	}

	public final void updateCurrentLivesDisplayed() {
		teamLives.set(team.getLives());
	}

	public final void updateCurrentAbilityDisplayed() {
		paddleAbility.set(myPaddle1.getCurrentAbility().toString());
	}

	public final void updatePlayerStatusDisplayed(String str) {
		playerStatus.set(str);
	}

	public void addStartMenuText(StartMenu menu) {
		menu.addLabel("Welcome to 2-Player Breakout!", Color.WHITE, LARGE_FONT);
		menu.addLabel("\nObjective:", Color.WHITE, MEDIUM_FONT);
		menu.addLabel("", Color.WHITE, NEWLINE_FONT);
		menu.addLabel("Conquer 3 Brick Levels with a Friend!", Color.WHITE, SMALL_FONT);
		menu.addLabel("\nControls:", Color.WHITE, MEDIUM_FONT);
		menu.addLabel("", Color.WHITE, NEWLINE_FONT);
		menu.addLabel("Move Top Paddle: [A] [D]", Color.WHITE, SMALL_FONT);
		menu.addLabel("Move Bottom Paddle: <- ->", Color.WHITE, SMALL_FONT);
		menu.addLabel("Release Ball: [SPACE]", Color.WHITE, SMALL_FONT);
		menu.addLabel("\nPower Ups:", Color.WHITE, MEDIUM_FONT);
		menu.addLabel("", Color.WHITE, NEWLINE_FONT);
		menu.addLabel("Green Brick = +1 Team Life", Color.WHITE, SMALL_FONT);
		menu.addLabel("Yellow Brick = Bigger Ball", Color.WHITE, SMALL_FONT);
		menu.addLabel("Red Brick = Slower Ball", Color.WHITE, SMALL_FONT);
		menu.addLabel("Blue Brick = Activates 1-hit Safety Barrier", Color.WHITE, SMALL_FONT);
		menu.addLabel("\nPaddle Abilities:", Color.WHITE, MEDIUM_FONT);
		menu.addLabel("", Color.WHITE, NEWLINE_FONT);
		menu.addLabel("Extended, Sticky, Edge-Warped (one per level, random)", Color.WHITE, SMALL_FONT);
		menu.addLabel("\nCheat Keys:", Color.WHITE, MEDIUM_FONT);
		menu.addLabel("", Color.WHITE, NEWLINE_FONT);
		menu.addLabel("Previous Level: [1] , Next Level: [2]", Color.WHITE, SMALL_FONT);
		menu.addLabel("Toggle Extended Paddle: [SHIFT]", Color.WHITE, SMALL_FONT);
		menu.addLabel("Activate Barrier [B] , Deactivate Barrier [N]", Color.WHITE, SMALL_FONT);
		menu.addLabel("Add 1 Life [L]", Color.WHITE, SMALL_FONT);
	}

	/**
	 * Start the program.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
