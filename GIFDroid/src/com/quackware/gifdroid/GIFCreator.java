/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid;

import java.util.ArrayList;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.quackware.gifdroid.util.GIFPrep;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaMetadataRetriever;
import android.util.Log;


public class GIFCreator {

	Context _context;

	double _duration;
	long _counter = 1;

	long _incrementer;
	String _path;
	
	ArrayList<Bitmap> bitmapList;
	ModifiedFrameGrabber f;
	private GIFPrep _gifInfo;
	
	private static final String TAG = "GIFCreator";

	public GIFCreator(Context context, String path, double fps) {
		_context = context;
		bitmapList = new ArrayList<Bitmap>();
		_path = path;
	}

	private Bitmap iplToBitmap(IplImage image)
	{
		Log.i(TAG,"height: " + image.height() + " width: " + image.width());
		Bitmap b = Bitmap.createBitmap(image.width(),image.height(),Config.ARGB_8888);
		b.copyPixelsFromBuffer(image.getByteBuffer());
		Bitmap newB = null;
		try
		{
			Log.i(TAG,"Scale: " + _gifInfo.getWidth() + " x " + _gifInfo.getHeight());
			newB = Bitmap.createScaledBitmap(b, _gifInfo.getWidth(), _gifInfo.getHeight(), true);
		}
		catch(Exception ex)
		{
			Log.i("test","caught exception " + ex.getMessage());
		}
		b.recycle();
		return newB;
	}
	
	private Bitmap scale(Bitmap largeBitmap)
	{
		return Bitmap.createScaledBitmap(largeBitmap, _gifInfo.getWidth(), _gifInfo.getHeight(), true);
	}
	
	public GIFFrames createGIFFramesMMDR(GIFPrep gifInfo)
	{
		try
		{
			_gifInfo = gifInfo;
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			mmr.setDataSource(_path);
			GIFFrames gifFrames = new GIFFrames(gifInfo.getFps()*1000);
			double inc = 1000*1000 / gifInfo.getFps();
			for(double i = Math.floor(gifInfo.getStartTime());i<Math.ceil(gifInfo.getEndTime());i+= inc)
			{
				gifFrames.addBitmap(scale(mmr.getFrameAtTime((long)i,MediaMetadataRetriever.OPTION_CLOSEST)));
			}
			
			return gifFrames;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public GIFFrames createGIFFrames(GIFPrep gifInfo)
	{
		try
		{
			_gifInfo = gifInfo;
			f = new ModifiedFrameGrabber(_path);
			f.start();
			//microseconds
			//1000000 = 1sec

			GIFFrames gifFrames = new GIFFrames(gifInfo.getFps()*1000);
			//Seek to the first frame.
			//todo, add seek function to fix bug
			gifFrames.addBitmap(iplToBitmap(f.grabAtTimestamp((long) gifInfo.getStartTime())));
			for(int i = 0;i<gifInfo.getFps();i++)
			{
				f.grab();
			}
			
			double fpsRecord = 30;
			double fpsDivider = fpsRecord / gifInfo.getFps();
			double deltaTime = gifInfo.getEndTime() - gifInfo.getStartTime();
			for(int timeInc = 1000000;timeInc < deltaTime;timeInc+= 1000000)
			{
				for(int fpsRecordInc = 0;fpsRecordInc < gifInfo.getFps();fpsRecordInc++)
				{
					try
					{
						gifFrames.addBitmap(iplToBitmap(f.grab()));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						f.stop();
						return gifFrames;
					}
					for(int i = 0;i<fpsDivider;i++)
					{
						f.grab();
					}
					
				}
			}
			f.stop();
			return gifFrames;
		}
		
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Bitmap> getBitmapList()
	{
		return bitmapList;
	}


	public GIF createGIF(ArrayList<Bitmap> selectedBitmaps) {
		GIF g = new GIF(selectedBitmaps,_gifInfo.getFps(),_gifInfo.getDelay());
		g.create(_context,_gifInfo.getHeight(),_gifInfo.getWidth());
		return g;
	}

}
