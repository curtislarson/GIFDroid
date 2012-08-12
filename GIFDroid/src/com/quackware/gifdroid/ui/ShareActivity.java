/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.ui;

import com.quackware.gifdroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShareActivity extends Activity {
	
	
	private String _imgurUrl = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			_imgurUrl  = extras.getString("gifPath");
		}
		else
		{
			//UH OH
		}
		
		setImgurUrl();
		
	}
	
	private void setImgurUrl()
	{
		((TextView)findViewById(R.id.urlTextView)).setText(_imgurUrl);
	}

}
