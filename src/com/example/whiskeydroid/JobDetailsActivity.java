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
    	TextView job_name = (TextView) findViewById(R.id.job_details_name);
    	job_name.setText(job.getName());
	}
	
	private void setJob() {
     	Bundle extras = getIntent().getExtras();
    	if (extras != null) {
    		job = extras.getParcelable(job_data_key);	
    	}
    }
	
}
