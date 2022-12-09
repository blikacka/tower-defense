package com.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartActivity extends Activity implements View.OnClickListener {

	int level;
	Button new_game, score, settings, exit;
	Animation animAlpha;
	MediaPlayer mediaPlayer;
	Context context;
	Bitmap bmp;
	AudioPlayer bgMusic;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.start);

		bgMusic = new AudioPlayer(this, R.raw.background_music_marilyn);
		if (MainActivity.sound) {
			bgMusic.play();
		}

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Paint paint = new Paint();
		Rand rand = new Rand();

		if (MainActivity.level == 0) {
//			level = 3;
			level = rand.randInt(1, Map.numberMaps);
			MainActivity.level = level;
		} else {
			level = MainActivity.level;
		}

		context = this;

		Map map = new Map(size.x, size.y, this, paint, level);
		EndActivity ea = new EndActivity();
		bmp = map.draw(level);

		Bitmap bmpx = Bitmap.createScaledBitmap(bmp, size.x, size.y, true);
		Bitmap bmpz = ea.makeTransparent(bmpx, 100);
		Bitmap bmpzz = ea.makeBrightness(bmpz, 150);
		ImageView iv_background = (ImageView) findViewById(R.id.bg);
		iv_background.setImageBitmap(bmpzz);

		int ide = this.getResources().getIdentifier("logo", "drawable", "com.project");
		Bitmap imgB = BitmapFactory.decodeResource(this.getResources(), ide);
		Bitmap logobmp = Bitmap.createScaledBitmap(imgB, size.x / 2, size.x / 12, true);
		ImageView logo = (ImageView) findViewById(R.id.logo);
		logo.setImageBitmap(logobmp);

		ImageView anim1 = (ImageView) findViewById(R.id.anim);
		ImageView anim2 = (ImageView) findViewById(R.id.anim2);
		ImageView anim3 = (ImageView) findViewById(R.id.anim3);
		ImageView anim4 = (ImageView) findViewById(R.id.anim4);
		anim1.getLayoutParams().height = size.x / 16;
		anim1.getLayoutParams().width = size.x / 16;
		anim2.getLayoutParams().height = size.x / 16;
		anim2.getLayoutParams().width = size.x / 16;
		anim3.getLayoutParams().height = size.x / 16;
		anim3.getLayoutParams().width = size.x / 16;
		anim4.getLayoutParams().height = size.x / 16;
		anim4.getLayoutParams().width = size.x / 16;
		anim1.setBackgroundResource(R.drawable.animation);
		anim2.setBackgroundResource(R.drawable.animation);
		anim3.setBackgroundResource(R.drawable.animation);
		anim4.setBackgroundResource(R.drawable.animation);

		final ImageView animArr[] = {anim1, anim2, anim3, anim4};

		animArr[0].post(new Runnable() {
			@Override
			public void run() {
				AnimationDrawable frameAnimation = (AnimationDrawable) animArr[0].getBackground();
				frameAnimation.start();
				AnimationDrawable frameAnimation2 = (AnimationDrawable) animArr[1].getBackground();
				frameAnimation2.start();
				AnimationDrawable frameAnimation3 = (AnimationDrawable) animArr[2].getBackground();
				frameAnimation3.start();
				AnimationDrawable frameAnimation4 = (AnimationDrawable) animArr[3].getBackground();
				frameAnimation4.start();
			}
		});

		animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		mediaPlayer = MediaPlayer.create(this, R.raw.onclick);

		new_game = (Button) findViewById(R.id.new_game_butt);
		score = (Button) findViewById(R.id.score_butt);
		settings = (Button) findViewById(R.id.settings_butt);
		exit = (Button) findViewById(R.id.exit_butt);

		new_game.getLayoutParams().height = size.x / 16;
		new_game.getLayoutParams().width = size.x / 2;
		new_game.setTextSize(size.x / 35);
		new_game.setLayoutParams(new_game.getLayoutParams());

		score.getLayoutParams().height = size.x / 16;
		score.getLayoutParams().width = size.x / 2;
		score.setTextSize(size.x / 35);
		score.setLayoutParams(score.getLayoutParams());

		settings.getLayoutParams().height = size.x / 16;
		settings.getLayoutParams().width = size.x / 2;
		settings.setTextSize(size.x / 35);
		settings.setLayoutParams(settings.getLayoutParams());

		exit.getLayoutParams().height = size.x / 16;
		exit.getLayoutParams().width = size.x / 2;
		exit.setTextSize(size.x / 35);
		exit.setLayoutParams(exit.getLayoutParams());

		new_game.setOnClickListener(this);
		score.setOnClickListener(this);
		settings.setOnClickListener(this);
		exit.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.new_game_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}
				bgMusic.stopPlaying();
				Intent intent = new Intent(this, MainActivity.class);
				finish();
				this.startActivity(intent);
				break;
			case R.id.score_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}
				Intent intentx = new Intent(context, ScoreActivity.class);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				intentx.putExtra("image", byteArray);
				finish();
				context.startActivity(intentx);
				break;
			case R.id.settings_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}
				Intent intenty = new Intent(context, SettingsActivity.class);
				ByteArrayOutputStream streams = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, streams);
				byte[] byteArrays = streams.toByteArray();
				intenty.putExtra("image", byteArrays);
				finish();
				context.startActivity(intenty);
				break;
			case R.id.exit_butt:
				v.startAnimation(animAlpha);
				if (MainActivity.effect) {
					mediaPlayer.start();
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.getLogger(StartActivity.class.getName()).log(Level.SEVERE, null, ex);
				}
				System.exit(0);
				break;
		}
	}

}
