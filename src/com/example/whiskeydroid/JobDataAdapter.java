package com.example.whiskeydroid;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * http://www.josecgomez.com/2010/05/03/android-putting-custom-objects-in-listview/
 */
public class JobDataAdapter extends ArrayAdapter<JobData> {

	int resource;
	String response;
	Context context;
	
	public JobDataAdapter(Context context, int resource, List<JobData> items) {
		super(context, resource, items);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout jobDataView;
		JobData job_data = getItem(position);

		if(convertView == null) {
			jobDataView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, jobDataView, true);
		} else {
			jobDataView = (LinearLayout) convertView;
		}
		//Get the text boxes from the listitem.xml file
		TextView jobID = (TextView) jobDataView.findViewById(R.id.jobDataID);
		TextView jobName = (TextView) jobDataView.findViewById(R.id.jobDataName);

		//Assign the appropriate data from our alert object above
		jobID.setText(Integer.toString(job_data.getId()));
		jobName.setText(job_data.getName());
		return jobDataView;
	}

}