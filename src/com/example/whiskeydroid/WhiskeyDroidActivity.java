package com.example.whiskeydroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/** Mostly from http://developer.android.com/guide/topics/media/camera.html#intent-receive **/

public class WhiskeyDroidActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static String path_to_photo = null;
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri() {
		File output = getOutputMediaFile();
	    path_to_photo = output.getAbsolutePath(); 
	    return Uri.fromFile(output);
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile() {
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    try {
	    	mediaFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    Log.i("MyCameraApp", "IMAGE DIR:"+ mediaFile.getAbsolutePath());
	    return mediaFile;
	}
	
	private static Button photo_button;
	private static Button post_button;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	createPhotoButton();
    	createPostButton();
    }
    
    private void createPhotoButton() {
    	photo_button = (Button) findViewById(R.id.photo_button);
    	photo_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	takePicture();
            }
        });
     }
    
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);            
     }
    
    private void createPostButton() {   
      	post_button = (Button) findViewById(R.id.post_button);
    	post_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (path_to_photo == null) {
            		displayNoPhotoAlert();
            		return;
            	}
            	postImageToServer();
            		
            }
        });     	
    }
    
    private void displayNoPhotoAlert() {
	   new AlertDialog.Builder(this).setMessage("You haven't taken a photo to post yet!")  
       .setTitle("No Photo To Post")  
       .setCancelable(true)  
       .setNeutralButton(android.R.string.ok,  
          new DialogInterface.OnClickListener() {  
          public void onClick(DialogInterface dialog, int whichButton){
        	  dialog.dismiss();
          }  
          })  
       .show(); 
   }
   
    //TODO image takes up too much memory
    private void setThumbnailImage() {
    	if (path_to_photo == null) {
    		return;
    	}
    	ImageView ImagePreview = (ImageView) findViewById(R.id.image_preview);
    	
    	Bitmap full = BitmapFactory.decodeFile(path_to_photo);
    	Bitmap scaled = Bitmap.createScaledBitmap(full, 100, 100, false);
    	ImagePreview.setImageBitmap(scaled);
    }
   
    /* http://blog.sptechnolab.com/2011/03/09/android/android-upload-image-to-server/ */
    private void postImageToServer() {
    	Bitmap full = BitmapFactory.decodeFile(path_to_photo);
    	Bitmap scaled = Bitmap.createScaledBitmap(full, 500, 500, false);
    	ByteArrayOutputStream bao = new ByteArrayOutputStream();
    	scaled.compress(Bitmap.CompressFormat.JPEG, 90, bao);
    	byte [] ba = bao.toByteArray();
    	String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("image", ba1));
    	EditText url_text_widget = (EditText) findViewById(R.id.url_text);
    	String post_url = url_text_widget.getText().toString();
    	post_url = "http://192.168.2.25:8000/staff/nick-photo-dump/";
    	Log.w("a", "b");
    	try{
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost(post_url);
    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		HttpResponse response = httpclient.execute(httppost);
    		HttpEntity entity = response.getEntity();
    		InputStream is = entity.getContent();
    	} catch(Exception e) {
    		displayPostFailAlert(post_url);
    		e.printStackTrace();
    	}
    }

    private void displayPostFailAlert(String post_url) {
    	new AlertDialog.Builder(this).setMessage("Post to url " + post_url + " failed")
    	.setTitle("Image Post Failed")  
    	.setCancelable(true)  
    	.setNeutralButton(android.R.string.ok,  
          new DialogInterface.OnClickListener() {  
          public void onClick(DialogInterface dialog, int whichButton){
        	  dialog.dismiss();
          }  
          })  
       .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	/** data always come back null; perhaps http://stackoverflow.com/questions/1910608/android-action-image-capture-intent/1932268#1932268 **/
                // Image captured and saved to fileUri specified in the Intent
            	setThumbnailImage();
            	Toast.makeText(this, "Image saved to:\n" + path_to_photo, Toast.LENGTH_LONG).show();
   
            } else if (resultCode == RESULT_CANCELED) {
            	Toast.makeText(this, "You cancelled dummy", Toast.LENGTH_LONG).show();
            } else {
                // Image capture failed, advise user
            	Toast.makeText(this, "Something exploded and it's probably your fault, dummy", Toast.LENGTH_LONG).show();
            }
        }

    }
    
}