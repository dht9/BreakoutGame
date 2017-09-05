package game_dht9;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import game_dht9.Paddle.Type;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
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
	public static final String TITLE = "JavaFX: Initial Game";
	public static final String BALL_IMAGE = "ball.gif";
	public static final String PADDLE_IMAGE = "paddle.gif";
	public static final int SCREEN_WIDTH = 1004; // 504 prev
	public static final int SCREEN_HEIGHT = 700;
	public static final Paint BACKGROUND = Color.BLACK;
	public static final int FRAMES_PER_SECOND = 120;
	public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
	public static final int KEY_INPUT_SPEED = 10;
	public static final double GROWTH_RATE = 1.1;
	public int currentLevel = 1;
	public int numLevels = 4;
	public Group root;

	private Scene myScene;
	private Bouncer myBouncer;
	private Player player;
	private LongProperty player1Lives = new SimpleLongProperty(0);
	private Paddle myPaddle1;
	private Paddle myPaddle2;
	private Brick myBrick;
	private List<Brick> myBricks = new ArrayList<>();
	private List<Integer> abilityList = new ArrayList<>();

	/**
	 * Initialize what will be displayed and how it will be updated.
	 */

	@Override
	public void start(Stage primaryStage) throws Exception {
		// attach scene to the stage and display it

		// create one top level collection to organize the things in the scene
		root = new Group(addSceneText());
		Scene level1 = setupGame(root, SCREEN_WIDTH, SCREEN_HEIGHT, BACKGROUND, 1);

		// Button button1 = new Button("Go to scene " + (mySceneNum+1));
		// root.getChildren().add(button1);
		// button1.setOnAction(e -> changeScene(primaryStage, mySceneNum+1));
		// root.getChildren().add(Lives);
		// Lives.textProperty().bind(Bindings.createStringBinding(() -> ("GG! " +
		// player1Lives), player1Lives));

		primaryStage.setScene(level1);
		primaryStage.setTitle(TITLE);
		primaryStage.show();

		// attach "game loop" to timeline to play it
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();

		// System.out.println("scene set up");
	}

	// public void changeScene(Stage primaryStage, int sceneNum) {
	// for(Brick b : myBricks)
	// b.destroyBrick();
	// root = new Group();
	// primaryStage.setScene(setupGame(root, WIDTH, HEIGHT, BACKGROUND, sceneNum));
	// primaryStage.show();
	//
	// Button button1 = new Button("Go to scene " + (mySceneNum+1));
	// root.getChildren().add(button1);
	// button1.setOnAction(e -> changeScene(primaryStage, mySceneNum));
	// System.out.println("Scene: " + sceneNum);
	//
	// mySceneNum++;
	//
	// }

	// Create the game's "scene": what shapes will be in the game and their starting
	// properties
	private Scene setupGame(Group root, int width, int height, Paint background, int sceneNum) {

		// make some shapes and set their properties
		player = new Player();

		Image paddleImage = new Image(getClass().getClassLoader().getResourceAsStream(PADDLE_IMAGE));
		myPaddle1 = new Paddle(paddleImage, width / 2, height);
		myPaddle2 = new Paddle(paddleImage, width / 2, 150);

		Image ballImage = new Image(getClass().getClassLoader().getResourceAsStream(BALL_IMAGE));
		myBouncer = new Bouncer(ballImage, width, height);

		root.getChildren().add(myBouncer.getView());
		root.getChildren().add(myPaddle1.getView());
		root.getChildren().add(myPaddle2.getView());

		// set random order of paddle abilities for each level
		for (int i = 0; i < 3; i++) {
			abilityList.add(i);
		}
		Collections.shuffle(abilityList);

		loadLevel(root, width, height, sceneNum);

		// create a place to see the shapes
		myScene = new Scene(root, width, height, background);

		myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));

		myScene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));

		return myScene;
	}

	private void step(double elapsedTime) {
		// update attributes
//		System.out.println(myBouncer.hasRestarted());
		if (myBouncer.getVelocityY() == 0 && myBouncer.hasRestarted())
			startBall(elapsedTime);
		// move ball with paddle once magnetized by lower paddle
		else if (myBouncer.getVelocityY() == 0 && !myBouncer.hasRestarted() && myBouncer.isInBottomHalf(SCREEN_HEIGHT)) {
			myBouncer.recenter(myBouncer.myView.getX() + myPaddle1.myVelocity.getX()*elapsedTime,
					myBouncer.myView.getY());
			myPaddle1.move(elapsedTime, SCREEN_WIDTH);
			myPaddle2.move(elapsedTime, SCREEN_WIDTH);
		}
		// move ball with paddle once magnetized by upper paddle
		else if(myBouncer.getVelocityY() == 0 && !myBouncer.hasRestarted() && !myBouncer.isInBottomHalf(SCREEN_HEIGHT)) {
			myBouncer.recenter(myBouncer.myView.getX() + myPaddle2.myVelocity.getX()*elapsedTime,
					myBouncer.myView.getY());
			myPaddle1.move(elapsedTime, SCREEN_WIDTH);
			myPaddle2.move(elapsedTime, SCREEN_WIDTH);
		}
		// if ball is not magnetised or has restarted, ball is in movement
		// move ball and check intersections
		else {
			myBouncer.move(elapsedTime);
			myBouncer.bounce(myScene.getWidth(), myScene.getHeight());

			checkOutOfBounds();
			if (player.lives == 0) {
				loadLevel(root, SCREEN_WIDTH, SCREEN_HEIGHT, 1);
				System.out.println("YOU LOSE");
				player.lives = 3;
			}

			// update paddle speed
			myPaddle1.move(elapsedTime, SCREEN_WIDTH);
			myPaddle2.move(elapsedTime, SCREEN_WIDTH);

			// check if ball intersects paddle
			if (myBouncer.getView().getBoundsInParent().intersects(myPaddle1.getView().getBoundsInParent())) {
				
				// if paddle is not magnetic, bounce ball
				if (!myPaddle1.currType.toString().equals("MAGNETIC"))
					myBouncer.bounceOffPaddle(myPaddle1, SCREEN_HEIGHT);
				else {
					myBouncer.recenter(myBouncer.myView.getX(),
							myPaddle1.myView.getY() - myBouncer.myView.getFitHeight());
				}
					
			}
			if (myBouncer.getView().getBoundsInParent().intersects(myPaddle2.getView().getBoundsInParent())) {
				
				// if paddle is not magnetic, bounce ball
				if (!myPaddle1.currType.toString().equals("MAGNETIC"))
					myBouncer.bounceOffPaddle(myPaddle2, SCREEN_HEIGHT);
				else
					myBouncer.recenter(myBouncer.myView.getX(),
							myPaddle2.myView.getY() + myPaddle2.myView.getFitHeight());
			}

			// check if ball intersects brick
			checkBallBrickCollision();
		}
		
		// Edge Warp Paddles if paddle has edge-warp ability
		if (myPaddle1.currType.toString().equals("EDGEWARPPED")) {
			myPaddle1.edgeWarp(SCREEN_WIDTH);
			myPaddle2.edgeWarp(SCREEN_WIDTH);
		}
		else {
			myPaddle1.stopPaddleAtEdge(SCREEN_WIDTH);
			myPaddle2.stopPaddleAtEdge(SCREEN_WIDTH);
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
				if (myBouncer.isInBottomHalf(SCREEN_HEIGHT))
					myBouncer.releaseBall(myPaddle1, SCREEN_HEIGHT);
				else
					myBouncer.releaseBall(myPaddle2, SCREEN_HEIGHT);
			}
		}
		else if (code == KeyCode.B)
			createBarrier();
		else if (code == KeyCode.N)
			destroyBarrier();
		else if (code == KeyCode.L)
			addTeamLives();
		else if (code == KeyCode.SHIFT) {
			myPaddle1.doubleExtend();
			myPaddle2.doubleExtend();
		} else if (code == KeyCode.DIGIT1)
			recedeToPreviousLevel();
		else if (code == KeyCode.DIGIT2)
			advanceToNextLevel();
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
	 * Add text
	 */
	private TextFlow addSceneText() {
		TextFlow textFlow = new TextFlow();
		textFlow.setLayoutX(5);
		textFlow.setLayoutY(0);
		Text p1Lives = new Text("Team Lives: ");
		// text1.textProperty().bind(Bindings.createIntegerBinding(() ->
		// (player1.lives), player1));
		p1Lives.setFont(Font.font("Calibri", 20));
		p1Lives.setFill(Color.WHITE);
		textFlow.getChildren().addAll(p1Lives);
		return textFlow;
	}

	/**
	 * Load Level 1 Brick Layout
	 */
	private void loadLevel(Group root, int screenWidth, int screenHeight, int levelNum) {
		// do not break other bricks if loading barrier!
		System.out.println("Welcome to Level: " + levelNum);
		if (levelNum != 'b') {
			for (Brick b : myBricks) {
				b.destroyBrick();
			}
			recenterObjects();

			// set paddle ability if barrier is not being loaded
			if (levelNum - 1 >= 0 && levelNum - 1 < abilityList.size()) {
				myPaddle1.chooseAbility(abilityList.get(levelNum - 1));
				myPaddle2.chooseAbility(abilityList.get(levelNum - 1));
				System.out.println("Paddle Ability: " + myPaddle1.currType);
			}

			myPaddle1.enablePaddleAbility();
			myPaddle2.enablePaddleAbility();
		}
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
			case 'b':
				s = new Scanner(new File("Barrier.txt"));
				break;
			default:
				s = new Scanner(new File("YOU_WIN.txt"));
				break;
			}
			rows = s.nextInt();
			cols = s.nextInt();
			int board[][] = new int[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					board[i][j] = s.nextInt();
					// System.out.print(board[i][j] + " ");

					// set bricks in scene, distribute bricks across the screen
					if (board[i][j] != 0) {
						myBrick = new Brick(j, i, board[i][j]);
						myBricks.add(myBrick);
						myBrick.setFill(myBrick.brickType.getColor());

						root.getChildren().add(myBrick);
					}
				}
			}
			s.close();

		} catch (FileNotFoundException e) {
			System.out.print("Error in text file.");
		}
	}

	public void startBall(double elapsedTime) {
		myBouncer.recenter(myPaddle1.myView.getX() + myPaddle1.getWidth() / 2 - myBouncer.myView.getFitWidth() / 2,
				SCREEN_HEIGHT - myBouncer.myView.getFitHeight() - 75 - 1);
		myPaddle1.move(elapsedTime, SCREEN_WIDTH);
		myPaddle2.move(elapsedTime, SCREEN_WIDTH);
	}

	public void checkOutOfBounds() {
		if (myBouncer.outOfBounds(SCREEN_WIDTH, SCREEN_HEIGHT)) {
			player.decrementLives();
			System.out.println("TEAM LIVES LEFT: " + player.lives);
			recenterObjects();
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
				// simulate 1 bounce even if ball hits two bricks in 1 frame
				bricksHit++;
				if (bricksHit == 1) {
					myBouncer.bounceOffBrick(myBrick);
				}
				myBrick.decrementType();
				myBrick.setFill(myBrick.brickType.getColor());
			}
			// count non-permanent bricks left
			if ((!myBrick.brickType.toString().equals("INFINITE") && !(myBrick.brickType.toString().equals("DESTROYED"))
					&& !(myBrick.brickType.toString().equals("BARRIER")))) {
				bricksLeft++;
			}
		}
		if (bricksLeft == 0) {
			if (currentLevel != numLevels) {
				System.out.println("YOU WIN");
				for (Brick b : myBricks) {
					b.destroyBrick();
				}
				currentLevel++;
				loadLevel(root, SCREEN_WIDTH, SCREEN_HEIGHT, currentLevel);
				recenterObjects();
			}
			return;
		}
	}

	public void recenterObjects() {
		myPaddle1.recenter(SCREEN_WIDTH / 2 - myPaddle1.getWidth() / 2);
		myPaddle2.recenter(SCREEN_WIDTH / 2 - myPaddle2.getWidth() / 2);
		myBouncer.recenter(
				myPaddle1.myView.getX() + myPaddle1.myView.getFitWidth() / 2 - myBouncer.myView.getFitWidth() / 2,
				SCREEN_HEIGHT - myBouncer.myView.getFitHeight() - 75 - 1);
		myBouncer.restartBall();
	}

	/**
	 * Cheat Key Methods
	 */
	public void createBarrier() {
		loadLevel(root, SCREEN_WIDTH, SCREEN_HEIGHT, 'b');
	}

	public void destroyBarrier() {
		for (Brick b : myBricks) {
			if (b.brickType.toString().equals("BARRIER"))
				b.destroyBrick();
		}
	}

	public void addTeamLives() {
		player.lives++;
		System.out.println("Added Team 1 Life");
	}

	public void recedeToPreviousLevel() {
		// if not at first level, load previous level
		if (currentLevel >= 2) {
			currentLevel--;
			loadLevel(root, SCREEN_WIDTH, SCREEN_HEIGHT, currentLevel);
		}
	}

	public void advanceToNextLevel() {
		// if not at last level, load next level
		if (currentLevel < numLevels) {
			currentLevel++;
			loadLevel(root, SCREEN_WIDTH, SCREEN_HEIGHT, currentLevel);
		}
	}

	/**
	 * Start the program.
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
