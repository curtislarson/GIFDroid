/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
