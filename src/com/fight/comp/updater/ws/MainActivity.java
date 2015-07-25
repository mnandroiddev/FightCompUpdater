package com.fight.comp.updater.ws;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView textView;
	String link;
	Button btn;
	boolean created = false;
	TextView attribution;

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			textView.setText(intent.getStringExtra("text"));
			btn.setVisibility(View.VISIBLE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		textView = (TextView) findViewById(R.id.textView);
		btn = (Button) findViewById(R.id.btn);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		link = sharedPreferences.getString("link", "");

		this.registerReceiver(receiver, new IntentFilter("allDoneChangeText"));

		Intent newIntent = new Intent(this, ScrapingService.class);
		newIntent.putExtra("visible", true);
		startService(newIntent);

	}

	public void launch(View v) {
		Intent oldVideo = new Intent(Intent.ACTION_VIEW).setData(Uri
				.parse("http://www.worldstarhiphop.com" + link));
		startActivity(oldVideo);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		this.unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.registerReceiver(receiver, new IntentFilter("allDoneChangeText"));

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		link = sharedPreferences.getString("link", "");
		if (created) {
			textView.setText(R.string.done);
			btn.setVisibility(View.VISIBLE);
		}
		created = true;
		super.onResume();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.about:
			Toast.makeText(this, "Fight Comp Updater v1.3", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.art:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		new MenuInflater(this).inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getEventTime() - event.getDownTime() > 45000) {// 45s

				MediaPlayer mp = MediaPlayer.create(this, R.raw.bull);
				mp.start();
			}
		}
		return super.onTouchEvent(event);
	}

}
