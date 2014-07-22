package dyl.anjon.es.mole;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


public class Utils {
	
	static final String LOG_TAG = "Mole";
	static final boolean sandbox = false;
	static final String JG_SANDBOX_URL = "http://v3-sandbox.justgiving.com/thebteam-cardiff/donate";
	static final String JG_LIVE_URL = "http://justgiving.com/thebteam-cardiff/donate";
	static final String DOMAIN = "http://dyl.anjon.es/mole";

	public static void log (String string) {
		if ((!sandbox) && (string != null))
			return;
		else 
			Log.i(LOG_TAG, string);
	}
	
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size=1024;
        try {
            byte[] bytes=new byte[buffer_size];
            for(;;) {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception e) {
        	log(e.getMessage());
        }
    }
	
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
		   Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
		   return false;
        }
        return true;
     }

	public static String pluralise(int n, String word) {
		if((n > 1) || (n < 1))
			return n + " " + word + "s";
		return n + " " + word;
	}
	
	
}