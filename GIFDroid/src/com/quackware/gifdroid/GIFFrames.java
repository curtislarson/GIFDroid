package com.quackware.gifdroid;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.quackware.gifdroid.encoder.AnimatedGifEncoder;

import android.graphics.Bitmap;

public class GIFFrames {
	
	private ArrayList<Bitmap> _bitmaps;
	double _fps;
	
	public GIFFrames(double fps)
	{
		_bitmaps = new ArrayList<Bitmap>();
		_fps = fps;
	}
	
	public void addBitmap(Bitmap b)
	{
		_bitmaps.add(b);
	}
	
	public ArrayList<Bitmap> getBitmapList()
	{
		return _bitmaps;
	}
	
	public void setBitmapList(ArrayList<Bitmap> bmp)
	{
		_bitmaps = bmp;
	}
	
	
	public boolean saveToFileSystem(String fileName)
	{
		try
		{
			AnimatedGifEncoder e = new AnimatedGifEncoder();
			e.start(fileName);
			e.setDelay((int)_fps);
			addFrames(e);
			e.finish();
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public byte[] saveToByteArray()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start(bos);
		e.setDelay((int)_fps);
		addFrames(e);
		e.finish();
		return bos.toByteArray();
	}
	
	private void addFrames(AnimatedGifEncoder e)
	{
		int size = _bitmaps.size();
		for(int i = 0;i<size;i++)
		{
			
			e.addFrame(_bitmaps.get(i));
		}
	}

	
	 /*     AnimatedGifEncoder e = new AnimatedGifEncoder();
	 *     e.start(outputFileName);
	 *     e.setDelay(1000);   // 1 frame per sec
	 *     e.addFrame(image1);
	 *     e.addFrame(image2);
	 *     e.finish();
	 */
	
	/*public void saveAsGIF(String path)
	{
		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start(path);
		e.setDelay((int) (_fps * 1000));
		for(int i = 0;i<_bitmaps.size();i++)
		{
			e.addFrame(_bitmaps.get(i));
		}
		e.finish();
	}*/

}
