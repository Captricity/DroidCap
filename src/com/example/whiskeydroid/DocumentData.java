package com.example.whiskeydroid;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentData implements Parcelable {
	private String name;
	private int id;
	private int sheet_count;
	private String conversion_status;
	private boolean is_frozen;
	private String created;
	private String modified; 
	
	public DocumentData(String _name, int _id) {
		name = _name;
		id = _id;
	}
	
	public DocumentData(JSONObject json) {
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
	
	private DocumentData(Parcel in) {
		name = in.readString();
		id = in.readInt();
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

	public static final Parcelable.Creator<DocumentData> CREATOR
     		= new Parcelable.Creator<DocumentData>() {
		 			public DocumentData createFromParcel(Parcel in) {
		 					return new DocumentData(in);
		 			}

					public DocumentData[] newArray(int size) {
						return new DocumentData[size];
					}
	 }; 
}
