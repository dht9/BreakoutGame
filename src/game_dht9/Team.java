package game_dht9;

/**
 * Team class used in Breakout Game.
 * 
 * @author David Tran (dht9)
 */

public class Team {
	
	public static int STARTING_LIVES;
	private int lives;

	// two players make up a team and share three lives.
	public Team() {
		lives = STARTING_LIVES;
	}

	public void addLife() {
		lives++;
	}

	public void decrementLives() {
		lives--;
	}

	public void resetLives() {
		lives = STARTING_LIVES;
	}

	public boolean livesEqualTo(int val) {
		return lives == val;
	}

	public int getLives() {
		return lives;
	}

}
