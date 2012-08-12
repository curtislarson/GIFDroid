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
