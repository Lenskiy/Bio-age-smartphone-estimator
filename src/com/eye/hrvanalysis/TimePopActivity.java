package com.eye.hrvanalysis;

import com.eye.visualization.LineGraph;
import com.eye.visualization.MyDbAdapter;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimePopActivity extends Activity {
	private GraphicalView viewHRV, viewFilt;
	private LineGraph lineHRV;
	private LineGraph lineFilt;
	private MyDbAdapter mda;
	private String beatRate, date, hrv, filt, time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("oncreate", "timepopac start!");
		DisplayMetrics dm = getResources().getDisplayMetrics();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


		mda = new MyDbAdapter(this);
		time = getIntent().getStringExtra("time");
		beatRate = getIntent().getStringExtra("beatRate");
		date = getIntent().getStringExtra("date");
		hrv = getIntent().getStringExtra("hrv");
		filt= getIntent().getStringExtra("filt");
		
		//lineHRV = new LineGraph("HRV Graph", time);
		//lineFilt = new LineGraph("filt Graph", time);
		
//		ArrayList<Integer> hrvAl = new ArrayList<Integer>();		// draw hrv
//		String[] str=hrv.split("\n");
//		for(int i=0;i<str.length;i++)
//			hrvAl.add((int)Integer.parseInt(str[i]));
//		lineHRV.addNewPoints(hrvAl);
		
		
//		ArrayList<Double> filtAl = new ArrayList<Double>();			// draw filt
//		String[] str1=filt.split("\n");
//		for(int i=0;i<str1.length;i++)
//			filtAl.add(Double.parseDouble(str1[i]));
//		lineFilt.addNewPoints(filtAl, false);
		
		
		RelativeLayout layout = new RelativeLayout(this);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		layout.setBackgroundColor(Color.BLACK);
		RelativeLayout.LayoutParams gvParam = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		gvParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.setLayoutParams(gvParam);
		
		
		TextView tv = new TextView(this); 					// TextView "heartbeat rate"
		tv.setId(1);
		tv.setGravity(Gravity.CENTER);
		tv.setText("Date: "+date+"\n<Your heart rate is "+beatRate+">" );
		int pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,7, dm);
		tv.setTextSize(pixel1);
		tv.setBackgroundResource(R.drawable.back2);
		tv.setTextColor(Color.rgb(255, 255, 255));
		RelativeLayout.LayoutParams tvParam = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tvParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10, dm);
		tv.setPadding(0, pixel1, 0, pixel1);
		tv.setLayoutParams(tvParam);
		
		
		/* */
//		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,195, dm);
//		viewFilt = lineFilt.getView(this);
//		viewFilt.setId(2);
//		RelativeLayout.LayoutParams filtParam = new RelativeLayout.LayoutParams(
//				ViewGroup.LayoutParams.WRAP_CONTENT, pixel1);
//		filtParam.addRule(RelativeLayout.BELOW, 1);
//		viewFilt.setLayoutParams(filtParam);
		
//		viewHRV = lineHRV.getView(this);
//		viewHRV.setId(3);
//		RelativeLayout.LayoutParams HRVParam = new RelativeLayout.LayoutParams(
//				ViewGroup.LayoutParams.WRAP_CONTENT, pixel1);
//		HRVParam.addRule(RelativeLayout.BELOW, 2);
//		viewHRV.setLayoutParams(HRVParam);
		
		
//		Button btn = new Button(this); // Button
//		btn.setId(4);
//		btn.setText("     OK     ");
//		RelativeLayout.LayoutParams btnParam = new RelativeLayout.LayoutParams(
//				ViewGroup.LayoutParams.WRAP_CONTENT,
//				ViewGroup.LayoutParams.WRAP_CONTENT);
//		btnParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		btnParam.addRule(RelativeLayout.BELOW, 3);
//		btnParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		
//		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, dm);
//		btnParam.setMargins(pixel1*(pixel1/2), pixel1, pixel1, pixel1);
//		btn.setLayoutParams(btnParam);
//		
//		btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
		
		
		layout.addView(tv);
		
		/* */
		//layout.addView(viewHRV);
		//layout.addView(viewFilt);
		
//		layout.addView(btn);
		setContentView(layout);
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
}

