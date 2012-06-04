package com.example.whiskeydroid;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class JobDetailsActivity extends Activity implements CaptricityResultReceiver.Receiver {
	public static final String job_data_key = "document_data";
	private static String path_to_photo;
	public CaptricityResultReceiver mReceiver;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_PHOTO = 213;
	private JobData job;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jobdetails);
		setJob();
		displayJobData();
		createTakePhotoButton();
		createPickPhotoButton();
		createCancelButton();
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

	private void createTakePhotoButton() {
		Button add_images_button = (Button) findViewById(R.id.photo_button);
		add_images_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				takePhoto();
			}
		});
	}

	private void createPickPhotoButton() {
		Button pick_images_button = (Button) findViewById(R.id.browse_button);
		pick_images_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pickImage();
			}
		});
	}
	
	private void createCancelButton() {
		Button add_images_button = (Button) findViewById(R.id.cancel_button);
		add_images_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
        	postImageToServer();
        } else if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path_to_photo = cursor.getString(columnIndex);
            cursor.close();
            postImageToServer();
        }
    }
  
    private void postImageToServer() {
    	if (job == null) {
    		return;
    	}
    	final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.postPhotoCommand);
	    intent.putExtra(QueryCaptricityAPI.jobIdKey, job.getId());
	    intent.putExtra(QueryCaptricityAPI.photoPathKey, path_to_photo);
	    startService(intent);   	
    }
 
	
	private boolean canUploadToJob() {
		boolean success = true;
		String message = "Internal Error: Something went wrong!";
		
		if (job == null) {
			success = false;
		}
	    if (job.getSheetCount() != 1) {
	    	message = "Upload to multipage documents not currently supported!";
    		success = false;
    	}
	    if (! job.getStatus().equals("setup")) {
	    	message = "Job must be in 'setup' status to receive uploads!";
	    	success = false;
	    }
	    if (! success) {
	    	String title = "Cannot Upload To This Job!";
			new AlertDialog.Builder(this).setMessage(message)
    		.setTitle(title)
    		.setCancelable(true)  
    		.setNeutralButton(android.R.string.ok,  
    				new DialogInterface.OnClickListener() {  
    					public void onClick(DialogInterface dialog, int whichButton){
    						dialog.dismiss();
    					}  
    				})  
    		.show();    	
	    }
	    return success;
	}
	
    private void takePhoto() {
    	if (! canUploadToJob()) {
    		return;
    	}

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);            
     }
    
 	private static Uri getOutputMediaFileUri() {
		File output = QueryCaptricityAPI.getMediaFile();
	    path_to_photo = output.getAbsolutePath(); 
	    return Uri.fromFile(output);
	}
   
     private void pickImage() {
    	if (! canUploadToJob()) {
    		return;
    	}
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO);            
     }

	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == QueryCaptricityAPI.INSTANCE_POST_FINISHED) {
			new AlertDialog.Builder(this).setMessage("Your form has been successfully uploaded!")
    		.setTitle("Form Uploaded")
    		.setCancelable(true)  
    		.setNeutralButton(android.R.string.ok,  
    				new DialogInterface.OnClickListener() {  
    					public void onClick(DialogInterface dialog, int whichButton){
    						dialog.dismiss();
    					}  
    				})  
    		.show();    			
			job.setInstanceSetCount(job.getInstanceSetCount() + 1);
			displayJobData();
		}	
	}
 

}
