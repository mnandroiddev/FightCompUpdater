package com.fight.comp.updater.ws;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	
	Intent newIntent = new Intent(context, ScrapingService.class);
	newIntent.putExtra("visible", false);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				newIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
		                             AlarmManager.INTERVAL_DAY, pendingIntent);
		

	}

}
