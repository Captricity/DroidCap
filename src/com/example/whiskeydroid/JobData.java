package com.example.whiskeydroid;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class JobData implements Parcelable {
	private String status;
	private String name;
	private int id;
	private int instance_set_count;
	private int sheet_count;
	private String creation_date;
	private int document_id;
	private String document_name;
	
	public JobData(JSONObject json) {
		try {
			status = json.getString("status");
			name = json.getString("name");
			id = json.getInt("id");
			instance_set_count = json.getInt("instance_set_count");
			sheet_count = json.getInt("sheet_count");
			creation_date = json.getString("created");
			JSONObject document = json.getJSONObject("document");
			document_id = document.getInt("id");
			document_name = document.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
	
	public String getName() {
		return name;
	}
	
	public String getCreationDate() {
		return creation_date;
	}
	
	public int getPendingISets() {
		if (status.equals("setup")) {
			return instance_set_count;
		} else {
			return 0;
		}
	}
	
	public int getSheetCount() {
		return sheet_count;
	}
	
	public void debugPrint(String desc) {
		Log.w("JOBDATA debug print", desc);
		Log.w("JOBDATA-status", status);
		Log.w("JOBDATA-name", name);
		Log.w("JOBDATA-id", Integer.toString(id)); 
		Log.w("JOBDATA-isets", Integer.toString(instance_set_count)); 
		Log.w("JOBDATA-doc_id", Integer.toString(document_id)); 
		Log.w("JOBDATA", "Done");
	}

	public boolean canAcceptNewIsets() {
		return status.equals("setup");
	}

	public int describeContents() {
		return 0;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getId() {
		return id;
	}
	
	public int getInstanceSetCount() {
		return instance_set_count;
	}
	
	public int getDocumentId() {
		return document_id;
	}
	
	public String getDocumentName() {
		return document_name;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(status);
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeInt(instance_set_count);
		dest.writeInt(sheet_count);
		dest.writeString(creation_date);
		dest.writeInt(document_id);
		dest.writeString(document_name);
	}
	
	private JobData(Parcel in) {
		status = in.readString();
		name = in.readString();
		id = in.readInt();
		instance_set_count = in.readInt();
		sheet_count = in.readInt();
		creation_date = in.readString();
		document_id = in.readInt();
		document_name = in.readString();
	}
	
	public static final Parcelable.Creator<JobData> CREATOR
		= new Parcelable.Creator<JobData>() {
 			public JobData createFromParcel(Parcel in) {
 					return new JobData (in);
 			}

			public JobData[] newArray(int size) {
				return new JobData[size];
			}
	}; 
}
