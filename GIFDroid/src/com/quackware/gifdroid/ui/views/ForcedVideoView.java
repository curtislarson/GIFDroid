/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class ForcedVideoView extends VideoView {

	private int mForcedHeight = 0;
	private int mForcedWidth = 0;
	
	public ForcedVideoView(Context context) {
        super(context);
    }

    public ForcedVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForcedVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setDimensions(int height, int width)
    {
    	mForcedHeight = height;
    	mForcedWidth = width;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
    	setMeasuredDimension(mForcedWidth,mForcedHeight);
    }

}
