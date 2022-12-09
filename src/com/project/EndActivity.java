package com.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 *
 * @author Jakub Cieciala
 */
public class EndActivity extends Activity implements View.OnClickListener {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.end);

		Intent intent = getIntent();

		//BG
		byte[] byteArray = intent.getByteArrayExtra("image");
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Bitmap bmpx = Bitmap.createScaledBitmap(bmp, size.x, size.y, true);
		Bitmap bmpz = makeTransparent(bmpx, 100);
		Bitmap bmpzz = makeBrightness(bmpz, 150);
		ImageView iv_background = (ImageView) findViewById(R.id.bg);
		iv_background.setImageBitmap(bmpzz);

		Button play_again_butt = (Button) findViewById(R.id.play_again_butt);
		play_again_butt.setOnClickListener(this);

	}

	public Bitmap makeTransparent(Bitmap src, int value) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap transBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(transBitmap);
		canvas.drawARGB(0, 0, 0, 0);
		final Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAlpha(value);
		canvas.drawBitmap(src, 0, 0, paint);
		return transBitmap;
	}

	public Bitmap makeBrightness(Bitmap src, int value) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		int A, R, G, B;
		int pixel;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		return bmOut;
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.play_again_butt:
				Intent intent = new Intent(this, MainActivity.class);
				finish();
				this.startActivity(intent);
				break;
		}
	}
}
