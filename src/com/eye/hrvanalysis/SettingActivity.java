package com.eye.hrvanalysis;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingActivity extends Activity implements TabListener{

	private long m_startTime; 
	private long m_endTime;
	private boolean isTab;
	private RadioButton btn30, btn120;
	static boolean isCheck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();
		
		actionBar.addTab(actionBar.newTab().setTag("measure2").setText("Measure").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag("history2").setText("History").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag("setting").setText("Setting").setTabListener(this));
		
		actionBar.setSelectedNavigationItem(2);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back));

		
		btn30 = (RadioButton) findViewById(R.id.btn30);
		btn30.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked && isTab)
					Toast.makeText(getBaseContext(), "30 second will measure.", Toast.LENGTH_SHORT).show();
			}
		});
		btn120 = (RadioButton) findViewById(R.id.btn120);
		btn120.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked && isTab)
					Toast.makeText(getBaseContext(), "120 second will measure.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(isCheck)		btn120.setChecked(true);
		else			btn30.setChecked(true);
		
		isTab=true;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		 int curId = item.getItemId();
		 switch (curId) {
			 case R.id.b0tn1:
				 new AlertDialog.Builder(SettingActivity.this)							
					.setTitle("title")
					.setMessage(
							"\n1. 111 \n\n2. 222! \n\n3. 333!\n")
					.setPositiveButton("OK",null).show();
			 break;
		 }
	        return false;
	 }
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) { 

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			m_endTime = System.currentTimeMillis();
			if (m_endTime - m_startTime < 2000) {
				moveTaskToBack(true);                                 
				finish();                                                            
				android.os.Process.killProcess(android.os.Process.myPid());  
				System.exit(0);
			}
			m_startTime = System.currentTimeMillis();
			Toast.makeText(this, "'Back' one more Exist.", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if(isTab && tab.getTag().equals("measure2")){
			if(btn30.isChecked()){
				isCheck=false;
				RGBActivity.time =32;
			}
			else{
				isCheck=true;
				RGBActivity.time = 122;
			}
			finish();
			Intent intent = new Intent(SettingActivity.this, RGBActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			isTab=false;
		}
		else if(isTab && tab.getTag().equals("history2")){
			if(btn30.isChecked()){
				isCheck=false;
				RGBActivity.time =32;
			}
			else{
				isCheck=true;
				RGBActivity.time = 122;
			}
			finish();
			Intent intent = new Intent(SettingActivity.this, TimeLineActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			isTab=false;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}