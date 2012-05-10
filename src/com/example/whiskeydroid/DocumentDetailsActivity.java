package com.example.whiskeydroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    	addImagesButton();
    	launchJobButton();
    	Bundle extras = getIntent().getExtras();
    	DocumentData data = null;
    	if (extras != null) {
    		data = extras.getParcelable(document_data_key);
    	}
    	Log.w("DDA", "got details with doc id " + data.getId());
    	
    	final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.docDetails);
	    intent.putExtra(QueryCaptricityAPI.docIdKey, data.getId());
	    startService(intent);
	}
	
	private void addImagesButton() {
    	Button add_images_button = (Button) findViewById(R.id.add_images_button);
    	add_images_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent addImagesIntent = new Intent(v.getContext(), AddImagesActivity.class);
            	startActivity(addImagesIntent);
            }
        });
     }
	
	private void launchJobButton() {
    	Button launch_job_button = (Button) findViewById(R.id.launch_job_button);
    	launch_job_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent launchJobIntent = new Intent(v.getContext(), LaunchJobActivity.class);
            	startActivity(launchJobIntent);
            }
        });
     }
	
	private void updateDocumentDisplay(DocumentData doc) {
		((TextView) findViewById(R.id.text_name)).setText(doc.getName());
		((TextView) findViewById(R.id.text_doc_id)).setText(Integer.toString(doc.getId()));
		((TextView) findViewById(R.id.text_created)).setText(doc.getCreated());
		((TextView) findViewById(R.id.text_modified)).setText(doc.getModified());
		((TextView) findViewById(R.id.text_sheet_count)).setText(Integer.toString(doc.getSheetCount()));
		((TextView) findViewById(R.id.text_conversion_status)).setText(doc.getConversionStatus());
		((TextView) findViewById(R.id.text_is_frozen)).setText(Boolean.toString(doc.getIsFrozen()));
		((TextView) findViewById(R.id.text_job_count)).setText(Integer.toString(doc.getJobCount()));
		((TextView) findViewById(R.id.text_pending_isets)).setText(Integer.toString(doc.getPendingISets()));
		
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == QueryCaptricityAPI.FINISHED) {
			DocumentData doc = resultData.getParcelable(QueryCaptricityAPI.resultKey);
			updateDocumentDisplay(doc);
		}
	}

}
