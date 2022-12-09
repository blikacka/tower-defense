/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.io.File;

public class AudioPlayer {

	private Context c;
	private int file;
	static int position;
	static int num = -1, prevNum = 0;
	Handler handler;
	MediaPlayer ap;

	public AudioPlayer(Context c, int file) {
		this.c = c;
		this.file = file;
		num++;
		handler = new Handler();
	}

	public void play() {
		ap = MediaPlayer.create(c, file);
		ap.setLooping(true);
		ap.start();

		if (ap.isPlaying()) {

			Runnable notification = new Runnable() {

				public void run() {
					position = ap.getCurrentPosition();
				}

			};
			handler.postDelayed(notification, 100);

		}

		if (prevNum != num) {
			prevNum = num;
			ap.seekTo(position);
		}

	}

	public void stopPlaying() {
		if (ap != null) {
			ap.stop();
			ap.release();
			ap = null;
			position = 0;
			num = -1;
			prevNum = 0;
		}
	}

}
