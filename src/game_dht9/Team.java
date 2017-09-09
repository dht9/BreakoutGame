package game_dht9;

/**
 * Team class used in Breakout Game.
 * 
 * @author David Tran (dht9)
 */

public class Team {
	private int lives;

	// two players make up a team and share three lives.
	public Team() {
		lives = 3;
	}

	public void addLife() {
		lives++;
	}

	public void decrementLives() {
		lives--;
	}

	public void resetLives() {
		lives = 3;
	}

	public boolean livesEqualTo(int val) {
		return lives == val;
	}

	public int getLives() {
		return lives;
	}

}
