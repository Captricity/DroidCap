package com.example.whiskeydroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	createPostButton();
    	createDocButton();
    }
    
	private static String path_to_photo = null;
	

	private static Button post_button;
	private static Button doc_button;
    
   
    private void createPostButton() {   
      	post_button = (Button) findViewById(R.id.post_button);
    	post_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (path_to_photo == null) {
            		displayNoPhotoAlert();
            		return;
            	}
            }
        });     	
    }
    
    private void createDocButton() {   
      	doc_button = (Button) findViewById(R.id.list_doc_button);
    	doc_button .setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent listDocIntent = new Intent(v.getContext(), ListDocumentsActivity.class);
            	startActivity(listDocIntent);
            		
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
   
    private void setThumbnailImage() {
    	if (path_to_photo == null) {
    		return;
    	}
    	ImageView ImagePreview = (ImageView) findViewById(R.id.image_preview);
    	
    	Bitmap full = BitmapFactory.decodeFile(path_to_photo);
    	Bitmap scaled = Bitmap.createScaledBitmap(full, 100, 100, false);
    	ImagePreview.setImageBitmap(scaled);
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

   
}