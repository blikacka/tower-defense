/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

/**
 *
 * @author Jakub Cieciala
 */
public class Animation extends View {

	Context ctx;
	String items[];
	int sizeX = 1;
	int sizeY = 1;
	Paint paint;
	int numX, numY;
	static int callFunc = 0;

	public Animation(Context ctx, String items[], Paint paint, int sizeX, int sizeY) {
		super(ctx);
		this.ctx = ctx;
		this.paint = paint;
		this.items = items;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.numX = Map.numX;
		this.numY = Map.numY;
	}

	public void play(Canvas c, int x, int y, double scale, int rotate) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotate);
		double sizeImageX = sizeX / numX * scale;
		double sizeImageY = sizeY / numY * scale;
		int ide = ctx.getResources().getIdentifier(items[callFunc], "drawable", "com.project");
		Bitmap imgB = BitmapFactory.decodeResource(ctx.getResources(), ide);
		Bitmap img = Bitmap.createScaledBitmap(imgB, (int) sizeImageX, (int) sizeImageY, true);
		Bitmap rotatedBitmap = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
		c.drawBitmap(rotatedBitmap, x, y, paint);
		if (callFunc < items.length - 1) {
			callFunc++;
		} else {
			callFunc = 0;
		}
	}



}
