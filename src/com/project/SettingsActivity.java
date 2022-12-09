package com.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements View.OnClickListener {

	Button butt, sound_butt, effect_butt;
	Animation animAlpha;
	MediaPlayer mediaPlayer;
	AudioPlayer bgMusic;
	Context context;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.settings);

		animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		mediaPlayer = MediaPlayer.create(this, R.raw.onclick);

		butt = (Button) findViewById(R.id.back_butt);
		sound_butt = (Button) findViewById(R.id.sound_butt);
		effect_butt = (Button) findViewById(R.id.effect_butt);

		Intent intent = getIntent();
		EndActivity ea = new EndActivity();
		context = this;

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

		sound_butt.getLayoutParams().height = size.x / 16;
		sound_butt.getLayoutParams().width = size.x / 2;
		sound_butt.setTextSize(size.x / 35);
		sound_butt.setLayoutParams(sound_butt.getLayoutParams());

		effect_butt.getLayoutParams().height = size.x / 16;
		effect_butt.getLayoutParams().width = size.x / 2;
		effect_butt.setTextSize(size.x / 35);
		effect_butt.setLayoutParams(effect_butt.getLayoutParams());

		butt.getLayoutParams().height = size.x / 16;
		butt.setTextSize(size.x / 35);
		butt.setLayoutParams(butt.getLayoutParams());

		sound_butt.setOnClickListener(this);
		effect_butt.setOnClickListener(this);
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
			case R.id.sound_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}
				if (MainActivity.sound) {
					Toast.makeText(context, getText(R.string.sound_off), Toast.LENGTH_SHORT).show();
					MainActivity.sound = false;
				} else {
					Toast.makeText(context, getText(R.string.sound_on), Toast.LENGTH_SHORT).show();
					MainActivity.sound = true;
				}
				break;
			case R.id.effect_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}
				if (MainActivity.effect) {
					Toast.makeText(context, getText(R.string.effect_off), Toast.LENGTH_SHORT).show();
					MainActivity.effect = false;
				} else {
					Toast.makeText(context, getText(R.string.effect_on), Toast.LENGTH_SHORT).show();
					MainActivity.effect = true;
				}
				break;
		}
	}

}
