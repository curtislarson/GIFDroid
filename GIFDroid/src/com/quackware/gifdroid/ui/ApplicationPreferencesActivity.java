package com.quackware.gifdroid.ui;

import com.quackware.gifdroid.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class ApplicationPreferencesActivity extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.application_preferences);
		
		setupListeners();
	}
	
	//Mediametadataretriever only works on versions 10+
	
	private void setupListeners()
	{
		((Preference)findPreference("preferencePurchase")).setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.quackware.gifdroidd"));
				startActivity(intent);
				return true;
			} });
	}

}
