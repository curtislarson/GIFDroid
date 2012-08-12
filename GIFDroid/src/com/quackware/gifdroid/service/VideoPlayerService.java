package com.quackware.gifdroid.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.MediaController.MediaPlayerControl;

public class VideoPlayerService extends Service implements MediaPlayerControl {

	private MediaPlayer player;
	
	private static String TAG = "VideoPlayerService";
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG,"onCreate()");
		player = new MediaPlayer();
		setupPlayerListeners();
	}
	
	private void setupPlayerListeners()
	{
		if(player != null)
		{
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.i(TAG,"onCompletion called");
					
				} });
			
			player.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Log.e(TAG,"Error what= " + what + " extra= " + extra);
					return false;
				} });
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(TAG,"onDestroy()");
		if(player.isPlaying())
		{
			player.stop();
		}
		player.release();
	}
	
	public void play(String path)
	{
		
		Log.i(TAG,"play(String path)");
		if(player == null)
		{
			player = new MediaPlayer();
		}
		try
		{
			player.setDataSource(path);
			player.prepare();
			player.start();
		}
		catch(Exception ex)
		{
			Log.e(TAG,"Error in play(String path)");
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		try
		{
			int currentPosition = player.getCurrentPosition();
			return currentPosition;
		}
		catch(Exception ex)
		{
			Log.e(TAG,"Error in getCurrentPosition()");
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int getDuration() {
		try
		{
			return player.getDuration();
		} catch(Exception ex)
		{
			Log.e(TAG,"Error in getDuration()");
			ex.printStackTrace();
			return 0;
		}
		
	}

	@Override
	public boolean isPlaying() {
		Log.i(TAG, "isPlaying()");
		return player.isPlaying();
	}

	@Override
	public void pause() {
		Log.i(TAG, "pause()");
		player.pause();
	}

	@Override
	public void seekTo(int arg0) {
		player.seekTo(arg0);
	}

	@Override
	public void start() {
		player.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
