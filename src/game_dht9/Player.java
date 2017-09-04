package game_dht9;

public class Player {
	public int lives;
	public int score;
	
	public Player() {
		lives = 3;
		score = 0;
	}
	
	public void decrementLives() {
		lives--;
	}

}
