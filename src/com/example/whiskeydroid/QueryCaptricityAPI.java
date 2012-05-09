package com.example.whiskeydroid;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

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
	            	ArrayList<String> results = getDocumentList();
	            	b.putStringArrayList("results", results);
	                receiver.send(FINISHED, b);
	            } catch(Exception e) {
	                b.putString(Intent.EXTRA_TEXT, e.toString());
	                receiver.send(ERROR, b);
	            }    
	        }
	        this.stopSelf();
		
	}
	
	/* http://blog.sptechnolab.com/2011/03/09/android/android-upload-image-to-server/ */
    private ArrayList<String> getDocumentList() {
    	//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	//nameValuePairs.add(new BasicNameValuePair("image", ba1));
    	ArrayList<String> document_names = new ArrayList<String>();
    	String get_url = "https://nightly.captricity.com/api/shreddr/document/";
    	try{
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpGet document_list_get = new HttpGet(get_url);
    		document_list_get.addHeader("Accept", "text/json");
    		document_list_get.addHeader("User-Agent", "nick-android-app-v0-0.1");
    		document_list_get.addHeader("X_API_TOKEN", "db5fa1b05d17441191a921c390d5d34c");
    		document_list_get.addHeader("X_API_VERSION", "0.01b");
    		ResponseHandler<String> responseHandler = new BasicResponseHandler();
    		String response = httpclient.execute(document_list_get, responseHandler);
    		JSONArray document_list = new JSONArray(response);
    		for (int i = 0; i < document_list.length(); i++) {
    			JSONObject document = document_list.getJSONObject(i);
    			String name = document.getString("name");
    			document_names.add(name);
    		}
    		Log.w("GETRESULT", response);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return document_names;
    }


}
