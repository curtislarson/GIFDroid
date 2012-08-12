/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.quackware.gifdroid.util;

import com.quackware.gifdroid.ui.ImageAdapter;
import com.quackware.gifdroid.ui.FrameSelectorActivity.FrameGrabberTask;
import com.quackware.gifdroid.ui.FrameSelectorActivity.GifCreatorTask;

public class MyDataStore {
	
	//FrameSelectorActivity
	private FrameGrabberTask _fgt;
	private GifCreatorTask _gifCreatorTask;
	private boolean _loaded;
	private ImageAdapter _adapter;

	public void set_frameGrabberTask(FrameGrabberTask _fgt) {
		this._fgt = _fgt;
	}

	public FrameGrabberTask get_frameGrabberTask() {
		return _fgt;
	}

	public void setFrameGrabberTaskLoaded(boolean loaded) {
		_loaded = loaded;
	}

	public boolean getFrameGrabberTaskLoaded() {
		return _loaded;
	}

	public void setGifCreatorTask(GifCreatorTask _gifCreatorTask) {
		this._gifCreatorTask = _gifCreatorTask;
	}

	public GifCreatorTask getGifCreatorTask() {
		return _gifCreatorTask;
	}

	public void setImageAdapter(ImageAdapter _adapter) {
		this._adapter = _adapter;
	}

	public ImageAdapter getImageAdapter() {
		return _adapter;
	}

}
