package com.example.whiskeydroid;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class JobData implements Parcelable {
	private String status;
	private String name;
	private int id;
	private int instance_set_count;
	private int document_id;
	
	public JobData(JSONObject json) {
		try {
			status = json.getString("status");
			name = json.getString("name");
			id = json.getInt("id");
			instance_set_count = json.getInt("instance_set_count");
			JSONObject document = json.getJSONObject("document");
			document_id = document.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
	
	public int getPendingISets() {
		if (status.equals("setup")) {
			return instance_set_count;
		} else {
			return 0;
		}
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

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(status);
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeInt(instance_set_count);
		dest.writeInt(document_id);
	}
	
	private JobData(Parcel in) {
		status = in.readString();
		name = in.readString();
		id = in.readInt();
		instance_set_count = in.readInt();
		document_id = in.readInt();
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
