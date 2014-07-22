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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		new GetTotal().execute();
	}
	
	public void onResume() {
		super.onResume();
		User user = new User(getSharedPreferences("user", MODE_PRIVATE));
		ScrollView background = (ScrollView)findViewById(R.id.background);
		if (user.getCurrentTheme().equals("garden"))
			background.setBackgroundResource(R.drawable.garden);
		else if (user.getCurrentTheme().equals("circus"))
			background.setBackgroundResource(R.drawable.circus);
	}
	
	class GetTotal extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
        	
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
    		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mole Android Application");
    		HttpGet httpGet = new HttpGet("http://dyl.anjon.es/mole/total.json");
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
                        
    		JSONObject total = null;
    		try {
    			total = new JSONObject(result);
				TextView tv_name = (TextView)findViewById(R.id.raised);
				tv_name.setText("Total raised so far: £" + total.getString("grandTotalRaisedExcludingGiftAid"));
    		} catch (JSONException e) {
    			Utils.log(e.getMessage());
    		}
		    
        }
	}
	
}
