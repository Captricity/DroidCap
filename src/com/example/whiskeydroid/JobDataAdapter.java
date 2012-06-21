package com.example.whiskeydroid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
		TextView jobName = (TextView) jobDataView.findViewById(R.id.jobDataName);
		TextView jobIsetCount = (TextView) jobDataView.findViewById(R.id.jobDataInstanceSetCount);
		TextView jobDate = (TextView) jobDataView.findViewById(R.id.jobDataDate);

		//Assign the appropriate data from our alert object above
		jobName.setText(job_data.getName());
		int iset_count  = job_data.getInstanceSetCount();
		if (iset_count == 1) {	
			jobIsetCount.setText("1 page");
		} else {
			jobIsetCount.setText(Integer.toString(iset_count) + " pages");
		}
		
		//"created": "2012-05-29T13:08:37.029",
		SimpleDateFormat fin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try {
			Date creation = fin.parse(job_data.getCreationDate());
			SimpleDateFormat fout = new SimpleDateFormat("MMMMMMMMMM dd, hh:mm aaa");
			jobDate.setText(fout.format(creation));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ImageView done_icon = (ImageView) jobDataView.findViewById(R.id.done_icon);
		TextView done_txt = (TextView) jobDataView.findViewById(R.id.done_text);
		ImageView setup_icon = (ImageView) jobDataView.findViewById(R.id.setup_icon);
		TextView setup_txt = (TextView) jobDataView.findViewById(R.id.setup_text);
		ImageView progress_icon = (ImageView) jobDataView.findViewById(R.id.progress_icon);
		if (job_data.getStatus().equals("completed")) {
			done_icon.setVisibility(View.VISIBLE);
			done_txt.setVisibility(View.VISIBLE);
			setup_icon.setVisibility(View.GONE);
			setup_txt.setVisibility(View.GONE);
			progress_icon.setVisibility(View.GONE);
		} else if (job_data.getStatus().equals("setup")) {
			done_icon.setVisibility(View.GONE);
			done_txt.setVisibility(View.GONE);
			setup_icon.setVisibility(View.VISIBLE);
			setup_txt.setVisibility(View.VISIBLE);
			progress_icon.setVisibility(View.GONE);
		} else {
			done_icon.setVisibility(View.GONE);
			done_txt.setVisibility(View.GONE);
			setup_icon.setVisibility(View.GONE);
			setup_txt.setVisibility(View.GONE);
			progress_icon.setVisibility(View.VISIBLE);
		}
		
		ImageView img = (ImageView) jobDataView.findViewById(R.id.image_preview);
		if (job_data.getId() == 1476) {
			img.setImageResource(R.drawable.thumb1476);
		} else if (job_data.getDocumentName().contains("School")) {
			img.setImageResource(R.drawable.thumb1476);
		} else if (job_data.getId() == 1478) { 
			img.setImageResource(R.drawable.thumb1478);
		} else if (job_data.getId() == 1479) { 
			img.setImageResource(R.drawable.thumb1479);
		} else {
			img.setImageResource(R.drawable.aidfsurveythumb);
		}
		
		return jobDataView;
	}

}