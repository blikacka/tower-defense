/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.Log;
import android.view.View;

public class GifView extends View {

	private InputStream gifInputStream;
	private Movie gifMovie;
	private int movieWidth, movieHeight;
	private long movieDuration;
	private long mMovieStart;
	Context context;
	String item;
	Canvas canvas;
	int x;
	int y;

	public GifView(Context context, String item, int sizeScreenX, int sizeScreenY, int scale) {
		super(context);
		this.context = context;
		this.canvas = new Canvas();
		this.item = item;
		
		setFocusable(true);
		int ide = context.getResources().getIdentifier(item, "drawable", "com.project");
		gifInputStream = context.getResources().openRawResource(ide);

		byte[] array = streamToBytes(gifInputStream);
		gifMovie = Movie.decodeByteArray(array, 0, array.length);

		movieWidth = sizeScreenX / Map.numX * scale;
		movieHeight = sizeScreenY / Map.numY * scale;
		
//		movieWidth = gifMovie.width();
//		movieHeight = gifMovie.height();
		movieDuration = gifMovie.duration();
		
	}

	private static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
		}
		return os.toByteArray();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(movieWidth, movieHeight);
	}

	public int getMovieWidth() {
		return movieWidth;
	}

	public int getMovieHeight() {
		return movieHeight;
	}

	public long getMovieDuration() {
		return movieDuration;
	}

	public String getItem() {
		synchronized (this) {
			return this.item;
		}
	}

	public Canvas getCanvas() {
		synchronized (this) {
			return this.canvas;
		}
	}

	public int getXGif() {
		synchronized (this) {
			return this.x;
		}
	}

	public int getYGif() {
		synchronized (this) {
			return this.y;
		}
	}
	
	public void nextFrame() {
		
		long now = android.os.SystemClock.uptimeMillis();
		if (mMovieStart == 0) { 
			mMovieStart = now;
		}
		
		int relTime = (int) ((now - mMovieStart) % movieDuration);
		
		gifMovie.setTime(relTime);
		Log.d("DUR","relTime = " + relTime);
		gifMovie.draw(getCanvas(), getXGif(), getYGif());
		invalidate();
	}

	public void run() {
		while (true) {

			long now = android.os.SystemClock.uptimeMillis();
			if (mMovieStart == 0) { 
				mMovieStart = now;
			}

			if (gifMovie != null) {
				int dur = gifMovie.duration();
				if (dur == 0) {
					dur = 1000;
				}
//					View v = new View(getContext());
//					v.bringToFront();
				int relTime = (int) ((now - mMovieStart) % dur);

//				Log.d("DUR","relTime = " + relTime);
				gifMovie.setTime(relTime);

				gifMovie.draw(getCanvas(), getXGif(), getYGif());
				invalidate();
			}

		}
	}

}
