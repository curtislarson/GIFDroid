/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.util;

import java.io.File;
import java.io.FileInputStream;

public class Util {
	
	private static int parseMinutes(String time)
	{
		return Integer.parseInt(time.substring(0, time.indexOf(':')));
	}
	
	private static int parseSeconds(String time)
	{
		return Integer.parseInt(time.substring(time.indexOf(':') + 1,time.length()));
	}
	
	public static double parseMicrosecondTime(String time)
	{
		return parseMinutes(time)*60*1000000 + parseSeconds(time)*1000000;
	}
	
    public static byte[] readFromFile(File file)
    {
    	try
    	{
    		FileInputStream fis = new FileInputStream(file);
    		byte[] videoFile = new byte[(int)file.length()];
    		fis.read(videoFile);
    		fis.close();
    		return videoFile;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
    }
    
    

}
