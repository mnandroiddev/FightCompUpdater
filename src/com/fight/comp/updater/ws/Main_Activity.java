package com.fight.comp.updater.ws;

import com.fight.comp.updater.ws.R;

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

public class Main_Activity extends Activity {

	BroadcastReceiver receiver = new Radio();
	TextView textView;
	String link;
	Button btn;
	boolean created = false;
	TextView attribution;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		textView = (TextView) findViewById(R.id.textView);
		btn = (Button) findViewById(R.id.btn);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		link = sharedPreferences.getString("link", "");

		this.registerReceiver(receiver, new IntentFilter("allDoneChangeText"));

		Intent newIntent = new Intent(this, Servant.class);
		newIntent.putExtra("visible", true);
		startService(newIntent);

	}

	public void launch(View v) {
		MediaPlayer mp = MediaPlayer.create(this, R.raw.bell);
		mp.start();
		Intent oldVideo = new Intent(Intent.ACTION_VIEW).setData(Uri
				.parse("http://www.worldstarhiphop.com/videos/" + link));

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

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
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

	class Radio extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			textView.setText(R.string.done);
			btn.setVisibility(View.VISIBLE);

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case R.id.about:
			Toast.makeText(this, "Fight Comp Updater v1.0", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.art:
			startActivity(new Intent(this, info.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		new MenuInflater(this).inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override //Easter egg
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getEventTime() - event.getDownTime() > 30000) {//30 seconds
				MediaPlayer mp = MediaPlayer.create(this, R.raw.bull);
				mp.start();
			}
		}
		return super.onTouchEvent(event);
	}

}
