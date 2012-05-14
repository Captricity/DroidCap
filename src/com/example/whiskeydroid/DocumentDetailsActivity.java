package com.example.whiskeydroid;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentDetailsActivity extends Activity implements CaptricityResultReceiver.Receiver {
	public static final String document_data_key = "document_data";
	private static String path_to_photo;
	public CaptricityResultReceiver mReceiver;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_PHOTO = 213;
	private DocumentData document;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mReceiver = new CaptricityResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.docdetails);
    	createTakePhotoButton();
    	createPickPhotoButton();
    	createLaunchJobButton();
    	setDocument();
    	getDocumentDetailsFromServer();
	}
	
	private void setDocument() {
     	Bundle extras = getIntent().getExtras();
    	if (extras == null) {
    		document = null;
    	}
    	document = extras.getParcelable(document_data_key);	
    }
	
    private void getDocumentDetailsFromServer() {
    	if (document == null) {
    		return;
    	}
    	final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.docDetails);
	    intent.putExtra(QueryCaptricityAPI.docIdKey, document.getId());
	    startService(intent);   	
    }
	
	private void createTakePhotoButton() {
    	Button add_images_button = (Button) findViewById(R.id.add_images_button);
    	add_images_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	takePhoto();
            }
        });
    }
	
	private void createPickPhotoButton() {
    	Button pick_images_button = (Button) findViewById(R.id.pick_images_button);
    	pick_images_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pickImage();
            }
        });
    }
	
	private void createLaunchJobButton() {
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
		if (resultCode == QueryCaptricityAPI.DOC_DATA_FINISHED) {
			DocumentData doc = resultData.getParcelable(QueryCaptricityAPI.resultKey);
			updateDocumentDisplay(doc);
			this.document = doc;
		} else if (resultCode == QueryCaptricityAPI.INSTANCE_POST_FINISHED) {
			getDocumentDetailsFromServer();
		}
	}
	
	private void showUnsupportedDocumentAlert() {
		new AlertDialog.Builder(this).setMessage(
					"Adding instances to documents with " + Integer.toString(document.getSheetCount()) + " pages is not supported yet!")
    		.setTitle("Instance Add Failed")  
    		.setCancelable(true)  
    		.setNeutralButton(android.R.string.ok,  
    				new DialogInterface.OnClickListener() {  
    					public void onClick(DialogInterface dialog, int whichButton){
    						dialog.dismiss();
    					}  
    				})  
    		.show();
	}
	
	private boolean canUploadPhotoToDocument() {
	    if (document.getSheetCount() != 1) {
    		return false;
    	}	
	    return true;
	}

    private void takePhoto() {
    	if (! canUploadPhotoToDocument()) {
    		showUnsupportedDocumentAlert();
    		return;
    	}

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);            
     }
    
    
     private void pickImage() {
    	if (! canUploadPhotoToDocument()) {
    		showUnsupportedDocumentAlert();
    		return;
    	}
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO);            
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
    	if (document == null) {
    		return;
    	}
    	final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryCaptricityAPI.class);
	    intent.putExtra(QueryCaptricityAPI.receiverKey, mReceiver);
	    intent.putExtra(QueryCaptricityAPI.commandKey, QueryCaptricityAPI.postPhoto);
	    //TODO: may need to create a new job?
	    intent.putExtra(QueryCaptricityAPI.jobIdKey, document.getJobIdToPostTo());
	    intent.putExtra(QueryCaptricityAPI.photoPathKey, path_to_photo);
	    startService(intent);   	
    }
    
	private static Uri getOutputMediaFileUri() {
		File output = getOutputMediaFile();
	    path_to_photo = output.getAbsolutePath(); 
	    return Uri.fromFile(output);
	}

	private static File getOutputMediaFile() {
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "WhiskeyDroid");
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    try {
	    	mediaFile.createNewFile();
		} catch (IOException e) {
			mediaFile = null;
			e.printStackTrace();
		}
	    return mediaFile;
	}
	
	
}
