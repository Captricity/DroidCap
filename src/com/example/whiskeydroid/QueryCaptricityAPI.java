package com.example.whiskeydroid;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class QueryCaptricityAPI extends IntentService {
	public static final int RUNNING = 0;
	public static final int FINISHED = 1;
	public static final int ERROR = 2;	
	
	public QueryCaptricityAPI() {
		super("QueryCaptricityAPI");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("LDA", "IN QUERY");
		 final ResultReceiver receiver = intent.getParcelableExtra("receiver");
	        String command = intent.getStringExtra("command");
	        Bundle b = new Bundle();
	        if (command.equals("query")) {
	            receiver.send(RUNNING, Bundle.EMPTY);
	            try {
	                // get some data or something           
	            	ArrayList<String> results = new ArrayList<String>();
	            	results.add("Doc1");
	            	results.add("Doc2");
	            	results.add("Doc3");
	            	b.putStringArrayList("results", results);
	                receiver.send(FINISHED, b);
	            } catch(Exception e) {
	                b.putString(Intent.EXTRA_TEXT, e.toString());
	                receiver.send(ERROR, b);
	            }    
	        }
	        this.stopSelf();
		
	}

}
