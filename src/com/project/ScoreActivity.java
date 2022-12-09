package com.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ScoreActivity extends Activity implements View.OnClickListener {

	Button butt;
	Animation animAlpha;
	MediaPlayer mediaPlayer;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.score);
		
		animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		mediaPlayer = MediaPlayer.create(this, R.raw.onclick);
		
		int[] texts = new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5};

		butt = (Button) findViewById(R.id.back_butt);

		Intent intent = getIntent();
		EndActivity ea = new EndActivity();
		Database db = new Database(this);

		//BG
		byte[] byteArray = intent.getByteArrayExtra("image");
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Bitmap bmpx = Bitmap.createScaledBitmap(bmp, size.x, size.y, true);
		Bitmap bmpz = ea.makeTransparent(bmpx, 100);
		Bitmap bmpzz = ea.makeBrightness(bmpz, 150);
		ImageView iv_background = (ImageView) findViewById(R.id.bg);
		iv_background.setImageBitmap(bmpzz);

		for (int i = 0; i < db.selectAll().size(); i++) {
			TextView txt = (TextView) findViewById(texts[i]);
//			txt.setTextSize(size.x / 35);
			txt.setText(String.valueOf(db.selectAll().get(i)));
		}

		butt.getLayoutParams().height = size.x / 16;
		butt.setTextSize(size.x / 35);
		butt.setLayoutParams(butt.getLayoutParams());
		
		butt.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}
				Intent intent = new Intent(this, StartActivity.class);
				finish();
				this.startActivity(intent);
				break;
		}
	}

}
