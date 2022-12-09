package com.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.widget.Toast;
import java.util.Arrays;

public class Tower {

	Bitmap mapBitmap;
	String item;
	Context context;
	Map map;
	Paint paint;
	int touchX, touchY;
	int sizeX, sizeY;
	int mapX, mapY;
	static int[][] mapArr;
	static int prom = 0;

	public Tower(Bitmap mapBitmap, String item, Context context, Map map, Paint paint, int touchX, int touchY, int sizeX, int sizeY) {
		this.mapBitmap = mapBitmap;
		this.item = item;
		this.context = context;
		this.map = map;
		this.paint = paint;
		this.touchX = touchX;
		this.touchY = touchY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		if (prom == 0) {
			mapArr = map.getMap();
		}
		if (prom > 1000) {
			prom = 1;
		} else {
			prom++;
		}
	}

	public Tower(Map map) {
		this.map = map;
		if (prom == 0) {
			mapArr = map.getMap();
		}
		if (prom > 1000) {
			prom = 1;
		} else {
			prom++;
		}
	}

	public int[][] getMapArr() {
		return mapArr;
	}

	public Bitmap updateMap() {
		if (validate()) {
			int index = Arrays.asList(map.items).indexOf(item);
			mapArr[mapY][mapX] = index;
			return map.drawByArray(mapArr);
		} else {
			Toast.makeText(context, context.getString(R.string.cant_build), Toast.LENGTH_SHORT).show();
			return mapBitmap;
		}

	}

	public boolean validate() {
		mapX = touchX / (sizeX / Map.numX);
		mapY = touchY / (sizeY / Map.numY);
		return mapArr[mapY][mapX] == 0;
	}

}
