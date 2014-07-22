package dyl.anjon.es.mole;

import android.content.SharedPreferences;

public class Game {
	
	private boolean newGame;
	private boolean playing;
	
	private int moleDelay;
	private int moleUpFor;
	private boolean[] mole = new boolean[10];
		
	private int score;
	private int level;
	private int lives;
	private int bombs;
	
	public Game(SharedPreferences data) {
		newGame = data.getBoolean("newGame", true);
		playing =  data.getBoolean("playing", true);
		moleDelay = data.getInt("moleDelay", 1200);
		moleUpFor = data.getInt("moleUpFor", 2000);
		mole[1] = data.getBoolean("mole1", false);
		mole[2] = data.getBoolean("mole2", false);
		mole[3] = data.getBoolean("mole3", false);
		mole[4] = data.getBoolean("mole4", false);
		mole[5] = data.getBoolean("mole5", false);
		mole[6] = data.getBoolean("mole6", false);
		mole[7] = data.getBoolean("mole7", false);
		mole[8] = data.getBoolean("mole8", false);
		mole[9] = data.getBoolean("mole9", false);
		score = data.getInt("score", 0);
		level = data.getInt("level", 1);
		lives = data.getInt("lives", 3);
		bombs = data.getInt("bombs", 1);
	}
	
	public Game() {
		newGame =  true;
		playing =  true;
		moleDelay = 1200;
		moleUpFor = 2000;
		mole[1] = false;
		mole[2] = false;
		mole[3] = false;
		mole[4] = false;
		mole[5] = false;
		mole[6] = false;
		mole[7] = false;
		mole[8] = false;
		mole[9] = false;
		score = 0;
		level = 1;
		lives = 3;
		bombs = 1;
	}
	
	public boolean isNewGame() {
		return newGame;
	}

	public void setNewGame(boolean newGame) {
		this.newGame = newGame;
	}
	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean isPlaying() {
		return playing;
	}

	public int getMoleDelay() {
		return moleDelay;
	}

	public void setMoleDelay(int moleDelay) {
		this.moleDelay = moleDelay;
	}

	public int getMoleUpFor() {
		return moleUpFor;
	}

	public void setMoleUpFor(int moleUpFor) {
		this.moleUpFor = moleUpFor;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}
	
	public boolean isMoleUp(int i) {
		return mole[i];
	}
	
	public void moleUp(int i) {
		mole[i] = true;
	}
	
	public void moleDown(int i) {
		mole[i] = false;
	}
	
	public void save(SharedPreferences data) {
		SharedPreferences.Editor editor = data.edit();
		editor.putBoolean("newGame", newGame);
		editor.putBoolean("playing", playing);
		editor.putInt("moleDelay", moleDelay);
		editor.putInt("moleUpFor", moleUpFor);
		editor.putBoolean("mole1", mole[1]);
		editor.putBoolean("mole2", mole[2]);
		editor.putBoolean("mole3", mole[3]);
		editor.putBoolean("mole4", mole[4]);
		editor.putBoolean("mole5", mole[5]);
		editor.putBoolean("mole6", mole[6]);
		editor.putBoolean("mole7", mole[7]);
		editor.putBoolean("mole8", mole[8]);
		editor.putBoolean("mole9", mole[9]);
		editor.putInt("score", score);
		editor.putInt("level", level);
		editor.putInt("lives", lives);
		editor.putInt("bombs", bombs);
        editor.commit();
	}
	
	public void clear(SharedPreferences data) {
		newGame =  true;
		playing =  true;
		moleDelay = 1200;
		moleUpFor = 2000;
		mole[1] = false;
		mole[2] = false;
		mole[3] = false;
		mole[4] = false;
		mole[5] = false;
		mole[6] = false;
		mole[7] = false;
		mole[8] = false;
		mole[9] = false;
		score = 0;
		level = 1;
		lives = 3;
		bombs = 1;
		SharedPreferences.Editor editor = data.edit();
		editor.clear();
        editor.commit();
	}
	
}
