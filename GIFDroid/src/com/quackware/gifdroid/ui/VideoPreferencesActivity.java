package com.quackware.gifdroid.ui;

import com.quackware.gifdroid.R;
import com.quackware.gifdroid.util.Compatibility;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class VideoPreferencesActivity extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.video_preferences);
		
		Bundle extras = getIntent().getExtras();
		int width = 0;
		int height = 0;
		if(extras != null)
		{
			width = extras.getInt("width");
			height = extras.getInt("height");
		}
		else
		{
			//UH OH
		}
		
		setPreferences(width,height);
		checkCompatibilities();
	}
	
	private void checkCompatibilities()
	{
		//Remove mmdr as a choice.
		if(!Compatibility.SUPPORT_MEDIAMETADATARETRIEVER)
		{
			ListPreference pref = (ListPreference)findPreference("listPreferenceDecoder");
			pref.setEntries(getResources().getStringArray(R.array.frameDecoderArrayFFMPEG));
			pref.setEntryValues(getResources().getStringArray(R.array.frameDecoderArrayFFMPEGValues));
		}
		if(!Compatibility.SUPPORT_FFMPEG)
		{
			ListPreference pref = (ListPreference)findPreference("listPreferenceDecoder");
			pref.setEntries(getResources().getStringArray(R.array.frameDecoderArrayMMDR));
			pref.setEntryValues(getResources().getStringArray(R.array.frameDecoderArrayMMDRValues));
		}
	}
	
	private void setPreferences(int width, int height)
	{
		if(width != 0 && height != 0)
		{
			((ListPreference)findPreference("listPreferenceWidth")).setEntries(new CharSequence[] {"" + width*.75,"" + width*.5, "" + width*.25, "" + width*.1});
			((ListPreference)findPreference("listPreferenceWidth")).setEntryValues(new CharSequence[] {"" + width*.75,"" + width*.5, "" + width*.25, "" + width*.1});
			((ListPreference)findPreference("listPreferenceWidth")).setDefaultValue("" + width*.1);
			((ListPreference)findPreference("listPreferenceHeight")).setEntries(new CharSequence[] {"" + height*.75,"" + height*.5, "" + height*.25, "" + height*.1});
			((ListPreference)findPreference("listPreferenceHeight")).setEntryValues(new CharSequence[] {"" + height*.75,"" + height*.5, "" + height*.25, "" + height*.1});
			((ListPreference)findPreference("listPreferenceHeight")).setDefaultValue("" + height*.1);
		}
	}

}
