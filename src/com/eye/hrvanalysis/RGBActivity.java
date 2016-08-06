package com.eye.hrvanalysis;

import java.io.IOException;
import java.util.ArrayList;

import com.eye.visualization.HRV;
import com.eye.visualization.LineGraph;

import org.achartengine.GraphicalView;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RGBActivity extends Activity implements TabListener {

	static ArrayList<Integer> r = new ArrayList<Integer>();
	static ArrayList<Integer> g = new ArrayList<Integer>();
	static ArrayList<Integer> b = new ArrayList<Integer>();
	public static int wWidth;
	public static int wHeight;
	static boolean isOk;
	private GraphicalView graphicView;
	private LineGraph linegraph;
	private volatile ArrayList<Double> toDraw = new ArrayList<Double>();
	private volatile byte[] copy = new byte[176 * 144];
	private ArrayList<Integer> maxpos = new ArrayList<Integer>();
	private boolean isTimerExpired = false, isWindow = false;
	private int beatrate = 0;
	private TextView tv, tv2, tv4;
	private Button startBtn;
	private Preview pv;
	private boolean isRunning = true;
	private boolean isOnOff;
	private long m_startTime, m_endTime;
	private ImageView iv3;
	private ArrayList<Double> filt = new ArrayList<Double>();
	static ArrayList<Double> saveRGB = new ArrayList<Double>();
	private ActionBar actionBar;
	private Resources res;
	private Bitmap a;
	private int width, height;
	static int time = 32;
	private Handler handler;
	private ArrayList<Integer> heartBeat = new ArrayList<Integer>();
	public static int pixel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.removeAllTabs();

		actionBar.addTab(actionBar.newTab().setTag("measure")
				.setText("Measure").setTabListener(this)); // tab
		actionBar.addTab(actionBar.newTab().setTag("history2")
				.setText("History").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag("setting2")
				.setText("Setting").setTabListener(this));
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.back));

		DisplayMetrics dm = getResources().getDisplayMetrics();
		int pixel1, pixel2;
		pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
				dm); // graph label text size

		pv = new Preview(this);
		addContentView(pv, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		
		linegraph = new LineGraph("HEART BIT RATE","");

		RelativeLayout layout = new RelativeLayout(this); // Layout
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		layout.setBackgroundResource(R.drawable.back);

		ImageView iv = new ImageView(this); // center rec bakground first
		iv.setBackgroundResource(R.drawable.down);
		RelativeLayout.LayoutParams ivParam = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ivParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		iv.setLayoutParams(ivParam);

		ImageView iv2 = new ImageView(this); // yellow rec cover?
		iv2.setId(5);
		iv2.setBackgroundResource(R.drawable.up);
		RelativeLayout.LayoutParams iv2Param = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		iv2Param.addRule(RelativeLayout.CENTER_IN_PARENT);
		iv2.setLayoutParams(iv2Param);

		ImageView ivv = new ImageView(this); // center rec bakground second
		ivv.setBackgroundResource(R.drawable.down2);
		RelativeLayout.LayoutParams ivvParam = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ivvParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		ivv.setLayoutParams(ivvParam);

		graphicView = linegraph.getView(this); // graphic
												// View***********************
		graphicView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		graphicView.setId(1); // GraphicView
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				240, dm);
		pixel2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				45, dm);
		RelativeLayout.LayoutParams gvParam = new RelativeLayout.LayoutParams(
				pixel1, pixel2);
		gvParam.addRule(RelativeLayout.BELOW, 2);
		gvParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
		graphicView.setLayoutParams(gvParam);

		tv = new TextView(this); // TextView "heartbeat rate  95  85  75  76 74"
		Typeface face = Typeface.createFromAsset(getAssets(),
				"font/DS-DIGII.TTF");
		tv.setTypeface(face);
		tv.setId(2);
		tv.setText("0");
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				30, dm);
		tv.setTextSize(pixel1);
		tv.setTextColor(Color.rgb(150, 150, 150));//
		RelativeLayout.LayoutParams tvParam = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tvParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		tv.setLayoutParams(tvParam);

		tv2 = new TextView(this); // TextView "measuring value"
		tv2.setId(22);
		tv2.setText("MEASURING\nVALUE");
		tv2.setGravity(Gravity.CENTER);
		tv2.setTextColor(Color.rgb(150, 150, 150)); //
		RelativeLayout.LayoutParams tv2Param = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tv2Param.addRule(RelativeLayout.ABOVE, 2);
		tv2Param.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tv2.setLayoutParams(tv2Param);

		TextView tv3 = new TextView(this); // TextView "bpm"
		face = Typeface.createFromAsset(getAssets(), "font/DS-DIGII.TTF");
		tv3.setTypeface(face);
		tv3.setId(33);
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, dm);
		tv3.setTextSize(pixel1);
		tv3.setText("  BPM");
		tv3.setTextColor(Color.rgb(10, 10, 10));
		RelativeLayout.LayoutParams tv3Param = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tv3Param.addRule(RelativeLayout.ALIGN_BASELINE, 2);
		tv3Param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				85, dm);
		tv3Param.setMargins(0, 0, pixel1, 0);
		tv3.setLayoutParams(tv3Param);

		iv3 = new ImageView(this); // heartImage
		iv3.setBackgroundResource(R.drawable.heart);
		RelativeLayout.LayoutParams iv3Param = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		iv3Param.addRule(RelativeLayout.CENTER_VERTICAL);
		iv3Param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				75, dm);
		pixel2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, dm);
		iv3Param.setMargins(0, 0, pixel1, pixel2);
		iv3.setLayoutParams(iv3Param);

		ImageView iv4 = new ImageView(this); // timerImage
		iv4.setId(36);
		iv4.setBackgroundResource(R.drawable.timer1);
		RelativeLayout.LayoutParams iv4Param = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		iv4Param.addRule(RelativeLayout.CENTER_VERTICAL);
		iv4Param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				60, dm);
		iv4Param.setMargins(pixel1, 0, 0, 0);
		iv4.setLayoutParams(iv4Param);

		tv4 = new TextView(this); // TextView "timer number"
		face = Typeface.createFromAsset(getAssets(), "font/DS-DIGI.TTF");
		tv4.setTypeface(face);
		tv4.setId(99);
		pixel2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, dm);
		tv4.setTextSize(pixel2);
		if (time == 32)
			tv4.setText("30");
		else
			tv4.setText("120");
		tv4.setTextColor(Color.rgb(150, 150, 150));
		RelativeLayout.LayoutParams tv4Param = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tv4Param.addRule(RelativeLayout.RIGHT_OF, 36);
		tv4Param.addRule(RelativeLayout.CENTER_VERTICAL);
		tv4.setLayoutParams(tv4Param);

		res = getResources(); // image heart scale
		a = BitmapFactory.decodeResource(res, R.drawable.heart);
		width = a.getWidth();
		height = a.getHeight();

		startBtn = new Button(this); // start Button
		startBtn.setId(3);
		startBtn.setBackgroundResource(R.drawable.startbtn);
		RelativeLayout.LayoutParams btnParam = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		btnParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		pixel1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				30, dm);
		btnParam.setMargins(0, 0, pixel1, pixel1);
		startBtn.setLayoutParams(btnParam);

		layout.addView(iv);
		layout.addView(tv);
		layout.addView(tv2);
		layout.addView(iv3);
		layout.addView(iv4);
		layout.addView(tv3);
		layout.addView(tv4); // TIMER NUM
		layout.addView(graphicView); // graph
		layout.addView(ivv);
		layout.addView(iv2);
		layout.addView(startBtn); // START BTN
		addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		startBtn.setOnClickListener(new OnClickListener() { // StartButton
															// Listener*
			@Override
			public void onClick(View v) {
				if (!isOnOff) {
					r.clear();
					g.clear();
					b.clear();
					saveRGB.clear();
					startBtn.setBackgroundResource(R.drawable.stopbtn);
					isRunning = true; // Timer Thread Start!
					new PvThread().start();
					pv.startPreview();
					isOnOff = true;
					Log.e("StartButton", "Start!");
				} else {
					try {
						r.clear();
						g.clear();
						b.clear();
						saveRGB.clear();
						isTimerExpired = true;
						isRunning = false;
						pv.destroyedHelper();
						Intent intent = getIntent();
						finish();
						startActivity(intent);
						overridePendingTransition(R.anim.fade, R.anim.hold);
					} catch (Exception ee) {
						Log.e("Button stop error", "error");
					}
				}
			}
		});

		handler = new Handler() { // thread ui
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1)
					iv3.setImageBitmap(a);
				else if (msg.what == 2) {
					tv2.setTextColor(Color.rgb(255, 0, 0));
					tv.setTextColor(Color.rgb(0, 0, 0));
				} else if (msg.what == 1234){
					Toast.makeText(getApplication(), "Wait a little.",Toast.LENGTH_LONG).show();
				}
				else if (msg.what == 8888){
					Toast.makeText(getApplication(),
							"Your Heart Rate is " + beatrate + ".",
							Toast.LENGTH_LONG).show();
				}
				else {
					if (msg.what == 3)
						tv4.setTextColor(Color.rgb(50, 50, 50));
					else if ((time == 12 && msg.what == 27) || msg.what == 117)
						tv4.setTextColor(Color.rgb(250, 50, 50));
					tv4.setText((time - msg.what) + "");
				}
			}
		};
	}

	public class heartThread extends Thread { // heart THREAD
		public void run() {
			while (isRunning) {
				a = Bitmap.createScaledBitmap(a, (int) (width * 1.1),
						(int) (height * 1.1), true);
				handler.sendEmptyMessage(1);
				try {
					Thread.sleep(100);
				} catch (Exception ee) {
				}
				a = Bitmap.createScaledBitmap(a, (int) (width * 1.4),
						(int) (height * 1.4), true);
				handler.sendEmptyMessage(1);
				try {
					Thread.sleep(450);
				} catch (Exception ee) {
				}
				a = Bitmap.createScaledBitmap(a, (int) (width), (int) (height),
						true);
				handler.sendEmptyMessage(1);
				try {
					Thread.sleep(450);
				} catch (Exception ee) {
				}
			}
		}

	}

	public class PvThread extends Thread { // TIMER THREAD
		public void run() {
			int k = -1;
			Log.e("TIMER", "*START*");
			new Process(); // Start Thread! Process();
			while (k < time && isRunning) {
				k++;
				if (k == 5) { // heartbeatrate
					handler.sendEmptyMessage(2);
					isWindow = true;
					new heartThread().start();
				}

				if (k >= 2 && k <= time)
					handler.sendEmptyMessage(k); // timer.. 30 29 28 ...
				
				if (k == time)
					handler.sendEmptyMessage(1234);

				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
					
				Log.e("TIMER", k + "s");
			}
			if(isTimerExpired){
				Log.e("out", "time thread return");
				return;
			}
			isRunning = false;
			isTimerExpired = true;
			isWindow = false;
			isOk = false;
			handler.sendEmptyMessage(8888);
			Log.e("GoHRVActivity", "from RGBActivity");
			Bundle extras = new Bundle();
			extras.putString("beatRate", beatrate + ""); //
			Intent intent = new Intent(RGBActivity.this, HRVActivity.class);
			intent.putExtras(extras);
			startActivityForResult(intent, 33333);
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		}
	}

	public class Process extends Thread { // TIMER THREAD

		Process() {
			super();
			start();
		}

		public void run() {
			try {
				Thread.sleep(2000);
			} catch (Exception ee) {
			}
			if (isRunning) {
				byte array[] = new byte[(176 * 216)];
				int copyrgb[] = new int[(176 * 144)];
				int arr[] = new int[3];

				while (!isTimerExpired) {
					if (!array.equals(copy)) {
						array = copy;
						arr = calculateEcg(calculate(copyrgb, array, 176, 144));
						r.add(arr[0]);
						g.add(arr[1]);
						b.add(arr[2]);
						saveRGB.add((double) ((arr[0] + arr[1] + arr[2])));
						if (toDraw.size() <= 170) {
							toDraw.add((double) ((arr[0] + arr[1] + arr[2])));
						} else {
							for (int i = 0; i < 169; i++)
								toDraw.set(i, toDraw.get(i + 1));
							toDraw.set(169, (double) ((arr[0] + arr[1] + arr[2])));
							filt = HRV.getInstance().conv(
									HRV.getInstance().findPeak2Peak(toDraw),
									HRV.getInstance().kernel);
						}
						linegraph.addNewPoints(toDraw, true);
						graphicView.repaint();
					}
				}
			}
		}
		
		public int[] calculate(int[] rgb, byte[] yuv420sp, int width, int height) {
			final int frameSize = width * height;
			for (int j = 0, yp = 0; j < height; j++) {
				int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
				for (int i = 0; i < width; i++, yp++) {
					int y = (0xff & ((int) yuv420sp[yp])) - 16;
					if (y < 0)
						y = 0;
					if ((i & 1) == 0) {
						v = (0xff & yuv420sp[uvp++]) - 128;
						u = (0xff & yuv420sp[uvp++]) - 128;
					}

					int y1192 = 1192 * y;
					int r = (y1192 + 1634 * v);
					int g = (y1192 - 833 * v - 400 * u);
					int b = (y1192 + 2066 * u);

					if (r < 0)
						r = 0;
					else if (r > 262143)
						r = 262143;
					if (g < 0)
						g = 0;
					else if (g > 262143)
						g = 262143;
					if (b < 0)
						b = 0;
					else if (b > 262143)
						b = 262143;

					rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
							| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
				}
			}
			return rgb;
		}

		public int[] calculateEcg(int[] rgb) {
			int sred = 0, sgr = 0, sbl = 0;
			int rgbcopy[] = new int[3];
			for (int j = 0; j < rgb.length; j++) {
				int rdvalue = 255 - (rgb[j] >> 16) & 0xff;
				int grvalue = 255 - (rgb[j] >> 8) & 0xff;
				int blvalue = 255 - rgb[j] & 0xff;
				sred += rdvalue;
				sgr += grvalue;
				sbl += blvalue;
			}
			rgbcopy[0] = sred;
			rgbcopy[1] = sgr;
			rgbcopy[2] = sbl;
			return rgbcopy;
		}
	}

	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;
		long fpsStartTime = 0L;
		int frameCnt = 0; // Frame
		double timeElapsed = 0.0f; //

		long winStartTime = 0L;
		double winElapsed = 0.0f;

		Preview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		}

		public void surfaceCreated(SurfaceHolder holder) {
			wWidth = getWidth();
			wHeight = getHeight();
			// Log.e("getWidth?", getWidth()+":"+wWidth);
			// Log.e("getHeight?", getHeight()+":"+wHeight);

			mCamera = Camera.open();
			Camera.Parameters param = mCamera.getParameters();
			param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
			}

			mCamera.setPreviewCallback(new Camera.PreviewCallback() {
				public synchronized void onPreviewFrame(byte[] data,
						Camera camera) {
					copy = data;
					frameCnt++;

					long winEndTime = System.currentTimeMillis();
					float winDelta = (winEndTime - winStartTime) * 0.001f;
					winElapsed += winDelta;
					if (winElapsed >= 1f) { // Show beatrate
						Log.e("fps", "fps : " + (float) (frameCnt / winElapsed));
						frameCnt = 0;
						winElapsed = 0.0f;

						if (isWindow) {
							maxpos = HRV.getInstance().findFilterMaxima(
									HRV.getInstance().conv(
											HRV.getInstance().findPeak2Peak(
													toDraw),
											HRV.getInstance().kernel2));
							try {
								int sum = 0;
								for (int i = 0; i < maxpos.size() - 1; i++) {
									sum += (maxpos.get(i + 1) - maxpos.get(i));
								}
								beatrate = (1800 * (maxpos.size() - 1)) / sum;
								heartBeat.add(beatrate);
							} catch (Exception e) {
								Log.e("sizeError", "max sizeError");
							}
						}
						tv.setText("" + beatrate);
					}
					winStartTime = System.currentTimeMillis();

					if (isTimerExpired) { // TIMEOUT
						destroyedHelper();
						return;
					}
				}
			});
		}

		public void startPreview() {
			mCamera.startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			destroyedHelper();
		}

		public void destroyedHelper() {
			try {
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				Log.e("PreView End", "End");
			} catch (Exception e) {
				Log.e("TimeOut", "CameraPreview Exist.");
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			if (isTimerExpired)
				return;

			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			parameters.setPreviewSize(176, 144);

			mCamera.setParameters(parameters);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		isTimerExpired = true;
		isRunning = false;
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
			new AlertDialog.Builder(RGBActivity.this)
					.setTitle("")
					.setMessage(
							"\n1. \n\n2.  Start! \n\n3. !\n")
					.setPositiveButton("OK", null).show();
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 33333) {
			if (resultCode == RESULT_OK) { // HRVActivity �����
				try {
					isRunning = false;
					pv.destroyedHelper();
					Intent intent = getIntent();
					finish();
					startActivity(intent);
					overridePendingTransition(R.anim.fade, R.anim.hold);
				} catch (Exception ee) {
					Log.e("Button stop error", "error~~~~~~~~~~~~~*");
				}
				return;
			}
		}
	}// onActivityResult

	public boolean onKeyDown(int keyCode, KeyEvent event) { // ***************
															// exist
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			m_endTime = System.currentTimeMillis();
			if (m_endTime - m_startTime < 2000) {
				pv.destroyedHelper();
				moveTaskToBack(true);
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
			m_startTime = System.currentTimeMillis();
			Toast.makeText(this, "'Back' one more Exist.", Toast.LENGTH_SHORT)
					.show();
		}
		return true;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		if (tab.getTag().equals("history2")) {
			finish();
			Intent intent = new Intent(RGBActivity.this, TimeLineActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
		} else if (tab.getTag().equals("setting2")) {
			finish();
			Intent intent = new Intent(RGBActivity.this, SettingActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
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
