package com.example.whiskeydroid;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListDocumentsActivity extends ListActivity implements CaptricityResultReceiver.Receiver {
	
	public CaptricityResultReceiver mReceiver;
	public ArrayAdapter<DocumentData> adapter;
	ArrayList<DocumentData> listItems = new ArrayList<DocumentData>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);
	    
		super.onCreate(savedInstanceState);
		
		adapter = new ArrayAdapter<DocumentData>(this, R.layout.listdocs, listItems);
		setListAdapter(adapter);
	    final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra("receiver", mReceiver);
	    intent.putExtra("command", "query");
	    startService(intent);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		DocumentData item = listItems.get(position);
		Log.w("LDA", "You clicked " + item + "!");
		Intent docDetailsIntent = new Intent(v.getContext(), DocumentDetailsActivity.class);
		// TODO: doc id
		docDetailsIntent.putExtra(DocumentDetailsActivity.document_data_key, item);
        startActivity(docDetailsIntent);
	}

	/*
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String doc_name = adapter.getItem(info.position);
		Log.w("LDA", "TOUCHED " + doc_name);
        Toast.makeText(this, "clicked " + doc_name, Toast.LENGTH_LONG).show();
        return true;
	}
	*/

	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case QueryCaptricityAPI.RUNNING:
			Log.i("LDA", "RUNNING");
			break;
		case QueryCaptricityAPI.FINISHED:
			Log.i("LDA", "FINISHED");
			ArrayList<DocumentData> results = resultData.getParcelableArrayList("results");
			listItems.clear();
			for (DocumentData result:results) {
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
