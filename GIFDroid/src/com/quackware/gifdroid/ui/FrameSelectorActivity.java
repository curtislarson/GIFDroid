package com.quackware.gifdroid.ui;


import java.util.ArrayList;

import com.quackware.gifdroid.GIF;
import com.quackware.gifdroid.GIFCreator;
import com.quackware.gifdroid.GIFFrames;
import com.quackware.gifdroid.R;
import com.quackware.gifdroid.util.Donate;
import com.quackware.gifdroid.util.GIFPrep;
import com.quackware.gifdroid.util.MyDataStore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class FrameSelectorActivity extends Activity {
	
	private static final String TAG = "FrameSelectorActivity";
	
	private GIFCreator _gifCreator;
	private FrameGrabberTask _frameGrabberTask;
	private GifCreatorTask _gifCreatorTask;
	private MyDataStore _dataStore;
	private ImageAdapter _adapter;
	private GIFPrep _gp;
	
	private boolean loaded = false;
	
	private static final int CRASH_DIALOG = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.i(TAG,"onCreate() called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frameselector);
		loadPreferences();
		setupListeners();
		boolean areWeGonnaCrash = setupGIFPrep();
		if(areWeGonnaCrash)
		{
			//We are going to crash.
			showDialog(CRASH_DIALOG);
		}
		else
		{
			startFrameGrabberTask();
		}
		
	}
	
	private void startFrameGrabberTask()
	{
		if (_frameGrabberTask == null) {
			if(!loaded)
			{
				_frameGrabberTask = new FrameGrabberTask(this);
				_frameGrabberTask.execute(_gp);
				loaded = true;
			}
		} else {
			_frameGrabberTask.attach(this);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch(id)
		{
		case CRASH_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("WARNING!");
			builder.setMessage(getString(R.string.WARNING_OUTOFMEMORY));
			builder.setPositiveButton("Return and be safe", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					FrameSelectorActivity.this.finish();
				} });
			builder.setNegativeButton("Proceed and most likely crash", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(FrameSelectorActivity.this, "You've been warned!", Toast.LENGTH_SHORT);
					startFrameGrabberTask();
					
				}});
			return builder.create();
		default:
			return null;
		}
		
	}
	
	private void showDonateMessage()
	{
		Toast.makeText(this, getString(R.string.WARNING_WATERMARK), Toast.LENGTH_LONG).show();
	}
	
	private void setupListeners()
	{	
		((Button)findViewById(R.id.createGifButton)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					if(_adapter != null && _adapter.areAnySelected())
					{
						ArrayList<Bitmap> selectedBitmaps = _adapter.getSelectedBitmaps();
						if (_gifCreatorTask == null) {
							_gifCreatorTask = new GifCreatorTask(FrameSelectorActivity.this);
							_gifCreatorTask.execute(selectedBitmaps);
						} else {
							_gifCreatorTask.attach(FrameSelectorActivity.this);
						}
						if(!Donate.isDonate(FrameSelectorActivity.this))
						{
							FrameSelectorActivity.this.showDonateMessage();
						}

					}
					else
					{
						Toast.makeText(FrameSelectorActivity.this,getString(R.string.WARNING_SELECT_FRAME),Toast.LENGTH_SHORT).show();
					}
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });

		((Button) findViewById(R.id.selectAllFrameButton)).setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setBackgroundColor(0xFFFFA500);
							return true;
						case MotionEvent.ACTION_UP:
							v.setBackgroundColor(Color.BLACK);
							_adapter.selectAll(((GridView) findViewById(R.id.bitmapgridview)));
							return true;
						default:
							v.setBackgroundColor(Color.BLACK);
							return false;
						}
					}
				});
		
		((Button)findViewById(R.id.deselectAllFrameButton)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					if(_adapter != null)
					{
						_adapter.deselectAll(((GridView)findViewById(R.id.bitmapgridview)));
					}
					else
					{
						//Somehow _adapter is now null...
						if(_dataStore != null)
						{
							_adapter = _dataStore.getImageAdapter();
							if(_adapter != null)
							{
								_adapter.deselectAll(((GridView)findViewById(R.id.bitmapgridview)));
							}
						}
					}
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });
	}
	
	private boolean setupGIFPrep()
	{
		Bundle extras = getIntent().getExtras();
		double fps = 1.0;
		int start = 0;
		int end = 0;
		String path = null;
		int width = 0;
		int height = 0;
		int delay = 0;
		if(extras != null)
		{
			fps = extras.getDouble("fps");
			start = extras.getInt("start")*1000;
			end = extras.getInt("end")*1000;
			path = extras.getString("path");
			width = extras.getInt("width");
			height = extras.getInt("height");
			delay = extras.getInt("delay");
		}
		else
		{
			//UH OH
		}
		
		_gifCreator = new GIFCreator(this,path,fps);
		if(areWeGoingToCrash(fps,width,height,end-start))
		{
			_gp = new GIFPrep(fps,start,end,width,height,delay);
			return true;
		}
		else
		{
			_gp = new GIFPrep(fps,start,end,width,height,delay);
			return false;
		}
	}

	
	private boolean areWeGoingToCrash(double fps, int width, int height,int time)
	{
		//In bytes
		long maxVM = 24*1024*1024;
		long currentMem = 0;
		for(int i = 0;i<Math.ceil(time);i+= 1000000)
		{
			currentMem += width*height*4*fps;
		}
		if(currentMem > maxVM)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void removeFrameGrabberTask()
	{
		_frameGrabberTask = null;
	}
	
	private void removeGifCreatorTask()
	{
		_gifCreatorTask = null;
	}
	
	private void loadPreferences()
	{
		_dataStore = (MyDataStore) getLastNonConfigurationInstance();
		if (_dataStore != null) {
			_frameGrabberTask = _dataStore.get_frameGrabberTask();
			_gifCreatorTask = _dataStore.getGifCreatorTask();
			loaded = _dataStore.getFrameGrabberTaskLoaded();
			_adapter = _dataStore.getImageAdapter();
			if(_adapter != null)
			{
				GridView gridView = (GridView)findViewById(R.id.bitmapgridview);
				gridView.setAdapter(_adapter);
			}
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (_dataStore == null) {
			_dataStore = new MyDataStore();
		}
		if(_frameGrabberTask != null)
		{
			_frameGrabberTask.detatch();
		}
		if(_gifCreatorTask != null)
		{
			_gifCreatorTask.detatch();
		}
		
		_dataStore.set_frameGrabberTask(_frameGrabberTask);
		_dataStore.setGifCreatorTask(_gifCreatorTask);
		_dataStore.setFrameGrabberTaskLoaded(loaded);
		_dataStore.setImageAdapter(_adapter);
		return _dataStore;
	}
	
	public void displayBitmapsToUser(ArrayList<Bitmap> bitmapList)
	{
		GridView gridView = (GridView)findViewById(R.id.bitmapgridview);
		_adapter = new ImageAdapter(this,bitmapList);
		gridView.setAdapter(_adapter);
	}

	public class GifCreatorTask extends AsyncTask<ArrayList<Bitmap>,Void,String>
	{

		private FrameSelectorActivity activity = null;
		private ProgressDialog _gifCreatorSpinner = null;
		
		public GifCreatorTask(FrameSelectorActivity act)
		{
			attach(act);
		}
		
		@Override
		protected void onPreExecute()
		{
			_gifCreatorSpinner = new ProgressDialog(FrameSelectorActivity.this);
			_gifCreatorSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_gifCreatorSpinner.setMessage(getString(R.string.gifCreatorSpinner));
			_gifCreatorSpinner.setCancelable(false);
			_gifCreatorSpinner.show();
		}
		
		@Override
		protected String doInBackground(ArrayList<Bitmap>... selectedBitmaps) {
			try
			{
				GIF g = _gifCreator.createGIF(selectedBitmaps[0]);
				String path = g.writeToFile(FrameSelectorActivity.this);
				return path;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String path)
		{
			try {
				if (_gifCreatorSpinner != null) {
					_gifCreatorSpinner.dismiss();
					_gifCreatorSpinner = null;
				}
			} catch (Exception ex) {
				_gifCreatorSpinner = null;
			}
			removeGifCreatorTask();
			if(path != null)
			{
				Intent intent = new Intent(FrameSelectorActivity.this,PreviewActivity.class);
				intent.putExtra("gifPath", path);
				startActivity(intent);
			}
			else
			{
				//Some sort of warning that we were unable to write to file.
			}
			
		}
		public void attach(FrameSelectorActivity fsa)
		{
			activity = fsa;
		}
		
		public void detatch()
		{
			activity = null;
		}
		
	}
	
	
	public class FrameGrabberTask extends AsyncTask<GIFPrep,Void,GIFFrames>
	{

		private FrameSelectorActivity activity = null;
		private ProgressDialog _frameGrabberSpinner = null;
		
		public FrameGrabberTask(FrameSelectorActivity act)
		{
			attach(act);
		}
		
		public void attach(FrameSelectorActivity fsa)
		{
			Log.i(TAG,"FrameGrabberTask attach()");
			activity = fsa;
		}
		
		public void detatch()
		{
			Log.i(TAG,"FrameGrabberTask detatch()");
			activity = null;
		}
		
		@Override
		protected void onPreExecute()
		{
			Log.i(TAG,"FrameGrabberTask onPreExecute()");
			_frameGrabberSpinner = new ProgressDialog(FrameSelectorActivity.this);
			_frameGrabberSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_frameGrabberSpinner.setMessage(getString(R.string.frameGrabberSpinner));
			_frameGrabberSpinner.setCancelable(false);
			_frameGrabberSpinner.show();
		}
		
		@Override
		protected GIFFrames doInBackground(GIFPrep... gp) {
			Log.i(TAG,"FrameGrabberTask doInBackground()");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FrameSelectorActivity.this);
			//0 for mmdr, 1 for ffmpeg
			int type = Integer.parseInt(prefs.getString("listPreferenceDecoder", "1"));
			try
			{
				switch(type)
				{
				case 0:
					return _gifCreator.createGIFFramesMMDR(gp[0]);
				case 1:
					return _gifCreator.createGIFFrames(gp[0]);
				default:
					return _gifCreator.createGIFFrames(gp[0]);
				}
			}
			catch(OutOfMemoryError ex)
			{
				ex.printStackTrace();		
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(GIFFrames g)
		{
			Log.i(TAG,"FrameGrabberTask onPostExecute()");
			try {
				if (_frameGrabberSpinner != null) {
					_frameGrabberSpinner.dismiss();
					_frameGrabberSpinner = null;
				}
			} catch (Exception ex) {
				_frameGrabberSpinner = null;
			}
			if(g != null)
			{
				ArrayList<Bitmap> bitmapList = g.getBitmapList();
				displayBitmapsToUser(bitmapList);
			}
			else
			{
				//We have ran out of memory, return to the previous activity.
				Toast.makeText(FrameSelectorActivity.this, getString(R.string.ERROR_GRAB_FRAMES), Toast.LENGTH_LONG).show();
				FrameSelectorActivity.this.finish();
			}
			
			removeFrameGrabberTask();
		}
	}

}
