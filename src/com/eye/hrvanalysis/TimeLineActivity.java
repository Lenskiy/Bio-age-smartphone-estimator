package com.eye.hrvanalysis;

import java.util.ArrayList;

import com.eye.visualization.MyDbAdapter;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimeLineActivity extends Activity implements OnItemClickListener, TabListener{

	private long m_startTime; 
	private long m_endTime;
	private ArrayList<Info> al;			
	private HRVAdapter cadapter;	
	private MyDbAdapter mda;
	private ListView listView;
	private boolean isTab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();
//		actionBar.addTab(actionBar.newTab().setTag("measure2").setIcon(R.drawable.measure2).setTabListener(this));
//		actionBar.addTab(actionBar.newTab().setTag("history").setIcon(R.drawable.history).setTabListener(this));
//		actionBar.addTab(actionBar.newTab().setTag("setting2").setIcon(R.drawable.setting2).setTabListener(this));
		
		actionBar.addTab(actionBar.newTab().setTag("measure2").setText("Measure").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag("history").setText("History").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag("setting2").setText("Setting").setTabListener(this));
		
		actionBar.setSelectedNavigationItem(1);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back));
		mda = new MyDbAdapter(this);

		
		listView = (ListView)findViewById(R.id.listview);	
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {		//long delete

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				new AlertDialog.Builder(TimeLineActivity.this)							
				.setTitle("Delete? ")
				.setMessage(
						"\n Will you really erase this?")
				.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mda.hrvRemover(al.get(position).getDate());
								al.remove(position);
								reset();
								Toast.makeText(getApplicationContext(), "The nocitce have been deleted. ", Toast.LENGTH_LONG).show();
							}
						}).setNegativeButton("No", null).show();
				return false;
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isTab=true;
		read();
		reset();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		mda.close();
	}
	
	/*  */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
		
//		Bundle extras = new Bundle();
//		Intent intent = new Intent(TimeLineActivity.this, TimePopActivity.class);
//		Log.e("getFilt",  (al.get(position).getFilt().length()+""));
//		Log.e("getHRV",  (al.get(position).getHRV().length()+""));
//		extras.putString("time", (al.get(position).getHRV().length()>100 ? "120" : "30"));	
//		extras.putString("hrv", al.get(position).getHRV());			
//		extras.putString("beatRate", al.get(position).beatrate);	
//		extras.putString("date", al.get(position).getDate());	
//		extras.putString("filt", al.get(position).getFilt());	
//		intent.putExtras(extras);
//		startActivity(intent);
//		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
	}
	
	public void read(){
		al=new ArrayList<Info>();
		Cursor c = mda.hrvReading();
		if (c.getCount() == 0)
			return;
		if (c.moveToFirst()) {
			for (;;) {
				Info w = new Info(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
				al.add(0,w);
				if (!c.moveToNext()){
					break;
				}
			}
		}
		
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
				 new AlertDialog.Builder(TimeLineActivity.this)							
					.setTitle("")
					.setMessage(
							"\n1. 111!\n\n2. 222! \n\n3. 333!\n")
					.setPositiveButton("OK",null).show();
			 break;
		 }
	        return false;
	 }
	
	public void reset() { 
		cadapter = new HRVAdapter(TimeLineActivity.this, R.layout.list_view_row,al);
		listView.setAdapter(cadapter);
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
	
	class HRVAdapter extends ArrayAdapter<Info> { 

		private ArrayList<Info> items;

		public HRVAdapter(Context context, int textViewResourceId,
				ArrayList<Info> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {		// �ϳ��� ArrayList �ε������� �� �޼ҵ带 �����ϸ鼭 ���� ������

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_view_row, null);
			}
			Info p = items.get(position);
			if (p != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView mt = (TextView) v.findViewById(R.id.middletext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null)
					tt.setText("<BeatRate>: " + p.getBeatrate());
				if (mt != null)
					mt.setText("<Date>: " + p.getDate());
				if (bt != null){
					bt.setText("<Time>: " + (p.getHRV().length()>200 ? "120s" : "30s"));
				}
				
			}
			return v;
		}
	}
	
	class Info{
		
		private String date, beatrate, hrv, filt;
		
		public Info(String date, String hrv, String filt, String beatrate) {
			super();
			this.date = date;
			this.beatrate = beatrate;
			this.hrv = hrv;
			this.filt = filt;
			
		}
		
		public String getDate(){
			return date;
		}
		
		public String getBeatrate(){
			return beatrate;
		}
		
		public String getHRV(){
			return hrv;
		}
		public String getFilt(){
			return filt;
		}
	}

//	@Override
//	public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
//		if(isTab && tab.getTag().equals("measure2")){
//			finish();
//			Intent intent = new Intent(TimeLineActivity.this, RGBActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.fade, R.anim.hold);
//			isTab=false;
//		}else if(isTab && tab.getTag().equals("setting2")){
//			finish();
//			Intent intent = new Intent(TimeLineActivity.this, SettingActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.fade, R.anim.hold);
//			isTab=false;
//		}
//		
//	}
//
//	@Override
//	public void onTabUnselected(Tab tab,
//			android.support.v4.app.FragmentTransaction ft) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onTabReselected(Tab tab,
//			android.support.v4.app.FragmentTransaction ft) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if(isTab && tab.getTag().equals("measure2")){
			finish();
			Intent intent = new Intent(TimeLineActivity.this, RGBActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			isTab=false;
		}else if(isTab && tab.getTag().equals("setting2")){
			finish();
			Intent intent = new Intent(TimeLineActivity.this, SettingActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			isTab=false;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
