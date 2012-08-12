/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	ArrayList<Bitmap> bitmaps;
	private Context mContext;
	private int[] mapping;
	
	private View mTempView = null;

	public ImageAdapter(Context c, ArrayList<Bitmap> b)
	{
		mContext = c;
		bitmaps = b;
		mapping = new int[bitmaps.size()];
	}
	@Override
	public int getCount() {
		return bitmaps.size();
	}

	@Override
	public Object getItem(int position) {
		return bitmaps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean areAnySelected()
	{
		int size = mapping.length;
		for(int i = 0;i<size;i++)
		{
			if(mapping[i] == 1)
			{
				return true;
			}
		}
		return false;
	}
	
	public void selectAll(GridView v)
	{
		for(int i = 0;i<mapping.length;i++)
		{
			mapping[i] = 1;
			View view = v.getChildAt(i);
			if(view != null)	
				view.setBackgroundColor(Color.GREEN);	
		}
	}
	
	public void deselectAll(GridView v)
	{
		for(int i = 0;i<mapping.length;i++)
		{
			mapping[i] = 0;
			View view = v.getChildAt(i);
			if(view != null)
				view.setBackgroundColor(Color.BLACK);
		}
	}

	public ArrayList<Bitmap> getSelectedBitmaps()
	{
		ArrayList<Bitmap> returnBitmaps = new ArrayList<Bitmap>();
		for(int i = 0;i<mapping.length;i++)
		{
			if(mapping[i] == 1)
			{
				returnBitmaps.add(bitmaps.get(i));
			}
		}
		return returnBitmaps;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        //if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            if(mapping[position] == 1)
            {
            	imageView.setBackgroundColor(Color.GREEN);
            }
            else
            {
            	imageView.setBackgroundColor(Color.BLACK);
            }
            setListeners(imageView);
        //} else {
         //   imageView = (ImageView) convertView;
        //}

        imageView.setImageBitmap(bitmaps.get(position));
        return imageView;
	}
	
	private void setListeners(ImageView imageView)
	{
		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				
				Log.i("Action",event.getAction() + "");
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					mTempView = v;
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (v.equals(mTempView)) {
						Bitmap b = ((BitmapDrawable) ((ImageView) v)
								.getDrawable()).getBitmap();
						int position = bitmaps.indexOf(b);
						if (mapping[position] == 1) {
							v.setBackgroundColor(Color.BLACK);
							mapping[position] = 0;
						} else {
							v.setBackgroundColor(Color.GREEN);
							mapping[position] = 1;
						}
					}
					mTempView = null;
				}
				return true;
			} });
	}
}
