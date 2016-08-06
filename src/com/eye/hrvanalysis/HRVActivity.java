package com.eye.hrvanalysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.eye.visualization.HRV;
import com.eye.visualization.MyDbAdapter;

import org.codeandmagic.android.gauge.GaugeView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class HRVActivity extends Activity{
	
	private GaugeView mGaugeView1;
	private TextView title_sub, bottom;
	private ArrayList<Double> Ecg_orig = new ArrayList<Double>();
	private long maxred = 1, maxgreen = 1, maxblue = 1;
	private MyDbAdapter mda;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("oncreate", "hrvactivity start!");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.activity_result);

		
		/* find HRV VALUE */
		for (int i = 0; i < RGBActivity.r.size(); i++) {
			if (RGBActivity.r.get(i) > maxred)
				maxred = RGBActivity.r.get(i);
			if (RGBActivity.g.get(i) > maxgreen)
				maxgreen = RGBActivity.g.get(i);
			if (RGBActivity.b.get(i) > maxblue)
				maxblue = RGBActivity.b.get(i);
		}

		for (int i = 0; i < RGBActivity.r.size(); i++) {
			Ecg_orig.add((double) RGBActivity.r.get(i) / maxred
					+ (double) RGBActivity.g.get(i) / maxgreen
					+ (double) RGBActivity.b.get(i) / maxblue);
		}
		/***********************/
		
		
		/* light green ~ RED .. Color select*/
		String beatRate = getIntent().getStringExtra("beatRate");
		title_sub = (TextView)findViewById(R.id.title_sub);
		int result = (int)HRV.getpNN50(HRV.getInstance().getHRV(Ecg_orig));
		String resultStr;
		if(result < 20){
			resultStr = "Terrible!";
			title_sub.setTextColor(Color.RED);
		}else if(result < 40){
			resultStr = "Bad!";
			title_sub.setTextColor(Color.MAGENTA);
		}else if(result < 60){
			resultStr = "Normal!";
			title_sub.setTextColor(Color.YELLOW);
		}else if(result < 80){
			resultStr = "Good!";
			title_sub.setTextColor(Color.BLUE);
		}else{
			resultStr = "Very Good!";
			title_sub.setTextColor(Color.GREEN);
		}
		title_sub.setText(resultStr);
		/***************************************/
		
		
		
		
		
		/* Age TextView */
		bottom = (TextView)findViewById(R.id.bottom);
		int age = 25+(int)(Math.random()*10);
		bottom.setText("Your Age is "+(10+(int)(100 - HRV.getpNN50(HRV.getInstance().getHRV(Ecg_orig)))/2)+" years old.");
		/**************************** AGE AGE AGE *************/
		/*********  AGE AGE AGE AGE AGE*****************/
		/********************* AGE AGE AGE AGE AGE ********************/
		/*****************************************/
		
		
		
		
		
		/* instant GaugeView */
		mGaugeView1 = (GaugeView) findViewById(R.id.gauge_view1);
		mGaugeView1.setTargetValue(result);
		/*********************/
		
		
		/* save to DB */
		mda = new MyDbAdapter(getApplicationContext());
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy.MM.dd  h:mm:ss a", Locale.US); // Now Date
		Date currentTime = new Date();
		String dTime = formatter.format(currentTime);
		mda.hrvGenerator(dTime,HRV.getInstance().getHRV(Ecg_orig),
					HRV.getInstance().filtfilt(Ecg_orig,HRV.kernel), beatRate);
		/**************/
		
		
		Intent myIntent = new Intent(HRVActivity.this, RGBActivity.class);
		setResult(Activity.RESULT_OK, myIntent);
		
	}

	protected void onDestroy() {
		super.onDestroy();
		mda.close();
	}

}

