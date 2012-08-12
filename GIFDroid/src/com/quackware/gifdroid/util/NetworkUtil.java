/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class NetworkUtil {
	
	public static String getSourceCode(String surl)
	{
		try {
			URL url = new URL(surl);
			 URLConnection urlc = url.openConnection();
	            
	            BufferedInputStream buffer = new BufferedInputStream(urlc.getInputStream());
	            
	            StringBuilder builder = new StringBuilder();
	            int byteRead;
	            while ((byteRead = buffer.read()) != -1)
	                builder.append((char) byteRead);
	            
	            buffer.close();
	            return builder.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static String postData(String data, String surl)
	{
		
		String returnData = "";
		try {
		    // Send data
		    URL url = new URL(surl);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    conn.setDoInput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        returnData = returnData + line;
		    }
		    wr.close();
		    rd.close();
		    return returnData;
		} catch (Exception e) {
			return null;
		}
	}

}
