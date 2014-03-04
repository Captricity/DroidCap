package com.example.whiskeydroid;

import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class CapLogin extends Activity {

	/* This Activity is incomplete, but this is where you handle the callback
	 * from the loginURI (because we registered to handle scheme captricity://
	 * in the manifest).
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Uri data = getIntent().getData();
	    String scheme = data.getScheme(); 
	    Log.w("Caplogin", "Call back received!");
	    Log.w("Login-scheme", scheme); // "captricity" (the scheme you registered to handle)
	    String host = data.getHost();
	    Log.w("Login-host", host); // "logged-in"
	    List<String> params = data.getPathSegments();
	    for (String s: params) {
	    	Log.w("path:", s);
	    }
	    // Pull the user's token from the query and use it to authenticate to perform other API actions
	    Log.w("query", data.getQuery()); // "&token=ABC123&request-granted=true&signature=456DEF"
	    Log.w("Caplogin", "Call back done!");
	}

}
