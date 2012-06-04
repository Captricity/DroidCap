package com.example.whiskeydroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class JobDetailsActivity extends Activity {
	public static final String job_data_key = "document_data";
	private JobData job;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.jobdetails);
	    setJob();
	    displayJobData();
	}
	
	private void displayJobData() {
    	TextView job_name = (TextView) findViewById(R.id.job_details_name);
    	job_name.setText(job.getName());
    	
	    TextView document_name = (TextView) findViewById(R.id.document_name);
    	String doc_name = job.getDocumentName();
    	if (doc_name.length() > 16) {
    		doc_name = doc_name.substring(0, 16);
    	}
    	document_name.setText(doc_name);
	    
    	TextView sheet_count = (TextView) findViewById(R.id.sheet_count);
    	sheet_count.setText(Integer.toString(job.getSheetCount()));
    	
    	TextView iset_count = (TextView) findViewById(R.id.iset_count);
    	iset_count.setText(Integer.toString(job.getInstanceSetCount()));
  }
	
	private void setJob() {
     	Bundle extras = getIntent().getExtras();
    	if (extras != null) {
    		job = extras.getParcelable(job_data_key);	
    	}
    }
	
}
