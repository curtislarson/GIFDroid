/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid;

public class Utility {
	
		  public static byte[] int2byte(int[]src) {
		    int srcLength = src.length;
		    byte[]dst = new byte[srcLength << 2];
		    
		    for (int i=0; i<srcLength; i++) {
		        int x = src[i];
		        int j = i << 2;
		        dst[j++] = (byte) ((x >>> 0) & 0xff);           
		        dst[j++] = (byte) ((x >>> 8) & 0xff);
		        dst[j++] = (byte) ((x >>> 16) & 0xff);
		        dst[j++] = (byte) ((x >>> 24) & 0xff);
		    }
		    return dst;
		}
}
