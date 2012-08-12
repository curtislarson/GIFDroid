package com.quackware.gifdroid.ui;


import java.io.File;
import java.io.FilenameFilter;

import com.quackware.gifdroid.R;
import com.quackware.gifdroid.util.Compatibility;
import com.quackware.gifdroid.util.Donate;
import com.quackware.gifdroid.util.ErrorReporter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

// /mnt/sdcard/dcim/Camera/


//Fixed incorrect message from appearing
//Added Application Settings to choose various options such as Decode type
//Couple crash fixes

public class MainActivity extends Activity implements OnTouchListener {
	
	private static final int SELECT_VIDEO = 1;
	private static final int RECORD_VIDEO = 2;
	private static final int SELECT_GIF = 3;

	private static final String TAG = "MainActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupButtonClickListeners();
        checkForEnabledStorage();
        Compatibility.checkCompatibility(this);
        //set auto
        initiateErrorReporter();
        showPopup();
    }

    
    private void showPopup()
    {
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	boolean showPopup = prefs.getBoolean("checkPreferenceShowPopup", true);
    	if(showPopup)
    	{
    		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		 builder.setTitle(getString(R.string.greetTitle));
    		 if(Donate.isDonate(this))
    		 {
    			 builder.setMessage(getString(R.string.greetMessageAndDonate));
    		 }
    		 else
    		 {
    			 builder.setMessage(getString(R.string.greetMessage));
    		 }
    		 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Nothing needs to be here.
				}
			});
    		 builder.setNegativeButton("Never Show Again", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("checkPreferenceShowPopup", false);
					editor.commit();
					
				}
			});
    		 builder.create().show();
    	}
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	loadFileList();
    }
    
    private void initiateErrorReporter()
    {
    	ErrorReporter report = new ErrorReporter();
		report.Init(this);
		report.CheckErrorAndSendMail(this);
    }
    
    private void checkForEnabledStorage()
    {
    	String state = Environment.getExternalStorageState();
    	if(Environment.MEDIA_MOUNTED.equals(state))
    	{
    		//We can read + write
    	}
    	else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
    	{
    		//We can only read
    		Toast.makeText(this, getString(R.string.WARNING_READ_ONLY), Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		//Can neither read nor write.
    		Toast.makeText(this,getString(R.string.WARNING_NO_READ_NOR_WRITE),Toast.LENGTH_LONG).show();
    	}
    }
    
    
    private void setupButtonClickListeners()
    {
    	//Button exitButton = (Button)findViewById(R.id.exit);
    	//exitButton.setOnClickListener(this);

    	((Button)findViewById(R.id.selectVideo)).setOnTouchListener(this);
    	((Button)findViewById(R.id.recordVideo)).setOnTouchListener(this);
    	((Button)findViewById(R.id.viewGallery)).setOnTouchListener(this);
    	
    	
    }
   
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if(resultCode == RESULT_OK)
    	{
    		switch(requestCode)
    		{
    		//Honestly at this point we can treat both of these as the same thing.
    		case SELECT_VIDEO:
    		case RECORD_VIDEO:
    			Uri videoUri = data.getData();
    			String path = getPath(videoUri);
    			//Log.i(TAG,path);
    			if(path != null)
    			{
    				if(path.equals(""))
    				{
    					Toast.makeText(this, "Error retriving path", Toast.LENGTH_SHORT).show();
        				return;
    				}
    			}
    			else
    			{
    				Toast.makeText(this, "Error retriving path", Toast.LENGTH_SHORT).show();
    				return;
    			}
    			//Ok we have the path, thats all we really need so lets go ahead and pass it to CreatorActivity...
    			Intent intent = new Intent(this,IntervalSelectorActivity.class);
    			intent.putExtra("videoPath", path);
    			startActivity(intent);
    			break;
    		case SELECT_GIF:
    			Uri gifUri = data.getData();
    			String gifPath = getPath(gifUri);
    			Log.i(TAG,"GifPath: " + gifPath);
    			Intent previewIntent = new Intent(this,PreviewActivity.class);
				previewIntent.putExtra("gifPath", gifPath);
				startActivity(previewIntent);
    		}
    	}
    }
    
    private String getPath(Uri uri)
    {
    	//file:///mnt/sdcard/DCIM/Camera/VID_20111217_233451.mp4
    	
    	if(uri.toString().contains("content"))
    	{
        	try
        	{
        		String[] projection = {MediaColumns.DATA};
        		Cursor cursor = managedQuery(uri,projection,null,null,null);
        		int column_index = cursor.getColumnIndex(MediaColumns.DATA);
        		cursor.moveToFirst();
        		return cursor.getString(column_index);
        	}
        	catch(Exception ex)
        	{
        		return null;
        	}
    	}
    	else
    	{
    		return uri.toString();
    	}
    }
    
    private void handleClickEvent(View v)
    {
    	switch(v.getId())
		{
		case R.id.viewGallery:
			loadFileList();
			if(mFileList.length > 0)
			{
				AlertDialog.Builder builder = new Builder(this);

					builder.setTitle("Choose your file");
					builder.setItems(mFileList, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mChosenFile = mFileList[which];
							Intent previewIntent = new Intent(MainActivity.this,
									PreviewActivity.class);
							previewIntent.putExtra("gifPath", getApplicationContext()
									.getExternalFilesDir(null) + "/" + mChosenFile);
							startActivity(previewIntent);
						}
					});
				builder.create().show();
				
			}
			else
			{
				Toast.makeText(this, "No GIFs in your gallery!", Toast.LENGTH_SHORT).show();
			}
			///Intent viewIntent = new Intent();
			//viewIntent.setType("image/gif");
			//viewIntent.setAction(Intent.ACTION_PICK);
			//startActivityForResult(Intent.createChooser(viewIntent,"Select GIF"),SELECT_GIF);
			break;
		case R.id.selectVideo:
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent,"Select Video"),SELECT_VIDEO);
			//image/*
			break;
		case R.id.recordVideo:
			Intent recordIntent = new Intent();
			recordIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			recordIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			//recordIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(recordIntent,RECORD_VIDEO);
			break;
		}
    }
    
    

	//#FFA500
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			switch(v.getId())
			{
			case R.id.selectVideo:
				((Button)findViewById(R.id.selectVideo)).setBackgroundColor(0xFFFFA500);
				break;
			case R.id.recordVideo:
				((Button)findViewById(R.id.recordVideo)).setBackgroundColor(0xFFFFA500);
				break;
			case R.id.viewGallery:
				((Button)findViewById(R.id.viewGallery)).setBackgroundColor(0xFFFFA500);
				break;
			}
			return true;
		case MotionEvent.ACTION_UP:
			switch(v.getId())
			{
			case R.id.selectVideo:
				((Button)findViewById(R.id.selectVideo)).setBackgroundColor(Color.BLACK);
				handleClickEvent(v);
				break;
			case R.id.recordVideo:
				((Button)findViewById(R.id.recordVideo)).setBackgroundColor(Color.BLACK);
				handleClickEvent(v);
				break;
			case R.id.viewGallery:
				((Button)findViewById(R.id.viewGallery)).setBackgroundColor(Color.BLACK);
				handleClickEvent(v);
				break;
			}
			
		default:
			return true;
		}
	}
	
	   //In an Activity
	private String[] mFileList;
	private File mPath;
	private String mChosenFile;
	private static final String FTYPE = ".gif";    
	private static final int DIALOG_LOAD_FILE = 1000;

	private void loadFileList(){	
		
		try
		{
			mPath = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/");
			Log.i(TAG,"loadFileList() path: " + mPath.getAbsolutePath() + "/");
		}
		catch(Exception ex)
		{
			mPath = null;
			return;
		}
	  try{
	     mPath.mkdirs();
	  }
	  catch(SecurityException e){
	     Log.e(TAG, "unable to write on the sd card " + e.toString());
	  }
	  if(mPath.exists()){
	     FilenameFilter filter = new FilenameFilter(){
	         @Override
			public boolean accept(File dir, String filename){
	             File sel = new File(dir, filename);
	             return filename.contains(FTYPE) || sel.isDirectory();
	         }
	     };
	     mFileList = mPath.list(filter);
	  }
	  else{
	    mFileList= new String[0];
	  }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.settingsItem:
	        	Intent i = new Intent(MainActivity.this,ApplicationPreferencesActivity.class);
	        	startActivity(i);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose your file");
			if (mFileList == null) {
				Log.e(TAG, "Showing file picker before loading the file list");
				dialog = builder.create();
				return dialog;
			}
			builder.setItems(mFileList, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mChosenFile = mFileList[which];
					Intent previewIntent = new Intent(MainActivity.this,
							PreviewActivity.class);
					previewIntent.putExtra("gifPath", getApplicationContext()
							.getExternalFilesDir(null) + "/" + mChosenFile);
					startActivity(previewIntent);
				}
			});
			break;
		}
		dialog = builder.create();
		return dialog;
	}
}