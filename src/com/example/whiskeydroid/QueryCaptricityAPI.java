package com.example.whiskeydroid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class QueryCaptricityAPI extends IntentService {
	public static final int RUNNING = 0;
	public static final int FINISHED = 1;
	public static final int ERROR = 2;	
	public static final int DOC_DATA_FINISHED = 3;	
	public static final int INSTANCE_POST_FINISHED = 4;	
	public static final String api_base_url = "https://nightly.captricity.com/api/";
	public static final String api_user_agent = "nick-android-app-v0-0.1";
	public static final String api_auth_token = "db5fa1b05d17441191a921c390d5d34c";
	public static final String api_version = "0.01b";
	public static final String resultKey = "results";
	public static final String receiverKey = "receiver";
	public static final String commandKey = "command";
	public static final String listDocs = "listdocs" ;
	public static final String docDetails = "docdetails" ;
	public static final String postPhoto = "postphoto" ;
	public static final String docIdKey = "docid" ;
 	public static final String jobIdKey = "jobid";
	public static final String photoPathKey = "photopath";
	
	public QueryCaptricityAPI() {
		super("QueryCaptricityAPI");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final ResultReceiver receiver = intent.getParcelableExtra(receiverKey);
		String command = intent.getStringExtra(commandKey);
		Bundle b = new Bundle();
		if (command.equals(listDocs)) {
			receiver.send(RUNNING, Bundle.EMPTY);
			try {
				ArrayList<DocumentData> results = getDocumentDataList();
				b.putParcelableArrayList(resultKey, results);
				receiver.send(FINISHED, b);
			} catch(Exception e) {
				b.putString(Intent.EXTRA_TEXT, e.toString());
				receiver.send(ERROR, b);
			}    
		} else if (command.equals(docDetails)) {
			receiver.send(RUNNING, Bundle.EMPTY);
			int doc_id = intent.getIntExtra(docIdKey, 0);
			DocumentData doc = getDocumentDetails(doc_id);
			b.putParcelable(resultKey, doc);
			receiver.send(DOC_DATA_FINISHED, b);
		} else if (command.equals(postPhoto)) {
			int job_id = intent.getIntExtra(jobIdKey, 0);
			String path_to_photo = intent.getStringExtra(photoPathKey);
			String result = postImageToServer(job_id, path_to_photo);
			Log.w("NICK", result);
			receiver.send(INSTANCE_POST_FINISHED, b);
		}
		this.stopSelf();
	}

	/* http://stackoverflow.com/questions/2935946/sending-images-using-http-post */
    private String postImageToServer(int job_id, String path_to_photo) {
    	String url = "shreddr/job/" + Integer.toString(job_id);
    	HttpPost api_post = createAPIPost(url);
    	MultipartEntity multipart = new MultipartEntity();
    	multipart.addPart("images", new FileBody(new File(path_to_photo)));
    	api_post.setEntity(multipart);
    	return executeAPICall(api_post);
    	/*
		//http://blog.sptechnolab.com/2011/03/09/android/android-upload-image-to-server/
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
    		httpclient.execute(httppost);
    	} catch(Exception e) {
    		displayPostFailAlert(post_url);
    		e.printStackTrace();
    	}
    	*/
    }

	

	/* http://blog.sptechnolab.com/2011/03/09/android/android-upload-image-to-server/ */
    private ArrayList<JobData> getJobDataList() {
    	ArrayList<JobData> jobs = new ArrayList<JobData>();
    	JSONArray job_list = getJSONArrayFromURL("shreddr/job/");
    	for (int i = 0; i < job_list.length(); i++) {
			try {
				JSONObject json_job = job_list.getJSONObject(i);
				jobs.add(new JobData(json_job));
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	return jobs;
    }
    
	/* http://blog.sptechnolab.com/2011/03/09/android/android-upload-image-to-server/ */
    private ArrayList<DocumentData> getDocumentDataList() {
    	ArrayList<DocumentData> documents = new ArrayList<DocumentData>();
    	JSONArray document_list = getJSONArrayFromURL("shreddr/document/");
    	for (int i = 0; i < document_list.length(); i++) {
			try {
				JSONObject json_document = document_list.getJSONObject(i);
				documents.add(new DocumentData(json_document));
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	Collections.sort(documents, new Comparator<DocumentData>(){
			public int compare(DocumentData rhs, DocumentData lhs) {
				return -1*(rhs.getId() - lhs.getId());
			}
		});    	
    	return documents;
    }
    
    private DocumentData getDocumentDetails(int doc_id) {
    	String url = "shreddr/document/" + Integer.toString(doc_id);
		JSONObject json_doc = getJSONObjectFromURL(url);
		DocumentData doc = new DocumentData(json_doc);
		ArrayList<JobData> all_jobs = getJobDataList();
		doc.filterJobs(all_jobs);
		return doc;
    }
   
    private JSONObject getJSONObjectFromURL(String url) {
     	String response = getDataFromURL(url);
    	try {
    		return new JSONObject(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return new JSONObject();
    }
    
    private JSONArray getJSONArrayFromURL(String url) {
    	String response = getDataFromURL(url);
    	try {
			 return new JSONArray(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return new JSONArray();
    }

    private String getDataFromURL(String url) {
    	HttpGet urlget = createAPIGet(url);
     	return executeAPICall(urlget);
    }
    
    private String executeAPICall(HttpRequestBase request) {
    	HttpClient httpclient = new DefaultHttpClient();
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	try {
			return httpclient.execute(request, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "[]";
    }
    
 	private HttpGet createAPIGet(String url) {
	   	HttpGet http_get = new HttpGet(api_base_url + url);
	   	setHeaders(http_get);
    	return http_get;
    }
 	
 	private HttpPost createAPIPost(String url){
 	   	HttpPost http_post = new HttpPost(api_base_url + url);
	   	setHeaders(http_post);
    	return http_post;		
 	}
 	
 	private void setHeaders(HttpRequestBase request) {
     	request.addHeader("Accept", "text/json");
    	request.addHeader("User-Agent", api_user_agent);
    	request.addHeader("X_API_TOKEN", api_auth_token);
    	request.addHeader("X_API_VERSION", api_version);		
 	}
	
}
