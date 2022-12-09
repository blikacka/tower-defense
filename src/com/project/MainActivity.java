package com.project;

import android.app.Activity;
import android.content.Context;
import static android.content.Context.WIFI_SERVICE;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity implements OnTouchListener {

	OurView view;
	Move move;
	Bitmap ball, blob, bg, mapBitmap;
	Image stopwatch;
	Paint paint;
	int pressX, pressY, pointX, pointY, teamMateX, teamMateY, teamMateXM, teamMateYM;
	int sizeScreenX, sizeScreenY;
	int sizeBlockX, sizeBlockY;
	static int level = 0;
	int round = 1, money = 16, life = 1, newRoundProm = 500, score = 0;
	int spacing = 45, sp = 0, ssp = 0;  // MOB nastavení - spacing - mezera mezi mobama, sp a ssp pomocné proměnné
	String name;
	GifView gifView, wy, tr;
	Context context;
	Network network;
	GetLine getLine;
	Map map;
	Rand rand = new Rand();
	int[][] mapArr;
	Animation wisp, tree;
	String fromServer = null;
	Boolean multiplayer = false;
	String wisps[] = new String[23];
	String trees[] = new String[23];
	String knights[] = new String[23];
	String dragons[] = new String[23];
	String banshee[] = new String[23];
	String wolf[] = new String[23];
	String assasin[] = new String[23];
	String redwisp[] = new String[23];
	List<Mob> mob = new ArrayList<Mob>();
	Tower tower;
	boolean newRound = true, gameOver = false;
	static boolean sound = true, effect = true;
	String[] mobItems;
	MediaPlayer countdown, game_over, lvlup, new_round, create_tower, bgMusic;
	Database db;
//	List<Tower> tower = new ArrayList<Tower>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		name = rand.randString(10);

		if (multiplayer) {
			network = new Network();
			new Thread(network).start();

			getLine = new GetLine();
			new Thread(getLine).start();
		}

		if (sound) {
			bgMusic = MediaPlayer.create(context, R.raw.background_music_pacific);
			bgMusic.setLooping(true);
			final float volumes = (float) (1 - (Math.log(100 - 50) / Math.log(100)));
			bgMusic.setVolume(volumes, volumes);
			bgMusic.start();
		}

		countdown = MediaPlayer.create(context, R.raw.countdown);
		game_over = MediaPlayer.create(context, R.raw.game_over);
		lvlup = MediaPlayer.create(context, R.raw.lvlup);
		new_round = MediaPlayer.create(context, R.raw.new_round);
		create_tower = MediaPlayer.create(context, R.raw.create_tower);

//		Toast.makeText(this, ipaddr(), Toast.LENGTH_LONG).show();
		move = new Move(this);
		paint = new Paint();
		db = new Database(context);

		for (int i = 0; i < 23; i++) {
			wisps[i] = "wisp_" + String.valueOf(i + 1);
			trees[i] = "tree_" + String.valueOf(i + 1);
			knights[i] = "knight_" + String.valueOf(i + 1);
			dragons[i] = "dragon_" + String.valueOf(i + 1);
			banshee[i] = "banshee_" + String.valueOf(i + 1);
			wolf[i] = "wolf_" + String.valueOf(i + 1);
			assasin[i] = "assasin_" + String.valueOf(i + 1);
			redwisp[i] = "redwisp_" + String.valueOf(i + 1);
		}

		pressX = pressY = teamMateX = teamMateY = teamMateXM = teamMateYM = 0;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		sizeScreenX = dm.widthPixels;
		sizeScreenY = dm.heightPixels;
		pointX = pressX = sizeScreenX / 2;
		pointY = pressY = sizeScreenY / 2;

//		level = rand.randInt(1, 3);
//		level = 3;
		map = new Map(sizeScreenX, sizeScreenY, this, paint, level);
		mapBitmap = map.draw(level);

		wisp = new Animation(this, wisps, paint, sizeScreenX, sizeScreenY);

		wy = new GifView(this, "wisp_yellow", sizeScreenX, sizeScreenY, 1);
		stopwatch = new Image(this, "stopwatch", paint, sizeScreenX, sizeScreenY);

		view = new OurView(this);
		view.setOnTouchListener(this);
		android.view.ViewGroup.LayoutParams lp = new android.view.ViewGroup.LayoutParams(dm.widthPixels, dm.heightPixels);

		setContentView(view, lp);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sound_butt:
				if (sound) {
					bgMusic.stop();
					bgMusic.reset();
					bgMusic = null;
					sound = false;
					Toast.makeText(context, getText(R.string.sound_off), Toast.LENGTH_SHORT).show();
				} else {
					sound = true;
					bgMusic = MediaPlayer.create(context, R.raw.background_music_pacific);
					bgMusic.setLooping(true);
					final float volumes = (float) (1 - (Math.log(100 - 50) / Math.log(100)));
					bgMusic.setVolume(volumes, volumes);
					bgMusic.start();
					Toast.makeText(context, getText(R.string.sound_on), Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.effect_butt:
				if (effect) {
					effect = false;
					Toast.makeText(context, getText(R.string.effect_off), Toast.LENGTH_SHORT).show();
				} else {
					effect = true;
					Toast.makeText(context, getText(R.string.effect_on), Toast.LENGTH_SHORT).show();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public String ipaddr() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
		return ip;
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.resume();
	}

	public class OurView extends SurfaceView implements Runnable {

		int col = 0, row = 0;
		Thread t = null;
		SurfaceHolder holder;
		boolean isItOK = true;
		String fs;

		public void sleep(int num) {
			try {
				Thread.sleep(num);
			} catch (InterruptedException ex) {
				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		public OurView(Context context) {
			super(context);
			holder = getHolder();
		}

		public void mobLogic(Canvas c) {
			switch (round) {
				case 1:
					mobItems = trees;
					break;
				case 2:
					mobItems = knights;
					break;
				case 3:
					mobItems = wolf;
					break;
				case 4:
					mobItems = dragons;
					break;
				case 5:
					mobItems = banshee;
					break;
				case 6:
					mobItems = assasin;
					break;
				case 7:
					mobItems = redwisp;
					break;
				default:
					mobItems = wisps;
					break;
			}
			if (sp == spacing * ssp) {
				mob.add(new Mob(mobItems, context, map, sizeScreenX, sizeScreenY, paint, round));
				ssp++;
			}
			for (int i = 0; i < mob.size(); i++) {
				life = mob.get(i).getLife();

				if (mob.get(i).draw(c) >= 100) {
					score = score + (1 * round);
					money += 1;
					mob.remove(i);
				}
			}
			if (sp != spacing * (Mob.numberMobs - 1)) {
				sp++;
			}
			if (mob.isEmpty()) {
				round++;
				sp = 0;
				ssp = 0;
				money += 2;
				newRound = true;
				if (effect) {
					lvlup.start();
				}
			}

		}

		public void textInfo(Canvas c) {
			paint.setColor(Color.YELLOW);
			paint.setTextSize(sizeScreenX / Map.numX / 2);
			c.drawText(String.valueOf(money), sizeScreenX - ((sizeScreenX / Map.numX) * 7) + (sizeScreenX / Map.numX / 5), sizeScreenY - ((sizeScreenY / Map.numY) * 2) + (sizeScreenY / Map.numY / 5), paint);
			c.drawText(String.valueOf(round), sizeScreenX - ((sizeScreenX / Map.numX) * 7) + (sizeScreenX / Map.numX / 5), sizeScreenY - ((sizeScreenY / Map.numY)) + (sizeScreenY / Map.numY / 5), paint);
			c.drawText(String.valueOf(life), sizeScreenX - ((sizeScreenX / Map.numX) * 9) + (sizeScreenX / Map.numX / 5), sizeScreenY - ((sizeScreenY / Map.numY)) + (sizeScreenY / Map.numY / 5), paint);
			if (newRound) {
				stopwatch.draw(c, ((sizeScreenX / Map.numX) * Map.numX) - ((sizeScreenX / Map.numX) * 10), ((sizeScreenY / Map.numY) * Map.numY) - ((sizeScreenY / Map.numY) * 2), 1, 0);
				c.drawText(String.valueOf(newRoundProm / 100), sizeScreenX - ((sizeScreenX / Map.numX) * 9) + (sizeScreenX / Map.numX / 5), sizeScreenY - ((sizeScreenY / Map.numY) * 2) + (sizeScreenY / Map.numY / 5), paint);
			}
			paint.setColor(Color.BLACK);
		}

		public void run() {
			while (isItOK == true) {
				if (!holder.getSurface().isValid()) {
					continue;
				}
				if (!gameOver) {

					move.moving();
					Canvas c = holder.lockCanvas();
					c.drawRect(new Rect(0, 0, sizeScreenX, sizeScreenY), paint);
					c.drawBitmap(mapBitmap, 0, 0, paint);

					if (newRound) {
						if (newRoundProm == 0) {
							if (effect) {
								new_round.start();
							}
							newRoundProm = 500;
							newRound = false;
						} else {
							if (newRoundProm % 100 == 0) {
								if (effect) {
									final float volume = (float) (1 - (Math.log(100 - 50) / Math.log(100)));
									countdown.setVolume(volume, volume);
									countdown.start();
								}
							}
							newRoundProm--;
						}
					} else {
						mobLogic(c);
					}
					textInfo(c);
					wisp.play(c, pointX - wisp.getImageWidth() / 2, pointY - wisp.getImageHeight() / 2, 1.75, 0);

//		Animace z animace - ĹľlutĂˇ bludiÄŤka
//				wy.canvas = c;
//				wy.x = pointX;
//				wy.y = pointY;
//				wy.nextFrame();
//				if (multiplayer) {	// spoluhrĂˇÄŤ
//					network.pointX = pressX;
//					network.pointY = pressY;
//					network.name = name;
//				}
//
					holder.unlockCanvasAndPost(c);
					if (life == 0) {
						if (effect) {
							game_over.start();
						}
						gameOver = true;
						Tower.mapArr = map.getMap();
						Mob.life = 10;
						Tower.prom = 0;

						db.insert(score);
						Intent intent = new Intent(context, EndActivity.class);
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						mapBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] byteArray = stream.toByteArray();
						intent.putExtra("image", byteArray);
						finish();
						context.startActivity(intent);
					}
				}
			}
		}

		public void pause() {
			isItOK = false;
			while (true) {
				try {
					t.join();
				} catch (InterruptedException e) {
				}
				break;
			}
		}

		public void resume() {
			isItOK = true;
			t = new Thread(this);
			t.start();
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int pressedX = Math.round(event.getX());
			int pressedY = Math.round(event.getY());
			if ((pressedX <= sizeScreenX && pressedX >= sizeScreenX - (2 * sizeScreenX / Map.numX)) && (pressedY <= sizeScreenY && pressedY >= sizeScreenY - (2 * sizeScreenY / Map.numY))) {
				if (money >= 16) {
					Bitmap bitm = mapBitmap;
					tower = new Tower(mapBitmap, "tower_one", context, map, paint, pointX, pointY, sizeScreenX, sizeScreenY);
					mapBitmap = tower.updateMap();
					if (bitm != mapBitmap) {
						if (effect) {
							create_tower.start();
						}
						money -= 16;
					}
				} else {
					Toast.makeText(context, getText(R.string.no_money), Toast.LENGTH_SHORT).show();
				}
			} else if ((pressedX <= sizeScreenX - (2 * sizeScreenX / Map.numX) && pressedX >= sizeScreenX - (4 * sizeScreenX / Map.numX)) && (pressedY <= sizeScreenY && pressedY >= sizeScreenY - (2 * sizeScreenY / Map.numY))) {
				if (money >= 8) {

					Bitmap bitm = mapBitmap;
					tower = new Tower(mapBitmap, "tower_two", context, map, paint, pointX, pointY, sizeScreenX, sizeScreenY);
					mapBitmap = tower.updateMap();
					if (bitm != mapBitmap) {
						if (effect) {
							create_tower.start();
						}
						money -= 8;
					}
				} else {
					Toast.makeText(context, getText(R.string.no_money), Toast.LENGTH_SHORT).show();
				}
			} else if ((pressedX <= sizeScreenX - (4 * sizeScreenX / Map.numX) && pressedX >= sizeScreenX - (6 * sizeScreenX / Map.numX)) && (pressedY <= sizeScreenY && pressedY >= sizeScreenY - (2 * sizeScreenY / Map.numY))) {
				if (money >= 4) {
					Bitmap bitm = mapBitmap;
					tower = new Tower(mapBitmap, "tower_three", context, map, paint, pointX, pointY, sizeScreenX, sizeScreenY);
					mapBitmap = tower.updateMap();
					if (bitm != mapBitmap) {
						if (effect) {
							create_tower.start();
						}
						money -= 4;
					}
				} else {
					Toast.makeText(context, getText(R.string.no_money), Toast.LENGTH_SHORT).show();
				}
			} else {
				pressX = pressedX;
				pressY = pressedY;
			}
		}
		return true;
	}

	public class Move extends View {

		public Move(Context context) {
			super(context);
		}

		public void moving() {
			int greaterThan = 9;
			int moveNum = 3;
			if (pointX > pressX) {
				if ((pointX - pressX) >= greaterThan) {
					pointX -= moveNum;
				} else {
					pointX--;
				}
			} else if (pointX < pressX) {
				if ((pressX - pointX) >= greaterThan) {
					pointX += moveNum;
				} else {
					pointX++;
				}
			}
			if (pointY > pressY) {
				if ((pointY - pressY) >= greaterThan) {
					pointY -= moveNum;
				} else {
					pointY--;
				}
			} else if (pointY < pressY) {
				if ((pressY - pointY) >= greaterThan) {
					pointY += moveNum;
				} else {
					pointY++;
				}
			}
		}

		public void movingTeammate() {
			int greaterThan = 9;
			int moveNum = 3;
			if (teamMateXM > teamMateX) {
				if ((teamMateXM - teamMateX) >= greaterThan) {
					teamMateXM -= moveNum;
				} else {
					teamMateXM--;
				}
			} else if (teamMateXM < teamMateX) {
				if ((teamMateX - teamMateXM >= greaterThan)) {
					teamMateXM += moveNum;
				} else {
					teamMateXM++;
				}
			}
			if (teamMateYM > teamMateY) {
				if ((teamMateYM - teamMateY) >= greaterThan) {
					teamMateYM -= moveNum;
				} else {
					teamMateYM--;
				}
			} else if (teamMateYM < teamMateY) {
				if ((teamMateY - teamMateYM >= greaterThan)) {
					teamMateYM += moveNum;
				} else {
					teamMateYM++;
				}
			}

		}

	}

	class GetLine implements Runnable {

		Prom p;

		public GetLine() {
			p = new Prom();
		}

		BufferedReader in;

		public void run() {
			try {
				Socket socket = new Socket(p.dstAddress, p.dstPort);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				while (true) {
					String line = in.readLine();

					String response = line.substring(0) + "\n";
					String[] parts = response.split("!");
					if (!name.equals(parts[0])) {
						Number num = null;
						Number num2 = null;
						try {
							num = NumberFormat.getInstance().parse(parts[1]);
							num2 = NumberFormat.getInstance().parse(parts[2]);
						} catch (ParseException ex) {
							Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
						}
						int dnt = num.intValue();
						int dnt2 = num2.intValue();
						if (teamMateX != dnt && teamMateY != dnt2) {
							teamMateX = dnt;
							teamMateY = dnt2;
						}

					}

//				Log.d("FROM SERVER", msg);
				}
			} catch (IOException ex) {
				Logger.getLogger(GetLine.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
