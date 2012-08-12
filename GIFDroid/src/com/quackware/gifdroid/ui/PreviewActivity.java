/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.ui;

import java.io.File;

import com.quackware.gifdroid.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class PreviewActivity extends Activity {
	
	private static final String TAG = "PreviewActivity";
	private String _path = null;
	
	private static final int RENAME_DIALOG = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		setupListeners();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			_path = extras.getString("gifPath");
		}
		else
		{
			//UH OH
		}
		setWebView(_path);
		
	}
	
	private void setWebView(String path)
	{
		Log.i(TAG,"Setting WebView path: " + path);
		WebView wv = (WebView)findViewById(R.id.previewWebView);
		wv.getSettings().setLoadsImagesAutomatically(true);
		wv.setBackgroundColor(Color.BLACK);
		String html = "<html><center><img src=\"file://" + path + "\"></center></html>";
		wv.loadDataWithBaseURL(getApplicationContext()
				.getExternalFilesDir(null) + "/", html,"text/html","utf-8","");
		//wv.loadUrl("file://" + path);
	}
	
	@Override
	protected Dialog onCreateDialog(int id){
		  AlertDialog.Builder builder = new AlertDialog.Builder(this);

		  switch(id){
		  case RENAME_DIALOG:
			  builder.setTitle("Rename GIF");
			  final EditText input = new EditText(this);
			  final File f = new File(_path);
			  //Get the name, but strip .gif
			  input.setText(f.getName().replace(".gif", ""));
			  builder.setView(input);
			  builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				  @Override
				public void onClick(DialogInterface dialog, int whichButton) {
				    Editable value = input.getText();
				    try
				    {
				    	String newName = f.getParent() + "/" + value + ".gif";
				    	_path = newName;
				    	f.renameTo(new File(newName));
				    } catch(Exception ex) {
				    	Log.e(TAG,"Error renaming file");
				    	ex.printStackTrace();
				    }
				    }
				  });

			  builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
					public void onClick(DialogInterface dialog, int whichButton) {
				      // Canceled.
				    }
				  });
			  return builder.create();
		  
		  }
		  return null;
	}
	
	private void setupListeners()
	{
		((Button)findViewById(R.id.shareButton)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(_path)));
					sendIntent.setType("image/gif");
					startActivity(Intent.createChooser(sendIntent,getResources().getText(R.string.send_to)));
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });
		((Button)findViewById(R.id.returnButton)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					try
					{
						File f = new File(_path);
						f.delete();
					}
					catch(Exception ex)
					{
						
					}
					PreviewActivity.this.finish();
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });
		((Button)findViewById(R.id.renameButton)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					showDialog(RENAME_DIALOG);
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });
		((Button)findViewById(R.id.createAnotherButton)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(0xFFFFA500);
					return true;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.BLACK);
					startActivity(new Intent(getApplicationContext(),MainActivity.class));
					return true;
				default:
					v.setBackgroundColor(Color.BLACK);
					return false;
				}
			} });
	}

}
