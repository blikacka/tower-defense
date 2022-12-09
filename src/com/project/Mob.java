package com.project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import static com.project.AudioPlayer.position;

public class Mob {

	String[] mob;
	Context ctx;
	Paint paint;
	Map map;
	int monsterX = 0, monsterY = 0, startMonsterY, round;
	int direction = 0, monsterStep = 1, mapX = 0, mapY = 0;
	static int numberMobs = 15, life = 10;
	int sizeScreenX, sizeScreenY;
	double prom = 0, prom2 = 0;
	double hp = 0;
	int[][] mapArr;
	Animation anim;
	Tower tower;
	Rand rand;
	MediaPlayer shot_sound, doh;
	static boolean effect;

	public Mob(String[] mob, Context ctx, Map map, int sizeScreenX, int sizeScreenY, Paint paint, int round) {
		this.mob = mob;
		this.ctx = ctx;
		this.map = map;
		this.sizeScreenX = sizeScreenX;
		this.sizeScreenY = sizeScreenY;
		this.mapArr = map.getMap();
		this.paint = paint;
		this.round = round;
		this.anim = new Animation(ctx, mob, paint, sizeScreenX, sizeScreenY);
		startMonsterY = map.getStart() * (sizeScreenY / Map.numY);
		mapY = map.getStart();
		tower = new Tower(map);
		rand = new Rand();
		shot_sound = MediaPlayer.create(ctx, R.raw.shot);
		doh = MediaPlayer.create(ctx, R.raw.doh);
		effect = MainActivity.effect;

	}

	public int getLife() {
		return life;
	}

	public double draw(Canvas c) {
		effect = MainActivity.effect;
		movingMonster();
		shot(c);
		int scale = 1;
		anim.play(c, monsterX, monsterY, scale, direction * 90);
		hpBar(c, scale, hp);
		return hp;
	}

	public void line(Canvas c, int i, int j) {
		c.drawLine(((mapX - j) * sizeScreenX / Map.numX) + ((sizeScreenX / Map.numX) / 2), ((mapY - i) * sizeScreenY / Map.numY) + ((sizeScreenY / Map.numY) / 2), monsterX + (anim.getImageWidth() / 2), monsterY + (anim.getImageHeight() / 2), paint);
	}

	public void shot(Canvas c) {
		int[][] mapArrWithTowers = tower.getMapArr();

		if ((mapX >= 1 && mapX <= Map.numX) && (mapY >= 1 && mapY <= Map.numY)) {
			for (int i = -2; i <= 1; i++) {
				for (int j = -2; j <= 1; j++) {
					if (mapArrWithTowers[mapY - i][mapX - j] == 23 && prom % 70 == 0) {
						hp += rand.randDouble(10 / (double) round, 75 / (double) round + (double) 1);
						paint.setColor(Color.RED);
						line(c, i, j);
						if (effect) {
							shot_sound.start();
						}
					} else if (mapArrWithTowers[mapY - i][mapX - j] == 24 && prom % 17 == 0) {
						hp += rand.randDouble(5 / (double) round, 25 / (double) round);
						paint.setColor(Color.BLUE);
						line(c, i, j);
						if (effect) {
							shot_sound.start();
						}
					} else if (mapArrWithTowers[mapY - i][mapX - j] == 25 && prom % 5 == 0) {
						hp += rand.randDouble(1 / (double) round, 3 / (double) round);
						paint.setColor(Color.GREEN);
						line(c, i, j);
						if (effect) {
							shot_sound.start();
						}
					}
				}
			}
		}

		if (prom > 1000) {
			prom = 0;
		} else {
			prom++;
		}

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
			life--;
			if (effect) {
				doh.start();
			}
		}
	}

	public void hpBar(Canvas c, double scale, double hp) {
		int x = monsterX;
		int y = monsterY;
		double sizeImageX = sizeScreenX / Map.numX * scale;
		double sizeImageY = sizeScreenY / Map.numY * scale;
		paint.setColor(Color.RED);
		c.drawLine(x, y - (int) sizeImageY / 4, (x + (int) sizeImageX) - (int) ((double) sizeImageX * (double) hp / 100), y - (int) sizeImageY / 4, paint);
		paint.setColor(Color.BLACK);
	}

}
