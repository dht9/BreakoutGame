package game_dht9;

/**
 * Team class used in Breakout Game.
 * 
 * @author David Tran (dht9)
 */

public class Team {
	private int lives;
	private int score;
	
	public Team() {
		lives = 3;
		score = 0;
	}
	
	public void addLife() {
		lives++;
		System.out.println("Added 1 Life");
	}
	
	public void decrementLives() {
		lives--;
		System.out.println("Lost 1 Life");
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
	
	public void deleteLives() {
		lives = 0;
	}

}
