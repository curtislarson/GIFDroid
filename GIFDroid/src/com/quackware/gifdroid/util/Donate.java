package com.quackware.gifdroid.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class Donate {

	public static boolean isDonate(Context context)
	{
		String mainAppPackage = "com.quackware.gifdroid";
		String keyPackage = "com.quackware.gifdroidd";
		int sigMatch = context.getPackageManager().checkSignatures(mainAppPackage, keyPackage);
		return sigMatch == PackageManager.SIGNATURE_MATCH;
	}

}
