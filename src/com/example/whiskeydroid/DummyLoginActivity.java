package com.example.whiskeydroid;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.codec.binary.Hex;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;


/* get your flip on
 * http://www.inter-fuser.com/2009/08/android-animations-3d-flip.html
 */
public class DummyLoginActivity extends ListActivity implements CaptricityResultReceiver.Receiver {
	private View login_view;
	private View dark_side;
	private boolean isLoginView = true;
	public CaptricityResultReceiver mReceiver;
	public JobDataAdapter adapter;
	ArrayList<JobData> listItems = new ArrayList<JobData>();
	OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        RadioButton rb = (RadioButton) v;
	        if (rb.getText().equals("Name")) {
	        	Collections.sort(listItems, new Comparator<JobData>(){
	        		public int compare(JobData rhs, JobData lhs) {
	        			return rhs.getName().compareTo(lhs.getName());
	        		}
	        	});    	
         	
	        } else {
		        Collections.sort(listItems, new Comparator<JobData>(){
	        		public int compare(JobData rhs, JobData lhs) {
	        			return -1 * (rhs.getId() - lhs.getId());
	        		}
	        	});          	
	        }
			adapter.notifyDataSetChanged();
	    }
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);
	    
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dummylogin);
		login_view = findViewById(R.id.ll);
		dark_side = findViewById(R.id.dsl);
		dark_side.setVisibility(View.GONE);
		
		  final RadioButton radio_date = (RadioButton) findViewById(R.id.radio0);
		  final RadioButton radio_name = (RadioButton) findViewById(R.id.radio1);
		  radio_date.setOnClickListener(radio_listener);
		  radio_name.setOnClickListener(radio_listener);
	
		createSignInButton();
		
		adapter = new JobDataAdapter(this, R.layout.job_data_item, listItems);
		setListAdapter(adapter);
	    final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.listJobsCommand);
	    startService(intent);
	}
	
	@Override
	public void onBackPressed() {
		if (! isLoginView) {
			applyRotation(0, -90);
			isLoginView = ! isLoginView;
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		JobData job_data = listItems.get(position);
		Intent jobDetailsIntent = new Intent(v.getContext(), JobDetailsActivity.class);
		jobDetailsIntent.putExtra(JobDetailsActivity.job_data_key, job_data);
        startActivity(jobDetailsIntent);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == QueryCaptricityAPI.FINISHED) {
			ArrayList<JobData> results = resultData.getParcelableArrayList(QueryCaptricityAPI.resultKey);
			listItems.clear();
			for (JobData result:results) {
				listItems.add(result);
			}
			adapter.notifyDataSetChanged();
		}
	}

	private void createSignInButton() {
		Button sign_in_button = (Button) findViewById(R.id.sign_in_button);
		sign_in_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				signInReal(v);
			}
		});
	}
	
	private String getSignature(String return_url, String third_party_id) {
		String RETURN_URL_KEY = "return-url";
		String THIRD_PARTY_ID_KEY = "third-party-id";
		/* Fill in your app's "Secret key" here: https://shreddr.captricity.com/developer/ */
		String secret_key_value = "63a676d088ea45168aa6b5426f94d7c7";
		
		String hash_me = secret_key_value + ":" + RETURN_URL_KEY + "=" + URLEncoder.encode(return_url) + "&" + THIRD_PARTY_ID_KEY + "=" + URLEncoder.encode(third_party_id);
		MessageDigest digest = null;
		try {
			digest =  MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		try {
			digest.update(hash_me.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		} 
		return new String(Hex.encodeHex(digest.digest()));
	}

	private Uri generateLoginURI() {  
		String TARGET_BASE_ADDRESS = "shreddr.captricity.com";
		String return_url = "captricity://logged-in?";
		/* Fill in your app's "App ID" here: https://shreddr.captricity.com/developer/ */
		String third_party_id = "31";
		String signature = getSignature(return_url, third_party_id);
		String target = "https://" + TARGET_BASE_ADDRESS + "/accounts/request-access/?" + "return-url=" + URLEncoder.encode(return_url) + "&third-party-id=" + third_party_id + "&signature=" + signature;
		Log.w("NICK", target);
		return Uri.parse(target);
	}
	private void signInReal(View v) {
		Uri login_uri = generateLoginURI();
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, login_uri); 
		startActivity(launchBrowser);
		overridePendingTransition(R.anim.rotate_in,R.anim.rotate_out);	
	}
	
	// http://stackoverflow.com/questions/7853997/android-rotate-animation-between-two-activity
	private void signInDummy(View v) {
		if (isLoginView) {
			applyRotation(0, 90);
		} else {
			applyRotation(0, -90);
		}
		isLoginView = !isLoginView;		
	}

	private void applyRotation(float start, float end) {
		// Find the center of image
		final float centerX = login_view.getWidth() / 2.0f;
		final float centerY = login_view.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Flip3dAnimation rotation = new Flip3dAnimation(start, end, centerX, centerY);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(isLoginView, login_view, dark_side));

		if (isLoginView) {
			login_view.startAnimation(rotation);
		} else {
			dark_side.startAnimation(rotation);
		}

	}			

}
