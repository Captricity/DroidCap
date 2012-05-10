package com.example.whiskeydroid;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
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
	public static final String api_base_url = "https://nightly.captricity.com/api/";
	public static final String api_user_agent = "nick-android-app-v0-0.1";
	public static final String api_auth_token = "db5fa1b05d17441191a921c390d5d34c";
	public static final String api_version = "0.01b";
	public static final String resultKey = "results";
	public static final String receiverKey = "receiver";
	public static final String commandKey = "command";
	public static final String listDocs = "listdocs" ;
	public static final String docDetails = "docdetails" ;
	public static final String docIdKey = "docid" ;
 
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
			receiver.send(FINISHED, b);
		}
		this.stopSelf();
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
    
    private ArrayList<JSONObject> getJobList() {
    	ArrayList<JSONObject> jobs = new ArrayList<JSONObject>();
    	return jobs;
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
     	try{
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpGet urlget = createAPIGet(url);
    		ResponseHandler<String> responseHandler = new BasicResponseHandler();
    		return httpclient.execute(urlget, responseHandler);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
     	return "[]";
    }
    
 	private HttpGet createAPIGet(String url) {
	   	HttpGet http_get = new HttpGet(api_base_url + url);
    	http_get.addHeader("Accept", "text/json");
    	http_get.addHeader("User-Agent", api_user_agent);
    	http_get.addHeader("X_API_TOKEN", api_auth_token);
    	http_get.addHeader("X_API_VERSION", api_version);
    	return http_get;
    }
	
}
