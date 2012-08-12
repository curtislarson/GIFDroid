package com.quackware.gifdroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.quackware.gifdroid.R;
import com.quackware.gifdroid.encoder.AnimatedGifEncoder;
import com.quackware.gifdroid.util.Donate;

//import org.jiggawatt.giffle.Giffle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class GIF {
	
	ArrayList<Bitmap> _bitmaps;
	byte[] gifBytes;
	double _fps;
	double _delay;
	String _fileName;
	
	private static final String TAG = "GIF";
	
	public GIF(ArrayList<Bitmap> bitmaps, double fps, double delay)
	{
		_bitmaps = bitmaps;
		_fps = fps;
		_delay = delay;
	}
	
	public String getFileName()
	{
		return _fileName;
	}
	
	public String writeToFile(Context con)
	{
		try {
			if (gifBytes.length > 0) {
				//File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				File directory = con.getExternalFilesDir(null);
				directory.mkdirs();
				
				String filename = directory.getAbsolutePath() + "/gifdroid_"  +  Calendar.getInstance().getTimeInMillis()  + ".gif";
				File temp = new File(filename);
				temp.createNewFile();
				temp = null;
				FileOutputStream fos = new FileOutputStream(filename);	
				fos.write(gifBytes);
				fos.close();
				return filename;
			} else {
				Log.i(TAG, "gifBytes length is less than or equal to 0");
				return null;
			}
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
			return null;
		}
	}
	
	public void create(Context con,int width,int height)
	{
		if(width == -1)
		{
			width = 780;
		}
		if(height == -1)
		{
			height = 420;
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//BufferedOutputStream bos = new BufferedOutputStream(ByteOutputStream());
		AnimatedGifEncoder enc = new AnimatedGifEncoder();
		Log.i(TAG,"Delay: " + _delay);
		enc.setFrameRate((float) _delay);
		//enc.setSize(width, height);
		enc.setRepeat(0);
		enc.setQuality(100);
		enc.start(bos);
		for(int i = 0;i<_bitmaps.size();i++)
		{
			Log.i(TAG,"Adding frame");
			if(Donate.isDonate(con))
			{
				enc.addFrame(_bitmaps.get(i));
			}
			else
			{
				enc.addFrame(watermark(_bitmaps.get(i),con));
			}
			
		}
		Log.i(TAG,"Finished adding frames");
		gifBytes = bos.toByteArray();
		
		
		/*Giffle g = new Giffle();
		if(g.Init(filename,width,height,256,100,(int)_fps) != 0)
		{
			Log.e("gifflen","Init failed, check parameters");
		}
		for(int i = 0;i<_bitmaps.size();i++)
		{
			int[] pixels = new int[width*height];
			_bitmaps.get(i).getPixels(pixels, 0, width, 0, 0, width, height);
			g.AddFrame(pixels);
		}
		g.Close();*/
		//At this point we should have created our gif with the provided filename;
	}
	
	
	private Bitmap watermark(Bitmap src,Context con)
	{
		Canvas canvas = new Canvas(src);
		Drawable wm = con.getResources().getDrawable(R.drawable.watermark);
		canvas.drawBitmap(((BitmapDrawable)wm).getBitmap(), 0,0, null);
		return src;
	}
	
}
