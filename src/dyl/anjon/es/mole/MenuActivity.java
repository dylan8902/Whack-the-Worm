package dyl.anjon.es.mole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;

public class MenuActivity extends Activity {
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        User user = new User(getSharedPreferences("user", MODE_PRIVATE));
        int background = 0;
		if (user.getCurrentTheme().equals("garden"))
        	background = R.drawable.garden;
		else if (user.getCurrentTheme().equals("circus"))
        	background = R.drawable.circus;
		
        ScrollView sv_background = (ScrollView)findViewById(R.id.background);
		sv_background.setBackgroundResource(background);
				
		Button newGame = (Button)findViewById(R.id.new_game);
		newGame.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
        	   Intent i = new Intent(getApplicationContext(), GameActivity.class);
        	   i.putExtra("new", true);
           	   startActivity(i);
           }
        });
		
		Button resumeGame = (Button)findViewById(R.id.resume_game);
		Game game = new Game(getSharedPreferences("game", MODE_PRIVATE));
		if(game.getScore() != 0)
			resumeGame.setVisibility(View.VISIBLE);
		resumeGame.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
        	   Intent i = new Intent(getApplicationContext(), GameActivity.class);
        	   i.putExtra("new", false);
           	   startActivity(i);
           }
        });
		
		Button profile = (Button)findViewById(R.id.profile);
		profile.setOnClickListener(new OnClickListener() {                    
            @Override
            public void onClick(View v) {
        	   Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
           	   startActivity(i);
           }
        });
		
		Button highScores = (Button)findViewById(R.id.high_scores);
		highScores.setOnClickListener(new OnClickListener() {                    
            @Override
            public void onClick(View v) {
        	   Intent i = new Intent(getApplicationContext(), HighScoresActivity.class);
           	   startActivity(i);
           }
        });
		
		Button addons = (Button)findViewById(R.id.addons);
		addons.setOnClickListener(new OnClickListener() {                    
            @Override
            public void onClick(View v) {
        	   Intent i = new Intent(getApplicationContext(), AddonsActivity.class);
           	   startActivity(i);
           }
        });
		
		Button about = (Button)findViewById(R.id.about);
		about.setOnClickListener(new OnClickListener() {                    
            @Override
            public void onClick(View v) {
        	   Intent i = new Intent(getApplicationContext(), AboutActivity.class);
           	   startActivity(i);
           }
        });
        		
	}
	
	/** Called when the activity is returned to. */
	@Override
	public void onResume() {
		super.onResume();

		Game game = new Game(getSharedPreferences("game", MODE_PRIVATE));
		Button resumeGame = (Button)findViewById(R.id.resume_game);
		if(game.isNewGame())
			resumeGame.setVisibility(View.GONE);
		else
			resumeGame.setVisibility(View.VISIBLE);

		User user = new User(getSharedPreferences("user", MODE_PRIVATE));
		ScrollView background = (ScrollView)findViewById(R.id.background);
		if (user.getCurrentTheme().equals("garden"))
			background.setBackgroundResource(R.drawable.garden);
		else if (user.getCurrentTheme().equals("circus"))
			background.setBackgroundResource(R.drawable.circus);
	}
}
