package com.example.whiskeydroid;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class SpotCheckActivity extends Activity implements CaptricityResultReceiver.Receiver {
	private DocumentData document;
	private ImageView image;
	private TextView best_estimate;
	public CaptricityResultReceiver mReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.spotcheck);
	    setDocument();
		image = (ImageView) findViewById(R.id.shred_view);
		best_estimate = (TextView) findViewById(R.id.best_est_view);
		if (documentPassesSanityChecks()) {
			getImage();
		}
	}
	
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == QueryCaptricityAPI.SHRED_RECEIVED) {
			String image_path = resultData.getString(QueryCaptricityAPI.shredImagePathKey);
			File image_file = new File(image_path);
			image.setImageURI(Uri.fromFile(image_file));
		}
	}
	
	private void getImage() {
    	final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.getShredImageCommand);
	    intent.putExtra(QueryCaptricityAPI.shredIdKey, 2047);
	    startService(intent);
	}
	
	private boolean documentPassesSanityChecks() {
		if (document == null) {
			showSanityCheckFailAlert("Could not find document!");
			return false;
		}
		if (document.getCompletedJobCount() == 0) {
			showSanityCheckFailAlert("Document has no completed jobs!");
			return false;
		}
		return true;
	}
	
	private void showSanityCheckFailAlert(String desc) {
		new AlertDialog.Builder(this).setMessage(desc)
    		.setTitle("Cannot proceed with data spot check!")  
    		.setCancelable(true)  
    		.setNeutralButton(android.R.string.ok,  
    				new DialogInterface.OnClickListener() {  
    					public void onClick(DialogInterface dialog, int whichButton){
    						dialog.dismiss();
    					}  
    				})  
    		.show();
	}
	
	
	private void setDocument() {
     	Bundle extras = getIntent().getExtras();
    	if (extras == null) {
    		document = null;
    	}
    	document = extras.getParcelable(DocumentDetailsActivity.document_data_key);	
    }
	
}
