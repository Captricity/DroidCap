package com.example.whiskeydroid;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ListDocumentsActivity extends ListActivity implements CaptricityResultReceiver.Receiver {
	
	public CaptricityResultReceiver mReceiver;
	public ArrayAdapter<String> adapter;
	ArrayList<String> listItems = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);
	    
		super.onCreate(savedInstanceState);
		adapter = new ArrayAdapter<String>(this, R.layout.listdocs, listItems);
		setListAdapter(adapter);
	    final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra("receiver", mReceiver);
	    intent.putExtra("command", "query");
	    startService(intent);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case QueryCaptricityAPI.RUNNING:
			Log.i("LDA", "RUNNING");
			break;
		case QueryCaptricityAPI.FINISHED:
			Log.i("LDA", "FINISHED");
			ArrayList<String> results = resultData.getStringArrayList("results");
			listItems.clear();
			for (String result:results) {
				Log.i("LDA", "Adding item" + result);
				listItems.add(result);
			}
			adapter.notifyDataSetChanged();
			break;
		case QueryCaptricityAPI.ERROR:
			Log.i("LDA", "ERROR");
			break;
		}
	}
}
