/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.util;

public class GIFPrep {
	
	private double fps;
	private double startTime;
	private double endTime;
	private int width;
	private int height;
	private int delay;
	
	public GIFPrep(double fps, double startTime, double endTime, int width, int height, int delay)
	{
		this.setFps(fps);
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		this.setWidth(width);
		this.setHeight(height);
		this.setDelay(delay);
	}

	public void setFps(double fps) {
		this.fps = fps;
	}

	public double getFps() {
		return fps;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
