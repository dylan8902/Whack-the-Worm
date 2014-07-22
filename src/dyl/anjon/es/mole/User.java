package dyl.anjon.es.mole;

import android.content.SharedPreferences;

public class User {
	
	private String name;
	private String currentTheme;
	private int extraLives;
	private int extraBombs;
	private boolean christmasTheme;
	private boolean circusTheme;
	private boolean gardenTheme;
	
	private String facebookId;
	private boolean postToFacebook;
	private String facebookAccessToken;
    private long facebookAccessExpires;
    
    private int totalMoles;

	public User(SharedPreferences data) {
		name =  data.getString("name", "");
		currentTheme = data.getString("currentTheme", "garden");
		
		extraLives = data.getInt("extraLives", 0);
		extraBombs = data.getInt("extraBombs", 0);
		gardenTheme = data.getBoolean("gardenTheme", true);
		circusTheme = data.getBoolean("circusTheme", false);
		christmasTheme = data.getBoolean("christmasTheme", false);
		
		facebookId = data.getString("facebookId", "0");
		postToFacebook = data.getBoolean("postToFacebook", false);
		facebookAccessToken = data.getString("facebookAccessToken", "");
		facebookAccessExpires = data.getLong("facebookAccessExpires", 0);
		
		setTotalMoles(data.getInt("totalMoles", 0));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrentTheme() {
		return currentTheme;
	}

	public void setCurrentTheme(String currentTheme) {
		this.currentTheme = currentTheme;
	}
	
	public int getExtraLives() {
		return extraLives;
	}
	
	public void setExtraLives(int extraLives) {
		this.extraLives = extraLives;
	}
	
	public int getExtraBombs() {
		return extraBombs;
	}

	public void setExtraBombs(int extraBombs) {
		this.extraBombs = extraBombs;
	}

	public boolean hasChristmasTheme() {
		return christmasTheme;
	}

	public void setChristmasTheme(boolean christmasTheme) {
		this.christmasTheme = christmasTheme;
	}

	public boolean hasCircusTheme() {
		return circusTheme;
	}

	public void setCircusTheme(boolean circusTheme) {
		this.circusTheme = circusTheme;
	}

	public boolean hasGardenTheme() {
		return gardenTheme;
	}

	public void setGardenTheme(boolean gardenTheme) {
		this.gardenTheme = gardenTheme;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public boolean isPostToFacebook() {
		return postToFacebook;
	}

	public void setPostToFacebook(boolean postToFacebook) {
		this.postToFacebook = postToFacebook;
	}

	public String getFacebookAccessToken() {
		return facebookAccessToken;
	}

	public void setFacebookAccessToken(String facebookAccessToken) {
		this.facebookAccessToken = facebookAccessToken;
	}

	public long getFacebookAccessExpires() {
		return facebookAccessExpires;
	}

	public void setFacebookAccessExpires(long facebookAccessExpires) {
		this.facebookAccessExpires = facebookAccessExpires;
	}

	public int getTotalMoles() {
		return totalMoles;
	}

	public void setTotalMoles(int totalMoles) {
		this.totalMoles = totalMoles;
	}
	
	public void logout(SharedPreferences data) {
		SharedPreferences.Editor editor = data.edit();
		editor.putString("facebookId", "0");
        editor.putString("facebookAccessToken", "");
        editor.putLong("facebookAccessExpires", 0);
        editor.commit();
	}
	
	public void save(SharedPreferences data) {
		SharedPreferences.Editor editor = data.edit();
		editor.putString("name", name);
		editor.putString("currentTheme", currentTheme);
		editor.putInt("extraLives", extraLives);
		editor.putInt("extraBombs", extraBombs);
		editor.putBoolean("gardenTheme", gardenTheme);
		editor.putBoolean("circusTheme", circusTheme);
		editor.putBoolean("christmasTheme", christmasTheme);
		editor.putBoolean("postToFacebook", postToFacebook);
		editor.putBoolean("postToFacebook", postToFacebook);
		editor.putString("facebookId", facebookId);
		editor.putBoolean("postToFacebook", postToFacebook);
		editor.putString("facebookAccessToken", facebookAccessToken);
		editor.putLong("facebookAccessExpires", facebookAccessExpires);
		editor.putInt("totalMoles", totalMoles);
        editor.commit();
	}
	
}