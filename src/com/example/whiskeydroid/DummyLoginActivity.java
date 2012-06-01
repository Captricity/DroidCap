package com.example.whiskeydroid;

import java.util.ArrayList;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


/* get your flip on
 * http://www.inter-fuser.com/2009/08/android-animations-3d-flip.html
 */
public class DummyLoginActivity extends ListActivity implements CaptricityResultReceiver.Receiver {
	private View login_view;
	private View dark_side;
	private boolean isLoginView = true;
	public CaptricityResultReceiver mReceiver;
	public ArrayAdapter<DocumentData> adapter;
	ArrayList<DocumentData> listItems = new ArrayList<DocumentData>();

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
	
		createSignInButton();
		
		adapter = new ArrayAdapter<DocumentData>(this, android.R.layout.simple_list_item_1, listItems);
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

	private void createSignInButton() {
		Button sign_in_button = (Button) findViewById(R.id.sign_in_button);
		sign_in_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				signIn(v);
			}
		});
	}

	// http://stackoverflow.com/questions/7853997/android-rotate-animation-between-two-activity
	private void signIn(View v) {
		//Intent listDocIntent = new Intent(v.getContext(), ListDocumentsActivity.class);
		//startActivity(listDocIntent);
		//overridePendingTransition(R.anim.rotate_in,R.anim.rotate_out);
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
		if (dark_side == null) {
			Log.w("NICK", "FUCK");
		} else {
			Log.w("NICK", "yay");
		}
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
