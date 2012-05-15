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
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.listDocsCommand);
	    startService(intent);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		DocumentData item = listItems.get(position);
		Log.w("LDA", "You clicked " + item + "!");
		Intent docDetailsIntent = new Intent(v.getContext(), DocumentDetailsActivity.class);
		docDetailsIntent.putExtra(DocumentDetailsActivity.document_data_key, item);
        startActivity(docDetailsIntent);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == QueryCaptricityAPI.FINISHED) {
			ArrayList<DocumentData> results = resultData.getParcelableArrayList(QueryCaptricityAPI.resultKey);
			listItems.clear();
			for (DocumentData result:results) {
				listItems.add(result);
			}
			adapter.notifyDataSetChanged();
		}
	}
}
