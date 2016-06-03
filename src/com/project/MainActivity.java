package com.project;

import android.app.Activity;
import android.content.Context;
import static android.content.Context.WIFI_SERVICE;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity implements OnTouchListener {

	OurView view;
	Move move;
	Bitmap ball, blob, bg, mapBitmap;
	Paint paint;
	int pressX, pressY, pointX, pointY, teamMateX, teamMateY, teamMateXM, teamMateYM;
	int sizeScreenX, sizeScreenY;
	int sizeBlockX, sizeBlockY;
	int level = 1;
	int spacing = 15, sp = 0, ssp = 0, numberMobs = 5;  // MOB nastavení - spacing - mezera mezi mobama, sp a ssp pomocné proměnné, numberMobs - počet mobků
	String name;
	GifView gifView, wy, tr;
	Context context;
	Network network;
	GetLine getLine;
	Map map;
	Rand rand = new Rand();
	int[][] mapArr;
	Animation wisp, tree;
	Shot shot;
	String fromServer = null;
	Boolean multiplayer = false;
	String wisps[] = new String[23];
	String trees[] = new String[23];
	List<Mob> mob = new ArrayList<Mob>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		name = rand.randString(10);

		if (multiplayer) {
			network = new Network();
			new Thread(network).start();

			getLine = new GetLine();
			new Thread(getLine).start();
		}

		context = this;
		Toast.makeText(this, ipaddr(), Toast.LENGTH_LONG).show();

		wy = new GifView(this, "wisp_yellow");
		tr = new GifView((this), "tree_right");
		//new Thread(wy).start();

		move = new Move(this);
		paint = new Paint();
		shot = new Shot();

		for (int i = 0; i < 23; i++) {
			wisps[i] = "wisp_" + String.valueOf(i + 1);
			trees[i] = "tree_" + String.valueOf(i + 1);
		}

		ball = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		pressX = pressY = pointX = pointY = teamMateX = teamMateY = teamMateXM = teamMateYM = 0;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		sizeScreenX = dm.widthPixels;
		sizeScreenY = dm.heightPixels;

		level = rand.randInt(1, 3);
		map = new Map(sizeScreenX, sizeScreenY, this, paint, level);
		mapBitmap = map.draw(level);

		wisp = new Animation(this, wisps, paint, sizeScreenX, sizeScreenY);

		Bitmap bgB = BitmapFactory.decodeResource(getResources(), R.drawable.bgx);
		bg = Bitmap.createScaledBitmap(bgB, dm.widthPixels, dm.heightPixels, true);

		view = new OurView(this);
		view.setOnTouchListener(this);
		android.view.ViewGroup.LayoutParams lp = new android.view.ViewGroup.LayoutParams(dm.widthPixels, dm.heightPixels);

		setContentView(view, lp);

		//animation = new Animation(this, wisps);
		//new Thread(animation).start();
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

		public void run() {
			while (isItOK == true) {
				if (!holder.getSurface().isValid()) {
					continue;
				}
//				sleep(50);
				move.moving();
//				move.movingTeammate();

				Canvas c = holder.lockCanvas();
				c.drawRect(new Rect(0, 0, sizeScreenX, sizeScreenY), paint);
				c.drawBitmap(mapBitmap, 0, 0, paint);

				if (sp == spacing * ssp) {
					mob.add(new Mob(trees, context, map, sizeScreenX, sizeScreenY, paint));
					ssp++;
				}
				
				for (int i = 0; i < mob.size(); i++) {
					mob.get(i).draw(c, 0);
				}

				wisp.play(c, pointX, pointY, 1.75, 0);

				if (sp != spacing * (numberMobs - 1)) {
					sp++;
				}

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
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				pressX = Math.round(event.getX());
				pressY = Math.round(event.getY());
				break;
		}
		return true;
	}

	public class Shot {

		int x = 0, y = 0;

		public void shooting(Canvas canvas) {
			paint.setColor(Color.BLACK);
			if (x == 2 || y == 100) {
				x = 0;
				y = 0;
			}
			canvas.drawOval(new RectF(x, y, x + 10, y + 10), paint);
			x += 5;
			y += 5;
		}
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
