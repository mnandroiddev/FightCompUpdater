package com.fight.comp.updater.ws;

import com.fight.comp.updater.ws.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class info extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.pop);
	}
	
	public void liscense(View v){
		Intent i = new Intent(Intent.ACTION_VIEW).setData(Uri
				.parse("http://creativecommons.org/licenses/by/3.0/us/"));
		startActivity(i);
	}

}
