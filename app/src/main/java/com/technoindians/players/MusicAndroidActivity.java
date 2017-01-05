package com.technoindians.players;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dataappsinfo.viralfame.R;
import com.technoindians.pops.ShowToast;

import java.io.IOException;

public class MusicAndroidActivity extends Activity {

	static MediaPlayer mPlayer;
	Button buttonPlay;
	Button buttonStop;
	String url = "http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3"; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dummy_player_layout);
		
		buttonPlay = (Button) findViewById(R.id.dummy_player_play);
		buttonPlay.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mPlayer = new MediaPlayer();
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					mPlayer.setDataSource(url);
				} catch (IllegalArgumentException e) {
					ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
				} catch (SecurityException e) {
					ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
				} catch (IllegalStateException e) {
					ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					mPlayer.prepare();
				} catch (IllegalStateException e) {
					ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
				} catch (IOException e) {
					ShowToast.toast(getApplicationContext(), "You might not set the URI correctly!");
				}
				mPlayer.start();
			}
		});
		
		buttonStop = (Button) findViewById(R.id.dummy_player_stop);
		buttonStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mPlayer!=null && mPlayer.isPlaying()){
					mPlayer.stop();
				}
			}
		});
	}
	
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

}