package com.project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Mob {

	String[] mob;
	Context ctx;
	Paint paint;
	Map map;
	int monsterX = 0, monsterY = 0, startMonsterY;
	int direction = 0, monsterStep = 5, mapX = 0, mapY = 0;
	int sizeScreenX, sizeScreenY;
	int[][] mapArr;
	Animation anim;

	public Mob(String[] mob, Context ctx, Map map, int sizeScreenX, int sizeScreenY, Paint paint) {
		this.mob = mob;
		this.ctx = ctx;
		this.map = map;
		this.sizeScreenX = sizeScreenX;
		this.sizeScreenY = sizeScreenY;
		this.mapArr = map.getMap();
		this.paint = paint;
		this.anim = new Animation(ctx, mob, paint, sizeScreenX, sizeScreenY);
		startMonsterY = map.getStart() * (sizeScreenY / Map.numY);
		mapY = map.getStart();
	}

	public void draw(Canvas c, int hp) {
		movingMonster();
		int scale = 1;
		anim.play(c, monsterX, monsterY, scale, direction * 90);
		hpBar(c, scale, hp);
	}

	public void movingMonster() {
		if (monsterY == 0) {
			monsterY = startMonsterY;
		}

		if (direction == 0) {
			monsterX += monsterStep;
			if (monsterX > sizeScreenX / Map.numX * (mapX + 1)) {
				mapX++;
			};
		} else if (direction == 1) {
			monsterY += monsterStep;
			if (monsterY > sizeScreenY / Map.numY * (mapY + 1)) {
				mapY++;
			};
		} else if (direction == 2) {
			monsterX -= monsterStep;
			if (monsterX < sizeScreenX / Map.numX * (mapX - 1)) {
				mapX--;
			};
		} else if (direction == 3) {
			monsterY -= monsterStep;
			if (monsterY < sizeScreenY / Map.numY * (mapY - 1)) {
				mapY--;
			};
		}

		if (mapArr[mapY][mapX] == 6 && direction == 0) {
			direction = 1;
		} else if (mapArr[mapY][mapX] == 4 && direction == 1) {
			direction = 0;
		} else if (mapArr[mapY][mapX] == 3 && direction == 0) {
			direction = 3;
		} else if (mapArr[mapY][mapX] == 3 && direction == 0) {
			direction = 3;
		} else if (mapArr[mapY][mapX] == 5 && direction == 3) {
			direction = 0;
		} else if (mapArr[mapY][mapX] == 6 && direction == 3) {
			direction = 2;
		} else if (mapArr[mapY][mapX] == 4 && direction == 2) {
			direction = 3;
		} else if (mapArr[mapY][mapX] == 3 && direction == 1) {
			direction = 2;
		} else if (mapArr[mapY][mapX] == 5 && direction == 2) {
			direction = 1;
		} else if (mapArr[mapY][mapX] == Map.end) {
			mapX = 0;
			mapY = map.getStart();
			monsterY = map.getStart() * (sizeScreenY / Map.numY);
			monsterX = 0;
			direction = 0;

		}
	}

	public void hpBar(Canvas c, double scale, int hp) {
		int x = monsterX;
		int y = monsterY;
		double sizeImageX = sizeScreenX / Map.numX * scale;
		double sizeImageY = sizeScreenY / Map.numY * scale;
		paint.setColor(Color.RED);
		c.drawLine(x, y - (int) sizeImageY / 4, (x + (int) sizeImageX) - (int) ((double) sizeImageX * (double) hp / 100), y - (int) sizeImageY / 4, paint);
		paint.setColor(Color.BLACK);
	}

}
