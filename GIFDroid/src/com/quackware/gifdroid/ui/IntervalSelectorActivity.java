package com.quackware.gifdroid.ui;

import com.quackware.gifdroid.R;
import com.quackware.gifdroid.ui.views.ForcedVideoView;
import com.quackware.gifdroid.util.RangeSeekBar;
import com.quackware.gifdroid.util.RangeSeekBar.OnRangeSeekBarChangeListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

public class IntervalSelectorActivity extends Activity{
	RangeSeekBar<Integer> _seekBar;
	private ForcedVideoView vv;
	String path = null;
	
	private Integer previousMinValue;
	
	private boolean isPlaying = false;
	private boolean preferencesClicked = false;
	private Integer seekMinVal = null;
	private Integer seekMaxVal = null;
	

	private int width = 0;
	private int height = 0;
	private BitmapDrawable thumbnailDrawable = null;
	
	private static final String TAG = "IntervalSelectorActivity";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intervalselector);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			path = extras.getString("videoPath");
			setupViews(path);
		}
		else
		{
			Toast.makeText(this, getString(R.string.ERROR_NO_PATH), Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		//((Button)findViewById(R.id.setHeightButton)).setText(savedInstanceState.getString("height"));
		//((Button)findViewById(R.id.setWidthButton)).setText(savedInstanceState.getString("width"));
		preferencesClicked = savedInstanceState.getBoolean("preferencesClicked");
		isPlaying = savedInstanceState.getBoolean("isPlaying");
		seekMinVal = savedInstanceState.getInt("seekMinVal");
		seekMaxVal = savedInstanceState.getInt("seekMaxVal");
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("isPlaying", vv.isPlaying());
		savedInstanceState.putBoolean("preferencesClicked",preferencesClicked);
		//savedInstanceState.putString("fpsString", ((EditText)findViewById(R.id.intervalselector_fps)).toString());
		//savedInstanceState.putString("width", ((Button)findViewById(R.id.setWidthButton)).getText().toString());
		//savedInstanceState.putString("height", ((Button)findViewById(R.id.setHeightButton)).getText().toString());
		if(_seekBar != null)
		{
			savedInstanceState.putInt("seekMinVal", _seekBar.getSelectedMinValue());
			savedInstanceState.putInt("seekMaxVal", _seekBar.getSelectedMaxValue());
		}
		
	}
	
	private void setupViews(String path)
	{
		vv = (ForcedVideoView)findViewById(R.id.videoview);
		if(path != null)
		{
			vv.setVideoPath(path);
		}
		else
		{
			Toast.makeText(this, getString(R.string.ERROR_NO_PATH), Toast.LENGTH_SHORT).show();
			this.finish();
		}
		//Thumbnail for video.
		//Apparently there is a chance that the thumbnail will
		//fail due to outofmemory and cause a crash. Might as well be 
		
		//careful and catch it.
		try
		{
			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
			thumbnailDrawable = new BitmapDrawable(thumbnail);
			vv.setBackgroundDrawable(thumbnailDrawable.getCurrent());
		}
		catch(OutOfMemoryError e)
		{
			Log.e(TAG,e.getMessage());
			Toast.makeText(this, getString(R.string.ERROR_THUMBNAIL), Toast.LENGTH_SHORT);
		}
		vv.setDimensions(200,200);
		setupListeners();
	}

	private void setupListeners()
	{
		
		vv.setOnPreparedListener(new OnPreparedListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onPrepared(MediaPlayer mp) {
				width = mp.getVideoWidth();
				height = mp.getVideoHeight();
				//There may be some scaling issues, especially with phones
				//like my galaxy nexus.
				int tempWidth = width;
				int tempHeight = height;
				int windowWidth = getWindowManager().getDefaultDisplay().getWidth();
				double windowHeight = getWindowManager().getDefaultDisplay().getHeight();
				while(tempWidth >= windowWidth && tempHeight >= (windowHeight / 2.0))
				{
					//Scale by 10%
					tempWidth *= .9;
					tempHeight *= .9;
				}
				vv.setDimensions(tempHeight,tempWidth);
				_seekBar = ((RangeSeekBar<Integer>)findViewById(R.id.rangeSeekBar));
				_seekBar.setValues(0,mp.getDuration());
				_seekBar.setOnRangeSeekBarChangeListener(seekChange);
				
				if(seekMinVal != null && seekMaxVal != null)
				{
					_seekBar.setSelectedMaxValue(seekMaxVal);
					_seekBar.setSelectedMinValue(seekMinVal);
					vv.seekTo(seekMinVal);
				}

				if(isPlaying)
				{
					vv.start();
					onEverySecond.run();
					((ImageButton)findViewById(R.id.pausePlayButton)).setImageDrawable(getResources().getDrawable(R.drawable.ic_music_pause));
				}
				
			} });

		//Buttons
		((Button)findViewById(R.id.intervalselector_next)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					if(!preferencesClicked)
					{
						Toast.makeText(IntervalSelectorActivity.this, "Warning: Using default preferences.", Toast.LENGTH_LONG).show();
					}
					switchToFrameSelector();
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });
		
		((Button)findViewById(R.id.intervalselector_preferences)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					preferencesClicked = true;
					Intent i = new Intent(IntervalSelectorActivity.this,VideoPreferencesActivity.class);
					i.putExtra("width", width);
					i.putExtra("height", height);
					startActivity(i);
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });

		
		
		((ImageButton)findViewById(R.id.pausePlayButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Remove background of video.
				vv.setBackgroundColor(Color.BLACK);
				if(vv.isPlaying())
				{
					vv.pause();
					((VideoView)findViewById(R.id.videoview)).setBackgroundDrawable(thumbnailDrawable.getCurrent());
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.ic_music_play));
					
				}
				else
				{
					vv.start();
					onEverySecond.run();
					((VideoView)findViewById(R.id.videoview)).setBackgroundDrawable(null);
					((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.ic_music_pause));
				}
			} });
	}
	
	private Runnable onEverySecond=new Runnable() {

	    @Override
	    public void run() {

	        if(_seekBar != null) {
	        	_seekBar.setSelectedMinValue(vv.getCurrentPosition());
	        	if(vv.getCurrentPosition() > _seekBar.getSelectedMaxValue())
	        	{
	        		_seekBar.setSelectedMaxValue(vv.getCurrentPosition());
	        	}
	        }

	        if(vv.isPlaying()) {
	        	_seekBar.postDelayed(onEverySecond, 1000);
	        }

	    }
	};
	
	OnRangeSeekBarChangeListener<Integer> seekChange = new OnRangeSeekBarChangeListener<Integer>()
	{

		@Override
		public void rangeSeekBarValuesChanged(Integer minValue,
				Integer maxValue) {
			Log.i("minvalue, maxvalue","" + minValue  + " " + maxValue);
			if(previousMinValue != minValue)
			{
				vv.seekTo(minValue);
				previousMinValue = minValue;
			}
		}
		
	};
	
	private void switchToFrameSelector()
	{
		//First have to make sure they entered in a FPS value.
		//String fpsText = ((EditText)findViewById(R.id.intervalselector_fps)).getText().toString();
			try
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
				double fps = Double.parseDouble(prefs.getString("editTextFPS", "2"));
				
				int Rheight = (int)Double.parseDouble(prefs.getString("listPreferenceHeight", "100"));
				int Rwidth = (int)Double.parseDouble(prefs.getString("listPreferenceWidth","100"));
				int delay = Integer.parseInt(prefs.getString("editTextDelay","2"));
				//Everything is good at this point.
				Intent intent = new Intent(this,FrameSelectorActivity.class);
				intent.putExtra("fps",fps);
				//intent.putExtra("height",Integer.parseInt(((Button)findViewById(R.id.setHeightButton)).getText().toString()));
				//intent.putExtra("width",Integer.parseInt(((Button)findViewById(R.id.setWidthButton)).getText().toString()));
				intent.putExtra("height",Rheight);
				intent.putExtra("width",Rwidth);
				intent.putExtra("delay", delay);
				Log.i(TAG,"Video Start: " + _seekBar.getSelectedMinValue());
				Log.i(TAG,"Video End: " + _seekBar.getSelectedMaxValue());
				intent.putExtra("start",_seekBar.getSelectedMinValue());
				intent.putExtra("end",_seekBar.getSelectedMaxValue());
				intent.putExtra("path", path);
				startActivity(intent);
				
			} catch(Exception ex)
			{
				Toast.makeText(this, "Error parsing preferences. Please put valid values.", Toast.LENGTH_SHORT).show();
				ex.printStackTrace();
			}
		}
	

}
