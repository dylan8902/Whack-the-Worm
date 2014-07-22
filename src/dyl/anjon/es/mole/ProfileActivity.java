package dyl.anjon.es.mole;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class ProfileActivity extends Activity {
	
	User user;
    Facebook facebook = new Facebook("295057973939898");
    AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        user = new User(getSharedPreferences("user", MODE_PRIVATE));
		
        TextView tv_lives = (TextView)findViewById(R.id.lives);
        tv_lives.setText(String.valueOf(user.getExtraLives() + 3));
        
        TextView tv_bombs = (TextView)findViewById(R.id.bombs);
        tv_bombs.setText(String.valueOf(user.getExtraBombs() + 1));
        
        TextView tv_totalMoles = (TextView)findViewById(R.id.totalMoles);
        tv_totalMoles.setText("You have caught " + Utils.pluralise(user.getTotalMoles(), "mole"));
        
		RadioButton rb_christmas = (RadioButton)findViewById(R.id.christmas);
        if (user.hasChristmasTheme())
        	rb_christmas.setVisibility(View.VISIBLE);
        if (user.getCurrentTheme().equals("christmas"))
        	rb_christmas.setChecked(true);
        else
        	rb_christmas.setChecked(false);
        
        RadioButton rb_garden = (RadioButton)findViewById(R.id.garden);
        if (user.hasGardenTheme())
        	rb_garden.setVisibility(View.VISIBLE);
        if (user.getCurrentTheme().equals("garden"))
        	rb_garden.setChecked(true);
        else
        	rb_garden.setChecked(false);
       
        RadioButton rb_circus = (RadioButton)findViewById(R.id.circus);
        if (user.hasCircusTheme())
        	rb_circus.setVisibility(View.VISIBLE);
        if (user.getCurrentTheme().equals("circus"))
        	rb_circus.setChecked(true);
        else
        	rb_circus.setChecked(false);

        if(!user.getFacebookAccessToken().equals(""))
            facebook.setAccessToken(user.getFacebookAccessToken());
        if(user.getFacebookAccessExpires() != 0)
            facebook.setAccessExpires(user.getFacebookAccessExpires());
        if(!facebook.isSessionValid()) {
        
	        facebook.authorize(this, new DialogListener() {
	            @Override
	            public void onComplete(Bundle values) {
	            	 user.setFacebookAccessToken(facebook.getAccessToken());
       	 			 user.setFacebookAccessExpires(facebook.getAccessExpires());
       	 			 user.save(getSharedPreferences("user", MODE_PRIVATE));
                     AsyncFacebookRunner AsyncRunner = new AsyncFacebookRunner(facebook);
                     AsyncRunner.request("me", new IDRequestListener());
	            }
	
	            @Override
	            public void onFacebookError(FacebookError e) {
	    			Utils.log(e.getMessage());
	    			//not facebooking at the moment
	            }
	
	            @Override
	            public void onError(DialogError e) {
	    			Utils.log(e.getMessage());
	    			//not facebooking at the moment
	            }
	
	            @Override
	            public void onCancel() {
	            	//not facebooking!
	            }
	        });
	        
        }
        else {
        	updateLoggedInView();
        }
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {    
        super.onResume();
        facebook.extendAccessTokenIfNeeded(this, null);
        user = new User(getSharedPreferences("user", MODE_PRIVATE));
		ScrollView background = (ScrollView)findViewById(R.id.background);
		if (user.getCurrentTheme().equals("garden"))
			background.setBackgroundResource(R.drawable.garden);
		else if (user.getCurrentTheme().equals("circus"))
			background.setBackgroundResource(R.drawable.circus);
    }
    
    private class IDRequestListener implements RequestListener {
		 
		@Override
        public void onComplete(String me, Object state) {
             try {
                JSONObject json = Util.parseJson(me);              
            	user.setName(json.getString("name"));
            	user.setFacebookId(json.getString("id"));
            	user.save(getSharedPreferences("user", MODE_PRIVATE));
                ProfileActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	updateLoggedInView();
                    }
                });
                
            } catch (JSONException e) {
    			Utils.log(e.getMessage());

            } catch (FacebookError e) {
    			Utils.log(e.getMessage());
            }
        }

		@Override
		public void onIOException(IOException e, Object state) {
			Utils.log(e.getMessage());			
		}
	
		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			Utils.log(e.getMessage());
		}
	
		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			Utils.log(e.getMessage());			
		}
	
		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Utils.log(e.getMessage());
		}
    }
    
    public void facebookLogout(View v) {
    	asyncRunner.logout(getBaseContext(), new RequestListener() {
    		@Override
    		public void onComplete(String response, Object state) {
    			user.logout(getSharedPreferences("user", MODE_PRIVATE));
                finish();
    		}
    
    		@Override
    		public void onIOException(IOException e, Object state) {
    			Utils.log(e.getMessage());
    		}
    		  
    		@Override
    		public void onMalformedURLException(MalformedURLException e,
    	        Object state) {
    			Utils.log(e.getMessage());
    		}
    		  
    		@Override
    		public void onFacebookError(FacebookError e, Object state) {
    			Utils.log(e.getMessage());
    		}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {	
    			Utils.log(e.getMessage());
			}
    	});
    }
    
    public void updateLoggedInView() {
    			
	    TextView tv_name = (TextView)findViewById(R.id.name);
	    tv_name.setText(user.getName());
	    
        ImageView iv_photo = (ImageView)findViewById(R.id.photo);
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.DisplayImage("http://graph.facebook.com/" + user.getFacebookId() + "/picture", iv_photo);
        
    }
    
    public void changeTheme(View v) {
        ScrollView background = (ScrollView)findViewById(R.id.background);
        boolean checked = ((RadioButton) v).isChecked();
        switch(v.getId()) {
            case R.id.garden:
                if (checked) {
                    user.setCurrentTheme("garden");
        			background.setBackgroundResource(R.drawable.garden);
                }
             break;
            case R.id.circus:
                if (checked) {
                	user.setCurrentTheme("circus");
        			background.setBackgroundResource(R.drawable.circus);
                }
                break;
            case R.id.christmas:
                if (checked)
                	user.setCurrentTheme("christmas");
            break;
        }
        user.save(getSharedPreferences("user", MODE_PRIVATE));
    }
    
}
