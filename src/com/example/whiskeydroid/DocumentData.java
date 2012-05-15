package com.example.whiskeydroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DocumentData implements Parcelable {
	private String name;
	private int id;
	private int sheet_count;
	private String conversion_status;
	private boolean is_frozen;
	private String created;
	private String modified; 
	private ArrayList<JobData> jobs;
	
	public DocumentData() {
		jobs = new ArrayList<JobData>();
		
	}
	
	public DocumentData(String _name, int _id) {
		this();
		name = _name;
		id = _id;
	}
	
	public DocumentData(JSONObject json) {
		this();
		try {
			name = json.getString("name");
			id = json.getInt("id");
			setSheetCount(json.getInt("sheet_count"));
			setConversionStatus(json.getString("conversion_status"));
			setIsFrozen(json.getBoolean("is_frozen"));
			setCreated(json.getString("created"));
			setModified(json.getString("modified"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name + " (" + Integer.toString(id) + ")";
	}
	

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeInt(sheet_count);
		dest.writeString(conversion_status);
		dest.writeByte((byte) (is_frozen ? 1 : 0));
		dest.writeString(created);
		dest.writeString(modified);
		dest.writeTypedList(jobs);
	}
	
	private DocumentData(Parcel in) {
		this();
		name = in.readString();
		id = in.readInt();
		sheet_count = in.readInt();
		conversion_status = in.readString();
		is_frozen = in.readByte() == 1;
		created = in.readString();
		modified = in.readString();
		in.readTypedList(jobs, JobData.CREATOR);
	}
	
	public int getSheetCount() {
		return sheet_count;
	}
	
	public void setSheetCount(int sheet_count) {
		this.sheet_count = sheet_count;
	}

	public String getConversionStatus() {
		return conversion_status;
	}

	public void setConversionStatus(String conversion_status) {
		this.conversion_status = conversion_status;
	}

	public boolean getIsFrozen() {
		return is_frozen;
	}

	public void setIsFrozen(boolean is_frozen) {
		this.is_frozen = is_frozen;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public void filterJobs(ArrayList<JobData> all_jobs) {
		for (JobData job:all_jobs) {
			if (job.getDocumentId() == this.id) {
				this.jobs.add(job);
			}
		}
	}
	
	public int getJobCount() {
		return this.jobs.size();
	}
	
	public int getCompletedJobCount() {
		int completed_jobs = 0;
		for (JobData job:jobs) {
			if (job.getStatus().equals("completed")) {
				completed_jobs++;
			}
		}
		return completed_jobs;
	}
	
	public int getPendingISets() {
		int total_isets = 0;
		for (JobData job:jobs) {
			total_isets += job.getPendingISets();
		}
		return total_isets;
	}

	public static final Parcelable.Creator<DocumentData> CREATOR
     		= new Parcelable.Creator<DocumentData>() {
		 			public DocumentData createFromParcel(Parcel in) {
		 					return new DocumentData(in);
		 			}

					public DocumentData[] newArray(int size) {
						return new DocumentData[size];
					}
	 };

	public int getJobIdToPostTo() {
		//Get lowest id job in 'setup' status
		Collections.sort(jobs, new Comparator<JobData>(){
			public int compare(JobData rhs, JobData lhs) {
				return rhs.getId() - lhs.getId();
			}
		});
		for (JobData job:jobs) {
			if (job.canAcceptNewIsets()) {
				return job.getId();
			}
		}
		return 0;
	} 
}
