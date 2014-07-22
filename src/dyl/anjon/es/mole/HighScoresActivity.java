package dyl.anjon.es.mole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class HighScoresActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.high_scores);
		if(Utils.isNetworkAvailable(getApplicationContext()))
			new HighScores().execute();	
	}
	
    @Override
	public void onResume() {
		super.onResume();
		User user = new User(getSharedPreferences("user", MODE_PRIVATE));
		ScrollView background = (ScrollView)findViewById(R.id.background);
		if (user.getCurrentTheme().equals("garden"))
			background.setBackgroundResource(R.drawable.garden);
		else if (user.getCurrentTheme().equals("circus"))
			background.setBackgroundResource(R.drawable.circus);
	}
	
	class HighScores extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
        	
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
    		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mole Android Application");
    		HttpGet httpGet = new HttpGet("http://dyl.anjon.es/mole/highscores.json?limit=10");
    		try {
    			HttpResponse response = client.execute(httpGet);
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
                        
    		JSONArray scores = null;
    		try {
    			scores = new JSONArray(result);
    			for(int i = 0; i < scores.length(); i++) {
    				int id = getResources().getIdentifier("pos"+ (i+1), "id", getPackageName());
    				TextView tv_name = (TextView)findViewById(id);
    				tv_name.setText(scores.getJSONObject(i).getString("name"));
    				id = getResources().getIdentifier("score"+ (i+1), "id", getPackageName());
    				TextView tv_score = (TextView)findViewById(id);
    				tv_score.setText(scores.getJSONObject(i).getString("score"));
    				String facebook_id = scores.getJSONObject(i).getString("facebook_id");
    				if(!facebook_id.equals("0")) {
    					id = getResources().getIdentifier("image"+ (i+1), "id", getPackageName());
    					ImageView iv_photo = (ImageView)findViewById(id);
    					ImageLoader imageLoader = new ImageLoader(getApplicationContext());
    					imageLoader.DisplayImage("http://graph.facebook.com/" + facebook_id + "/picture", iv_photo);
    				}
    			}
    			
    		} catch (JSONException e) {
    			Utils.log(e.getMessage());
    		}
		    
        }
	}

}
