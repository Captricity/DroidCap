package com.example.whiskeydroid;

import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class CapLogin extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Uri data = getIntent().getData();
	    String scheme = data.getScheme(); 
	    Log.w("Caplogin", "Call back received!");
	    Log.w("Login-scheme", scheme);
	    String host = data.getHost(); 
	    Log.w("Login-host", host);
	    List<String> params = data.getPathSegments();
	    for (String s: params) {
	    	Log.w("path:", s);
	    }
	    Log.w("query", data.getQuery());
	    Log.w("Caplogin", "Call back done!");
	}

}
