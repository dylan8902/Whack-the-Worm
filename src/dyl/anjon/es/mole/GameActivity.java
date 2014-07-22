package dyl.anjon.es.mole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {
	
	Game game;
	User user;
	
	Handler handler;
	Runnable createMole = new Runnable() {
	  @Override
	  public void run() {
		  createMole();
	  }};
	
	Animation fadeOutAnimation;
	Animation fadeInAnimation;
	
	Drawable dr_mole;
	Drawable dr_moleHit;
	
	private SoundPool soundPool;
	private boolean soundLoaded = false;
	private int whack;
	
	final int MOLES_PER_LEVEL = 6;
	int molesThisLevel = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		handler = new Handler();
		fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	    soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	    soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	      public void onLoadComplete(SoundPool soundPool, int sampleId,
	          int status) {
	    	  soundLoaded = true;
	      }
	    });
	    whack = soundPool.load(this, R.raw.whack, 1);
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		user.save(getSharedPreferences("user", MODE_PRIVATE));
		pause();
	}
	
	public void onResume() {
		super.onResume();
		
		user = new User(getSharedPreferences("user", MODE_PRIVATE));
		boolean newGame = getIntent().getBooleanExtra("new", true);
		if(newGame) {
			game = new Game();
			game.setLives(game.getLives() + user.getExtraLives());
			game.setBombs(game.getBombs() + user.getExtraBombs());
			game.setNewGame(false);
		}
		else
			game = new Game(getSharedPreferences("game", MODE_PRIVATE));
		
		LinearLayout background = (LinearLayout)findViewById(R.id.background);
		if (user.getCurrentTheme().equals("garden")) {
			background.setBackgroundResource(R.drawable.garden);
			dr_mole = getResources().getDrawable(R.drawable.mole);
			dr_moleHit = getResources().getDrawable(R.drawable.mole_hit);
		}
		else if (user.getCurrentTheme().equals("circus")) {
			background.setBackgroundResource(R.drawable.circus);
			dr_mole = getResources().getDrawable(R.drawable.clown);
			dr_moleHit = getResources().getDrawable(R.drawable.clown_hit);
		}
		
		ImageButton bt_playPause = (ImageButton)findViewById(R.id.playPause);
		bt_playPause.setBackgroundColor(Color.RED);
		
		updateScoreBoard();
		
		if(game.isPlaying())
			play();
	}
	
	public void play() {
		ImageButton bt_playPause = (ImageButton)findViewById(R.id.playPause);
		bt_playPause.setBackgroundColor(Color.GREEN);
		game.setPlaying(true);
		createMole();
	}
	
	public void pause() {
		ImageButton bt_playPause = (ImageButton)findViewById(R.id.playPause);
		bt_playPause.setBackgroundColor(Color.RED);
		game.setPlaying(false);
		game.save(getSharedPreferences("game", MODE_PRIVATE));
	}
	
	public void playPause(View v) {
		if(game.isPlaying())
			pause();
		else
			play();
	}
	
	public void createMole() {
		
	   if(!game.isPlaying())
		   return;
		
		checkLives();
		int random;
		Random r = new Random();

		do {
			random = r.nextInt(10-1) + 1;
		}while (game.isMoleUp(random));
		Utils.log(random + " hole was chosen at random");
		
		final int mole = random;
		molesThisLevel++;
		
		int id = getResources().getIdentifier("a"+ mole, "id", getPackageName());
		Button bt_mole = (Button)findViewById(id);
		bt_mole.setBackgroundDrawable(dr_mole);
		//bt_mole.startAnimation(fadeInAnimation);
		
		game.moleUp(mole);
		
		if (molesThisLevel == MOLES_PER_LEVEL) {
			game.setMoleDelay((int) (game.getMoleDelay()/1.02));
			game.setMoleUpFor((int) (game.getMoleUpFor()/1.01));
			molesThisLevel = 0;
			game.setLevel(game.getLevel() + 1);
		}
		
		handler.postDelayed(createMole, game.getMoleDelay());
		handler.postDelayed(new Runnable() {
			   public void run() {
			       killMole(mole);
			    }
			}, game.getMoleUpFor());

	}
	
	public void killMole(int mole) {
		if(!game.isPlaying())
			return;
		if(game.isMoleUp(mole)) {
			game.moleDown(mole);
			int id = getResources().getIdentifier("a"+ mole, "id", getPackageName());
			final Button bt_mole = (Button)findViewById(id);
			bt_mole.setBackgroundColor(Color.TRANSPARENT);
			game.setScore(game.getScore() - 50);
			game.setLives(game.getLives() - 1);
		}
		updateScoreBoard();
	}
	
	public void whack(View v) {

		if(!game.isPlaying())
			return;
				
		int whacked = Integer.valueOf(v.getTag().toString());
		if(!game.isMoleUp(whacked)) {
			game.setScore(game.getScore() - 50);
			game.setLives(game.getLives() - 1);
		}
		else {
			game.moleDown(whacked);
			user.setTotalMoles(user.getTotalMoles() + 1);
			
			playSound();
			Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vib.vibrate(20);
			
			int id = getResources().getIdentifier("a"+ whacked, "id", getPackageName());
			final Button bt_mole = (Button)findViewById(id);
			bt_mole.setBackgroundDrawable(dr_moleHit);
			handler.postDelayed(new Runnable() {
				public void run() {
					bt_mole.setBackgroundColor(Color.TRANSPARENT);
				}
			}, 120);
			
			game.setScore(game.getScore() + 100);
		}
		
		updateScoreBoard();
	}
	
	public void updateScoreBoard() {
		if (game.getLives() < 0)
			game.setLives(0);
		TextView tv_score = (TextView)findViewById(R.id.score);
		tv_score.setText(String.valueOf(game.getScore()));
		TextView tv_lives = (TextView)findViewById(R.id.lives);
		tv_lives.setText(String.valueOf(game.getLives()));
		Utils.log("Mole Delay: " + game.getMoleDelay() + ", Mole up for: " + game.getMoleUpFor());
		checkLives();
	}
	
	public void checkLives() {
		if (game.getLives() < 1) {
			Utils.log("Game Over!");
			game.setPlaying(false);
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Game Over!");
			alert.setMessage("Congratulations, you scored " +
					game.getScore() + " points! Submit High Score?");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			input.setHint("Enter your name");
			input.setText(user.getName());
			alert.setView(input);
			alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					user.setName(input.getText().toString());
					if(Utils.isNetworkAvailable(getApplicationContext())) {
						new HighScore().execute(String.valueOf(game.getScore()));
						game.clear(getSharedPreferences("game", MODE_PRIVATE));
					}
					Intent i = new Intent(getApplicationContext(), HighScoresActivity.class);
		           	startActivity(i);
					finish();
				}
			});
			alert.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					game.clear(getSharedPreferences("game", MODE_PRIVATE));
					finish();
				}
			});
			alert.setCancelable(false);
			alert.show();
		}
	}
	
	class HighScore extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... score) {
        	        	        	
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
     	   	pairs.add(new BasicNameValuePair("name", user.getName()));
       	   	pairs.add(new BasicNameValuePair("facebook_id", user.getFacebookId()));     	   		
     	   	pairs.add(new BasicNameValuePair("score", score[0]));
        	
        	StringBuilder builder = new StringBuilder();
        	HttpClient client = new DefaultHttpClient();
    		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mole Android Application");
    		HttpPost httpPost = new HttpPost(Utils.DOMAIN + "/highscores.json");
    		try {
    			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
    		} catch (UnsupportedEncodingException e) {
    			Utils.log(e.getMessage());
    		}
    		try {
    			HttpResponse response = client.execute(httpPost);
    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while ((line = reader.readLine()) != null) {
    				builder.append(line);
    			}
    		}
    		catch (ClientProtocolException e) {
    			Utils.log(e.getMessage());
    		}
    		catch (IOException e) {
    			Utils.log(e.getMessage());
    		}
    		
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Utils.log(result);
        }
	}
	
	private void playSound() {
		 AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	      float actualVolume = (float) audioManager
	          .getStreamVolume(AudioManager.STREAM_MUSIC);
	      float maxVolume = (float) audioManager
	          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	      float volume = actualVolume / maxVolume;
	      if (soundLoaded)
	        soundPool.play(whack, volume, volume, 1, 0, 1f);
	}

}
