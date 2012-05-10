package com.example.whiskeydroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class DocumentDetailsActivity extends Activity implements CaptricityResultReceiver.Receiver {
	public static final String document_data_key = "document_data";
	public CaptricityResultReceiver mReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);	    
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.docdetails);
    	Bundle extras = getIntent().getExtras();
    	DocumentData data = null;
    	if (extras != null) {
    		data = extras.getParcelable(document_data_key);
    	}
    	Log.w("DDA", "got details with doc id " + data.getId());
    	
    	final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra("receiver", mReceiver);
	    intent.putExtra("command", "doc_query");
	    intent.putExtra("document_id", data.getId());
	    startService(intent);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case QueryCaptricityAPI.RUNNING:
			break;
		case QueryCaptricityAPI.FINISHED:
			DocumentData doc = resultData.getParcelable("results");
			((TextView) findViewById(R.id.text_name)).setText(doc.getName());
			((TextView) findViewById(R.id.text_doc_id)).setText(Integer.toString(doc.getId()));
			((TextView) findViewById(R.id.text_created)).setText(doc.getCreated());
			((TextView) findViewById(R.id.text_modified)).setText(doc.getModified());
			((TextView) findViewById(R.id.text_sheet_count)).setText(Integer.toString(doc.getSheetCount()));
			((TextView) findViewById(R.id.text_conversion_status)).setText(doc.getConversionStatus());
			((TextView) findViewById(R.id.text_is_frozen)).setText(Boolean.toString(doc.getIsFrozen()));
			
			break;
		case QueryCaptricityAPI.ERROR:
			break;
		}
	}

}
