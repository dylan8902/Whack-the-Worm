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
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class AddonsActivity extends Activity {
	
	User user;
	String code;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addons);
		
		user = new User(getSharedPreferences("user", MODE_PRIVATE));
		if(Utils.isNetworkAvailable(getApplicationContext()))
			new GetAddons().execute();
	}
	
    @Override
	public void onResume() {
		super.onResume();
		user = new User(getSharedPreferences("user", MODE_PRIVATE));
		ScrollView background = (ScrollView)findViewById(R.id.background);
		if (user.getCurrentTheme().equals("garden"))
			background.setBackgroundResource(R.drawable.garden);
		else if (user.getCurrentTheme().equals("circus"))
			background.setBackgroundResource(R.drawable.circus);
	}
	
	class GetAddons extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
        	
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
    		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mole Android Application");
    		HttpGet httpGet = new HttpGet("http://dyl.anjon.es/mole/addons.json?limit=10");
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
                        
    		JSONArray addons = null;
			LinearLayout layout = (LinearLayout) findViewById(R.id.addons);
			
    		try {
    			addons = new JSONArray(result);
    			
    			for(int i = 0; i < addons.length(); i++) {
    		        final String tCode = addons.getJSONObject(i).getString("code");
    		        final String amount = addons.getJSONObject(i).getString("amount");
    		        final String name = addons.getJSONObject(i).getString("name");
    				Button bt = (Button) getLayoutInflater().inflate(R.layout.button, null);
    				bt.setTag(tCode);
    		        bt.setText(name + " (£" + amount + ")");;
    		        layout.addView(bt);
    		        bt.setOnClickListener(new OnClickListener() {                    
    		            @Override
    		            public void onClick(View v) {
    		            	code = tCode;
    		        	    getAddon(amount, name);
    		           }
    		        });
    			}
    			
    		} catch (JSONException e) {
    			Utils.log(e.getMessage());
    		}
		    
        }
	}
	
	public void getAddon(final String amount, final String name) {
		
		String url;
		if(Utils.sandbox)
			url = Utils.JG_SANDBOX_URL;
		else
			url = Utils.JG_LIVE_URL;
		url += "?amount=" + amount +
				"&reference=" + code + "&defaultMessage=I%20bought%20" +  
				name.replace(" ", "%20") + "&exitUrl=http%3A%2F%2Fdyl.anjon.es" +
				"%2Fmole%2Fpurchase.json%3Fsource%3Dandroid%26facebook_id%3D" + 
				user.getFacebookId() + "%26jg%3DJUSTGIVING-DONATION-ID";
		if(Utils.sandbox)
			url += "%26env%3Dsandbox";
		else
			url += "%26env%3Dlive";
		
        final WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	if(url.contains("dyl.anjon.es/mole/purchase.json")) {
            		new ValidateDonation().execute(url);
            		Toast.makeText(getApplicationContext(), "Validating Donation", Toast.LENGTH_SHORT).show();
            		wv.setVisibility(View.GONE);
                    return false;
            	}
            	else {
	                view.loadUrl(url);
	                return true;
            	}
            }
        });

		LinearLayout layout = (LinearLayout) findViewById(R.id.addons);
		layout.removeAllViews();
		Toast.makeText(getApplicationContext(), "Loading Donation Page", Toast.LENGTH_SHORT).show();
        layout.addView(wv);
	}
	
	class ValidateDonation extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
        	
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
    		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mole Android Application");
    		HttpGet httpGet = new HttpGet(uri[0]);
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
            
    		JSONObject check = null;
    		try {
    			check = new JSONObject(result);
    			if(check.get("code").equals(code)) {
    				if(code.equals("5lives"))
    			        user.setExtraLives(user.getExtraLives() + 5);
    				else if(code.equals("5bombs"))
    					user.setExtraBombs(user.getExtraBombs() + 5);
    				else if(code.equals("christmas"))
    			        user.setChristmasTheme(true);
    				else if(code.equals("circus"))
    			        user.setCircusTheme(true);
    			    user.save(getSharedPreferences("user", MODE_PRIVATE));
    				Toast.makeText(getApplicationContext(), "Yey! You have now got " + check.get("name"), Toast.LENGTH_LONG).show();
    				Intent i = new Intent(getApplicationContext(), AddonsActivity.class);
    	           	startActivity(i);
    	           	finish();
    			}
    			else {
    				try {
    					check = new JSONObject(result);
        				Toast.makeText(getApplicationContext(), "Sorry! " + check.get("message"), Toast.LENGTH_LONG).show();
    				} catch (JSONException e) {
    	    			Utils.log(e.getMessage());
    	    		}
    			}
    		} catch (JSONException e) {
    			Utils.log(e.getMessage());
    		}  
        }
	}

	
}
